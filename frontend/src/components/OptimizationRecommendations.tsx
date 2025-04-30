import { useState } from 'react';
import { 
  Box, 
  Typography, 
  CircularProgress, 
  List,
  ListItem,
  ListItemText,
  Button,
  Chip,
  Paper,
  Divider,
  Alert,
  Snackbar
} from '@mui/material';
import { useTheme } from '@mui/material/styles';
import { useOptimization } from '@/api/useOptimization';

interface OptimizationRecommendationsProps {
  campaignId: number;
}

export const OptimizationRecommendations = ({ campaignId }: OptimizationRecommendationsProps) => {
  const theme = useTheme();
  const { recommendations, loading, error, applyOptimization, refetch } = useOptimization(campaignId);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const handleApplyOptimization = async (optimizationId: number) => {
    try {
      await applyOptimization(optimizationId);
      setSuccessMessage("Optimization successfully applied!");
    } catch (err) {
      setErrorMessage("Failed to apply optimization. Please try again.");
    }
  };

  const handleCloseSnackbar = () => {
    setSuccessMessage(null);
    setErrorMessage(null);
  };

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
        <Typography color="error">Error loading optimization recommendations: {error.message}</Typography>
      </Box>
    );
  }

  if (!recommendations || recommendations.length === 0) {
    return (
      <Box>
        <Typography>No optimization recommendations available at this time.</Typography>
      </Box>
    );
  }

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'APPLIED':
        return theme.palette.success.main;
      case 'PENDING':
        return theme.palette.warning.main;
      case 'REJECTED':
        return theme.palette.error.main;
      default:
        return theme.palette.primary.main;
    }
  };

  return (
    <Box>
      <Typography variant="subtitle2" gutterBottom>
        Optimization Recommendations
      </Typography>
      
      <List>
        {recommendations.map((recommendation) => (
          <Paper key={recommendation.id} sx={{ mb: 2, overflow: 'hidden' }}>
            <Box p={2}>
              <Box display="flex" justifyContent="space-between" alignItems="center" mb={1}>
                <Typography variant="subtitle1">
                  Bid Optimization #{recommendation.id}
                </Typography>
                <Chip 
                  label={recommendation.status}
                  size="small"
                  sx={{ 
                    bgcolor: getStatusColor(recommendation.status),
                    color: 'white'
                  }}
                />
              </Box>
              
              <Divider sx={{ my: 1 }} />
              
              <Box display="flex" justifyContent="space-between" mb={2}>
                <Box>
                  <Typography variant="caption" color="textSecondary">Current Bid</Typography>
                  <Typography variant="h6">${recommendation.previousBid.toFixed(2)}</Typography>
                </Box>
                <Box textAlign="center">
                  <Typography variant="caption" color="textSecondary">Recommended Bid</Typography>
                  <Typography variant="h6" color={theme.palette.primary.main}>
                    ${recommendation.optimizedBid.toFixed(2)}
                  </Typography>
                </Box>
                <Box textAlign="right">
                  <Typography variant="caption" color="textSecondary">Performance Gain</Typography>
                  <Typography variant="h6" color={theme.palette.success.main}>
                    +{recommendation.performanceChange.toFixed(1)}%
                  </Typography>
                </Box>
              </Box>
              
              {recommendation.status === 'PENDING' && (
                <Button
                  variant="contained"
                  fullWidth
                  onClick={() => handleApplyOptimization(recommendation.id)}
                >
                  Apply Optimization
                </Button>
              )}
            </Box>
          </Paper>
        ))}
      </List>
      
      <Snackbar open={!!successMessage} autoHideDuration={6000} onClose={handleCloseSnackbar}>
        <Alert onClose={handleCloseSnackbar} severity="success" sx={{ width: '100%' }}>
          {successMessage}
        </Alert>
      </Snackbar>
      
      <Snackbar open={!!errorMessage} autoHideDuration={6000} onClose={handleCloseSnackbar}>
        <Alert onClose={handleCloseSnackbar} severity="error" sx={{ width: '100%' }}>
          {errorMessage}
        </Alert>
      </Snackbar>
    </Box>
  );
}; 