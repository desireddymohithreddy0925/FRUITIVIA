import api from './api';

export interface Page<T> {
  content: T[];
  pageable: any;
  last: boolean;
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  sort: any;
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}

export const buyerApi = {
  // Profile
  getMyProfile: () => api.get('/buyers/me').then(res => res.data),
  updateMyProfile: (data: any) => api.put('/buyers/me', data).then(res => res.data),

  // Catalog
  getFruits: (params?: any) => api.get<Page<any>>('/catalog/fruits', { params }).then(res => res.data),
  getFruitDetails: (id: string) => api.get(`/catalog/fruits/${id}`).then(res => res.data),
  
  // Quotations
  getQuotations: (page = 0, size = 10, status?: string) => 
    api.get<Page<any>>('/buyer/quotations', { params: { page, size, status } }).then(res => res.data),
  getQuotationDetails: (id: string) => api.get(`/buyer/quotations/${id}`).then(res => res.data),
  requestQuotation: (data: any) => api.post('/buyer/quotations', data).then(res => res.data),
  acceptQuotation: (id: string) => api.post(`/buyer/quotations/${id}/accept`).then(res => res.data),
  rejectQuotation: (id: string) => api.post(`/buyer/quotations/${id}/reject`).then(res => res.data),

  // Orders
  getOrders: (page = 0, size = 10, status?: string) => 
    api.get<Page<any>>('/buyer/orders', { params: { page, size, status } }).then(res => res.data),
  getOrderDetails: (id: string) => api.get(`/buyer/orders/${id}`).then(res => res.data),
  
  // Shipments (Track by Order ID or Shipment ID depending on backend)
  getOrderShipments: (orderId: string) => api.get(`/buyer/orders/${orderId}/shipments`).then(res => res.data),
};
