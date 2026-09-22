package com.bookworm.repository;

import com.bookworm.model.promo.GiftPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GiftPointRepository extends JpaRepository<GiftPoint, UUID> {

    Page<GiftPoint> findByUserId(UUID userId, Pageable pageable);
}
