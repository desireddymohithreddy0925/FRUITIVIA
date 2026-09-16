import { createApiHooks } from './factory';

// Create API hooks for all 22 required domain modules using standard REST conventions
// They utilize TanStack Query (useQuery, useMutation) under the hood with cache invalidation

// Auth & Users
export const authApi = createApiHooks<any>('/api/v1/auth', 'auth');
export const usersApi = createApiHooks<any>('/api/v1/users', 'users');

// Core Entities
export const suppliersApi = createApiHooks<any>('/api/v1/suppliers', 'suppliers');
export const buyersApi = createApiHooks<any>('/api/v1/buyers', 'buyers');
export const fruitsApi = createApiHooks<any>('/api/v1/fruits', 'fruits');
export const varietiesApi = createApiHooks<any>('/api/v1/varieties', 'varieties');

// Operations
export const procurementApi = createApiHooks<any>('/api/v1/procurement', 'procurement');
export const qualityApi = createApiHooks<any>('/api/v1/quality', 'quality');
export const batchesApi = createApiHooks<any>('/api/v1/batches', 'batches');

// Storage & Inventory
export const warehousesApi = createApiHooks<any>('/api/v1/warehouses', 'warehouses');
export const inventoryApi = createApiHooks<any>('/api/v1/inventory', 'inventory');
export const coldchainApi = createApiHooks<any>('/api/v1/coldchain', 'coldchain');

// B2B Sales
export const quotationsApi = createApiHooks<any>('/api/v1/quotations', 'quotations');
export const ordersApi = createApiHooks<any>('/api/v1/orders', 'orders');
export const paymentsApi = createApiHooks<any>('/api/v1/payments', 'payments');

// Logistics
export const packagingApi = createApiHooks<any>('/api/v1/packaging', 'packaging');
export const shipmentsApi = createApiHooks<any>('/api/v1/shipments', 'shipments');
export const customsApi = createApiHooks<any>('/api/v1/customs', 'customs');

// Supporting
export const documentsApi = createApiHooks<any>('/api/v1/documents', 'documents');
export const notificationsApi = createApiHooks<any>('/api/v1/notifications', 'notifications');
export const auditApi = createApiHooks<any>('/api/v1/audit', 'audit');
export const analyticsApi = createApiHooks<any>('/api/v1/analytics', 'analytics');
