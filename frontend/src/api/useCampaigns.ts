import { useState, useEffect, useCallback } from 'react';
import { Campaign } from '../types/models';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:3001/api';

export interface UseCampaignsResult {
  campaigns: Campaign[];
  loading: boolean;
  error: Error | null;
  refetch: () => void;
}

export function useCampaigns(): UseCampaignsResult {
  const [campaigns, setCampaigns] = useState<Campaign[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<Error | null>(null);
  const [refetchIndex, setRefetchIndex] = useState<number>(0);

  const refetch = useCallback(() => {
    setRefetchIndex(prevIndex => prevIndex + 1);
  }, []);

  useEffect(() => {
    const fetchCampaigns = async () => {
      setLoading(true);
      setError(null);
      
      try {
        // Simulate API call with mock data for now
        // In a real app, this would be: const response = await fetch(`${API_BASE_URL}/campaigns`);
        // followed by const data = await response.json();
        
        await new Promise(resolve => setTimeout(resolve, 800)); // Simulating network delay
        
        const mockCampaigns: Campaign[] = [
          {
            id: 1,
            name: 'Summer Sale 2023',
            status: 'ACTIVE',
            totalBudget: 5000,
            dailyBudget: 250,
            startDate: '2023-06-01',
            endDate: '2023-08-31',
            objective: 'SALES',
            targetAudience: 'Shoppers 25-45',
            metrics: {
              impressions: 125000,
              clicks: 3750,
              conversions: 280,
              spend: 1875.5,
              ctr: 3.0,
              cvr: 7.47,
              cpa: 6.7
            }
          },
          {
            id: 2,
            name: 'Product Launch',
            status: 'DRAFT',
            totalBudget: 10000,
            dailyBudget: 500,
            startDate: '2023-09-15',
            endDate: '2023-10-15',
            objective: 'BRAND_AWARENESS',
            targetAudience: 'Tech enthusiasts',
            metrics: null
          },
          {
            id: 3,
            name: 'Holiday Promotion',
            status: 'PAUSED',
            totalBudget: 8000,
            dailyBudget: 400,
            startDate: '2023-11-15',
            endDate: '2023-12-31',
            objective: 'CONVERSIONS',
            targetAudience: 'Gift shoppers',
            metrics: {
              impressions: 87500,
              clicks: 2100,
              conversions: 168,
              spend: 1260.75,
              ctr: 2.4,
              cvr: 8.0,
              cpa: 7.5
            }
          }
        ];
        
        setCampaigns(mockCampaigns);
      } catch (err) {
        setError(err instanceof Error ? err : new Error('An error occurred while fetching campaigns'));
      } finally {
        setLoading(false);
      }
    };

    fetchCampaigns();
  }, [refetchIndex]);

  return { campaigns, loading, error, refetch };
} 