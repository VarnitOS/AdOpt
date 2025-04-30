package com.adopt.models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing an advertisement creative for ad campaigns
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdCreative {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    
    private String imageUrl;
    private String landingPageUrl;
    
    private Integer width;
    private Integer height;
    
    @Enumerated(EnumType.STRING)
    private CreativeType type;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AdCampaign campaign;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private boolean active;
    
    public enum CreativeType {
        IMAGE,
        TEXT,
        VIDEO,
        HTML
    }
} 