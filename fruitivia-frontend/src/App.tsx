import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import { ProtectedRoute } from './components/auth/ProtectedRoute'
import { RoleRoute } from './components/auth/RoleRoute'
import { LoginPage } from './pages/public/LoginPage'
import { RegisterPage } from './pages/public/RegisterPage'
import { UnauthorizedPage } from './pages/public/UnauthorizedPage'
import {
  PublicLayout,
  AdminLayout,
  BuyerLayout,
  EngineerLayout,
  QcLayout,
  LogisticsLayout,
} from './layouts/PortalLayouts'

// Dashboards
import AdminDashboard from './pages/admin/AdminDashboard'
import EngineerDashboard from './pages/engineer/EngineerDashboard'
import QcDashboard from './pages/qc/QcDashboard'
import LogisticsDashboard from './pages/logistics/LogisticsDashboard'

// Buyer Pages
import DashboardPage from './pages/buyer/DashboardPage'
import CompanyProfilePage from './pages/buyer/CompanyProfilePage'
import CatalogPage from './pages/buyer/CatalogPage'
import FruitDetailsPage from './pages/buyer/FruitDetailsPage'
import QuotationsPage from './pages/buyer/QuotationsPage'
import RequestQuotationPage from './pages/buyer/RequestQuotationPage'
import QuotationDetailsPage from './pages/buyer/QuotationDetailsPage'
import OrdersPage from './pages/buyer/OrdersPage'
import OrderDetailsPage from './pages/buyer/OrderDetailsPage'
import PaymentsPage from './pages/buyer/PaymentsPage'
import ShipmentsPage from './pages/buyer/ShipmentsPage'
import CustomsPage from './pages/buyer/CustomsPage'
import DocumentsPage from './pages/buyer/DocumentsPage'
import NotificationsPage from './pages/buyer/NotificationsPage'

function PlaceholderPage({ name }: { name: string }) {
  return (
    <div className="bg-card p-6 rounded-xl border border-border shadow-sm">
      <h3 className="text-xl font-heading font-semibold text-primary">{name}</h3>
      <p className="mt-2 text-muted-foreground font-sans">
        This is a placeholder page for the {name} feature.
      </p>
    </div>
  )
}

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<PublicLayout />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/unauthorized" element={<UnauthorizedPage />} />

          {/* Protected Routes */}
          <Route element={<ProtectedRoute />}>
            
            {/* Admin Routes */}
            <Route element={<RoleRoute allowedRoles={['ADMIN']} />}>
              <Route path="/admin" element={<AdminLayout />}>
                <Route index element={<AdminDashboard />} />
                <Route path="users" element={<PlaceholderPage name="System Users" />} />
                <Route path="suppliers" element={<PlaceholderPage name="Suppliers" />} />
                <Route path="buyers" element={<PlaceholderPage name="Buyers" />} />
                <Route path="catalog" element={<PlaceholderPage name="Catalog" />} />
                <Route path="procurement" element={<PlaceholderPage name="Procurement" />} />
                <Route path="qc" element={<PlaceholderPage name="QC" />} />
                <Route path="batches" element={<PlaceholderPage name="Batches" />} />
                <Route path="warehouses" element={<PlaceholderPage name="Warehouses" />} />
                <Route path="inventory" element={<PlaceholderPage name="Inventory" />} />
                <Route path="cold-chain" element={<PlaceholderPage name="Cold Chain" />} />
                <Route path="orders" element={<PlaceholderPage name="Orders" />} />
                <Route path="payments" element={<PlaceholderPage name="Payments" />} />
                <Route path="packaging" element={<PlaceholderPage name="Packaging" />} />
                <Route path="documents" element={<PlaceholderPage name="Documents" />} />
                <Route path="shipments" element={<PlaceholderPage name="Shipments" />} />
                <Route path="customs" element={<PlaceholderPage name="Customs" />} />
                <Route path="audit" element={<PlaceholderPage name="Audit Log" />} />
                <Route path="analytics" element={<PlaceholderPage name="Analytics" />} />
              </Route>
            </Route>

            {/* Buyer Routes */}
            <Route element={<RoleRoute allowedRoles={['BUYER']} />}>
              <Route path="/buyer" element={<BuyerLayout />}>
                <Route index element={<DashboardPage />} />
                <Route path="profile" element={<CompanyProfilePage />} />
                <Route path="catalog" element={<CatalogPage />} />
                <Route path="catalog/:id" element={<FruitDetailsPage />} />
                <Route path="quotations" element={<QuotationsPage />} />
                <Route path="quotations/request" element={<RequestQuotationPage />} />
                <Route path="quotations/:id" element={<QuotationDetailsPage />} />
                <Route path="orders" element={<OrdersPage />} />
                <Route path="orders/:id" element={<OrderDetailsPage />} />
                <Route path="payments" element={<PaymentsPage />} />
                <Route path="shipments" element={<ShipmentsPage />} />
                <Route path="customs" element={<CustomsPage />} />
                <Route path="documents" element={<DocumentsPage />} />
                <Route path="notifications" element={<NotificationsPage />} />
              </Route>
            </Route>

            {/* Engineer Routes */}
            <Route element={<RoleRoute allowedRoles={['ENGINEER']} />}>
              <Route path="/engineer" element={<EngineerLayout />}>
                <Route index element={<EngineerDashboard />} />
                <Route path="suppliers" element={<PlaceholderPage name="Suppliers" />} />
                <Route path="procurement" element={<PlaceholderPage name="Procurement" />} />
                <Route path="batches" element={<PlaceholderPage name="Batches" />} />
                <Route path="warehouses" element={<PlaceholderPage name="Warehouses" />} />
                <Route path="inventory" element={<PlaceholderPage name="Inventory" />} />
                <Route path="operations" element={<PlaceholderPage name="Operations" />} />
              </Route>
            </Route>

            {/* QC Inspector Routes */}
            <Route element={<RoleRoute allowedRoles={['QC_INSPECTOR']} />}>
              <Route path="/qc" element={<QcLayout />}>
                <Route index element={<QcDashboard />} />
                <Route path="inspections" element={<PlaceholderPage name="Inspections" />} />
                <Route path="grading" element={<PlaceholderPage name="Grading" />} />
                <Route path="failed-batches" element={<PlaceholderPage name="Failed Batches" />} />
                <Route path="reinspection" element={<PlaceholderPage name="Reinspection" />} />
                <Route path="history" element={<PlaceholderPage name="Quality History" />} />
              </Route>
            </Route>

            {/* Logistics Routes */}
            <Route element={<RoleRoute allowedRoles={['LOGISTICS']} />}>
              <Route path="/logistics" element={<LogisticsLayout />}>
                <Route index element={<LogisticsDashboard />} />
                <Route path="warehouse" element={<PlaceholderPage name="Warehouse" />} />
                <Route path="packaging" element={<PlaceholderPage name="Packaging" />} />
                <Route path="shipments" element={<PlaceholderPage name="Shipments" />} />
                <Route path="customs" element={<PlaceholderPage name="Customs" />} />
                <Route path="delayed-shipments" element={<PlaceholderPage name="Delayed Shipments" />} />
                <Route path="delivery" element={<PlaceholderPage name="Delivery" />} />
              </Route>
            </Route>

          </Route>

          {/* Fallback */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}

export default App
