import { useState, useEffect } from 'react';
import { 
  Box, 
  Typography, 
  CircularProgress, 
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip
} from '@mui/material';
import { useTheme } from '@mui/material/styles';
import { CompetitorProfile } from '@/types/models';

interface CompetitorAnalysisProps {
  campaignId: number;
}

export const CompetitorAnalysis = ({ campaignId }: CompetitorAnalysisProps) => {
  const theme = useTheme();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);
  const [competitors, setCompetitors] = useState<CompetitorProfile[]>([]);

  useEffect(() => {
    const fetchCompetitors = async () => {
      setLoading(true);
      setError(null);
      
      try {
        // Simulate API call with mock data
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        // Mock competitors data
        const mockCompetitors: CompetitorProfile[] = [
          {
            id: 1,
            competitorId: 'aggressive_1',
            competitorName: 'Aggressive Bidder',
            averageBidPrice: 2.85,
            minBidPrice: 1.25,
            maxBidPrice: 4.50,
            behaviorType: 'aggressive',
            preferredAdSlots: ['banner_top', 'sidebar_premium'],
            targetedSegments: ['tech_enthusiasts', 'gadget_buyers']
          },
          {
            id: 2,
            competitorId: 'conservative_1',
            competitorName: 'Conservative Corp',
            averageBidPrice: 1.20,
            minBidPrice: 0.75,
            maxBidPrice: 1.75,
            behaviorType: 'conservative',
            preferredAdSlots: ['footer', 'sidebar_standard'],
            targetedSegments: ['budget_shoppers', 'value_seekers']
          },
          {
            id: 3,
            competitorId: 'timeSensitive_1',
            competitorName: 'Time Sensitive Bidder',
            averageBidPrice: 1.65,
            minBidPrice: 0.95,
            maxBidPrice: 3.25,
            behaviorType: 'time_sensitive',
            activeDayParts: ['9-12', '13-17', '18-22'],
            preferredAdSlots: ['banner_top', 'in_content'],
            targetedSegments: ['daytime_users', 'working_professionals']
          }
        ];
        
        setCompetitors(mockCompetitors);
      } catch (err) {
        setError(err instanceof Error ? err : new Error('An error occurred while fetching competitor data'));
      } finally {
        setLoading(false);
      }
    };
    
    fetchCompetitors();
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
        <Typography color="error">Error loading competitor data: {error.message}</Typography>
      </Box>
    );
  }

  if (!competitors || competitors.length === 0) {
    return (
      <Box>
        <Typography>No competitor data available.</Typography>
      </Box>
    );
  }

  const getBehaviorColor = (behaviorType: string) => {
    switch (behaviorType) {
      case 'aggressive':
        return theme.palette.error.main;
      case 'conservative':
        return theme.palette.success.main;
      case 'time_sensitive':
        return theme.palette.warning.main;
      default:
        return theme.palette.primary.main;
    }
  };

  return (
    <Box>
      <Typography variant="subtitle2" gutterBottom>
        Competitor Bidding Behavior
      </Typography>
      
      <TableContainer component={Paper} sx={{ maxHeight: 320 }}>
        <Table size="small" stickyHeader>
          <TableHead>
            <TableRow>
              <TableCell>Competitor</TableCell>
              <TableCell align="right">Avg. Bid</TableCell>
              <TableCell align="right">Range</TableCell>
              <TableCell align="center">Behavior</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {competitors.map((competitor) => (
              <TableRow key={competitor.id}>
                <TableCell component="th" scope="row">
                  {competitor.competitorName}
                </TableCell>
                <TableCell align="right">${competitor.averageBidPrice.toFixed(2)}</TableCell>
                <TableCell align="right">
                  ${competitor.minBidPrice.toFixed(2)} - ${competitor.maxBidPrice.toFixed(2)}
                </TableCell>
                <TableCell align="center">
                  <Chip 
                    label={competitor.behaviorType.replace('_', ' ')}
                    size="small"
                    sx={{ 
                      bgcolor: getBehaviorColor(competitor.behaviorType),
                      color: 'white',
                      textTransform: 'capitalize'
                    }}
                  />
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      
      <Box mt={2}>
        <Typography variant="body2" color="textSecondary">
          Based on our game theory analysis, we recommend bidding slightly above the conservative 
          competitor but below the aggressive one for optimal ROI.
        </Typography>
      </Box>
    </Box>
  );
}; 