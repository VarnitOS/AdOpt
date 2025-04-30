export type CampaignStatus = 'ACTIVE' | 'PAUSED' | 'DRAFT' | 'COMPLETED';

export type CampaignObjective = 'CONVERSIONS' | 'TRAFFIC' | 'BRAND_AWARENESS' | 'SALES';

export interface CampaignMetrics {
  impressions: number;
  clicks: number;
  conversions: number;
  spend: number;
  ctr: number;
  cvr: number;
  cpa: number;
}

export interface Campaign {
  id: number;
  name: string;
  status: CampaignStatus;
  totalBudget: number;
  dailyBudget: number;
  startDate: string;
  endDate: string;
  objective: CampaignObjective;
  targetAudience: string;
  metrics: CampaignMetrics | null;
}

export interface BidStrategy {
  id: number;
  name: string;
  description: string;
  targetMetric: 'CTR' | 'CVR' | 'CPA';
  targetValue: number;
  maxBidAdjustment: number;
  minBidAdjustment: number;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Optimization {
  id: number;
  campaignId: number;
  bidStrategyId: number;
  previousBid: number;
  optimizedBid: number;
  performanceChange: number;
  status: 'PENDING' | 'APPLIED' | 'REJECTED';
  createdAt: string;
  appliedAt?: string;
}

export interface AdCreative {
  id: number;
  name: string;
  description: string;
  imageUrl: string;
  landingPageUrl: string;
  width: number;
  height: number;
  type: 'IMAGE' | 'TEXT' | 'VIDEO' | 'HTML';
  active: boolean;
}

export interface CampaignMetric {
  date: string;
  impressions: number;
  clicks: number;
  conversions: number;
  spend: number;
  clickThroughRate?: number;
  conversionRate?: number;
  costPerClick?: number;
  costPerMille?: number;
  costPerAcquisition?: number;
  returnOnInvestment?: number;
  dayOfWeek?: string;
}

export interface BidRequest {
  id: number;
  requestId: string;
  exchangeId: string;
  adSlotId: string;
  adSlotWidth: number;
  adSlotHeight: number;
  adSlotFloorPrice: number;
  publisherDomain: string;
  publisherUrl: string;
}

export interface BidResponse {
  id: number;
  responseId: string;
  bidRequestId: number;
  campaignId: number;
  creativeId: number;
  bidPrice: number;
  actualPrice?: number;
  status: 'PENDING' | 'SENT' | 'WON' | 'LOST' | 'TIMEOUT' | 'ERROR';
  isWon?: boolean;
  isClicked?: boolean;
  isConverted?: boolean;
  predictedCtr?: number;
  predictedCvr?: number;
  gameTheoryModelType: string;
  utilityScore?: number;
  timestamp: string;
}

export interface CompetitorProfile {
  id: number;
  competitorId: string;
  competitorName: string;
  averageBidPrice: number;
  minBidPrice: number;
  maxBidPrice: number;
  behaviorType: string;
  targetedSegments?: string[];
  preferredAdSlots?: string[];
  activeDayParts?: string[];
}

export interface GameTheoryModel {
  type: 'NASH_EQUILIBRIUM' | 'STACKELBERG' | 'BAYESIAN' | 'REINFORCEMENT_LEARNING' | 'MULTI_AGENT_LEARNING';
  parameters?: Record<string, any>;
  payoffMatrix?: number[][];
  utility?: number;
} 