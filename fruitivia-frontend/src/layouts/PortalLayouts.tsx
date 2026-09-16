import { 
  LayoutDashboard, Users, Tractor, Globe, Settings, ClipboardCheck, PackageSearch, 
  Truck, FileText, CreditCard, Bell, Ship, Building, BookOpen, Warehouse, 
  Thermometer, Archive, BarChart, History, Box, ShieldCheck, CheckSquare, AlertTriangle, Clock
} from "lucide-react"
import { AppLayout } from "../components/layout/AppLayout"

// --- Public Layout ---
export function PublicLayout() {
  return (
    <div className="min-h-screen bg-background font-sans text-foreground">
      <header className="h-16 border-b border-border bg-card flex items-center px-8 shadow-sm">
        <h1 className="text-2xl font-heading font-bold text-primary">Fruitivia</h1>
      </header>
      <main className="p-8">
        <h2 className="text-4xl font-heading font-bold">Welcome to Fruitivia</h2>
        <p className="mt-4 text-muted-foreground max-w-2xl text-lg">
          The enterprise B2B platform for premium agricultural commerce.
        </p>
      </main>
    </div>
  )
}

// --- Admin Layout ---
const adminNav = [
  { name: 'Dashboard', path: '/admin', icon: LayoutDashboard },
  { name: 'Users', path: '/admin/users', icon: Users },
  { name: 'Suppliers', path: '/admin/suppliers', icon: Tractor },
  { name: 'Buyers', path: '/admin/buyers', icon: Users },
  { name: 'Catalog', path: '/admin/catalog', icon: Globe },
  { name: 'Procurement', path: '/admin/procurement', icon: FileText },
  { name: 'QC', path: '/admin/qc', icon: ShieldCheck },
  { name: 'Batches', path: '/admin/batches', icon: Box },
  { name: 'Warehouses', path: '/admin/warehouses', icon: Warehouse },
  { name: 'Inventory', path: '/admin/inventory', icon: PackageSearch },
  { name: 'Cold Chain', path: '/admin/cold-chain', icon: Thermometer },
  { name: 'Orders', path: '/admin/orders', icon: FileText },
  { name: 'Payments', path: '/admin/payments', icon: CreditCard },
  { name: 'Packaging', path: '/admin/packaging', icon: Archive },
  { name: 'Documents', path: '/admin/documents', icon: BookOpen },
  { name: 'Shipments', path: '/admin/shipments', icon: Ship },
  { name: 'Customs', path: '/admin/customs', icon: ClipboardCheck },
  { name: 'Audit', path: '/admin/audit', icon: History },
  { name: 'Analytics', path: '/admin/analytics', icon: BarChart },
]

export function AdminLayout() {
  return <AppLayout title="Admin" navItems={adminNav} />
}

// --- Buyer Layout ---
const buyerNav = [
  { name: 'Dashboard', path: '/buyer', icon: LayoutDashboard },
  { name: 'Company Profile', path: '/buyer/profile', icon: Building },
  { name: 'Fruit Catalog', path: '/buyer/catalog', icon: Globe },
  { name: 'Quotations', path: '/buyer/quotations', icon: FileText },
  { name: 'Orders', path: '/buyer/orders', icon: Tractor },
  { name: 'Payments', path: '/buyer/payments', icon: CreditCard },
  { name: 'Shipments', path: '/buyer/shipments', icon: Ship },
  { name: 'Customs', path: '/buyer/customs', icon: ClipboardCheck },
  { name: 'Documents', path: '/buyer/documents', icon: BookOpen },
  { name: 'Notifications', path: '/buyer/notifications', icon: Bell },
]

export function BuyerLayout() {
  return <AppLayout title="Buyer" navItems={buyerNav} />
}

// --- Engineer Layout ---
const engineerNav = [
  { name: 'Dashboard', path: '/engineer', icon: LayoutDashboard },
  { name: 'Suppliers', path: '/engineer/suppliers', icon: Tractor },
  { name: 'Procurement', path: '/engineer/procurement', icon: FileText },
  { name: 'Batches', path: '/engineer/batches', icon: Box },
  { name: 'Warehouses', path: '/engineer/warehouses', icon: Warehouse },
  { name: 'Inventory', path: '/engineer/inventory', icon: PackageSearch },
  { name: 'Operations', path: '/engineer/operations', icon: Settings },
]

export function EngineerLayout() {
  return <AppLayout title="Engineer" navItems={engineerNav} />
}

// --- QC Inspector Layout ---
const qcNav = [
  { name: 'Dashboard', path: '/qc', icon: LayoutDashboard },
  { name: 'Inspections', path: '/qc/inspections', icon: ClipboardCheck },
  { name: 'Grading', path: '/qc/grading', icon: CheckSquare },
  { name: 'Failed Batches', path: '/qc/failed-batches', icon: AlertTriangle },
  { name: 'Reinspection', path: '/qc/reinspection', icon: History },
  { name: 'Quality History', path: '/qc/history', icon: BookOpen },
]

export function QcLayout() {
  return <AppLayout title="QC" navItems={qcNav} />
}

// --- Logistics Layout ---
const logisticsNav = [
  { name: 'Dashboard', path: '/logistics', icon: LayoutDashboard },
  { name: 'Warehouse', path: '/logistics/warehouse', icon: Warehouse },
  { name: 'Packaging', path: '/logistics/packaging', icon: Archive },
  { name: 'Shipments', path: '/logistics/shipments', icon: Ship },
  { name: 'Customs', path: '/logistics/customs', icon: ClipboardCheck },
  { name: 'Delayed Shipments', path: '/logistics/delayed-shipments', icon: Clock },
  { name: 'Delivery', path: '/logistics/delivery', icon: Truck },
]

export function LogisticsLayout() {
  return <AppLayout title="Logistics" navItems={logisticsNav} />
}
