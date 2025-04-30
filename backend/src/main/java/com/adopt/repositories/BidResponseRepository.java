package com.adopt.repositories;

import com.adopt.models.BidResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for managing bid response data in the database
 */
@Repository
public interface BidResponseRepository extends JpaRepository<BidResponse, String> {

    /**
     * Find bid responses by campaign ID
     */
    List<BidResponse> findByCampaignId(Long campaignId);
    
    /**
     * Find bid responses by status
     */
    List<BidResponse> findByStatus(BidResponse.BidStatus status);
    
    /**
     * Find bid responses created within a time range
     */
    List<BidResponse> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * Find winning bid responses
     */
    List<BidResponse> findByIsWonTrue();
    
    /**
     * Find clicked bid responses
     */
    List<BidResponse> findByIsClickedTrue();
    
    /**
     * Find converted bid responses
     */
    List<BidResponse> findByIsConvertedTrue();
    
    /**
     * Get average bid price by campaign ID
     */
    @Query("SELECT AVG(b.bidPrice) FROM BidResponse b WHERE b.campaign.id = :campaignId")
    Double getAverageBidPriceByCampaignId(Long campaignId);
    
    /**
     * Get bid responses for a specific request ID
     */
    List<BidResponse> findByBidRequestRequestId(String requestId);
} 