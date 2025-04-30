package com.adopt.services;

import com.adopt.models.AdCampaign;
import com.adopt.models.BidOptimization;
import com.adopt.models.gametheory.GameTheoryModel;
import com.adopt.repositories.BidOptimizationRepository;
import com.adopt.repositories.CampaignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * Service for scheduling automatic bid optimizations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BidOptimizationScheduler {

    private final CampaignRepository campaignRepository;
    private final BidOptimizationRepository bidOptimizationRepository;
    private final BidOptimizationService bidOptimizationService;
    private final Random random = new Random();

    /**
     * Generate bid optimizations every hour
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour
    public void generateBidOptimizations() {
        log.info("Generating bid optimizations...");
        
        // Find active campaigns
        List<AdCampaign> activeCampaigns = campaignRepository.findRunningCampaigns(LocalDateTime.now());
        
        for (AdCampaign campaign : activeCampaigns) {
            try {
                // For real production systems, we would use more sophisticated logic
                // to determine whether a campaign needs optimization
                if (shouldOptimizeCampaign(campaign)) {
                    generateOptimization(campaign);
                }
            } catch (Exception e) {
                log.error("Error generating optimization for campaign {}: {}", campaign.getId(), e.getMessage(), e);
            }
        }
    }
    
    /**
     * Apply pending optimizations that meet certain criteria
     */
    @Scheduled(cron = "0 0 3 * * *") // 3 AM daily
    public void autoApplyOptimizations() {
        log.info("Auto-applying optimizations...");
        
        // Find pending optimizations
        List<BidOptimization> pendingOptimizations = 
                bidOptimizationRepository.findByStatus(BidOptimization.OptimizationStatus.PENDING);
        
        for (BidOptimization optimization : pendingOptimizations) {
            try {
                // Apply optimizations with small changes automatically
                if (isSmallChange(optimization) && hasHighConfidence(optimization)) {
                    applyOptimization(optimization);
                }
            } catch (Exception e) {
                log.error("Error applying optimization {}: {}", optimization.getId(), e.getMessage(), e);
            }
        }
    }
    
    /**
     * Determine if a campaign should be optimized
     */
    private boolean shouldOptimizeCampaign(AdCampaign campaign) {
        // Check if the campaign has enough data to optimize
        return campaign.getMetrics() != null && !campaign.getMetrics().isEmpty();
    }
    
    /**
     * Generate an optimization for a campaign
     */
    private void generateOptimization(AdCampaign campaign) {
        // Get the current bid
        BigDecimal currentBid = campaign.getMaxBidPrice();
        
        // In a real system, we would use game theory to calculate the optimal bid
        // For this demo, we'll just simulate it
        BigDecimal optimizedBid = simulateOptimalBid(campaign);
        
        // Calculate the expected performance improvement
        double performanceChange = calculatePerformanceChange(currentBid, optimizedBid);
        
        // Create the optimization recommendation
        BidOptimization optimization = BidOptimization.builder()
                .campaign(campaign)
                .previousBid(currentBid)
                .optimizedBid(optimizedBid)
                .performanceChange(performanceChange)
                .status(BidOptimization.OptimizationStatus.PENDING)
                .optimizationReason(getOptimizationReason(currentBid, optimizedBid))
                .gameTheoryModel(GameTheoryModel.GameTheoryType.NASH_EQUILIBRIUM.name())
                .build();
        
        bidOptimizationRepository.save(optimization);
        log.info("Generated optimization {} for campaign {}", optimization.getId(), campaign.getId());
    }
    
    /**
     * Simulate the optimal bid for a campaign
     */
    private BigDecimal simulateOptimalBid(AdCampaign campaign) {
        // In a real system, this would come from the game theory model
        BigDecimal currentBid = campaign.getMaxBidPrice();
        double changeFactor = 0.8 + (random.nextDouble() * 0.4); // 0.8 to 1.2
        
        return currentBid.multiply(BigDecimal.valueOf(changeFactor))
                .setScale(2, java.math.RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate the expected performance change
     */
    private double calculatePerformanceChange(BigDecimal oldBid, BigDecimal newBid) {
        // Simple model: if we're increasing the bid, expect better performance
        // In reality, this would be much more sophisticated
        if (newBid.compareTo(oldBid) > 0) {
            return (newBid.doubleValue() / oldBid.doubleValue() - 1) * 50; // Up to 10% improvement
        } else {
            // If we're decreasing the bid, we might lose some performance but save money
            return (oldBid.doubleValue() / newBid.doubleValue() - 1) * 25; // Smaller improvement
        }
    }
    
    /**
     * Get a reason for the optimization
     */
    private String getOptimizationReason(BigDecimal oldBid, BigDecimal newBid) {
        if (newBid.compareTo(oldBid) > 0) {
            return "Competitor bidding increased; raising bid to maintain position";
        } else {
            return "Opportunity to maintain performance with a lower bid";
        }
    }
    
    /**
     * Determine if the optimization represents a small change
     */
    private boolean isSmallChange(BidOptimization optimization) {
        BigDecimal oldBid = optimization.getPreviousBid();
        BigDecimal newBid = optimization.getOptimizedBid();
        
        BigDecimal percentChange = newBid.subtract(oldBid)
                .abs()
                .divide(oldBid, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        
        return percentChange.compareTo(BigDecimal.valueOf(5)) <= 0; // Less than 5% change
    }
    
    /**
     * Determine if the optimization has high confidence
     */
    private boolean hasHighConfidence(BidOptimization optimization) {
        // In a real system, this would be based on data quality, model confidence, etc.
        return true;
    }
    
    /**
     * Apply an optimization
     */
    private void applyOptimization(BidOptimization optimization) {
        // Update the campaign's bid
        AdCampaign campaign = optimization.getCampaign();
        campaign.setMaxBidPrice(optimization.getOptimizedBid());
        campaignRepository.save(campaign);
        
        // Update the optimization status
        optimization.setStatus(BidOptimization.OptimizationStatus.APPLIED);
        optimization.setAppliedAt(LocalDateTime.now());
        bidOptimizationRepository.save(optimization);
        
        log.info("Applied optimization {} to campaign {}", optimization.getId(), campaign.getId());
    }
} 