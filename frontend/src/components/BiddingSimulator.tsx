import { useState, useEffect } from 'react';
import { 
  Box, 
  Typography, 
  Slider, 
  TextField, 
  Button, 
  CircularProgress,
  Grid,
  Paper 
} from '@mui/material';
import { useTheme } from '@mui/material/styles';

interface BiddingSimulatorProps {
  campaignId: number;
}

interface SimulationResult {
  winProbability: number;
  expectedCtr: number;
  expectedCvr: number;
  expectedCpa: number;
  expectedRoi: number;
}

export const BiddingSimulator = ({ campaignId }: BiddingSimulatorProps) => {
  const theme = useTheme();
  const [loading, setLoading] = useState(false);
  const [bidAmount, setBidAmount] = useState<number>(1.0);
  const [simulationResult, setSimulationResult] = useState<SimulationResult | null>(null);
  const [floorPrice, setFloorPrice] = useState<number>(0.5);
  const [maxBid, setMaxBid] = useState<number>(3.0);

  // Run simulation when bid amount changes
  useEffect(() => {
    runSimulation();
  }, [bidAmount, campaignId]);

  const runSimulation = async () => {
    setLoading(true);
    
    try {
      // Simulate API call with mock data
      await new Promise(resolve => setTimeout(resolve, 500));
      
      // Calculate simulation result based on bid amount
      // In a real app, this would come from the API
      const winProb = calculateWinProbability(bidAmount, floorPrice, maxBid);
      const result: SimulationResult = {
        winProbability: winProb,
        expectedCtr: 0.025 * (1 + (bidAmount / maxBid) * 0.2), // Higher bids might get better placements
        expectedCvr: 0.12 * (1 + (bidAmount / maxBid) * 0.1),
        expectedCpa: bidAmount / (0.025 * 0.12 * (1 + (bidAmount / maxBid) * 0.3)),
        expectedRoi: (1 + (bidAmount / maxBid) * 0.3) * 2 - bidAmount
      };
      
      setSimulationResult(result);
    } catch (error) {
      console.error("Simulation error:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleBidChange = (_event: Event, newValue: number | number[]) => {
    if (typeof newValue === 'number') {
      setBidAmount(parseFloat(newValue.toFixed(2)));
    }
  };

  const calculateWinProbability = (bid: number, floor: number, max: number): number => {
    // Simple sigmoid function to model win probability
    if (bid < floor) return 0;
    if (bid >= max) return 0.95; // Cap at 95%
    
    const normalizedBid = (bid - floor) / (max - floor);
    return 1 / (1 + Math.exp(-10 * (normalizedBid - 0.5)));
  };

  return (
    <Box className="bidding-simulator">
      <Typography variant="subtitle2" gutterBottom>
        Adjust your bid to see the simulated impact on performance
      </Typography>
      
      <Box sx={{ mb: 2 }}>
        <Typography id="bid-amount-slider" gutterBottom>
          Bid Amount: ${bidAmount.toFixed(2)}
        </Typography>
        <Slider
          value={bidAmount}
          onChange={handleBidChange}
          aria-labelledby="bid-amount-slider"
          min={0.1}
          max={5.0}
          step={0.1}
          marks={[
            { value: 0.5, label: '$0.5' },
            { value: 3.0, label: '$3.0' },
          ]}
        />
      </Box>
      
      {loading ? (
        <Box display="flex" justifyContent="center" m={2}>
          <CircularProgress size={24} />
        </Box>
      ) : simulationResult && (
        <Grid container spacing={2}>
          <Grid item xs={6}>
            <Paper className="metric-card">
              <Typography variant="body2">Win Probability</Typography>
              <Typography variant="h6" sx={{ color: theme.palette.primary.main }}>
                {(simulationResult.winProbability * 100).toFixed(1)}%
              </Typography>
            </Paper>
          </Grid>
          <Grid item xs={6}>
            <Paper className="metric-card">
              <Typography variant="body2">Expected CTR</Typography>
              <Typography variant="h6" sx={{ color: theme.palette.secondary.main }}>
                {(simulationResult.expectedCtr * 100).toFixed(2)}%
              </Typography>
            </Paper>
          </Grid>
          <Grid item xs={6}>
            <Paper className="metric-card">
              <Typography variant="body2">Expected CPA</Typography>
              <Typography variant="h6" sx={{ color: theme.palette.warning.main }}>
                ${simulationResult.expectedCpa.toFixed(2)}
              </Typography>
            </Paper>
          </Grid>
          <Grid item xs={6}>
            <Paper className="metric-card">
              <Typography variant="body2">Expected ROI</Typography>
              <Typography variant="h6" sx={{ color: theme.palette.success.main }}>
                {(simulationResult.expectedRoi * 100).toFixed(0)}%
              </Typography>
            </Paper>
          </Grid>
        </Grid>
      )}
      
      <Box mt={2}>
        <Button 
          variant="contained" 
          color="primary" 
          fullWidth
          disabled={loading}
          onClick={() => {
            // In a real app, this would save the bid to the campaign
            alert(`Optimal bid of $${bidAmount.toFixed(2)} applied to campaign ${campaignId}`);
          }}
        >
          Apply Optimal Bid
        </Button>
      </Box>
    </Box>
  );
}; 