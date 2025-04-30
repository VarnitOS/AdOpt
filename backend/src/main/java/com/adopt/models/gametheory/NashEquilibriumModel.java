package com.adopt.models.gametheory;

import com.adopt.models.AdCampaign;
import com.adopt.models.BidRequest;
import com.adopt.models.BidResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of a bidding strategy based on Nash Equilibrium game theory
 */
@Slf4j
@Component
public class NashEquilibriumModel implements GameTheoryModel {

    private static final int BID_LEVELS = 10; // Number of discrete bid levels to consider
    private static final double LEARNING_RATE = 0.1; // Rate at which model updates based on new observations
    
    private Map<String, double[][]> payoffMatrices = new HashMap<>();
    
    @Override
    public BigDecimal calculateOptimalBid(
            BidRequest bidRequest, 
            AdCampaign campaign, 
            Map<String, CompetitorProfile> competitorProfiles, 
            Map<String, Object> parameters) {
        
        log.debug("Calculating optimal bid for request {} and campaign {}", 
                bidRequest.getRequestId(), campaign.getId());
        
        // Extract user value estimate from parameters
        Double userValueEstimate = parameters.containsKey("userValueEstimate") 
                ? (Double) parameters.get("userValueEstimate") 
                : 0.0;
        
        // Create or get payoff matrix for this campaign and competitor set
        String matrixKey = generateMatrixKey(campaign, competitorProfiles);
        double[][] payoffMatrix = payoffMatrices.computeIfAbsent(matrixKey, k -> initializePayoffMatrix());
        
        // Calculate Nash Equilibrium
        double[] strategyDistribution = calculateNashEquilibrium(payoffMatrix);
        
        // Convert strategy distribution to a specific bid
        BigDecimal baselineOptimalBid = determineOptimalBidFromStrategy(strategyDistribution, campaign);
        
        // Adjust bid based on user value
        BigDecimal adjustedBid = adjustBidForUserValue(baselineOptimalBid, userValueEstimate);
        
        // Ensure bid stays within campaign constraints
        return enforceBidConstraints(adjustedBid, campaign, bidRequest);
    }

    @Override
    public GameTheoryType getType() {
        return GameTheoryType.NASH_EQUILIBRIUM;
    }

    @Override
    public double calculateUtility(
            BidRequest bidRequest, 
            AdCampaign campaign, 
            BigDecimal bidPrice, 
            Map<String, Object> parameters) {
        
        // Get expected CTR and CVR
        Double expectedCtr = parameters.containsKey("predictedCtr") 
                ? (Double) parameters.get("predictedCtr") 
                : 0.01; // Default 1% CTR
                
        Double expectedCvr = parameters.containsKey("predictedCvr") 
                ? (Double) parameters.get("predictedCvr") 
                : 0.1; // Default 10% conversion rate
        
        // Get campaign objectives
        int campaignObjectiveWeight = getCampaignObjectiveWeight(campaign);
        
        // Calculate expected value (revenue)
        double expectedClicks = expectedCtr;
        double expectedConversions = expectedCtr * expectedCvr;
        double expectedValue = expectedClicks + (campaignObjectiveWeight * expectedConversions);
        
        // Calculate cost (as a positive number)
        double cost = bidPrice.doubleValue();
        
        // Calculate utility as profit (value - cost)
        return expectedValue - cost;
    }

    @Override
    public void updateModel(BidResponse bidResponse, Map<String, CompetitorProfile> competitorProfiles) {
        if (bidResponse == null || bidResponse.getCampaign() == null) {
            return;
        }
        
        // Get the payoff matrix key for this campaign and competitor set
        String matrixKey = generateMatrixKey(bidResponse.getCampaign(), competitorProfiles);
        double[][] payoffMatrix = payoffMatrices.get(matrixKey);
        
        if (payoffMatrix == null) {
            return; // No matrix to update
        }
        
        // Update the payoff matrix based on the auction result
        updatePayoffMatrix(payoffMatrix, bidResponse);
    }
    
    // Helper methods
    
    private String generateMatrixKey(AdCampaign campaign, Map<String, CompetitorProfile> competitorProfiles) {
        StringBuilder keyBuilder = new StringBuilder("campaign_" + campaign.getId());
        
        if (competitorProfiles != null) {
            for (String competitorId : competitorProfiles.keySet()) {
                keyBuilder.append("_comp_").append(competitorId);
            }
        }
        
        return keyBuilder.toString();
    }
    
    private double[][] initializePayoffMatrix() {
        // For simplicity, initialize with a reasonable set of expected values
        double[][] matrix = new double[BID_LEVELS][BID_LEVELS];
        
        for (int i = 0; i < BID_LEVELS; i++) {
            for (int j = 0; j < BID_LEVELS; j++) {
                // Higher bids have higher chance of winning but lower profit margin
                double ourBidLevel = (i + 1.0) / BID_LEVELS;
                double competitorBidLevel = (j + 1.0) / BID_LEVELS;
                
                // Our expected payoff depends on whether we win or lose
                if (ourBidLevel > competitorBidLevel) {
                    // We win - get value but pay bid
                    matrix[i][j] = 1.0 - ourBidLevel;
                } else {
                    // We lose - no value, no cost
                    matrix[i][j] = 0.0;
                }
            }
        }
        
        return matrix;
    }
    
    private double[] calculateNashEquilibrium(double[][] payoffMatrix) {
        // Simplified Nash equilibrium calculation for 2-player zero-sum game
        // In a real implementation, we would use a more sophisticated algorithm
        
        try {
            int n = payoffMatrix.length;
            
            // Create coefficient matrix for linear system (A)
            RealMatrix coefficients = new Array2DRowRealMatrix(n + 1, n + 1);
            
            // Fill the first n rows with the payoff matrix transpose
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    coefficients.setEntry(i, j, payoffMatrix[j][i]);
                }
                // Last column is 1 (for the sum = 1 constraint)
                coefficients.setEntry(i, n, 1.0);
            }
            
            // Last row is all 1's (for sum = 1 constraint)
            for (int j = 0; j < n; j++) {
                coefficients.setEntry(n, j, 1.0);
            }
            coefficients.setEntry(n, n, 0.0);
            
            // Create the right-hand side vector (b)
            RealVector rhs = new ArrayRealVector(n + 1);
            // First n elements are 0 (payoff = 0 at equilibrium)
            for (int i = 0; i < n; i++) {
                rhs.setEntry(i, 0.0);
            }
            // Last element is 1 (probabilities sum to 1)
            rhs.setEntry(n, 1.0);
            
            // Solve the system Ax = b
            DecompositionSolver solver = new LUDecomposition(coefficients).getSolver();
            RealVector solution = solver.solve(rhs);
            
            // Extract the strategy distribution (first n elements)
            double[] strategy = new double[n];
            for (int i = 0; i < n; i++) {
                // Ensure probabilities are non-negative
                strategy[i] = Math.max(0.0, solution.getEntry(i));
            }
            
            // Normalize to ensure probabilities sum to 1
            double sum = 0.0;
            for (double p : strategy) {
                sum += p;
            }
            
            if (sum > 0) {
                for (int i = 0; i < n; i++) {
                    strategy[i] /= sum;
                }
            } else {
                // Fallback to uniform distribution if solver fails
                for (int i = 0; i < n; i++) {
                    strategy[i] = 1.0 / n;
                }
            }
            
            return strategy;
            
        } catch (Exception e) {
            log.error("Error calculating Nash equilibrium: {}", e.getMessage());
            
            // Fallback to a reasonable strategy - gradually increasing probability
            double[] fallbackStrategy = new double[BID_LEVELS];
            double sum = 0.0;
            for (int i = 0; i < BID_LEVELS; i++) {
                fallbackStrategy[i] = i + 1;
                sum += fallbackStrategy[i];
            }
            for (int i = 0; i < BID_LEVELS; i++) {
                fallbackStrategy[i] /= sum;
            }
            return fallbackStrategy;
        }
    }
    
    private BigDecimal determineOptimalBidFromStrategy(double[] strategyDistribution, AdCampaign campaign) {
        // Convert strategy distribution to a specific bid
        // We could either:
        // 1. Pick the highest probability strategy
        // 2. Take the expected value (weighted average)
        // We'll use approach 2 for smoother bidding
        
        double expectedBidLevel = 0.0;
        for (int i = 0; i < strategyDistribution.length; i++) {
            double bidLevel = (i + 1.0) / BID_LEVELS;
            expectedBidLevel += bidLevel * strategyDistribution[i];
        }
        
        // Scale to campaign's max bid price
        double scaledBid = expectedBidLevel * campaign.getMaxBidPrice().doubleValue();
        return BigDecimal.valueOf(scaledBid).setScale(2, RoundingMode.HALF_UP);
    }
    
    private BigDecimal adjustBidForUserValue(BigDecimal baselineBid, Double userValueEstimate) {
        // Adjust bid based on user value estimate (higher value = higher bid)
        double adjustmentFactor = 1.0 + Math.max(0.0, userValueEstimate);
        double adjustedBid = baselineBid.doubleValue() * adjustmentFactor;
        return BigDecimal.valueOf(adjustedBid).setScale(2, RoundingMode.HALF_UP);
    }
    
    private BigDecimal enforceBidConstraints(BigDecimal bid, AdCampaign campaign, BidRequest bidRequest) {
        // Ensure bid is at least the floor price
        BigDecimal floorPrice = bidRequest.getAdSlotFloorPrice();
        if (floorPrice != null && bid.compareTo(floorPrice) < 0) {
            bid = floorPrice;
        }
        
        // Ensure bid doesn't exceed campaign max
        if (campaign.getMaxBidPrice() != null && bid.compareTo(campaign.getMaxBidPrice()) > 0) {
            bid = campaign.getMaxBidPrice();
        }
        
        // Ensure bid doesn't go below campaign minimum
        if (campaign.getBidFloor() != null && bid.compareTo(campaign.getBidFloor()) < 0) {
            bid = campaign.getBidFloor();
        }
        
        return bid;
    }
    
    private int getCampaignObjectiveWeight(AdCampaign campaign) {
        // Get campaign objective weights based on campaign type
        switch (campaign.getCampaignType()) {
            case CPC:
                return 1; // Focus on clicks
            case CPA:
                return 20; // Focus on conversions
            case HYBRID:
                return 10; // Balance clicks and conversions
            default:
                return 5; // Default balanced approach
        }
    }
    
    private void updatePayoffMatrix(double[][] payoffMatrix, BidResponse bidResponse) {
        // Determine which bid level this response corresponds to
        double bidLevel = bidResponse.getBidPrice().doubleValue();
        double maxBid = bidResponse.getCampaign().getMaxBidPrice().doubleValue();
        
        int bidLevelIndex = (int) Math.floor((bidLevel / maxBid) * BID_LEVELS);
        bidLevelIndex = Math.min(Math.max(bidLevelIndex, 0), BID_LEVELS - 1);
        
        // Determine competitor bid level (estimated)
        int competitorLevelIndex;
        if (Boolean.TRUE.equals(bidResponse.getIsWon())) {
            // We won, so competitor bid less than us
            competitorLevelIndex = Math.max(0, bidLevelIndex - 1);
        } else {
            // We lost, so competitor bid more than us
            competitorLevelIndex = Math.min(BID_LEVELS - 1, bidLevelIndex + 1);
        }
        
        // Calculate utility
        double utility = 0.0;
        if (Boolean.TRUE.equals(bidResponse.getIsWon())) {
            // We won - utility is value minus cost
            double clickValue = 1.0;
            double conversionValue = getCampaignObjectiveWeight(bidResponse.getCampaign());
            
            double value = 0.0;
            if (Boolean.TRUE.equals(bidResponse.getIsClicked())) {
                value += clickValue;
                if (Boolean.TRUE.equals(bidResponse.getIsConverted())) {
                    value += conversionValue;
                }
            }
            
            double cost = bidResponse.getActualPrice().doubleValue();
            utility = value - cost;
        }
        
        // Update matrix using learning rate
        payoffMatrix[bidLevelIndex][competitorLevelIndex] = 
                (1 - LEARNING_RATE) * payoffMatrix[bidLevelIndex][competitorLevelIndex] + 
                LEARNING_RATE * utility;
    }
} 