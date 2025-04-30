package com.adopt.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Embeddable entity representing campaign metrics and performance data
 */
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignMetric {
    private LocalDate date;
    
    private Long impressions;
    private Long clicks;
    private Long conversions;
    
    private BigDecimal spend;
    
    private Double clickThroughRate; // CTR = clicks / impressions
    private Double conversionRate; // CR = conversions / clicks
    
    private BigDecimal costPerClick; // CPC = spend / clicks
    private BigDecimal costPerMille; // CPM = spend / (impressions / 1000)
    private BigDecimal costPerAcquisition; // CPA = spend / conversions
    
    private Double returnOnInvestment; // ROI
    
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;
    
    public enum DayOfWeek {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }
} 