package com.adopt.services;

import com.adopt.models.AdCampaign;
import com.adopt.models.BidRequest;
import com.adopt.models.gametheory.CompetitorProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for analyzing and tracking competitor behavior in RTB auctions
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CompetitorAnalysisService {

    // In-memory cache of competitor profiles
    private final Map<String, CompetitorProfile> competitorProfileCache = new ConcurrentHashMap<>();
    
    /**
     * Get competitor profiles relevant to the current bid request and campaign
     * 
     * @param bidRequest the current bid request
     * @param campaign the campaign we're bidding for
     * @return map of competitor profiles identified by ID
     */
    public Map<String, CompetitorProfile> getCompetitorProfiles(BidRequest bidRequest, AdCampaign campaign) {
        Map<String, CompetitorProfile> relevantProfiles = new HashMap<>();
        
        // In a real implementation, we would query a database for known competitors
        // who typically bid on similar ad slots, audiences, etc.
        
        // For demonstration, we'll create some synthetic competitor profiles
        // representing different bidding behaviors
        
        // Aggressive competitor who bids high
        String aggressiveId = getCompetitorKey("aggressive", bidRequest.getAdSlotId());
        CompetitorProfile aggressiveCompetitor = competitorProfileCache.computeIfAbsent(
                aggressiveId, k -> createAggressiveCompetitor(bidRequest));
        relevantProfiles.put("aggressive", aggressiveCompetitor);
        
        // Conservative competitor who bids low but consistently
        String conservativeId = getCompetitorKey("conservative", bidRequest.getAdSlotId());
        CompetitorProfile conservativeCompetitor = competitorProfileCache.computeIfAbsent(
                conservativeId, k -> createConservativeCompetitor(bidRequest));
        relevantProfiles.put("conservative", conservativeCompetitor);
        
        // Time-sensitive competitor who bids differently at different times of day
        String timeSensitiveId = getCompetitorKey("timeSensitive", bidRequest.getAdSlotId());
        CompetitorProfile timeSensitiveCompetitor = competitorProfileCache.computeIfAbsent(
                timeSensitiveId, k -> createTimeSensitiveCompetitor(bidRequest));
        updateTimeSensitiveCompetitor(timeSensitiveCompetitor);
        relevantProfiles.put("timeSensitive", timeSensitiveCompetitor);
        
        return relevantProfiles;
    }
    
    /**
     * Update competitor profiles based on observed auction results
     * 
     * @param bidRequest the bid request
     * @param actualPrice the price we paid (second-price auctions)
     * @param competitorProfiles the competitor profiles to update
     */
    public void updateCompetitorProfiles(
            BidRequest bidRequest, 
            BigDecimal actualPrice, 
            Map<String, CompetitorProfile> competitorProfiles) {
        
        // In a real implementation, we would update our records of competitor behavior
        // based on observed auction outcomes. For instance, in a second-price auction,
        // the price we paid is the highest competitor bid + minimum increment.
        
        for (CompetitorProfile profile : competitorProfiles.values()) {
            // For demonstration, we'll just add this observation to the profile
            // In reality, we'd do more sophisticated updating of our behavioral models
            BigDecimal estimatedBid = calculateEstimatedBid(profile, actualPrice);
            profile.addBidObservation(estimatedBid, bidRequest.getAdSlotId(), false);
        }
    }
    
    // Helper methods
    
    private String getCompetitorKey(String competitorType, String adSlotId) {
        return competitorType + "_" + adSlotId;
    }
    
    private CompetitorProfile createAggressiveCompetitor(BidRequest bidRequest) {
        BigDecimal estimatedMax = bidRequest.getAdSlotFloorPrice().multiply(BigDecimal.valueOf(2.5));
        
        return CompetitorProfile.builder()
                .competitorId("aggressive_" + bidRequest.getAdSlotId())
                .competitorName("Aggressive Bidder")
                .averageBidPrice(bidRequest.getAdSlotFloorPrice().multiply(BigDecimal.valueOf(2.0)))
                .minBidPrice(bidRequest.getAdSlotFloorPrice())
                .maxBidPrice(estimatedMax)
                .competitorStrategy("aggressive")
                .bidCount(10) // Start with some synthetic observations
                .lastUpdated(LocalDateTime.now())
                .build();
    }
    
    private CompetitorProfile createConservativeCompetitor(BidRequest bidRequest) {
        BigDecimal estimatedMax = bidRequest.getAdSlotFloorPrice().multiply(BigDecimal.valueOf(1.3));
        
        return CompetitorProfile.builder()
                .competitorId("conservative_" + bidRequest.getAdSlotId())
                .competitorName("Conservative Bidder")
                .averageBidPrice(bidRequest.getAdSlotFloorPrice().multiply(BigDecimal.valueOf(1.1)))
                .minBidPrice(bidRequest.getAdSlotFloorPrice())
                .maxBidPrice(estimatedMax)
                .competitorStrategy("conservative")
                .bidCount(10) // Start with some synthetic observations
                .lastUpdated(LocalDateTime.now())
                .build();
    }
    
    private CompetitorProfile createTimeSensitiveCompetitor(BidRequest bidRequest) {
        BigDecimal estimatedMax = bidRequest.getAdSlotFloorPrice().multiply(BigDecimal.valueOf(2.0));
        
        return CompetitorProfile.builder()
                .competitorId("timeSensitive_" + bidRequest.getAdSlotId())
                .competitorName("Time Sensitive Bidder")
                .averageBidPrice(bidRequest.getAdSlotFloorPrice().multiply(BigDecimal.valueOf(1.5)))
                .minBidPrice(bidRequest.getAdSlotFloorPrice())
                .maxBidPrice(estimatedMax)
                .competitorStrategy("time_sensitive")
                .notes("Active during hours: 9-12,13-17,18-22") // Active during these hours
                .bidCount(10) // Start with some synthetic observations
                .lastUpdated(LocalDateTime.now())
                .build();
    }
    
    private void updateTimeSensitiveCompetitor(CompetitorProfile competitor) {
        // Adjust the bid prices based on time of day
        // Morning: moderate bids
        // Afternoon: highest bids
        // Evening: lower bids
        
        int hour = LocalTime.now().getHour();
        BigDecimal baseAverage = competitor.getAverageBidPrice();
        
        if (hour >= 9 && hour < 12) {
            // Morning - moderate bids
            competitor.setAverageBidPrice(baseAverage);
        } else if (hour >= 13 && hour < 17) {
            // Afternoon - highest bids
            competitor.setAverageBidPrice(baseAverage.multiply(BigDecimal.valueOf(1.2)));
        } else if (hour >= 18 && hour < 22) {
            // Evening - lower bids
            competitor.setAverageBidPrice(baseAverage.multiply(BigDecimal.valueOf(0.9)));
        } else {
            // Night - lowest bids
            competitor.setAverageBidPrice(baseAverage.multiply(BigDecimal.valueOf(0.7)));
        }
    }
    
    private BigDecimal calculateEstimatedBid(CompetitorProfile profile, BigDecimal actualPrice) {
        // In a second-price auction, the actual price is close to the second-highest bid
        // So we can estimate that the highest competitor bid was slightly below our bid
        
        // For demonstration, we'll assume our bid was 10% higher than the highest competitor
        return actualPrice.multiply(BigDecimal.valueOf(0.9));
    }
} 