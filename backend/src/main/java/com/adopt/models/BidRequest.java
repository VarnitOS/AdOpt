package com.adopt.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Entity representing a bid request from an ad exchange
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BidRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String requestId;
    private String exchangeId;
    
    private String ipAddress;
    private String userAgent;
    private String deviceId;
    private String cookieId;
    
    private String userProfileIds;
    private String geoRegion;
    private String geoCity;
    
    private String publisherDomain;
    private String publisherUrl;
    
    private String adSlotId;
    private Integer adSlotWidth;
    private Integer adSlotHeight;
    
    private BigDecimal adSlotFloorPrice; // Minimum price set by publisher
    
    private LocalDateTime timestamp;
    
    // Transient properties for runtime use (not persisted)
    private transient Map<String, Object> userProfile;
    private transient Map<String, Double> competitorPredictions;
} 