package com.adopt.utils;

import com.adopt.models.AdCampaign;
import com.adopt.models.AdCreative;
import com.adopt.models.BidOptimization;
import com.adopt.models.CampaignMetric;
import com.adopt.models.gametheory.GameTheoryModel;
import com.adopt.repositories.BidOptimizationRepository;
import com.adopt.repositories.CampaignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Utility to initialize sample data in the database
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CampaignRepository campaignRepository;
    private final BidOptimizationRepository bidOptimizationRepository;
    private final Random random = new Random();

    @Override
    public void run(String... args) {
        // Only initialize data if the database is empty
        if (campaignRepository.count() == 0) {
            log.info("Initializing sample data...");
            initializeSampleData();
            log.info("Sample data initialization complete");
        }
    }
    
    private void initializeSampleData() {
        // Create sample campaigns
        AdCampaign techCampaign = createTechCampaign();
        AdCampaign fashionCampaign = createFashionCampaign();
        
        // Create sample optimizations
        createSampleOptimizations(techCampaign);
        createSampleOptimizations(fashionCampaign);
    }
    
    private AdCampaign createTechCampaign() {
        // Create campaign
        AdCampaign campaign = AdCampaign.builder()
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
        
        // Add creatives
        campaign.setCreatives(new HashSet<>());
        campaign.getCreatives().add(createSampleCreative(300, 250, campaign));
        campaign.getCreatives().add(createSampleCreative(728, 90, campaign));
        
        // Add target audiences
        campaign.setTargetAudiences(new HashSet<>());
        campaign.getTargetAudiences().add("tech_enthusiasts");
        campaign.getTargetAudiences().add("gadget_buyers");
        
        // Add metrics
        campaign.setMetrics(generateSampleMetrics(14));
        
        // Save campaign
        return campaignRepository.save(campaign);
    }
    
    private AdCampaign createFashionCampaign() {
        // Create campaign
        AdCampaign campaign = AdCampaign.builder()
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
        
        // Add creatives
        campaign.setCreatives(new HashSet<>());
        campaign.getCreatives().add(createSampleCreative(300, 250, campaign));
        campaign.getCreatives().add(createSampleCreative(300, 600, campaign));
        
        // Add target audiences
        campaign.setTargetAudiences(new HashSet<>());
        campaign.getTargetAudiences().add("fashion_enthusiasts");
        campaign.getTargetAudiences().add("online_shoppers");
        
        // Add metrics
        campaign.setMetrics(generateSampleMetrics(14));
        
        // Save campaign
        return campaignRepository.save(campaign);
    }
    
    private AdCreative createSampleCreative(int width, int height, AdCampaign campaign) {
        return AdCreative.builder()
                .name(String.format("Creative %dx%d", width, height))
                .description(String.format("A %dx%d ad creative", width, height))
                .width(width)
                .height(height)
                .imageUrl(String.format("https://example.com/images/ad_%dx%d.jpg", width, height))
                .landingPageUrl("https://example.com/landing")
                .type(AdCreative.CreativeType.IMAGE)
                .campaign(campaign)
                .createdAt(LocalDateTime.now().minusDays(5))
                .updatedAt(LocalDateTime.now())
                .active(true)
                .build();
    }
    
    private Set<CampaignMetric> generateSampleMetrics(int days) {
        Set<CampaignMetric> metrics = new HashSet<>();
        LocalDate today = LocalDate.now();
        
        for (int i = 0; i < days; i++) {
            LocalDate date = today.minusDays(i);
            CampaignMetric dailyMetric = CampaignMetric.builder()
                    .date(date)
                    .impressions(1000L + random.nextInt(1000))
                    .clicks(20L + random.nextInt(50))
                    .conversions(2L + random.nextInt(8))
                    .spend(BigDecimal.valueOf(10.0 + random.nextDouble() * 50.0).setScale(2, java.math.RoundingMode.HALF_UP))
                    .dayOfWeek(CampaignMetric.DayOfWeek.valueOf(date.getDayOfWeek().name()))
                    .build();
            
            // Calculate derived metrics
            if (dailyMetric.getImpressions() > 0) {
                dailyMetric.setClickThroughRate((double) dailyMetric.getClicks() / dailyMetric.getImpressions());
                
                if (dailyMetric.getClicks() > 0) {
                    dailyMetric.setConversionRate((double) dailyMetric.getConversions() / dailyMetric.getClicks());
                }
            }
            
            metrics.add(dailyMetric);
        }
        
        return metrics;
    }
    
    private void createSampleOptimizations(AdCampaign campaign) {
        // Create a pending optimization
        BidOptimization pendingOpt = BidOptimization.builder()
                .campaign(campaign)
                .previousBid(campaign.getMaxBidPrice())
                .optimizedBid(campaign.getMaxBidPrice().multiply(BigDecimal.valueOf(1.1)).setScale(2, java.math.RoundingMode.HALF_UP))
                .performanceChange(15.0)
                .status(BidOptimization.OptimizationStatus.PENDING)
                .optimizationReason("Competitor bidding increased; raising bid to maintain position")
                .gameTheoryModel(GameTheoryModel.GameTheoryType.NASH_EQUILIBRIUM.name())
                .createdAt(LocalDateTime.now().minusHours(12))
                .build();
        
        bidOptimizationRepository.save(pendingOpt);
        
        // Create an applied optimization from the past
        BidOptimization appliedOpt = BidOptimization.builder()
                .campaign(campaign)
                .previousBid(BigDecimal.valueOf(1.50))
                .optimizedBid(BigDecimal.valueOf(1.65))
                .performanceChange(8.5)
                .status(BidOptimization.OptimizationStatus.APPLIED)
                .optimizationReason("Increased competition during peak hours")
                .gameTheoryModel(GameTheoryModel.GameTheoryType.NASH_EQUILIBRIUM.name())
                .createdAt(LocalDateTime.now().minusDays(3))
                .appliedAt(LocalDateTime.now().minusDays(3).plusHours(2))
                .build();
        
        bidOptimizationRepository.save(appliedOpt);
        
        // Create a rejected optimization
        BidOptimization rejectedOpt = BidOptimization.builder()
                .campaign(campaign)
                .previousBid(BigDecimal.valueOf(1.80))
                .optimizedBid(BigDecimal.valueOf(1.50))
                .performanceChange(7.2)
                .status(BidOptimization.OptimizationStatus.REJECTED)
                .optimizationReason("Opportunity to maintain performance with a lower bid")
                .gameTheoryModel(GameTheoryModel.GameTheoryType.NASH_EQUILIBRIUM.name())
                .createdAt(LocalDateTime.now().minusDays(5))
                .build();
        
        bidOptimizationRepository.save(rejectedOpt);
    }
} 