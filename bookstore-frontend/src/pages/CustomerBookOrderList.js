import React, { useState, useEffect } from 'react';
import {
  Container,
  Grid,
  Card,
  CardContent,
  CardActions,
  Typography,
  Button,
  TextField,
  Box,
  CircularProgress,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { Add as AddIcon, Edit as EditIcon, Delete as DeleteIcon } from '@mui/icons-material';
import customerBookOrderService from '../services/customerBookOrderService';

const CustomerBookOrderList = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [openDialog, setOpenDialog] = useState(false);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [formData, setFormData] = useState({
    customerId: '',
    bookIds: [],
    orderDate: '',
    totalAmount: '',
    status: 'PENDING',
  });

  const navigate = useNavigate();

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      const response = await customerBookOrderService.getAllCustomerBookOrders();
      setOrders(response.data);
    } catch (err) {
      setError('Failed to fetch orders');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateOrder = async () => {
    try {
      await customerBookOrderService.createCustomerBookOrder(formData);
      setOpenDialog(false);
      setFormData({
        customerId: '',
        bookIds: [],
        orderDate: '',
        totalAmount: '',
        status: 'PENDING',
      });
      fetchOrders();
    } catch (err) {
      setError('Failed to create order');
    }
  };

  const handleUpdateOrder = async () => {
    try {
      await customerBookOrderService.updateCustomerBookOrder(selectedOrder.id, formData);
      setOpenDialog(false);
      setSelectedOrder(null);
      fetchOrders();
    } catch (err) {
      setError('Failed to update order');
    }
  };

  const handleDeleteOrder = async (id) => {
    if (window.confirm('Are you sure you want to delete this order?')) {
      try {
        await customerBookOrderService.deleteCustomerBookOrder(id);
        fetchOrders();
      } catch (err) {
        setError('Failed to delete order');
      }
    }
  };

  const openCreateOrderDialog = () => {
    setSelectedOrder(null);
    setFormData({
      customerId: '',
      bookIds: [],
      orderDate: '',
      totalAmount: '',
      status: 'PENDING',
    });
    setOpenDialog(true);
  };

  const openEditOrderDialog = (order) => {
    setSelectedOrder(order);
    setFormData({
      customerId: order.customerId,
      bookIds: order.bookIds,
      orderDate: order.orderDate,
      totalAmount: order.totalAmount,
      status: order.status,
    });
    setOpenDialog(true);
  };

  if (loading) return <CircularProgress />;
  if (error) return <Alert severity="error">{error}</Alert>;

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4">Customer Book Orders Management</Typography>
        <Button
          variant="contained"
          color="primary"
          startIcon={<AddIcon />}
          onClick={openCreateOrderDialog}
        >
          Add New Order
        </Button>
      </Box>

      <Box mb={3}>
        <TextField
          fullWidth
          variant="outlined"
          label="Search orders..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          sx={{ mb: 2 }}
        />
      </Box>

      <Grid container spacing={3}>
        {orders.map((order) => (
          <Grid item xs={12} sm={6} md={4} key={order.id}>
            <Card>
              <CardContent>
                <Typography variant="h6" component="h2">
                  Order #{order.id}
                </Typography>
                <Typography color="text.secondary" gutterBottom>
                  Customer ID: {order.customerId}
                </Typography>
                <Typography variant="body2">
                  Total: ${order.totalAmount}
                </Typography>
                <Typography variant="body2">
                  Status: {order.status}
                </Typography>
                <Typography variant="body2">
                  Date: {order.orderDate}
                </Typography>
              </CardContent>
              <CardActions>
                <Button
                  size="small"
                  color="primary"
                  startIcon={<EditIcon />}
                  onClick={() => openEditOrderDialog(order)}
                >
                  Edit
                </Button>
                <Button
                  size="small"
                  color="error"
                  startIcon={<DeleteIcon />}
                  onClick={() => handleDeleteOrder(order.id)}
                >
                  Delete
                </Button>
              </CardActions>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="md" fullWidth>
        <DialogTitle>{selectedOrder ? 'Edit Order' : 'Add New Order'}</DialogTitle>
        <DialogContent>
          <Box component="form" sx={{ mt: 2 }}>
            <TextField
              fullWidth
              margin="normal"
              label="Customer ID"
              value={formData.customerId}
              onChange={(e) => setFormData({ ...formData, customerId: e.target.value })}
            />
            <TextField
              fullWidth
              margin="normal"
              label="Book IDs"
              value={formData.bookIds.join(',')}
              onChange={(e) => setFormData({ ...formData, bookIds: e.target.value.split(',').map(id => parseInt(id)) })}
            />
            <TextField
              fullWidth
              margin="normal"
              label="Order Date"
              type="date"
              value={formData.orderDate}
              onChange={(e) => setFormData({ ...formData, orderDate: e.target.value })}
            />
            <TextField
              fullWidth
              margin="normal"
              label="Total Amount"
              type="number"
              value={formData.totalAmount}
              onChange={(e) => setFormData({ ...formData, totalAmount: e.target.value })}
            />
            <FormControl fullWidth margin="normal">
              <InputLabel>Status</InputLabel>
              <Select
                value={formData.status}
                onChange={(e) => setFormData({ ...formData, status: e.target.value })}
              >
                <MenuItem value="PENDING">PENDING</MenuItem>
                <MenuItem value="COMPLETED">COMPLETED</MenuItem>
                <MenuItem value="CANCELLED">CANCELLED</MenuItem>
              </Select>
            </FormControl>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
          <Button onClick={selectedOrder ? handleUpdateOrder : handleCreateOrder} variant="contained">
            {selectedOrder ? 'Update' : 'Create'}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default CustomerBookOrderList;
