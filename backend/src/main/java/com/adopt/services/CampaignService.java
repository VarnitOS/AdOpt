package com.adopt.services;

import com.adopt.models.AdCampaign;
import com.adopt.models.BidRequest;
import com.adopt.models.BidResponse;
import com.adopt.models.CampaignMetric;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Service for managing ad campaigns and responses
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CampaignService {

    // ID generator for new campaigns
    private final AtomicLong campaignIdGenerator = new AtomicLong(3); // Start from 3 since we have 2 sample campaigns
    
    // In-memory storage of campaigns (in a real system, this would be a database)
    private final Map<Long, AdCampaign> campaignCache = new HashMap<>();
    
    // In-memory storage of bid responses by ID
    private final Map<String, BidResponse> bidResponseCache = new ConcurrentHashMap<>();
    
    /**
     * Find all campaigns
     */
    public List<AdCampaign> findAllCampaigns() {
        // Initialize sample campaigns if needed
        if (campaignCache.isEmpty()) {
            createSampleCampaigns();
        }
        
        return new ArrayList<>(campaignCache.values());
    }
    
    /**
     * Find a campaign by ID
     */
    public AdCampaign findCampaignById(Long campaignId) {
        // Initialize sample campaigns if needed
        if (campaignCache.isEmpty()) {
            createSampleCampaigns();
        }
        
        return campaignCache.get(campaignId);
    }
    
    /**
     * Save a campaign (create or update)
     */
    public AdCampaign saveCampaign(AdCampaign campaign) {
        if (campaign.getId() == null) {
            // New campaign
            campaign.setId(campaignIdGenerator.incrementAndGet());
        }
        
        campaignCache.put(campaign.getId(), campaign);
        return campaign;
    }
    
    /**
     * Delete a campaign
     */
    public void deleteCampaign(Long campaignId) {
        campaignCache.remove(campaignId);
    }
    
    /**
     * Update campaign status
     */
    public AdCampaign updateCampaignStatus(Long campaignId, AdCampaign.CampaignStatus status) {
        AdCampaign campaign = findCampaignById(campaignId);
        
        if (campaign != null) {
            campaign.setStatus(status);
            campaignCache.put(campaignId, campaign);
        }
        
        return campaign;
    }
    
    /**
     * Get campaign metrics within a date range
     */
    public Set<CampaignMetric> getCampaignMetrics(Long campaignId, LocalDate startDate, LocalDate endDate) {
        AdCampaign campaign = findCampaignById(campaignId);
        
        if (campaign == null) {
            return new HashSet<>();
        }
        
        // Filter metrics by date range if provided
        if (startDate != null && endDate != null) {
            return campaign.getMetrics().stream()
                    .filter(metric -> !metric.getDate().isBefore(startDate) && !metric.getDate().isAfter(endDate))
                    .collect(Collectors.toSet());
        } else if (startDate != null) {
            return campaign.getMetrics().stream()
                    .filter(metric -> !metric.getDate().isBefore(startDate))
                    .collect(Collectors.toSet());
        } else if (endDate != null) {
            return campaign.getMetrics().stream()
                    .filter(metric -> !metric.getDate().isAfter(endDate))
                    .collect(Collectors.toSet());
        } else {
            return campaign.getMetrics();
        }
    }
    
    /**
     * Get campaign performance summary
     */
    public Map<String, Object> getCampaignPerformanceSummary(Long campaignId) {
        AdCampaign campaign = findCampaignById(campaignId);
        Map<String, Object> summary = new HashMap<>();
        
        if (campaign == null) {
            return summary;
        }
        
        // Campaign details
        summary.put("id", campaign.getId());
        summary.put("name", campaign.getName());
        summary.put("status", campaign.getStatus());
        summary.put("type", campaign.getCampaignType());
        
        // Budget metrics
        summary.put("totalBudget", campaign.getTotalBudget());
        summary.put("remainingBudget", campaign.getRemainingBudget());
        summary.put("spentBudget", campaign.getTotalBudget().subtract(campaign.getRemainingBudget()));
        
        // Performance metrics
        long totalImpressions = 0;
        long totalClicks = 0;
        long totalConversions = 0;
        BigDecimal totalSpend = BigDecimal.ZERO;
        
        for (CampaignMetric metric : campaign.getMetrics()) {
            totalImpressions += metric.getImpressions();
            totalClicks += metric.getClicks();
            totalConversions += metric.getConversions();
            totalSpend = totalSpend.add(metric.getSpend());
        }
        
        summary.put("impressions", totalImpressions);
        summary.put("clicks", totalClicks);
        summary.put("conversions", totalConversions);
        summary.put("spend", totalSpend);
        
        // Calculated metrics
        if (totalImpressions > 0) {
            double ctr = (double) totalClicks / totalImpressions;
            summary.put("ctr", ctr);
            
            if (totalClicks > 0) {
                double cvr = (double) totalConversions / totalClicks;
                summary.put("cvr", cvr);
            }
            
            if (totalSpend.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal cpm = totalSpend
                        .multiply(BigDecimal.valueOf(1000))
                        .divide(BigDecimal.valueOf(totalImpressions), 2, RoundingMode.HALF_UP);
                summary.put("cpm", cpm);
            }
        }
        
        if (totalClicks > 0 && totalSpend.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal cpc = totalSpend.divide(BigDecimal.valueOf(totalClicks), 2, RoundingMode.HALF_UP);
            summary.put("cpc", cpc);
        }
        
        if (totalConversions > 0 && totalSpend.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal cpa = totalSpend.divide(BigDecimal.valueOf(totalConversions), 2, RoundingMode.HALF_UP);
            summary.put("cpa", cpa);
        }
        
        return summary;
    }
    
    /**
     * Find all campaigns that are eligible to bid on the given request
     */
    public List<AdCampaign> findEligibleCampaigns(BidRequest bidRequest) {
        // In a real system, we would query the database for eligible campaigns
        // For this demonstration, we'll return all active campaigns that match the criteria
        
        if (campaignCache.isEmpty()) {
            // If no campaigns exist yet, create some sample campaigns
            createSampleCampaigns();
        }
        
        return campaignCache.values().stream()
                .filter(campaign -> isEligible(campaign, bidRequest))
                .collect(Collectors.toList());
    }
    
    /**
     * Find a bid response by its ID
     */
    public BidResponse findBidResponseById(String bidId) {
        return bidResponseCache.get(bidId);
    }
    
    /**
     * Save a bid response
     */
    public void saveBidResponse(BidResponse bidResponse) {
        bidResponseCache.put(bidResponse.getResponseId(), bidResponse);
    }
    
    /**
     * Update campaign metrics based on auction results
     */
    public void updateCampaignMetrics(
            BidResponse bidResponse, 
            boolean won, 
            boolean clicked, 
            boolean converted) {
        
        AdCampaign campaign = bidResponse.getCampaign();
        if (campaign == null) {
            return;
        }
        
        // Get today's metric record or create a new one
        LocalDate today = LocalDate.now();
        CampaignMetric todayMetric = campaign.getMetrics().stream()
                .filter(metric -> today.equals(metric.getDate()))
                .findFirst()
                .orElseGet(() -> {
                    CampaignMetric newMetric = CampaignMetric.builder()
                            .date(today)
                            .impressions(0L)
                            .clicks(0L)
                            .conversions(0L)
                            .spend(BigDecimal.ZERO)
                            .dayOfWeek(CampaignMetric.DayOfWeek.valueOf(today.getDayOfWeek().name()))
                            .build();
                    campaign.getMetrics().add(newMetric);
                    return newMetric;
                });
        
        // Update metrics
        if (won) {
            // Increment impressions
            todayMetric.setImpressions(todayMetric.getImpressions() + 1);
            
            // Update spend
            BigDecimal actualPrice = bidResponse.getActualPrice();
            if (actualPrice != null) {
                BigDecimal newSpend = todayMetric.getSpend().add(actualPrice);
                todayMetric.setSpend(newSpend);
                
                // Update campaign remaining budget
                campaign.setRemainingBudget(
                        campaign.getRemainingBudget().subtract(actualPrice)
                );
            }
        }
        
        if (clicked) {
            // Increment clicks
            todayMetric.setClicks(todayMetric.getClicks() + 1);
        }
        
        if (converted) {
            // Increment conversions
            todayMetric.setConversions(todayMetric.getConversions() + 1);
        }
        
        // Recalculate derived metrics
        updateDerivedMetrics(todayMetric);
        
        // Update the campaign cache
        campaignCache.put(campaign.getId(), campaign);
    }
    
    // Helper methods
    
    private boolean isEligible(AdCampaign campaign, BidRequest bidRequest) {
        // Check if campaign is active
        if (campaign.getStatus() != AdCampaign.CampaignStatus.ACTIVE) {
            return false;
        }
        
        // Check if campaign has budget remaining
        if (campaign.getRemainingBudget().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        // Check if campaign is within its scheduled time period
        LocalDateTime now = LocalDateTime.now();
        if (campaign.getStartDate() != null && now.isBefore(campaign.getStartDate())) {
            return false;
        }
        if (campaign.getEndDate() != null && now.isAfter(campaign.getEndDate())) {
            return false;
        }
        
        // Check if ad slot dimensions match our creatives
        boolean dimensionsMatch = campaign.getCreatives().stream()
                .anyMatch(creative -> 
                    creative.getWidth().equals(bidRequest.getAdSlotWidth()) && 
                    creative.getHeight().equals(bidRequest.getAdSlotHeight()));
        
        if (!dimensionsMatch) {
            return false;
        }
        
        // Additional targeting criteria can be checked here
        
        return true;
    }
    
    private void updateDerivedMetrics(CampaignMetric metric) {
        // Calculate CTR (click-through rate)
        if (metric.getImpressions() > 0) {
            double ctr = (double) metric.getClicks() / metric.getImpressions();
            metric.setClickThroughRate(ctr);
        }
        
        // Calculate CVR (conversion rate)
        if (metric.getClicks() > 0) {
            double cvr = (double) metric.getConversions() / metric.getClicks();
            metric.setConversionRate(cvr);
        }
        
        // Calculate CPC (cost per click)
        if (metric.getClicks() > 0) {
            BigDecimal cpc = metric.getSpend().divide(
                    BigDecimal.valueOf(metric.getClicks()), 
                    2, RoundingMode.HALF_UP);
            metric.setCostPerClick(cpc);
        }
        
        // Calculate CPM (cost per mille/thousand impressions)
        if (metric.getImpressions() > 0) {
            BigDecimal cpm = metric.getSpend()
                    .multiply(BigDecimal.valueOf(1000))
                    .divide(BigDecimal.valueOf(metric.getImpressions()), 
                            2, RoundingMode.HALF_UP);
            metric.setCostPerMille(cpm);
        }
        
        // Calculate CPA (cost per acquisition/conversion)
        if (metric.getConversions() > 0) {
            BigDecimal cpa = metric.getSpend().divide(
                    BigDecimal.valueOf(metric.getConversions()), 
                    2, RoundingMode.HALF_UP);
            metric.setCostPerAcquisition(cpa);
        }
    }
    
    private void createSampleCampaigns() {
        // Create some sample campaigns for demonstration
        
        // Campaign 1: CPC campaign for a technology company
        AdCampaign cpcCampaign = AdCampaign.builder()
                .id(1L)
                .name("Tech Gadget Promotion")
                .description("Promoting the latest tech gadgets")
                .advertiserId("tech_company_123")
                .campaignType(AdCampaign.CampaignType.CPC)
                .totalBudget(BigDecimal.valueOf(1000.00))
                .remainingBudget(BigDecimal.valueOf(1000.00))
                .dailyBudget(BigDecimal.valueOf(100.00))
                .bidFloor(BigDecimal.valueOf(0.10))
                .maxBidPrice(BigDecimal.valueOf(2.00))
                .targetCTR(0.02) // 2% target CTR
                .targetConversionRate(0.15) // 15% target conversion rate
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(30))
                .status(AdCampaign.CampaignStatus.ACTIVE)
                .build();
        
        // Add creatives to the campaign
        cpcCampaign.getCreatives().add(createSampleCreative(1L, 300, 250, cpcCampaign));
        cpcCampaign.getCreatives().add(createSampleCreative(2L, 728, 90, cpcCampaign));
        
        // Add target audiences
        cpcCampaign.getTargetAudiences().add("tech_enthusiasts");
        cpcCampaign.getTargetAudiences().add("gadget_buyers");
        
        // Campaign 2: CPA campaign for an e-commerce company
        AdCampaign cpaCampaign = AdCampaign.builder()
                .id(2L)
                .name("Fashion Sale")
                .description("Promoting seasonal fashion sale")
                .advertiserId("fashion_ecommerce_456")
                .campaignType(AdCampaign.CampaignType.CPA)
                .totalBudget(BigDecimal.valueOf(2000.00))
                .remainingBudget(BigDecimal.valueOf(2000.00))
                .dailyBudget(BigDecimal.valueOf(200.00))
                .bidFloor(BigDecimal.valueOf(0.50))
                .maxBidPrice(BigDecimal.valueOf(5.00))
                .targetCTR(0.015) // 1.5% target CTR
                .targetConversionRate(0.05) // 5% target conversion rate
                .startDate(LocalDateTime.now().minusDays(2))
                .endDate(LocalDateTime.now().plusDays(15))
                .status(AdCampaign.CampaignStatus.ACTIVE)
                .build();
        
        // Add creatives to the campaign
        cpaCampaign.getCreatives().add(createSampleCreative(3L, 300, 250, cpaCampaign));
        cpaCampaign.getCreatives().add(createSampleCreative(4L, 300, 600, cpaCampaign));
        
        // Add target audiences
        cpaCampaign.getTargetAudiences().add("fashion_enthusiasts");
        cpaCampaign.getTargetAudiences().add("online_shoppers");
        
        // Store campaigns in the cache
        campaignCache.put(cpcCampaign.getId(), cpcCampaign);
        campaignCache.put(cpaCampaign.getId(), cpaCampaign);
    }
    
    private com.adopt.models.AdCreative createSampleCreative(
            Long id, Integer width, Integer height, AdCampaign campaign) {
        
        return com.adopt.models.AdCreative.builder()
                .id(id)
                .name("Sample Creative " + id)
                .description("A sample ad creative")
                .width(width)
                .height(height)
                .imageUrl("https://example.com/images/ad_" + width + "x" + height + ".jpg")
                .landingPageUrl("https://example.com/landing?creative=" + id)
                .type(com.adopt.models.AdCreative.CreativeType.IMAGE)
                .campaign(campaign)
                .createdAt(LocalDateTime.now().minusDays(5))
                .updatedAt(LocalDateTime.now())
                .active(true)
                .build();
    }
} 