import { useState, useEffect, useCallback } from 'react';
import { Optimization } from '../types/models';
import api from '../utils/api';

export interface UseOptimizationResult {
  recommendations: Optimization[];
  loading: boolean;
  error: Error | null;
  applyOptimization: (optimizationId: number) => Promise<void>;
  refetch: () => void;
}

export function useOptimization(campaignId: number): UseOptimizationResult {
  const [recommendations, setRecommendations] = useState<Optimization[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<Error | null>(null);
  const [refetchIndex, setRefetchIndex] = useState<number>(0);

  const refetch = useCallback(() => {
    setRefetchIndex(prevIndex => prevIndex + 1);
  }, []);

  useEffect(() => {
    const fetchRecommendations = async () => {
      setLoading(true);
      setError(null);
      
      try {
        // In a real app, we would fetch from the API
        // const response = await api.get(`/campaigns/${campaignId}/optimizations`);
        // setRecommendations(response.data);
        
        // Mock data for demo
        await new Promise(resolve => setTimeout(resolve, 800));
        
        const mockRecommendations: Optimization[] = [
          {
            id: 1,
            campaignId: campaignId,
            bidStrategyId: 1,
            previousBid: 1.50,
            optimizedBid: 1.75,
            performanceChange: 15.5,
            status: 'PENDING',
            createdAt: new Date().toISOString()
          },
          {
            id: 2,
            campaignId: campaignId,
            bidStrategyId: 2,
            previousBid: 0.95,
            optimizedBid: 1.20,
            performanceChange: 10.2,
            status: 'PENDING',
            createdAt: new Date().toISOString()
          }
        ];
        
        setRecommendations(mockRecommendations);
      } catch (err) {
        setError(err instanceof Error ? err : new Error('An error occurred while fetching optimization recommendations'));
      } finally {
        setLoading(false);
      }
    };

    fetchRecommendations();
  }, [campaignId, refetchIndex]);

  const applyOptimization = useCallback(async (optimizationId: number) => {
    try {
      // In a real app, we would call the API
      // await api.post(`/optimizations/${optimizationId}/apply`);
      
      // For the demo, we'll just update the local state
      await new Promise(resolve => setTimeout(resolve, 600));
      
      setRecommendations(current => 
        current.map(opt => 
          opt.id === optimizationId 
            ? { ...opt, status: 'APPLIED', appliedAt: new Date().toISOString() } 
            : opt
        )
      );
      
      return Promise.resolve();
    } catch (err) {
      const error = err instanceof Error ? err : new Error('An error occurred while applying optimization');
      return Promise.reject(error);
    }
  }, []);

  return { recommendations, loading, error, applyOptimization, refetch };
} 