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
  Chip,
  useTheme,
  Paper,
  Divider,
  IconButton,
  Tooltip,
} from '@mui/material';
import { 
  Refresh as RefreshIcon,
  TrendingUp as TrendingUpIcon,
  Campaign as CampaignIcon,
  Psychology as PsychologyIcon,
  Insights as InsightsIcon,
  ShowChart as ShowChartIcon,
  People as PeopleIcon,
} from '@mui/icons-material';
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
  const theme = useTheme();
  
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
        <Box className="page-header">
          <Grid container alignItems="center" spacing={2}>
            <Grid item>
              <Typography variant="h4" component="h1" fontWeight={700}>
                Ad Optimization Dashboard
              </Typography>
              <Typography variant="subtitle1" color="textSecondary">
                Powered by Game Theory - Make data-driven decisions for your ad campaigns
              </Typography>
            </Grid>
            <Grid item xs />
            <Grid item>
              <Tooltip title="Refresh data">
                <IconButton 
                  color="primary" 
                  sx={{ 
                    backgroundColor: theme.palette.background.paper,
                    boxShadow: theme.shadows[1],
                    '&:hover': { boxShadow: theme.shadows[3] },
                  }}
                >
                  <RefreshIcon />
                </IconButton>
              </Tooltip>
            </Grid>
          </Grid>
        </Box>
        
        {loading && (
          <Box 
            display="flex" 
            justifyContent="center" 
            alignItems="center" 
            flexDirection="column" 
            my={8}
          >
            <CircularProgress size={60} thickness={4} />
            <Typography variant="subtitle1" color="textSecondary" sx={{ mt: 2 }}>
              Loading your campaigns...
            </Typography>
          </Box>
        )}
        
        {error && (
          <Paper 
            sx={{ 
              p: 3, 
              borderLeft: '4px solid', 
              borderColor: 'error.main',
              bgcolor: 'error.light',
              color: 'error.dark',
            }}
          >
            <Typography fontWeight={500}>
              Error loading dashboard data. Please try again later.
            </Typography>
          </Paper>
        )}
        
        {campaigns && campaigns.length > 0 && (
          <Box mb={4}>
            <Typography 
              variant="subtitle1" 
              fontWeight={600} 
              mb={2}
              color="text.secondary"
            >
              Select Campaign
            </Typography>
            <div className="campaign-selector">
              {campaigns.map((campaign) => (
                <Button 
                  key={campaign.id}
                  variant={selectedCampaignId === campaign.id ? "contained" : "outlined"}
                  color="primary"
                  onClick={() => setSelectedCampaignId(campaign.id)}
                  startIcon={<CampaignIcon />}
                  sx={{ 
                    px: 2, 
                    py: 1,
                    borderRadius: 2,
                    fontWeight: 500,
                  }}
                >
                  {campaign.name}
                </Button>
              ))}
            </div>
          </Box>
        )}
        
        {selectedCampaign && (
          <Grid container spacing={3}>
            {/* Campaign Performance Overview */}
            <Grid item xs={12} md={8} lg={9}>
              <Card className="dashboard-card">
                <CardHeader 
                  title={
                    <Box display="flex" alignItems="center">
                      <ShowChartIcon className="card-title-icon" color="primary" />
                      Campaign Performance
                    </Box>
                  }
                  action={
                    <Chip 
                      label={selectedCampaign.status} 
                      size="small"
                      color={selectedCampaign.status.toLowerCase() === 'active' ? 'success' : 'default'}
                      sx={{ fontWeight: 500 }}
                    />
                  }
                />
                <Divider />
                <CardContent className="dashboard-card-content">
                  <CampaignPerformance campaignId={selectedCampaign.id} />
                </CardContent>
              </Card>
            </Grid>
            
            {/* Bidding Simulator */}
            <Grid item xs={12} md={4} lg={3}>
              <Card className="dashboard-card">
                <CardHeader 
                  title={
                    <Box display="flex" alignItems="center">
                      <TrendingUpIcon className="card-title-icon" color="primary" />
                      Bidding Simulator
                    </Box>
                  }
                  subheader="Test different bid strategies"
                />
                <Divider />
                <CardContent className="dashboard-card-content">
                  <BiddingSimulator campaignId={selectedCampaign.id} />
                </CardContent>
              </Card>
            </Grid>
            
            {/* Optimization Recommendations */}
            <Grid item xs={12} md={6}>
              <Card className="dashboard-card">
                <CardHeader 
                  title={
                    <Box display="flex" alignItems="center">
                      <InsightsIcon className="card-title-icon" color="primary" />
                      Optimization Recommendations
                    </Box>
                  }
                  subheader="AI-driven improvement suggestions"
                />
                <Divider />
                <CardContent className="dashboard-card-content">
                  <OptimizationRecommendations campaignId={selectedCampaign.id} />
                </CardContent>
              </Card>
            </Grid>
            
            {/* Game Theory Insights */}
            <Grid item xs={12} md={6}>
              <Card className="dashboard-card">
                <CardHeader 
                  title={
                    <Box display="flex" alignItems="center">
                      <PsychologyIcon className="card-title-icon" color="primary" />
                      Game Theory Insights
                    </Box>
                  }
                  subheader="Strategic competitive analysis"
                />
                <Divider />
                <CardContent className="dashboard-card-content">
                  <GameTheoryInsights campaignId={selectedCampaign.id} />
                </CardContent>
              </Card>
            </Grid>
            
            {/* Competitor Analysis */}
            <Grid item xs={12}>
              <Card className="dashboard-card">
                <CardHeader 
                  title={
                    <Box display="flex" alignItems="center">
                      <PeopleIcon className="card-title-icon" color="primary" />
                      Competitor Analysis
                    </Box>
                  }
                  subheader="Market position and competitor strategies"
                />
                <Divider />
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