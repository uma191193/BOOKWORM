package com.bookworm.service.impl;

import com.bookworm.dto.shipping.ShipmentResponse;
import com.bookworm.repository.ShipmentRepository;
import com.bookworm.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;

    @Override
    public List<ShipmentResponse> listByOrder(UUID orderId, UUID requesterId) {
        return shipmentRepository.findByOrderId(orderId).stream()
                .map(s -> new ShipmentResponse(s.getId(), s.getOrderId(), s.getTrackingNumber(),
                        s.getCarrier(), s.getEstimatedDeliveryDate(), s.getActualDeliveryDate(),
                        s.getShippingRate(), s.getStatus(), s.isReturn()))
                .toList();
    }
}
