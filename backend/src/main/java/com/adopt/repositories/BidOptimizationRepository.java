package com.adopt.repositories;

import com.adopt.models.BidOptimization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for managing bid optimization data in the database
 */
@Repository
public interface BidOptimizationRepository extends JpaRepository<BidOptimization, Long> {

    /**
     * Find optimizations by campaign ID
     */
    List<BidOptimization> findByCampaignId(Long campaignId);
    
    /**
     * Find optimizations by status
     */
    List<BidOptimization> findByStatus(BidOptimization.OptimizationStatus status);
    
    /**
     * Find optimizations by campaign ID and status
     */
    List<BidOptimization> findByCampaignIdAndStatus(Long campaignId, BidOptimization.OptimizationStatus status);
    
    /**
     * Find optimizations created within a time range
     */
    List<BidOptimization> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * Find optimizations applied within a time range
     */
    List<BidOptimization> findByAppliedAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * Find optimizations by game theory model type
     */
    List<BidOptimization> findByGameTheoryModel(String gameTheoryModel);
} 