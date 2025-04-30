import React, { useState } from 'react';
import { 
  Box, 
  Table, 
  TableBody, 
  TableCell, 
  TableContainer, 
  TableHead, 
  TableRow, 
  Paper, 
  IconButton, 
  Chip, 
  TablePagination,
  Typography,
  Button
} from '@mui/material';
import { Edit as EditIcon, Delete as DeleteIcon, Visibility as ViewIcon } from '@mui/icons-material';
import { useRouter } from 'next/router';
import { useCampaigns } from '../api/useCampaigns';
import { Campaign } from '../types/models';

export const CampaignList = () => {
  const router = useRouter();
  const { campaigns, loading, error, refetch } = useCampaigns();
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  const handleChangePage = (event: unknown, newPage: number) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event: React.ChangeEvent<HTMLInputElement>) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleViewCampaign = (id: number) => {
    router.push(`/campaigns/${id}`);
  };

  const handleEditCampaign = (id: number) => {
    router.push(`/campaigns/edit/${id}`);
  };

  const handleDeleteCampaign = (id: number) => {
    // In a real app, this would make an API call
    if (window.confirm('Are you sure you want to delete this campaign?')) {
      console.log('Deleting campaign', id);
      // After successful deletion:
      // refetch();
    }
  };

  const getStatusChipColor = (status: string) => {
    switch (status) {
      case 'ACTIVE':
        return 'success';
      case 'PAUSED':
        return 'warning';
      case 'COMPLETED':
        return 'info';
      case 'DRAFT':
        return 'default';
      default:
        return 'default';
    }
  };

  if (loading) {
    return <Typography>Loading campaigns...</Typography>;
  }

  if (error) {
    return <Typography color="error">Error loading campaigns: {error.message}</Typography>;
  }

  if (!campaigns || campaigns.length === 0) {
    return (
      <Box my={4} textAlign="center">
        <Typography variant="h6" gutterBottom>No campaigns found</Typography>
        <Button 
          variant="contained" 
          color="primary" 
          onClick={() => router.push('/campaigns/new')}
        >
          Create New Campaign
        </Button>
      </Box>
    );
  }

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
        <Typography variant="h6">Your Campaigns</Typography>
        <Button 
          variant="contained" 
          color="primary" 
          onClick={() => router.push('/campaigns/new')}
        >
          Create New Campaign
        </Button>
      </Box>
      
      <TableContainer component={Paper}>
        <Table aria-label="campaigns table">
          <TableHead>
            <TableRow>
              <TableCell>Name</TableCell>
              <TableCell>Status</TableCell>
              <TableCell align="right">Budget</TableCell>
              <TableCell align="right">Spend</TableCell>
              <TableCell align="right">CTR</TableCell>
              <TableCell align="right">CVR</TableCell>
              <TableCell align="center">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {campaigns
              .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
              .map((campaign: Campaign) => (
                <TableRow key={campaign.id}>
                  <TableCell component="th" scope="row">
                    {campaign.name}
                  </TableCell>
                  <TableCell>
                    <Chip 
                      label={campaign.status} 
                      color={getStatusChipColor(campaign.status) as any}
                      size="small"
                    />
                  </TableCell>
                  <TableCell align="right">
                    ${campaign.totalBudget?.toLocaleString()}
                  </TableCell>
                  <TableCell align="right">
                    ${campaign.metrics?.spend?.toLocaleString() || 0}
                  </TableCell>
                  <TableCell align="right">
                    {campaign.metrics?.ctr ? `${(campaign.metrics.ctr * 100).toFixed(2)}%` : '0.00%'}
                  </TableCell>
                  <TableCell align="right">
                    {campaign.metrics?.cvr ? `${(campaign.metrics.cvr * 100).toFixed(2)}%` : '0.00%'}
                  </TableCell>
                  <TableCell align="center">
                    <IconButton 
                      aria-label="view" 
                      onClick={() => handleViewCampaign(campaign.id)}
                      size="small"
                    >
                      <ViewIcon />
                    </IconButton>
                    <IconButton 
                      aria-label="edit" 
                      onClick={() => handleEditCampaign(campaign.id)}
                      size="small"
                    >
                      <EditIcon />
                    </IconButton>
                    <IconButton 
                      aria-label="delete" 
                      onClick={() => handleDeleteCampaign(campaign.id)}
                      size="small"
                    >
                      <DeleteIcon />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
          </TableBody>
        </Table>
        <TablePagination
          rowsPerPageOptions={[5, 10, 25]}
          component="div"
          count={campaigns.length}
          rowsPerPage={rowsPerPage}
          page={page}
          onPageChange={handleChangePage}
          onRowsPerPageChange={handleChangeRowsPerPage}
        />
 