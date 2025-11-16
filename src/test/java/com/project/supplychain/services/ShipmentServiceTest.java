package com.project.supplychain.services;

import com.project.supplychain.DTOs.shipmentDTOs.ShipmentDTO;
import com.project.supplychain.enums.OrderStatus;
import com.project.supplychain.enums.ShipmentStatus;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.mappers.ShipmentMapper;
import com.project.supplychain.models.Carrier;
import com.project.supplychain.models.SalesOrder;
import com.project.supplychain.models.Shipment;
import com.project.supplychain.repositories.CarrierRepository;
import com.project.supplychain.repositories.SalesOrderRepository;
import com.project.supplychain.repositories.ShipmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @Mock
    private CarrierRepository carrierRepository;

    @Mock
    private ShipmentMapper shipmentMapper;

    @Mock
    private CarrierService carrierService;

    @InjectMocks
    private ShipmentService service;

    @Test
    void create_success_returnsDto() {
        UUID soId = UUID.randomUUID();
        UUID carrierId = UUID.randomUUID();

        ShipmentDTO dto = ShipmentDTO.builder().salesOrderId(soId).carrierId(carrierId).trackingNumber("T123").build();

        SalesOrder so = new SalesOrder(); so.setId(soId);
        Carrier carrier = new Carrier(); carrier.setId(carrierId);

        Shipment entity = new Shipment();
        Shipment saved = new Shipment(); saved.setId(UUID.randomUUID());

        when(salesOrderRepository.findById(soId)).thenReturn(Optional.of(so));
        when(carrierRepository.findById(carrierId)).thenReturn(Optional.of(carrier));
        when(shipmentMapper.toEntity(dto)).thenReturn(entity);
        when(shipmentRepository.save(entity)).thenReturn(saved);
        when(shipmentMapper.toDTO(saved)).thenReturn(dto);

        var res = service.create(dto);
        assertThat(res).containsEntry("message", "Shipment created successfully");
    }

    @Test
    void ship_success_marksInTransit_andIncrementsCarrier() {
        UUID id = UUID.randomUUID();
        Shipment s = new Shipment(); s.setId(id); s.setStatus(ShipmentStatus.PLANNED);
        Carrier carrier = new Carrier(); carrier.setId(UUID.randomUUID()); carrier.setStatus(null);
        s.setCarrier(carrier);
        SalesOrder so = new SalesOrder(); so.setId(UUID.randomUUID()); s.setSalesOrder(so);

        when(shipmentRepository.findById(id)).thenReturn(Optional.of(s));
        when(shipmentRepository.save(s)).thenReturn(s);

        doNothing().when(carrierService).ensureCanShip(carrier);
        doNothing().when(carrierService).incrementDailyShipments(carrier);

        var res = service.ship(id);

        verify(carrierService).ensureCanShip(carrier);
        verify(carrierService).incrementDailyShipments(carrier);
        verify(salesOrderRepository).save(so);
        assertThat(res).containsEntry("message", "Shipment marked as IN_TRANSIT");
    }

    @Test
    void ship_noCarrier_throws() {
        UUID id = UUID.randomUUID();
        Shipment s = new Shipment(); s.setId(id); s.setStatus(ShipmentStatus.PLANNED); s.setCarrier(null);
        when(shipmentRepository.findById(id)).thenReturn(Optional.of(s));
        assertThatThrownBy(() -> service.ship(id)).isInstanceOf(BadRequestException.class).hasMessageContaining("Shipment has no carrier assigned");
    }

    @Test
    void deliver_success_marksDelivered() {
        UUID id = UUID.randomUUID();
        Shipment s = new Shipment(); s.setId(id); s.setStatus(ShipmentStatus.IN_TRANSIT);
        SalesOrder so = new SalesOrder(); so.setId(UUID.randomUUID()); s.setSalesOrder(so);
        when(shipmentRepository.findById(id)).thenReturn(Optional.of(s));
        when(shipmentRepository.save(s)).thenReturn(s);

        var res = service.deliver(id);

        verify(salesOrderRepository).save(so);
        assertThat(res).containsEntry("message", "Shipment marked as DELIVERED");
    }

    @Test
    void update_notPlanned_throws() {
        UUID id = UUID.randomUUID();
        Shipment s = new Shipment(); s.setId(id); s.setStatus(ShipmentStatus.IN_TRANSIT);
        when(shipmentRepository.findById(id)).thenReturn(Optional.of(s));
        ShipmentDTO dto = new ShipmentDTO();
        assertThatThrownBy(() -> service.update(id, dto)).isInstanceOf(BadRequestException.class).hasMessageContaining("Only PLANNED shipments can be updated");
    }
}

