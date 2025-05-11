import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    primary: {
      main: '#2563eb', // Vibrant blue
      light: '#60a5fa',
      dark: '#1e40af',
      contrastText: '#ffffff',
    },
    secondary: {
      main: '#7e22ce', // Purple
      light: '#a855f7',
      dark: '#5b21b6',
      contrastText: '#ffffff',
    },
    error: {
      main: '#ef4444',
      light: '#f87171',
      dark: '#b91c1c',
    },
    warning: {
      main: '#f59e0b',
      light: '#fbbf24',
      dark: '#d97706',
    },
    info: {
      main: '#3b82f6',
      light: '#60a5fa',
      dark: '#2563eb',
    },
    success: {
      main: '#10b981',
      light: '#34d399',
      dark: '#059669',
    },
    background: {
      default: '#f9fafb',
      paper: '#ffffff',
    },
    text: {
      primary: '#1f2937',
      secondary: '#6b7280',
      disabled: '#9ca3af',
    },
  },
  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
    h1: {
      fontWeight: 700,
    },
    h2: {
      fontWeight: 700,
    },
    h3: {
      fontWeight: 600,
    },
    h4: {
      fontWeight: 600,
    },
    h5: {
      fontWeight: 600,
    },
    h6: {
      fontWeight: 600,
    },
    button: {
      fontWeight: 500,
      textTransform: 'none',
    },
  },
  shape: {
    borderRadius: 12,
  },
  shadows: [
    'none',
    '0px 2px 4px rgba(0, 0, 0, 0.05)',
    '0px 4px 6px rgba(0, 0, 0, 0.06)',
    '0px 6px 8px rgba(0, 0, 0, 0.07)',
    '0px 8px 12px rgba(0, 0, 0, 0.08)',
    '0px 10px 15px rgba(0, 0, 0, 0.09)',
    '0px 12px 20px rgba(0, 0, 0, 0.1)',
    '0px 14px 25px rgba(0, 0, 0, 0.11)',
    '0px 16px 30px rgba(0, 0, 0, 0.12)',
    '0px 18px 35px rgba(0, 0, 0, 0.13)',
    '0px 20px 40px rgba(0, 0, 0, 0.14)',
    '0px 22px 45px rgba(0, 0, 0, 0.15)',
    '0px 24px 50px rgba(0, 0, 0, 0.16)',
    '0px 26px 55px rgba(0, 0, 0, 0.17)',
    '0px 28px 60px rgba(0, 0, 0, 0.18)',
    '0px 30px 65px rgba(0, 0, 0, 0.19)',
    '0px 32px 70px rgba(0, 0, 0, 0.2)',
    '0px 34px 75px rgba(0, 0, 0, 0.21)',
    '0px 36px 80px rgba(0, 0, 0, 0.22)',
    '0px 38px 85px rgba(0, 0, 0, 0.23)',
    '0px 40px 90px rgba(0, 0, 0, 0.24)',
    '0px 42px 95px rgba(0, 0, 0, 0.25)',
    '0px 44px 100px rgba(0, 0, 0, 0.26)',
    '0px 46px 105px rgba(0, 0, 0, 0.27)',
    '0px 48px 110px rgba(0, 0, 0, 0.28)',
  ],
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          padding: '10px 16px',
          boxShadow: '0px 4px 6px rgba(0, 0, 0, 0.05)',
          transition: 'all 0.2s ease-in-out',
          '&:hover': {
            transform: 'translateY(-2px)',
            boxShadow: '0px 6px 8px rgba(0, 0, 0, 0.1)',
          },
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          boxShadow: '0px 8px 25px rgba(0, 0, 0, 0.08)',
          transition: 'all 0.3s ease-in-out',
          '&:hover': {
            boxShadow: '0px 12px 30px rgba(0, 0, 0, 0.12)',
          },
        },
      },
    },
    MuiCardHeader: {
      styleOverrides: {
        root: {
          padding: '20px 24px 0px 24px',
        },
        title: {
          fontSize: '1.25rem',
          fontWeight: 600,
        },
      },
    },
    MuiCardContent: {
      styleOverrides: {
        root: {
          padding: '12px 24px 24px 24px',
          '&:last-child': {
            paddingBottom: 24,
          },
        },
      },
    },
  },
});

export default theme; 