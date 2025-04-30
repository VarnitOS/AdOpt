package com.adopt.repositories;

import com.adopt.models.AdCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for managing campaign data in the database
 */
@Repository
public interface CampaignRepository extends JpaRepository<AdCampaign, Long> {

    /**
     * Find campaigns by advertiser ID
     */
    List<AdCampaign> findByAdvertiserId(String advertiserId);
    
    /**
     * Find active campaigns
     */
    List<AdCampaign> findByStatus(AdCampaign.CampaignStatus status);
    
    /**
     * Find campaigns that are running (active and within start/end dates)
     */
    @Query("SELECT c FROM AdCampaign c WHERE c.status = 'ACTIVE' " +
           "AND (c.startDate IS NULL OR c.startDate <= :now) " +
           "AND (c.endDate IS NULL OR c.endDate >= :now)")
    List<AdCampaign> findRunningCampaigns(LocalDateTime now);
    
    /**
     * Find campaigns with remaining budget
     */
    List<AdCampaign> findByRemainingBudgetGreaterThan(java.math.BigDecimal amount);
    
    /**
     * Find campaigns by campaign type
     */
    List<AdCampaign> findByCampaignType(AdCampaign.CampaignType campaignType);
} 