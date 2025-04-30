package com.adopt.services;

import com.adopt.models.AdCampaign;
import com.adopt.models.BidRequest;
import com.adopt.models.BidResponse;
import com.adopt.models.gametheory.CompetitorProfile;
import com.adopt.models.gametheory.GameTheoryModel;
import com.adopt.models.gametheory.NashEquilibriumModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for optimizing bid prices using game theory principles
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BidOptimizationService {

    private final Map<GameTheoryModel.GameTheoryType, GameTheoryModel> gameTheoryModels;
    private final UserProfileService userProfileService;
    private final CompetitorAnalysisService competitorAnalysisService;
    private final PredictionService predictionService;

    /**
     * Generate an optimal bid response for the given bid request and campaign
     * 
     * @param bidRequest the current bid request
     * @param campaign the campaign to bid for
     * @return the generated bid response
     */
    public BidResponse generateBidResponse(BidRequest bidRequest, AdCampaign campaign) {
        log.debug("Generating bid response for request {} and campaign {}", 
                bidRequest.getRequestId(), campaign.getId());
        
        // Check if we should bid (budget, targeting, etc.)
        if (!shouldBid(bidRequest, campaign)) {
            return createNoBidResponse(bidRequest, campaign);
        }
        
        // Enrich bid request with user profile data
        enrichBidRequestWithUserProfile(bidRequest);
        
        // Get competitor profiles for this auction
        Map<String, CompetitorProfile> competitorProfiles = 
                competitorAnalysisService.getCompetitorProfiles(bidRequest, campaign);
        
        // Get predictions for this auction
        Map<String, Object> predictionParams = getPredictions(bidRequest, campaign);
        
        // Select game theory model based on campaign and request characteristics
        GameTheoryModel model = selectGameTheoryModel(bidRequest, campaign);
        
        // Calculate the optimal bid price
        BigDecimal optimalBidPrice = model.calculateOptimalBid(
                bidRequest, campaign, competitorProfiles, predictionParams);
        
        // Calculate utility score for the bid
        double utilityScore = model.calculateUtility(
                bidRequest, campaign, optimalBidPrice, predictionParams);
        
        // Create and return bid response
        return createBidResponse(bidRequest, campaign, optimalBidPrice, utilityScore, 
                model.getType(), predictionParams);
    }
    
    /**
     * Process auction results and update models
     */
    public void processAuctionResult(BidResponse bidResponse, boolean won, boolean clicked, boolean converted) {
        // Update bid response with results
        bidResponse.setIsWon(won);
        bidResponse.setIsClicked(clicked);
        bidResponse.setIsConverted(converted);
        bidResponse.setProcessedAt(LocalDateTime.now());
        
        // Update bid status
        bidResponse.setStatus(won ? BidResponse.BidStatus.WON : BidResponse.BidStatus.LOST);
        
        // Get the game theory model that was used
        GameTheoryModel.GameTheoryType modelType = 
                GameTheoryModel.GameTheoryType.valueOf(bidResponse.getGameTheoryModelType());
        GameTheoryModel model = gameTheoryModels.get(modelType);
        
        if (model != null) {
            // Get competitor profiles
            Map<String, CompetitorProfile> competitorProfiles = 
                    competitorAnalysisService.getCompetitorProfiles(bidResponse.getBidRequest(), bidResponse.getCampaign());
            
            // Update the model with the results
            model.updateModel(bidResponse, competitorProfiles);
            
            // Update competitor profiles with observed behavior
            if (won) {
                // We won, so we know our bid was higher than competitors
                competitorAnalysisService.updateCompetitorProfiles(
                        bidResponse.getBidRequest(), bidResponse.getActualPrice(), competitorProfiles);
            }
        }
    }
    
    // Private helper methods
    
    private boolean shouldBid(BidRequest bidRequest, AdCampaign campaign) {
        // Check if campaign is active
        if (campaign.getStatus() != AdCampaign.CampaignStatus.ACTIVE) {
            log.debug("Campaign {} is not active", campaign.getId());
            return false;
        }
        
        // Check if campaign has budget remaining
        if (campaign.getRemainingBudget().compareTo(BigDecimal.ZERO) <= 0) {
            log.debug("Campaign {} has no remaining budget", campaign.getId());
            return false;
        }
        
        // Check if ad slot dimensions match our creatives
        boolean dimensionsMatch = campaign.getCreatives().stream()
                .anyMatch(creative -> 
                    creative.getWidth().equals(bidRequest.getAdSlotWidth()) && 
                    creative.getHeight().equals(bidRequest.getAdSlotHeight()));
        
        if (!dimensionsMatch) {
            log.debug("No matching creative dimensions for campaign {}", campaign.getId());
            return false;
        }
        
        // Additional targeting checks can be added here
        
        return true;
    }
    
    private void enrichBidRequestWithUserProfile(BidRequest bidRequest) {
        // Get user profile data based on cookie ID or device ID
        Map<String, Object> userProfile = userProfileService.getUserProfile(
                bidRequest.getCookieId(), bidRequest.getDeviceId());
        
        // Attach to bid request
        bidRequest.setUserProfile(userProfile);
    }
    
    private Map<String, Object> getPredictions(BidRequest bidRequest, AdCampaign campaign) {
        Map<String, Object> predictions = new HashMap<>();
        
        // Predict CTR (click-through rate)
        double predictedCtr = predictionService.predictCtr(bidRequest, campaign);
        predictions.put("predictedCtr", predictedCtr);
        
        // Predict CVR (conversion rate)
        double predictedCvr = predictionService.predictCvr(bidRequest, campaign);
        predictions.put("predictedCvr", predictedCvr);
        
        // Estimate user value
        double userValueEstimate = predictionService.estimateUserValue(bidRequest, campaign);
        predictions.put("userValueEstimate", userValueEstimate);
        
        // Predict win probability at different bid levels
        Map<BigDecimal, Double> winProbabilities = predictionService.predictWinProbabilities(bidRequest, campaign);
        predictions.put("winProbabilities", winProbabilities);
        
        return predictions;
    }
    
    private GameTheoryModel selectGameTheoryModel(BidRequest bidRequest, AdCampaign campaign) {
        // For now, always use Nash Equilibrium model as default
        // In a more sophisticated system, we could select different models based on
        // campaign characteristics, auction type, or other factors
        return gameTheoryModels.get(GameTheoryModel.GameTheoryType.NASH_EQUILIBRIUM);
    }
    
    private BidResponse createBidResponse(
            BidRequest bidRequest, 
            AdCampaign campaign, 
            BigDecimal bidPrice, 
            double utilityScore,
            GameTheoryModel.GameTheoryType modelType,
            Map<String, Object> predictionParams) {
        
        return BidResponse.builder()
                .bidRequest(bidRequest)
                .responseId(UUID.randomUUID().toString())
                .campaign(campaign)
                .creative(selectCreative(campaign, bidRequest))
                .bidPrice(bidPrice)
                .status(BidResponse.BidStatus.PENDING)
                .predictedCtr((Double) predictionParams.get("predictedCtr"))
                .predictedCvr((Double) predictionParams.get("predictedCvr"))
                .gameTheoryModelType(modelType.name())
                .utilityScore(utilityScore)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    private BidResponse createNoBidResponse(BidRequest bidRequest, AdCampaign campaign) {
        return BidResponse.builder()
                .bidRequest(bidRequest)
                .responseId(UUID.randomUUID().toString())
                .campaign(campaign)
                .bidPrice(BigDecimal.ZERO)
                .status(BidResponse.BidStatus.PENDING) // Will not be sent
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    private com.adopt.models.AdCreative selectCreative(AdCampaign campaign, BidRequest bidRequest) {
        // For simplicity, just select the first creative that matches dimensions
        return campaign.getCreatives().stream()
                .filter(creative -> 
                    creative.getWidth().equals(bidRequest.getAdSlotWidth()) && 
                    creative.getHeight().equals(bidRequest.getAdSlotHeight()) &&
                    creative.isActive())
                .findFirst()
                .orElse(null);
    }
}
