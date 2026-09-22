package com.bookworm.repository;

import com.bookworm.model.recommendation.Recommendation;
import com.bookworm.model.recommendation.RecommendationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, UUID> {

    Page<Recommendation> findByUserIdAndRecommendationType(UUID userId, RecommendationType type, Pageable pageable);

    Page<Recommendation> findBySourceBookIdAndRecommendationType(UUID sourceBookId, RecommendationType type, Pageable pageable);
}
