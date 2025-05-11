import { styled } from '@mui/material/styles';
import { Box, Card, Paper, Typography, Chip, Button, IconButton, Grid } from '@mui/material';

// Gradient text
export const GradientText = styled(Typography)(({ theme }) => ({
  background: 'var(--gradient-primary)',
  WebkitBackgroundClip: 'text',
  WebkitTextFillColor: 'transparent',
  backgroundClip: 'text',
  color: 'transparent',
}));

// Gradient border card
export const GradientBorderCard = styled(Card)(({ theme }) => ({
  position: 'relative',
  overflow: 'hidden',
  borderRadius: theme.shape.borderRadius,
  boxShadow: theme.shadows[3],
  '&::before': {
    content: '""',
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    height: '4px',
    background: 'var(--gradient-primary)',
  },
  transition: 'transform 0.3s ease-in-out, box-shadow 0.3s ease-in-out',
  '&:hover': {
    transform: 'translateY(-4px)',
    boxShadow: theme.shadows[6],
  }
}));

// Glass morphism effect
export const GlassCard = styled(Card)(({ theme }) => ({
  background: 'rgba(255, 255, 255, 0.7)',
  backdropFilter: 'blur(10px)',
  boxShadow: '0 8px 32px rgba(0, 0, 0, 0.08)',
  border: '1px solid rgba(255, 255, 255, 0.3)',
  borderRadius: theme.shape.borderRadius,
}));

// Stats card with animation
export const StatsCard = styled(Paper)(({ theme }) => ({
  padding: theme.spacing(3),
  textAlign: 'center',
  transition: 'all 0.3s ease',
  boxShadow: theme.shadows[2],
  borderRadius: theme.shape.borderRadius,
  position: 'relative',
  overflow: 'hidden',
  '&:hover': {
    boxShadow: theme.shadows[8],
    '&::after': {
      transform: 'translateX(0%)',
    }
  },
  '&::after': {
    content: '""',
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    height: '3px',
    background: 'var(--gradient-primary)',
    transform: 'translateX(-100%)',
    transition: 'transform 0.3s ease',
  }
}));

// Status chip
export const StatusChip = styled(Chip)(({ theme, color }) => ({
  borderRadius: '50px',
  fontWeight: 500,
  textTransform: 'capitalize',
  fontSize: '0.75rem',
}));

// Floating action button with animation
export const FloatingActionButton = styled(Button)(({ theme }) => ({
  borderRadius: '50px',
  boxShadow: theme.shadows[4],
  padding: '12px 24px',
  transition: 'all 0.3s ease',
  fontWeight: 600,
  '&:hover': {
    transform: 'translateY(-4px)',
    boxShadow: theme.shadows[8],
  }
}));

// Circular icon button with pulse animation
export const PulseIconButton = styled(IconButton)(({ theme }) => ({
  position: 'relative',
  '&::after': {
    content: '""',
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    borderRadius: '50%',
    animation: 'pulse 1.5s ease-in-out infinite',
    border: `2px solid ${theme.palette.primary.main}`,
    opacity: 0.5,
  }
}));

// Grid container with animation for children
export const AnimatedGrid = styled(Grid)(({ theme }) => ({
  '& > .MuiGrid-item': {
    opacity: 0,
    animation: 'fadeIn 0.5s ease forwards',
    '&:nth-of-type(1)': { animationDelay: '0ms' },
    '&:nth-of-type(2)': { animationDelay: '100ms' },
    '&:nth-of-type(3)': { animationDelay: '200ms' },
    '&:nth-of-type(4)': { animationDelay: '300ms' },
    '&:nth-of-type(5)': { animationDelay: '400ms' },
    '&:nth-of-type(6)': { animationDelay: '500ms' },
  }
}));

// Dashboard section with title
export const DashboardSection = styled(Box)(({ theme }) => ({
  marginBottom: theme.spacing(4),
  position: 'relative',
  paddingBottom: theme.spacing(1),
  '&::after': {
    content: '""',
    position: 'absolute',
    bottom: 0,
    left: 0,
    width: 60,
    height: 4,
    background: 'var(--gradient-primary)',
    borderRadius: 2,
  }
})); 