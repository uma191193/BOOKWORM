package com.bookworm.service;

import com.bookworm.dto.shipping.ShipmentResponse;

import java.util.List;
import java.util.UUID;

public interface ShipmentService {

    List<ShipmentResponse> listByOrder(UUID orderId, UUID requesterId);
}
