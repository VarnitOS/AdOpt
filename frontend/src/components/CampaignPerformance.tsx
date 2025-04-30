import { useState, useEffect } from 'react';
import { Box, Grid, Typography, CircularProgress, Paper } from '@mui/material';
import { useTheme } from '@mui/material/styles';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  ChartData,
} from 'chart.js';
import { Line } from 'react-chartjs-2';
import axios from 'axios';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

interface CampaignPerformanceProps {
  campaignId: number;
}

interface MetricSummary {
  label: string;
  value: string | number;
  unit?: string;
  color?: string;
}

export const CampaignPerformance = ({ campaignId }: CampaignPerformanceProps) => {
  const theme = useTheme();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);
  const [metrics, setMetrics] = useState<any>(null);

  useEffect(() => {
    const fetchPerformanceData = async () => {
      setLoading(true);
      setError(null);

      try {
        // In a real app, this would fetch from the API
        // const response = await axios.get(`/api/campaigns/${campaignId}/performance`);
        // setMetrics(response.data);
        
        // Mock data for demo
        setTimeout(() => {
          const mockData = {
            id: campaignId,
            name: campaignId === 1 ? 'Tech Gadget Promotion' : 'Fashion Sale',
            impressions: 125000,
            clicks: 2500,
            conversions: 375,
            spend: campaignId === 1 ? 250 : 500,
            ctr: 0.02,
            cvr: 0.15,
            cpc: campaignId === 1 ? 0.10 : 0.20,
            cpm: campaignId === 1 ? 2.00 : 4.00,
            cpa: campaignId === 1 ? 0.67 : 1.33,
            dailyPerformance: generateMockDailyData(campaignId, 14),
          };
          
          setMetrics(mockData);
          setLoading(false);
        }, 1000);
      } catch (err) {
        setError(err instanceof Error ? err : new Error('An unknown error occurred'));
        setLoading(false);
      }
    };

    fetchPerformanceData();
  }, [campaignId]);

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" height="300px">
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box>
        <Typography color="error">Error loading performance data: {error.message}</Typography>
      </Box>
    );
  }

  if (!metrics) {
    return (
      <Box>
        <Typography>No performance data available.</Typography>
      </Box>
    );
  }

  const metricSummaries: MetricSummary[] = [
    { label: 'Impressions', value: metrics.impressions.toLocaleString(), color: theme.palette.primary.main },
    { label: 'Clicks', value: metrics.clicks.toLocaleString(), color: theme.palette.secondary.main },
    { label: 'Conversions', value: metrics.conversions.toLocaleString(), color: theme.palette.success.main },
    { label: 'Spend', value: `$${metrics.spend.toLocaleString()}`, color: theme.palette.error.main },
    { label: 'CTR', value: `${(metrics.ctr * 100).toFixed(2)}%`, color: theme.palette.primary.dark },
    { label: 'CVR', value: `${(metrics.cvr * 100).toFixed(2)}%`, color: theme.palette.secondary.dark },
    { label: 'CPC', value: `$${metrics.cpc.toFixed(2)}`, color: theme.palette.info.main },
    { label: 'CPM', value: `$${metrics.cpm.toFixed(2)}`, color: theme.palette.warning.main },
  ];

  const chartData: ChartData<'line'> = {
    labels: metrics.dailyPerformance.map((day: any) => day.date),
    datasets: [
      {
        label: 'Impressions',
        data: metrics.dailyPerformance.map((day: any) => day.impressions),
        borderColor: theme.palette.primary.main,
        backgroundColor: theme.palette.primary.main,
        yAxisID: 'y',
      },
      {
        label: 'Clicks',
        data: metrics.dailyPerformance.map((day: any) => day.clicks),
        borderColor: theme.palette.secondary.main,
        backgroundColor: theme.palette.secondary.main,
        yAxisID: 'y1',
      },
      {
        label: 'Conversions',
        data: metrics.dailyPerformance.map((day: any) => day.conversions),
        borderColor: theme.palette.success.main,
        backgroundColor: theme.palette.success.main,
        yAxisID: 'y1',
      },
    ],
  };

  const chartOptions = {
    responsive: true,
    interaction: {
      mode: 'index' as const,
      intersect: false,
    },
    scales: {
      y: {
        type: 'linear' as const,
        display: true,
        position: 'left' as const,
        title: {
          display: true,
          text: 'Impressions',
        },
      },
      y1: {
        type: 'linear' as const,
        display: true,
        position: 'right' as const,
        grid: {
          drawOnChartArea: false,
        },
        title: {
          display: true,
          text: 'Clicks & Conversions',
        },
      },
    },
  };

  return (
    <Box>
      <Grid container spacing={2} mb={4}>
        {metricSummaries.map((metric) => (
          <Grid item xs={6} sm={3} key={metric.label}>
            <Paper className="metric-card">
              <Typography variant="body2" className="metric-label">{metric.label}</Typography>
              <Typography variant="h5" className="metric-value" style={{ color: metric.color }}>
                {metric.value}
              </Typography>
            </Paper>
          </Grid>
        ))}
      </Grid>
      
      <Box className="chart-container">
        <Line options={chartOptions} data={chartData} />
      </Box>
    </Box>
  );
};

function generateMockDailyData(campaignId: number, days: number) {
  const result = [];
  const today = new Date();
  const baseImpressions = campaignId === 1 ? 8000 : 10000;
  const baseClicks = campaignId === 1 ? 160 : 150;
  const baseConversions = campaignId === 1 ? 24 : 8;
  
  for (let i = days - 1; i >= 0; i--) {
    const date = new Date(today);
    date.setDate(date.getDate() - i);
    
    // Add some randomness and a slight upward trend
    const dayFactor = 1 + ((days - i) / 100);
    const randomFactor = 0.8 + Math.random() * 0.4; // 0.8 to 1.2
    
    const impressions = Math.round(baseImpressions * dayFactor * randomFactor);
    const clicks = Math.round(baseClicks * dayFactor * randomFactor);
    const conversions = Math.round(baseConversions * dayFactor * randomFactor);
    
    result.push({
      date: date.toISOString().split('T')[0],
      impressions,
      clicks,
      conversions,
    });
  }
  
  return result;
} 