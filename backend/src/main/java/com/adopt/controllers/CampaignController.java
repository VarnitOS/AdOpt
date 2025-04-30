package com.adopt.controllers;

import com.adopt.models.AdCampaign;
import com.adopt.models.CampaignMetric;
import com.adopt.services.CampaignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * REST controller for campaign management
 */
@Slf4j
@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    /**
     * Get all campaigns
     */
    @GetMapping
    public ResponseEntity<List<AdCampaign>> getAllCampaigns() {
        List<AdCampaign> campaigns = campaignService.findAllCampaigns();
        return ResponseEntity.ok(campaigns);
    }
    
    /**
     * Get a single campaign by ID
     */
    @GetMapping("/{campaignId}")
    public ResponseEntity<AdCampaign> getCampaign(@PathVariable Long campaignId) {
        AdCampaign campaign = campaignService.findCampaignById(campaignId);
        
        if (campaign == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(campaign);
    }
    
    /**
     * Create a new campaign
     */
    @PostMapping
    public ResponseEntity<AdCampaign> createCampaign(@Valid @RequestBody AdCampaign campaign) {
        AdCampaign savedCampaign = campaignService.saveCampaign(campaign);
        return ResponseEntity.ok(savedCampaign);
    }
    
    /**
     * Update an existing campaign
     */
    @PutMapping("/{campaignId}")
    public ResponseEntity<AdCampaign> updateCampaign(
            @PathVariable Long campaignId, 
            @Valid @RequestBody AdCampaign campaign) {
        
        AdCampaign existingCampaign = campaignService.findCampaignById(campaignId);
        
        if (existingCampaign == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Set the ID to ensure we're updating the right campaign
        campaign.setId(campaignId);
        
        AdCampaign updatedCampaign = campaignService.saveCampaign(campaign);
        return ResponseEntity.ok(updatedCampaign);
    }
    
    /**
     * Delete a campaign
     */
    @DeleteMapping("/{campaignId}")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Long campaignId) {
        AdCampaign existingCampaign = campaignService.findCampaignById(campaignId);
        
        if (existingCampaign == null) {
            return ResponseEntity.notFound().build();
        }
        
        campaignService.deleteCampaign(campaignId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get campaign metrics for a date range
     */
    @GetMapping("/{campaignId}/metrics")
    public ResponseEntity<Set<CampaignMetric>> getCampaignMetrics(
            @PathVariable Long campaignId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        
        AdCampaign campaign = campaignService.findCampaignById(campaignId);
        
        if (campaign == null) {
            return ResponseEntity.notFound().build();
        }
        
        Set<CampaignMetric> metrics = campaignService.getCampaignMetrics(campaignId, startDate, endDate);
        return ResponseEntity.ok(metrics);
    }
    
    /**
     * Get campaign performance summary
     */
    @GetMapping("/{campaignId}/performance")
    public ResponseEntity<Map<String, Object>> getCampaignPerformance(@PathVariable Long campaignId) {
        AdCampaign campaign = campaignService.findCampaignById(campaignId);
        
        if (campaign == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> performance = campaignService.getCampaignPerformanceSummary(campaignId);
        return ResponseEntity.ok(performance);
    }
    
    /**
     * Update campaign status (activate, pause, etc.)
     */
    @PatchMapping("/{campaignId}/status")
    public ResponseEntity<AdCampaign> updateCampaignStatus(
            @PathVariable Long campaignId,
            @RequestBody Map<String, String> statusUpdate) {
        
        AdCampaign campaign = campaignService.findCampaignById(campaignId);
        
        if (campaign == null) {
            return ResponseEntity.notFound().build();
        }
        
        String status = statusUpdate.get("status");
        if (status == null) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            AdCampaign.CampaignStatus newStatus = AdCampaign.CampaignStatus.valueOf(status.toUpperCase());
            AdCampaign updatedCampaign = campaignService.updateCampaignStatus(campaignId, newStatus);
            return ResponseEntity.ok(updatedCampaign);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 