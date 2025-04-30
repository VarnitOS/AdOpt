package com.adopt.controllers;

import com.adopt.models.BidOptimization;
import com.adopt.repositories.BidOptimizationRepository;
import com.adopt.services.BidOptimizationScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for optimization operations
 */
@Slf4j
@RestController
@RequestMapping("/api/optimizations")
@RequiredArgsConstructor
public class OptimizationController {

    private final BidOptimizationRepository optimizationRepository;
    private final BidOptimizationScheduler optimizationScheduler;
    
    /**
     * Get all optimization recommendations
     */
    @GetMapping
    public ResponseEntity<List<BidOptimization>> getAllOptimizations() {
        List<BidOptimization> optimizations = optimizationRepository.findAll();
        return ResponseEntity.ok(optimizations);
    }
    
    /**
     * Get optimization recommendations for a specific campaign
     */
    @GetMapping("/campaign/{campaignId}")
    public ResponseEntity<List<BidOptimization>> getOptimizationsByCampaign(@PathVariable Long campaignId) {
        List<BidOptimization> optimizations = optimizationRepository.findByCampaignId(campaignId);
        return ResponseEntity.ok(optimizations);
    }
    
    /**
     * Get a specific optimization by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<BidOptimization> getOptimization(@PathVariable Long id) {
        Optional<BidOptimization> optimizationOpt = optimizationRepository.findById(id);
        if (optimizationOpt.isPresent()) {
            return ResponseEntity.ok(optimizationOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Apply an optimization
     */
    @PostMapping("/{id}/apply")
    public ResponseEntity<BidOptimization> applyOptimization(@PathVariable Long id) {
        Optional<BidOptimization> optimizationOpt = optimizationRepository.findById(id);
        if (!optimizationOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        BidOptimization optimization = optimizationOpt.get();
        // Only apply if it's still pending
        if (optimization.getStatus() != BidOptimization.OptimizationStatus.PENDING) {
            return ResponseEntity.badRequest().build();
        }
        
        // Apply the optimization
        optimization.setStatus(BidOptimization.OptimizationStatus.APPLIED);
        optimization.setAppliedAt(LocalDateTime.now());
        
        // Update the campaign with the optimized bid
        optimization.getCampaign().setMaxBidPrice(optimization.getOptimizedBid());
        
        // Save changes
        BidOptimization savedOptimization = optimizationRepository.save(optimization);
        return ResponseEntity.ok(savedOptimization);
    }
    
    /**
     * Reject an optimization
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<BidOptimization> rejectOptimization(@PathVariable Long id) {
        Optional<BidOptimization> optimizationOpt = optimizationRepository.findById(id);
        if (!optimizationOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        BidOptimization optimization = optimizationOpt.get();
        // Only reject if it's still pending
        if (optimization.getStatus() != BidOptimization.OptimizationStatus.PENDING) {
            return ResponseEntity.badRequest().build();
        }
        
        // Reject the optimization
        optimization.setStatus(BidOptimization.OptimizationStatus.REJECTED);
        
        // Save changes
        BidOptimization savedOptimization = optimizationRepository.save(optimization);
        return ResponseEntity.ok(savedOptimization);
    }
    
    /**
     * Trigger optimization generation (for testing)
     */
    @PostMapping("/generate")
    public ResponseEntity<Map<String, String>> generateOptimizations() {
        optimizationScheduler.generateBidOptimizations();
        return ResponseEntity.ok(Map.of("message", "Optimization generation triggered"));
    }
} 