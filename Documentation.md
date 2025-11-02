# Digital Logistics & Supply Chain Platform — Documentation

This document gives you a simple, practical guide to the project: what it does, the main building blocks, how the data models relate to each other, the key business rules, and how to try the API with example requests.

## 1) What this project is

A modular REST API for logistics operations:
- Products and inventory across warehouses
- Purchase orders (suppliers → inbound/receiving)
- Sales orders (clients → reservation → shipment → delivery)
- Shipments with carriers and cut-off rules
- Strict no-negative-stock with auditable inventory movements

Built with:
- Spring Boot, Spring Web, Spring Data JPA, Bean Validation
- DTO + Mapper layer (implemented with simple mapper components)
- Centralized exception handling

Tip: The app focuses on clean layering (Controller → Service → Repository → Model) with DTOs validated at the edge.

## 2) Roles and responsibilities

- ADMIN: manages catalog (products), warehouses, parameters, purchase orders
- WAREHOUSE_MANAGER: manages stocks, movements, reservations, shipments
- CLIENT: creates sales orders and tracks status

Note: The project simulates roles at the business logic level (no JWT security wired in by default).

## 3) Data model and relations

High level entities and how they connect:

- Product
  - Has many Inventory records (one per Warehouse)
  - Appears in SalesOrderLine and PurchaseOrderLine

- Warehouse
  - Has many Inventory records for products
  - Source of stock for SalesOrders

- Inventory (Product × Warehouse)
  - qtyOnHand: physical stock
  - qtyReserved: reserved for orders
  - available = qtyOnHand - qtyReserved
  - Has many InventoryMovements

- InventoryMovement
  - Types: INBOUND, OUTBOUND, ADJUSTMENT
  - Always updates Inventory and keeps an audit trail

- Supplier → PurchaseOrder → PurchaseOrderLine
  - PurchaseOrder has a Supplier and a WarehouseManager
  - Lines reference Products and quantities
  - Receiving creates INBOUND movements and increases qtyOnHand

- Client → SalesOrder → SalesOrderLine
  - SalesOrder references the client and the source Warehouse
  - Lines reference Products, quantities, and prices
  - Reservation increases qtyReserved (no negative available)

- Carrier → Shipment
  - Shipment references the SalesOrder and the Carrier
  - Carrier may enforce a cut-off time and daily capacity

Status fields:
- SalesOrderStatus: CREATED → RESERVED → SHIPPED → DELIVERED → CANCELED
- ShipmentStatus: PLANNED → IN_TRANSIT → DELIVERED
- PurchaseOrderStatus: CREATED → APPROVED → RECEIVED → CANCELED

## 4) Architecture in a nutshell

- Controller layer: REST endpoints, request validation (@Valid)
- Service layer: business rules (reservation, status transitions, receiving, cut-off checks)
- Repository layer: Spring Data JPA
- DTO layer: request/response models, validated, no JPA annotations
- Mapper layer: converts between Entities and DTOs
- Exception handling: centralized via a GlobalExceptionHandler

## 5) Core business rules (highlights)

- No negative stock: OUTBOUND or negative ADJUSTMENT must not drop qtyOnHand below qtyReserved
- Reservation before shipping: SalesOrder must be RESERVED before Shipment can ship
- Backorders: if not enough availability at reservation time, quantities can be backordered (service-level logic)
- Cut-off time and capacity: Carriers can restrict when shipments can depart (cut-off) and how many per day (capacity)
- Purchase order receiving: Receiving a PO creates INBOUND movements and increases qtyOnHand

## 6) Typical flows (with examples)

### A) Purchasing and receiving
1) Create a PurchaseOrder with Supplier and WarehouseManager
2) Approve the PO
3) Receive (partial or full): creates INBOUND movements, increases inventory

Example create PO request:
```http
POST /api/purchaseOrders
Content-Type: application/json

{
  "supplierId": "d1e2c3a4-...",
  "warehouseManagerId": "a1b2c3d4-...",
  "expectedDelivery": "2025-11-10T10:00:00"
}
```
Then approve and receive:
```http
POST /api/purchaseOrders/{id}/approve
POST /api/purchaseOrders/{id}/receive
```

Add lines to a PO:
```http
POST /api/purchaseOrderLines
Content-Type: application/json

{
  "purchaseOrderId": "<po-id>",
  "productId": "<product-id>",
  "quantity": 100,
  "unitPrice": 9.99
}
```

### B) Selling and shipping
1) Create SalesOrder and lines for a selected source Warehouse
2) Reserve: increases qtyReserved if available; backorder if not
3) Create Shipment (must be RESERVED) and ship respecting carrier cut-off and capacity
4) Delivery: mark shipment and order as DELIVERED

Example reservation-type flow (simplified endpoints may vary):
```http
POST /api/salesOrders
POST /api/salesOrderLines
# Service reserves quantities (qtyReserved) if available at the selected Warehouse
```

Example shipment:
```http
POST /api/shipments         # create PLANNED shipment
POST /api/shipments/{id}/ship     # moves to IN_TRANSIT, order → SHIPPED
POST /api/shipments/{id}/deliver  # shipment → DELIVERED, order → DELIVERED
```

### C) Inventory operations (direct)
- INBOUND: increase qtyOnHand
- OUTBOUND: decrease qtyOnHand (must respect available)
- ADJUSTMENT: correct stock, never below qtyReserved

Example movement:
```http
POST /api/inventoryMovements
Content-Type: application/json

{
  "productId": "<product-id>",
  "warehouseId": "<warehouse-id>",
  "type": "INBOUND",
  "quantity": 20
}
```

## 7) API modules and key endpoints

Note: Endpoints follow a consistent CRUD pattern (POST, GET, PUT, DELETE). Here’s the overview:

- Products: `/api/products`
  - CRUD, plus `GET /api/products/sku/{sku}`

- Warehouses: `/api/warehouses`
  - CRUD; assign/clear manager via service logic

- Inventory: `/api/inventories`
  - CRUD; quantities validated; linked to Product and Warehouse

- Inventory Movements: `/api/inventoryMovements`
  - Create applies movement; delete reverts; INBOUND/OUTBOUND/ADJUSTMENT

- Suppliers: `/api/suppliers`
  - CRUD

- Purchase Orders: `/api/purchaseOrders`
  - CRUD; `POST /{id}/approve`, `POST /{id}/cancel`, `POST /{id}/receive`

- Purchase Order Lines: `/api/purchaseOrderLines`
  - CRUD; allowed when PO is CREATED/APPROVED

- Sales Orders: `/api/salesOrders`
  - CRUD; createdAt/status managed; integrates with reservation logic

- Sales Order Lines: `/api/salesOrderLines`
  - CRUD; reservation and backorder logic applied; releases reservation on change

- Shipments: `/api/shipments`
  - CRUD; `POST /{id}/ship`, `POST /{id}/deliver`; updates SalesOrder status/timestamps

- Carriers: `/api/carriers`
  - CRUD; activate/suspend/resetDailyCount; ensure cut-off and capacity; integrated with shipping

## 8) Validation and error handling

- DTO validation via jakarta.validation:
  - Examples: @NotBlank, @NotNull, @Min, @DecimalMin, @FutureOrPresent
- Centralized exception handler returns structured JSON errors, for example:
```json
{
  "timestamp": "2025-10-26T11:00:00Z",
  "status": 400,
  "message": "Quantity must be at least 1",
  "path": "/api/purchaseOrderLines"
}
```
- Typical error types:
  - BadRequest for invalid inputs or business rule violations
  - Not found for missing resources
  - Conflict for stock unavailability (no negative stock)

## 9) How to run and try it

- Build and run the Spring Boot app (standard Maven Spring Boot workflow)
- Use a REST client (Postman, curl, VS Code Thunder Client) to call endpoints above
- Suggested order to play with the system:
  1. Create Warehouses and Products
  2. Create Inventory (product × warehouse)
  3. Record some INBOUND movements to add stock
  4. Create a Supplier and a Purchase Order, add lines, approve, receive
  5. Create a Client (user) and a Sales Order with lines; trigger reservation logic
  6. Create a Carrier; create Shipment for the order; ship and deliver

## 10) Status transitions (cheat sheet)

- PurchaseOrder: CREATED → APPROVED → RECEIVED; or CANCELED
- SalesOrder: CREATED → RESERVED → SHIPPED → DELIVERED; or CANCELED
- Shipment: PLANNED → IN_TRANSIT → DELIVERED

Rules to remember:
- PO lines editable only while PO is CREATED/APPROVED
- SalesOrder must be RESERVED to ship
- Inventory can never go negative; keep qtyOnHand ≥ qtyReserved

## 11) What’s built vs. future work

Implemented now:
- Full CRUD stacks for core entities (DTO/Mapper/Repository/Service/Controller)
- Reservation logic on sales order lines (qtyReserved and backorders)
- Inventory movements with transactional apply/revert
- Shipment lifecycle with carrier checks (cut-off, capacity, status transitions)
- Purchase order lifecycle with basic approve/cancel/receive

Good next steps:
- Reservation TTL (auto-release after a delay)
- Multi-warehouse allocation strategies
- Pagination and filtering endpoints
- Swagger/OpenAPI documentation
- Scheduled job to reset carrier daily capacity counters
- Unit and integration tests (JUnit5 + Mockito) for critical rules

---

If you want deeper examples (end-to-end scripts for “Create order → Reserve → Ship → Deliver” or “Create PO → Receive → Stock up”), say the word and we’ll add a step-by-step guide with sample payloads tailored to your current data.
