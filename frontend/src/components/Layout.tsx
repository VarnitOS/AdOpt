import { ReactNode, useState, useEffect } from 'react';
import {
  AppBar,
  Box,
  Toolbar,
  Typography,
  Button,
  IconButton,
  Drawer,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Divider,
  Avatar,
  Tooltip,
  Paper,
  useTheme,
  useMediaQuery,
  Badge,
  alpha,
} from '@mui/material';
import {
  Menu as MenuIcon,
  Dashboard as DashboardIcon,
  Campaign as CampaignIcon,
  Analytics as AnalyticsIcon,
  Settings as SettingsIcon,
  Notifications as NotificationsIcon,
  Search as SearchIcon,
  Help as HelpIcon,
  ChevronLeft as ChevronLeftIcon,
  Person as PersonIcon,
  Logout as LogoutIcon,
} from '@mui/icons-material';
import Link from 'next/link';
import { useRouter } from 'next/router';

interface LayoutProps {
  children: ReactNode;
}

export const Layout = ({ children }: LayoutProps) => {
  const [drawerOpen, setDrawerOpen] = useState(false);
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const router = useRouter();
  
  // Close drawer on mobile when route changes
  useEffect(() => {
    if (isMobile && drawerOpen) {
      setDrawerOpen(false);
    }
  }, [router.pathname, isMobile]);

  const toggleDrawer = () => {
    setDrawerOpen(!drawerOpen);
  };

  const menuItems = [
    { text: 'Dashboard', icon: <DashboardIcon />, href: '/' },
    { text: 'Campaigns', icon: <CampaignIcon />, href: '/campaigns' },
    { text: 'Analytics', icon: <AnalyticsIcon />, href: '/analytics' },
    { text: 'Settings', icon: <SettingsIcon />, href: '/settings' },
  ];

  const drawer = (
    <Box 
      sx={{ 
        width: 260,
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
      }} 
      role="presentation"
    >
      <Box 
        sx={{ 
          p: 2, 
          display: 'flex', 
          alignItems: 'center', 
          justifyContent: 'space-between',
          borderBottom: `1px solid ${theme.palette.divider}`,
        }}
      >
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <Typography 
            variant="h6" 
            component="div" 
            sx={{ 
              fontWeight: 700,
              background: 'var(--gradient-primary)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
            }}
          >
            AdOpt
          </Typography>
        </Box>
        {isMobile && (
          <IconButton onClick={toggleDrawer} size="small">
            <ChevronLeftIcon />
          </IconButton>
        )}
      </Box>
      
      <Box sx={{ flexGrow: 1, overflow: 'auto', p: 2 }}>
        <List>
          {menuItems.map((item) => {
            const isActive = router.pathname === item.href;
            return (
              <Link key={item.text} href={item.href} passHref>
                <ListItem 
                  button 
                  component="a"
                  sx={{
                    mb: 1,
                    borderRadius: 2,
                    backgroundColor: isActive ? 
                      alpha(theme.palette.primary.main, 0.1) : 'transparent',
                    color: isActive ? theme.palette.primary.main : theme.palette.text.primary,
                    '&:hover': {
                      backgroundColor: isActive ? 
                        alpha(theme.palette.primary.main, 0.15) : 
                        alpha(theme.palette.primary.main, 0.05),
                    },
                    transition: 'all 0.2s ease-in-out',
                  }}
                >
                  <ListItemIcon 
                    sx={{ 
                      color: isActive ? theme.palette.primary.main : 'inherit',
                      minWidth: 40,
                    }}
                  >
                    {item.icon}
                  </ListItemIcon>
                  <ListItemText 
                    primary={item.text} 
                    primaryTypographyProps={{ 
                      fontWeight: isActive ? 600 : 400,
                    }}
                  />
                </ListItem>
              </Link>
            );
          })}
        </List>
      </Box>
      
      <Box p={2} borderTop={`1px solid ${theme.palette.divider}`}>
        <Box display="flex" alignItems="center" mb={1}>
          <Avatar 
            sx={{ 
              width: 36, 
              height: 36, 
              mr: 1.5,
              background: 'var(--gradient-primary)'
            }}
          >
            <PersonIcon fontSize="small" />
          </Avatar>
          <Box>
            <Typography variant="subtitle2" fontWeight={600}>
              John Doe
            </Typography>
            <Typography variant="caption" color="textSecondary">
              Marketing Manager
            </Typography>
          </Box>
        </Box>
        <Button 
          fullWidth 
          variant="outlined" 
          size="small" 
          startIcon={<LogoutIcon />}
          sx={{ mt: 1, borderRadius: 2 }}
        >
          Sign Out
        </Button>
      </Box>
    </Box>
  );

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
      <AppBar 
        position="sticky" 
        elevation={0}
        sx={{ 
          background: '#ffffff',
          borderBottom: `1px solid ${theme.palette.divider}`,
          zIndex: (theme) => theme.zIndex.drawer + 1,
        }}
      >
        <Toolbar>
          <IconButton
            edge="start"
            aria-label="menu"
            onClick={toggleDrawer}
            sx={{ 
              mr: 2,
              color: theme.palette.text.primary,
            }}
          >
            <MenuIcon />
          </IconButton>
          
          <Typography 
            variant="h6" 
            component="div" 
            sx={{ 
              display: { xs: 'none', md: 'block' },
              fontWeight: 700,
              color: theme.palette.text.primary,
            }}
          >
            AdOpt
          </Typography>
          
          <Box sx={{ flexGrow: 1 }} />
          
          {/* Search, notifications, help and user actions */}
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <Tooltip title="Search">
              <IconButton 
                sx={{ 
                  mx: 0.5,
                  backgroundColor: alpha(theme.palette.primary.main, 0.05),
                  '&:hover': { backgroundColor: alpha(theme.palette.primary.main, 0.1) },
                }}
              >
                <SearchIcon fontSize="small" />
              </IconButton>
            </Tooltip>
            
            <Tooltip title="Notifications">
              <IconButton 
                sx={{ 
                  mx: 0.5,
                  backgroundColor: alpha(theme.palette.primary.main, 0.05),
                  '&:hover': { backgroundColor: alpha(theme.palette.primary.main, 0.1) },
                }}
              >
                <Badge badgeContent={3} color="primary">
                  <NotificationsIcon fontSize="small" />
                </Badge>
              </IconButton>
            </Tooltip>
            
            <Tooltip title="Help">
              <IconButton 
                sx={{ 
                  ml: 0.5, 
                  mr: 1,
                  backgroundColor: alpha(theme.palette.primary.main, 0.05),
                  '&:hover': { backgroundColor: alpha(theme.palette.primary.main, 0.1) },
                }}
              >
                <HelpIcon fontSize="small" />
              </IconButton>
            </Tooltip>
            
            <Tooltip title="My Profile">
              <Avatar 
                sx={{ 
                  width: 34, 
                  height: 34,
                  cursor: 'pointer',
                  background: 'var(--gradient-primary)'
                }}
              >
                <PersonIcon fontSize="small" />
              </Avatar>
            </Tooltip>
          </Box>
        </Toolbar>
      </AppBar>
      
      {/* Drawer for navigation */}
      <Drawer 
        variant={isMobile ? "temporary" : "permanent"}
        open={isMobile ? drawerOpen : true}
        onClose={toggleDrawer}
        sx={{
          width: 260,
          flexShrink: 0,
          '& .MuiDrawer-paper': {
            width: 260,
            boxSizing: 'border-box',
            border: 'none',
            boxShadow: isMobile ? 4 : 'none',
          },
          display: { xs: drawerOpen ? 'block' : 'none', md: 'block' },
        }}
      >
        {drawer}
      </Drawer>
      
      {/* Main content */}
      <Box 
        component="main" 
        sx={{ 
          flexGrow: 1,
          p: 0,
          ml: { xs: 0, md: '260px' },
          backgroundColor: theme.palette.background.default,
          transition: 'margin 0.2s ease-in-out',
        }}
      >
        {children}
      </Box>
    </Box>
  );
}; 