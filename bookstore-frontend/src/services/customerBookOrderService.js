import api from './api';

const customerBookOrderService = {
  getAllCustomerBookOrders: () => api.get('/customer-book-orders'),
  getCustomerBookOrderById: (id) => api.get(`/customer-book-orders/${id}`),
  createCustomerBookOrder: (orderData) => api.post('/customer-book-orders', orderData),
  updateCustomerBookOrder: (id, orderData) => api.put(`/customer-book-orders/${id}`, orderData),
  deleteCustomerBookOrder: (id) => api.delete(`/customer-book-orders/${id}`),
  getOrdersByCustomer: (customerId) => api.get(`/customer-book-orders/customer/${customerId}`),
  getOrdersByBook: (bookId) => api.get(`/customer-book-orders/book/${bookId}`),
  updateOrderStatus: (id, status) => api.put(`/customer-book-orders/${id}/status`, { status }),
  getOrderItems: (orderId) => api.get(`/customer-book-orders/${orderId}/items`),
  addOrderItem: (orderId, itemData) => api.post(`/customer-book-orders/${orderId}/items`, itemData),
  updateOrderItem: (orderId, itemId, itemData) => api.put(`/customer-book-orders/${orderId}/items/${itemId}`, itemData),
  deleteOrderItem: (orderId, itemId) => api.delete(`/customer-book-orders/${orderId}/items/${itemId}`),
  getOrderTotal: (orderId) => api.get(`/customer-book-orders/${orderId}/total`),
  getOrderHistory: (customerId) => api.get(`/customer-book-orders/${customerId}/history`),
  getOrderSummary: (customerId) => api.get(`/customer-book-orders/${customerId}/summary`),
  getOrderDetails: (orderId) => api.get(`/customer-book-orders/${orderId}/details`),
};

export default customerBookOrderService;
