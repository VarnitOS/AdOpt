package com.adopt.models;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing an ad campaign in the system
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdCampaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String advertiserId;
    
    @Enumerated(EnumType.STRING)
    private CampaignType campaignType;

    private BigDecimal totalBudget;
    private BigDecimal remainingBudget;
    private BigDecimal dailyBudget;
    
    private BigDecimal bidFloor; // Minimum bid price
    private BigDecimal maxBidPrice; // Maximum bid price
    
    private Double targetCTR; // Target click-through rate
    private Double targetConversionRate; // Target conversion rate
    
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    @Enumerated(EnumType.STRING)
    private CampaignStatus status;
    
    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<AdCreative> creatives = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "campaign_target_audiences", joinColumns = @JoinColumn(name = "campaign_id"))
    private Set<String> targetAudiences = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "campaign_metrics", joinColumns = @JoinColumn(name = "campaign_id"))
    private Set<CampaignMetric> metrics = new HashSet<>();
    
    public enum CampaignType {
        CPC, // Cost per click
        CPM, // Cost per thousand impressions
        CPA, // Cost per acquisition/conversion
        HYBRID // Multiple objectives
    }
    
    public enum CampaignStatus {
        DRAFT,
        ACTIVE,
        PAUSED,
        COMPLETED,
        CANCELLED
    }
} 