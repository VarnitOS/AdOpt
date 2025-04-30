import { useState, useEffect } from 'react';
import { 
  Box, 
  Typography, 
  CircularProgress, 
  Card,
  CardContent,
  Grid,
  Divider,
  List,
  ListItem,
  ListItemText,
  Paper
} from '@mui/material';
import { useTheme } from '@mui/material/styles';

interface GameTheoryInsightsProps {
  campaignId: number;
}

interface GameTheoryModel {
  name: string;
  description: string;
  recommendedBid: number;
  expectedUtility: number;
  confidenceScore: number;
}

interface InsightItem {
  title: string;
  content: string;
}

export const GameTheoryInsights = ({ campaignId }: GameTheoryInsightsProps) => {
  const theme = useTheme();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);
  const [model, setModel] = useState<GameTheoryModel | null>(null);
  const [insights, setInsights] = useState<InsightItem[]>([]);

  useEffect(() => {
    const fetchInsights = async () => {
      setLoading(true);
      setError(null);
      
      try {
        // Simulate API call with mock data
        await new Promise(resolve => setTimeout(resolve, 700));
        
        // Mock game theory model data
        const mockModel: GameTheoryModel = {
          name: 'Nash Equilibrium',
          description: 'A strategy profile where no player can benefit by changing their strategy unilaterally',
          recommendedBid: 1.75,
          expectedUtility: 3.2,
          confidenceScore: 0.85
        };
        
        // Mock insights
        const mockInsights: InsightItem[] = [
          {
            title: 'Bidding Strategy',
            content: 'Your competitors are using mixed strategies. Recommended approach is to vary your bids slightly to prevent them from learning your patterns.'
          },
          {
            title: 'Market Position',
            content: 'You are in a strong position against conservative bidders but vulnerable to aggressive competitors.'
          },
          {
            title: 'Time Sensitivity',
            content: 'Consider increasing bids during 1-3 PM when time-sensitive competitors are most active.'
          },
          {
            title: 'Audience Overlap',
            content: 'You share 65% audience overlap with your main competitor. Focus on differentiating your ads.'
          }
        ];
        
        setModel(mockModel);
        setInsights(mockInsights);
      } catch (err) {
        setError(err instanceof Error ? err : new Error('An error occurred while fetching game theory insights'));
      } finally {
        setLoading(false);
      }
    };
    
    fetchInsights();
  }, [campaignId]);

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" height="200px">
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box>
        <Typography color="error">Error loading game theory insights: {error.message}</Typography>
      </Box>
    );
  }

  if (!model) {
    return (
      <Box>
        <Typography>No game theory insights available.</Typography>
      </Box>
    );
  }

  return (
    <Box>
      <Box mb={3}>
        <Paper sx={{ p: 2, bgcolor: theme.palette.primary.light, color: 'white' }}>
          <Typography variant="subtitle1" gutterBottom fontWeight="bold">
            {model.name} Model
          </Typography>
          <Typography variant="body2">
            {model.description}
          </Typography>
          <Box mt={2} display="flex" justifyContent="space-between">
            <Box>
              <Typography variant="caption">Recommended Bid</Typography>
              <Typography variant="h6">${model.recommendedBid.toFixed(2)}</Typography>
            </Box>
            <Box>
              <Typography variant="caption">Expected Utility</Typography>
              <Typography variant="h6">{model.expectedUtility.toFixed(1)}x</Typography>
            </Box>
            <Box>
              <Typography variant="caption">Confidence</Typography>
              <Typography variant="h6">{(model.confidenceScore * 100).toFixed(0)}%</Typography>
            </Box>
          </Box>
        </Paper>
      </Box>
      
      <Typography variant="subtitle2" gutterBottom>
        Strategic Insights
      </Typography>
      
      <List>
        {insights.map((insight, index) => (
          <Box key={index} mb={1.5}>
            <Paper sx={{ p: 1.5 }}>
              <Typography variant="body2" fontWeight="medium" color={theme.palette.primary.main}>
                {insight.title}
              </Typography>
              <Typography variant="body2" color="textSecondary" sx={{ mt: 0.5 }}>
                {insight.content}
              </Typography>
            </Paper>
          </Box>
        ))}
      </List>
    </Box>
  );
}; 