package com.project.supplychain.services;

import com.project.supplychain.DTOs.productDTOs.ProductDTO;
import com.project.supplychain.DTOs.salesOrderDTOs.SalesOrderDTO;
import com.project.supplychain.enums.OrderStatus;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.mappers.ProductMapper;
import com.project.supplychain.models.Inventory;
import com.project.supplychain.models.Product;
import com.project.supplychain.models.SalesOrderLine;
import com.project.supplychain.repositories.InventoryRepository;
import com.project.supplychain.repositories.ProductRepository;
import com.project.supplychain.repositories.SalesOrderLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private SalesOrderLineRepository salesOrderLineRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    public HashMap<String, Object> createProduct(ProductDTO dto) {
        Product product = productMapper.toEntity(dto);
        product.setId(null);
        HashMap<String, Object> result = new HashMap<>();
        if(productRepository.getBySku(product.getSku()) != null){
            throw new BadRequestException("The Sku Already Exists");
        }
        Product saved = productRepository.save(product);

        result.put("message", "Product created successfully");
        result.put("product", productMapper.toDTO(saved));
        return result;
    }

    public HashMap<String, Object> getProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Product not found"));
        HashMap<String, Object> result = new HashMap<>();
        result.put("product", productMapper.toDTO(product));
        return result;
    }
    public HashMap<String, Object> getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new BadRequestException("Product not found"));
        HashMap<String, Object> result = new HashMap<>();
        result.put("product", productMapper.toDTO(product));
        return result;
    }

    public HashMap<String, Object> listProducts() {
        List<ProductDTO> products = productRepository.findAll()
                .stream()
                .map(productMapper::toDTO)
                .toList();
        HashMap<String, Object> result = new HashMap<>();
        result.put("products", products);
        return result;
    }

    public HashMap<String, Object> updateProduct(UUID id, ProductDTO dto) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Product not found"));

        existing.setSku(dto.getSku());
        existing.setName(dto.getName());
        existing.setCategory(dto.getCategory());
        existing.setActive(dto.isActive());
        existing.setOriginalPrice(dto.getOriginalPrice());
        existing.setProfit(dto.getProfit());

        Product saved = productRepository.save(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Product updated successfully");
        result.put("product", productMapper.toDTO(saved));
        return result;
    }

    public HashMap<String, Object> deleteProduct(UUID id) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Product not found"));
        productRepository.delete(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Product deleted successfully");
        return result;
    }

    public HashMap<String , Object> DesactivateProduct(String sku){
        try{
            Product product = productRepository.findProductBySku(sku);
            List<SalesOrderLine> salesOrderLineCreated = salesOrderLineRepository.getByProduct_SkuAndSalesOrder_Status(sku, OrderStatus.CREATED);
            List<SalesOrderLine> salesOrderLineReserved = salesOrderLineRepository.getByProduct_SkuAndSalesOrder_Status(sku, OrderStatus.RESERVED);
            List<Inventory> inventoriesHasProduct = inventoryRepository.getByQtyOnHandGreaterThanAndProduct(0,product);
            HashMap<String, Object> result = new HashMap<>();

            if(!inventoriesHasProduct.isEmpty()){
                throw new BadRequestException("There is a stock of this product on inventories , You cant delete it ");
            }

            if(product != null ){
                for(SalesOrderLine salesOrderLine : salesOrderLineCreated){
                    if(salesOrderLine.getProduct().isActive()){
                        throw new BadRequestException("There is a product is active so you cant desactivate it .");
                    }
                }
                for(SalesOrderLine salesOrderLine : salesOrderLineReserved){
                    if(salesOrderLine.getProduct().isActive()){
                        throw new BadRequestException("There is a product is active so you cant desactivate it .");
                    }
                }
                product.setActive(false);
                productRepository.saveAndFlush(product);
                result.put("message","The product has been desactivated successfully .");
                return result;
            }else{
                throw new BadRequestException("The product is not found !");
            }
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

}
