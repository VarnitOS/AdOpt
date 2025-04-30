package com.adopt.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a bid response sent to an ad exchange
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BidResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "bid_request_id")
    private BidRequest bidRequest;
    
    private String responseId;
    
    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private AdCampaign campaign;
    
    @ManyToOne
    @JoinColumn(name = "creative_id")
    private AdCreative creative;
    
    private BigDecimal bidPrice;
    private BigDecimal actualPrice; // What we actually paid (second price in auctions)
    
    @Enumerated(EnumType.STRING)
    private BidStatus status;
    
    private Boolean isWon;
    private Boolean isClicked;
    private Boolean isConverted;
    
    private Double predictedCtr;
    private Double predictedCvr;
    
    private LocalDateTime timestamp;
    private LocalDateTime processedAt;
    
    // Game theory model parameters used to make the decision
    private String gameTheoryModelType;
    private String gameTheoryParameters;
    private Double utilityScore;
    
    public enum BidStatus {
        PENDING,
        SENT,
        WON,
        LOST,
        TIMEOUT,
        ERROR
    }
} 