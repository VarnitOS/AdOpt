package com.adopt.services;

import com.adopt.models.AdCampaign;
import com.adopt.models.BidRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Service for predicting click-through rates, conversion rates, and other metrics
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PredictionService {

    private final Random random = new Random();
    
    // In a real implementation, these would be trained ML models
    private MultiLayerNetwork ctrModel;
    private MultiLayerNetwork cvrModel;
    private MultiLayerNetwork userValueModel;
    
    /**
     * Predict the click-through rate (CTR) for the given request and campaign
     */
    public double predictCtr(BidRequest bidRequest, AdCampaign campaign) {
        // In a real implementation, we would use a trained ML model
        // For demonstration, we'll use a combination of heuristics and randomness
        
        double baseCtr = campaign.getTargetCTR() != null ? campaign.getTargetCTR() : 0.01;
        
        // Adjust CTR based on user profile
        double userMultiplier = getUserProfileCtrMultiplier(bidRequest);
        
        // Adjust CTR based on time of day
        double timeMultiplier = getTimeOfDayCtrMultiplier();
        
        // Adjust CTR based on ad slot positioning
        double positionMultiplier = getAdPositionCtrMultiplier(bidRequest);
        
        // Add some randomness to simulate real-world variation
        double randomFactor = 0.8 + (random.nextDouble() * 0.4); // 0.8 to 1.2
        
        // Calculate final CTR (cap at realistic values)
        double predictedCtr = baseCtr * userMultiplier * timeMultiplier * positionMultiplier * randomFactor;
        return Math.min(0.1, Math.max(0.001, predictedCtr)); // Keep within 0.1% to 10%
    }
    
    /**
     * Predict the conversion rate (CVR) for the given request and campaign
     */
    public double predictCvr(BidRequest bidRequest, AdCampaign campaign) {
        // In a real implementation, we would use a trained ML model
        // For demonstration, we'll use a combination of heuristics and randomness
        
        double baseCvr = campaign.getTargetConversionRate() != null ? campaign.getTargetConversionRate() : 0.1;
        
        // Adjust CVR based on user profile
        double userMultiplier = getUserProfileCvrMultiplier(bidRequest);
        
        // Add some randomness to simulate real-world variation
        double randomFactor = 0.9 + (random.nextDouble() * 0.2); // 0.9 to 1.1
        
        // Calculate final CVR (cap at realistic values)
        double predictedCvr = baseCvr * userMultiplier * randomFactor;
        return Math.min(0.5, Math.max(0.01, predictedCvr)); // Keep within 1% to 50%
    }
    
    /**
     * Estimate the value of a user for the given campaign
     */
    public double estimateUserValue(BidRequest bidRequest, AdCampaign campaign) {
        // In a real implementation, we would use a trained ML model
        // For demonstration, we'll use a simple approach based on user profile and campaign type
        
        // Base value depends on campaign type
        double baseValue;
        switch (campaign.getCampaignType()) {
            case CPC:
                baseValue = 0.5; // Clicks are moderately valuable
                break;
            case CPA:
                baseValue = 1.0; // Conversions are most valuable
                break;
            case CPM:
                baseValue = 0.2; // Impressions are least valuable
                break;
            default:
                baseValue = 0.5; // Default moderate value
        }
        
        // Adjust based on user profile
        double userValueMultiplier = getUserValueMultiplier(bidRequest);
        
        // Add some randomness
        double randomFactor = 0.8 + (random.nextDouble() * 0.4); // 0.8 to 1.2
        
        return baseValue * userValueMultiplier * randomFactor;
    }
    
    /**
     * Predict the probability of winning at different bid levels
     */
    public Map<BigDecimal, Double> predictWinProbabilities(BidRequest bidRequest, AdCampaign campaign) {
        Map<BigDecimal, Double> probabilities = new HashMap<>();
        
        // For simplicity, we'll create a sigmoid curve from floor price to 3x floor price
        BigDecimal floorPrice = bidRequest.getAdSlotFloorPrice();
        BigDecimal maxPrice = floorPrice.multiply(BigDecimal.valueOf(3.0));
        
        // Create 10 price points
        for (int i = 0; i <= 10; i++) {
            double ratio = i / 10.0;
            BigDecimal price = floorPrice.add(
                    maxPrice.subtract(floorPrice).multiply(BigDecimal.valueOf(ratio))
            ).setScale(2, RoundingMode.HALF_UP);
            
            // Sigmoid function for win probability
            double winProbability = 1.0 / (1.0 + Math.exp(-10 * (ratio - 0.5)));
            probabilities.put(price, winProbability);
        }
        
        return probabilities;
    }
    
    // Helper methods
    
    private double getUserProfileCtrMultiplier(BidRequest bidRequest) {
        // In a real implementation, we would use features from the user profile
        // For demonstration, we'll use a simple approach
        
        Map<String, Object> userProfile = bidRequest.getUserProfile();
        if (userProfile == null || userProfile.isEmpty()) {
            return 1.0; // No profile data, use baseline
        }
        
        // Check if user is in a target audience
        boolean isTargetAudience = userProfile.containsKey("isTargetAudience") && 
                (boolean) userProfile.get("isTargetAudience");
        
        // Check recency of user's interest
        int daysSinceLastInterest = userProfile.containsKey("daysSinceLastInterest") ? 
                (int) userProfile.get("daysSinceLastInterest") : 30;
        
        double targetMultiplier = isTargetAudience ? 1.5 : 0.8;
        double recencyMultiplier = Math.max(0.5, Math.min(1.5, Math.exp(-daysSinceLastInterest / 10.0)));
        
        return targetMultiplier * recencyMultiplier;
    }
    
    private double getUserProfileCvrMultiplier(BidRequest bidRequest) {
        // Similar to CTR but with different weights for conversion
        
        Map<String, Object> userProfile = bidRequest.getUserProfile();
        if (userProfile == null || userProfile.isEmpty()) {
            return 1.0; // No profile data, use baseline
        }
        
        // Check if user has converted before
        boolean hasPreviousConversion = userProfile.containsKey("hasPreviousConversion") && 
                (boolean) userProfile.get("hasPreviousConversion");
        
        // Check if user has visited the site recently
        int daysSinceLastVisit = userProfile.containsKey("daysSinceLastVisit") ? 
                (int) userProfile.get("daysSinceLastVisit") : 30;
        
        double conversionMultiplier = hasPreviousConversion ? 2.0 : 0.7;
        double recencyMultiplier = Math.max(0.5, Math.min(1.5, Math.exp(-daysSinceLastVisit / 7.0)));
        
        return conversionMultiplier * recencyMultiplier;
    }
    
    private double getTimeOfDayCtrMultiplier() {
        // Different times of day have different CTRs
        int hour = LocalTime.now().getHour();
        
        if (hour >= 7 && hour < 10) {
            return 1.2; // Morning commute - higher engagement
        } else if (hour >= 10 && hour < 12) {
            return 0.9; // Mid-morning - lower engagement
        } else if (hour >= 12 && hour < 14) {
            return 1.1; // Lunch break - higher engagement
        } else if (hour >= 14 && hour < 17) {
            return 0.8; // Afternoon work - lower engagement
        } else if (hour >= 17 && hour < 20) {
            return 1.3; // Evening commute/leisure - highest engagement
        } else if (hour >= 20 && hour < 23) {
            return 1.2; // Evening leisure - higher engagement
        } else {
            return 0.7; // Late night/early morning - lowest engagement
        }
    }
    
    private double getAdPositionCtrMultiplier(BidRequest bidRequest) {
        // Ad position greatly affects CTR
        // For simplicity, we'll just check if it's above the fold
        
        // Assume all ads are equally visible for this demo
        return 1.0;
    }
    
    private double getUserValueMultiplier(BidRequest bidRequest) {
        Map<String, Object> userProfile = bidRequest.getUserProfile();
        if (userProfile == null || userProfile.isEmpty()) {
            return 1.0; // No profile data, use baseline
        }
        
        // Check purchasing power
        double purchasingPower = userProfile.containsKey("purchasingPower") ? 
                (double) userProfile.get("purchasingPower") : 0.5;
        
        // Check engagement level
        double engagementLevel = userProfile.containsKey("engagementLevel") ? 
                (double) userProfile.get("engagementLevel") : 0.5;
        
        return (0.7 * purchasingPower) + (0.3 * engagementLevel);
    }
} 