package com.adopt.controllers;

import com.adopt.models.AdCampaign;
import com.adopt.models.BidRequest;
import com.adopt.models.BidResponse;
import com.adopt.services.BidOptimizationService;
import com.adopt.services.CampaignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for RTB bidding operations
 */
@Slf4j
@RestController
@RequestMapping("/api/bid")
@RequiredArgsConstructor
public class BidController {

    private final BidOptimizationService bidOptimizationService;
    private final CampaignService campaignService;

    /**
     * Generate an optimal bid for a given bid request
     */
    @PostMapping
    public ResponseEntity<BidResponse> generateBid(@Valid @RequestBody BidRequest bidRequest) {
        log.debug("Received bid request: {}", bidRequest.getRequestId());
        
        // Find eligible campaigns for this bid request
        List<AdCampaign> eligibleCampaigns = campaignService.findEligibleCampaigns(bidRequest);
        
        if (eligibleCampaigns.isEmpty()) {
            log.debug("No eligible campaigns found for bid request: {}", bidRequest.getRequestId());
            return ResponseEntity.noContent().build();
        }
        
        // For simplicity, just use the first eligible campaign
        // In a real system, we would select the best campaign or bid for multiple
        AdCampaign campaign = eligibleCampaigns.get(0);
        
        // Generate the bid response
        BidResponse bidResponse = bidOptimizationService.generateBidResponse(bidRequest, campaign);
        
        return ResponseEntity.ok(bidResponse);
    }
    
    /**
     * Process auction results (win notification)
     */
    @PostMapping("/{bidId}/win")
    public ResponseEntity<Void> processWin(
            @PathVariable String bidId,
            @RequestParam(required = false, defaultValue = "false") boolean clicked,
            @RequestParam(required = false, defaultValue = "false") boolean converted) {
        
        log.debug("Received win notification for bid: {}", bidId);
        
        // Find the bid response by ID
        BidResponse bidResponse = campaignService.findBidResponseById(bidId);
        
        if (bidResponse == null) {
            log.warn("No bid response found with ID: {}", bidId);
            return ResponseEntity.notFound().build();
        }
        
        // Process the auction result
        bidOptimizationService.processAuctionResult(bidResponse, true, clicked, converted);
        
        // Update campaign metrics
        campaignService.updateCampaignMetrics(bidResponse, true, clicked, converted);
        
        return ResponseEntity.ok().build();
    }
    
    /**
     * Process auction results (loss notification)
     */
    @PostMapping("/{bidId}/loss")
    public ResponseEntity<Void> processLoss(@PathVariable String bidId) {
        log.debug("Received loss notification for bid: {}", bidId);
        
        // Find the bid response by ID
        BidResponse bidResponse = campaignService.findBidResponseById(bidId);
        
        if (bidResponse == null) {
            log.warn("No bid response found with ID: {}", bidId);
            return ResponseEntity.notFound().build();
        }
        
        // Process the auction result
        bidOptimizationService.processAuctionResult(bidResponse, false, false, false);
        
        return ResponseEntity.ok().build();
    }
    
    /**
     * Process a click event
     */
    @PostMapping("/{bidId}/click")
    public ResponseEntity<Void> processClick(@PathVariable String bidId) {
        log.debug("Received click event for bid: {}", bidId);
        
        // Find the bid response by ID
        BidResponse bidResponse = campaignService.findBidResponseById(bidId);
        
        if (bidResponse == null) {
            log.warn("No bid response found with ID: {}", bidId);
            return ResponseEntity.notFound().build();
        }
        
        // Update bid response and campaign metrics
        bidResponse.setIsClicked(true);
        campaignService.updateCampaignMetrics(bidResponse, true, true, false);
        
        return ResponseEntity.ok().build();
    }
    
    /**
     * Process a conversion event
     */
    @PostMapping("/{bidId}/conversion")
    public ResponseEntity<Void> processConversion(@PathVariable String bidId) {
        log.debug("Received conversion event for bid: {}", bidId);
        
        // Find the bid response by ID
        BidResponse bidResponse = campaignService.findBidResponseById(bidId);
        
        if (bidResponse == null) {
            log.warn("No bid response found with ID: {}", bidId);
            return ResponseEntity.notFound().build();
        }
        
        // Update bid response and campaign metrics
        bidResponse.setIsConverted(true);
        campaignService.updateCampaignMetrics(bidResponse, true, true, true);
        
        return ResponseEntity.ok().build();
    }
} 