package com.adopt.models.gametheory;

import com.adopt.models.AdCampaign;
import com.adopt.models.BidRequest;
import com.adopt.models.BidResponse;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Interface for game theory based bidding models
 */
public interface GameTheoryModel {
    
    /**
     * Calculate the optimal bid price based on game theory principles
     * 
     * @param bidRequest the current bid request
     * @param campaign the campaign we're bidding for
     * @param competitorProfiles profiles of competitors in the market
     * @param parameters additional parameters for the game theory model
     * @return the optimal bid price
     */
    BigDecimal calculateOptimalBid(
            BidRequest bidRequest, 
            AdCampaign campaign, 
            Map<String, CompetitorProfile> competitorProfiles,
            Map<String, Object> parameters);
    
    /**
     * Get the type of game theory model
     */
    GameTheoryType getType();
    
    /**
     * Calculate the utility score for a given bid
     * 
     * @param bidRequest the current bid request
     * @param campaign the campaign we're bidding for
     * @param bidPrice the bid price to evaluate
     * @param parameters additional parameters for utility calculation
     * @return the utility score (higher is better)
     */
    double calculateUtility(
            BidRequest bidRequest, 
            AdCampaign campaign, 
            BigDecimal bidPrice,
            Map<String, Object> parameters);
    
    /**
     * Update the model based on auction results
     * 
     * @param bidResponse the bid response with auction results
     * @param competitorProfiles profiles of competitors in the market
     */
    void updateModel(BidResponse bidResponse, Map<String, CompetitorProfile> competitorProfiles);
    
    enum GameTheoryType {
        NASH_EQUILIBRIUM,
        STACKELBERG,
        BAYESIAN,
        REINFORCEMENT_LEARNING,
        MULTI_AGENT_LEARNING
    }
} 