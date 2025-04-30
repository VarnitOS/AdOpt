import { useState, useEffect } from 'react';
import {
  Box,
  Container,
  Grid,
  Typography,
  Card,
  CardContent,
  CardHeader,
  Button,
  CircularProgress,
} from '@mui/material';
import { Layout } from '@/components/Layout';
import { CampaignPerformance } from '@/components/CampaignPerformance';
import { BiddingSimulator } from '@/components/BiddingSimulator';
import { CompetitorAnalysis } from '@/components/CompetitorAnalysis';
import { GameTheoryInsights } from '@/components/GameTheoryInsights';
import { OptimizationRecommendations } from '@/components/OptimizationRecommendations';
import { useCampaigns } from '@/api/useCampaigns';

export default function Home() {
  const { campaigns, loading, error } = useCampaigns();
  const [selectedCampaignId, setSelectedCampaignId] = useState<number | null>(null);
  
  // Select the first campaign when data is loaded
  useEffect(() => {
    if (campaigns && campaigns.length > 0 && !selectedCampaignId) {
      setSelectedCampaignId(campaigns[0].id);
    }
  }, [campaigns, selectedCampaignId]);
  
  const selectedCampaign = campaigns?.find(c => c.id === selectedCampaignId);
  
  return (
    <Layout>
      <Container maxWidth={false} className="pageContainer">
        <Box mb={4}>
          <Typography variant="h4" component="h1" gutterBottom>
            Ad Optimization Dashboard
          </Typography>
          <Typography variant="subtitle1" color="textSecondary">
            Powered by Game Theory
          </Typography>
        </Box>
        
        {loading && (
          <Box display="flex" justifyContent="center" my={8}>
            <CircularProgress />
          </Box>
        )}
        
        {error && (
          <Box my={4}>
            <Typography color="error">
              Error loading dashboard data. Please try again later.
            </Typography>
          </Box>
        )}
        
        {campaigns && campaigns.length > 0 && (
          <Box mb={4}>
            <Grid container spacing={2}>
              {campaigns.map((campaign) => (
                <Grid item key={campaign.id}>
                  <Button 
                    variant={selectedCampaignId === campaign.id ? "contained" : "outlined"}
                    onClick={() => setSelectedCampaignId(campaign.id)}
                  >
                    {campaign.name}
                  </Button>
                </Grid>
              ))}
            </Grid>
          </Box>
        )}
        
        {selectedCampaign && (
          <Grid container spacing={3}>
            {/* Campaign Performance Overview */}
            <Grid item xs={12} md={8} lg={9}>
              <Card className="dashboard-card">
                <CardHeader title="Campaign Performance" />
                <CardContent className="dashboard-card-content">
                  <CampaignPerformance campaignId={selectedCampaign.id} />
                </CardContent>
              </Card>
            </Grid>
            
            {/* Bidding Simulator */}
            <Grid item xs={12} md={4} lg={3}>
              <Card className="dashboard-card">
                <CardHeader title="Bidding Simulator" />
                <CardContent className="dashboard-card-content">
                  <BiddingSimulator campaignId={selectedCampaign.id} />
                </CardContent>
              </Card>
            </Grid>
            
            {/* Optimization Recommendations */}
            <Grid item xs={12} md={6}>
              <Card className="dashboard-card">
                <CardHeader title="Optimization Recommendations" />
                <CardContent className="dashboard-card-content">
                  <OptimizationRecommendations campaignId={selectedCampaign.id} />
                </CardContent>
              </Card>
            </Grid>
            
            {/* Game Theory Insights */}
            <Grid item xs={12} md={6}>
              <Card className="dashboard-card">
                <CardHeader title="Game Theory Insights" />
                <CardContent className="dashboard-card-content">
                  <GameTheoryInsights campaignId={selectedCampaign.id} />
                </CardContent>
              </Card>
            </Grid>
            
            {/* Competitor Analysis */}
            <Grid item xs={12}>
              <Card className="dashboard-card">
                <CardHeader title="Competitor Analysis" />
                <CardContent className="dashboard-card-content">
                  <CompetitorAnalysis campaignId={selectedCampaign.id} />
                </CardContent>
              </Card>
            </Grid>
          </Grid>
        )}
      </Container>
    </Layout>
  );
} 