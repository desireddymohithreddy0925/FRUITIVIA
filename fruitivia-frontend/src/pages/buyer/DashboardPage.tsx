import { useEffect, useState } from 'react';
import { Package, FileText, CheckCircle, Clock } from 'lucide-react';
import { buyerApi } from '../../services/buyerApi';
import { useNavigate } from 'react-router-dom';

export default function DashboardPage() {
  const navigate = useNavigate();
  const [quotations, setQuotations] = useState<any[]>([]);
  const [orders, setOrders] = useState<any[]>([]);

  useEffect(() => {
    // Quick and dirty fetch for dashboard stats
    buyerApi.getQuotations(0, 100).then(data => setQuotations(data.content)).catch(() => {});
    buyerApi.getOrders(0, 100).then(data => setOrders(data.content)).catch(() => {});
  }, []);

  const pendingQuotations = quotations.filter(q => q.status === 'REQUESTED' || q.status === 'SENT').length;
  const activeOrders = orders.filter(o => o.status !== 'DELIVERED' && o.status !== 'CANCELLED').length;
  
  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-3xl font-heading font-bold">Buyer Dashboard</h2>
        <p className="text-muted-foreground mt-1">Overview of your activity and recent updates.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {/* Metric Cards */}
        <div className="bg-card p-6 rounded-xl border border-border shadow-sm flex items-center space-x-4">
          <div className="p-3 bg-blue-100 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400 rounded-lg">
            <FileText size={24} />
          </div>
          <div>
            <p className="text-sm text-muted-foreground font-medium">Pending Quotations</p>
            <h3 className="text-2xl font-bold font-heading">{pendingQuotations}</h3>
          </div>
        </div>

        <div className="bg-card p-6 rounded-xl border border-border shadow-sm flex items-center space-x-4">
          <div className="p-3 bg-green-100 dark:bg-green-900/30 text-green-600 dark:text-green-400 rounded-lg">
            <Package size={24} />
          </div>
          <div>
            <p className="text-sm text-muted-foreground font-medium">Active Orders</p>
            <h3 className="text-2xl font-bold font-heading">{activeOrders}</h3>
          </div>
        </div>

        <div className="bg-card p-6 rounded-xl border border-border shadow-sm flex items-center space-x-4">
          <div className="p-3 bg-purple-100 dark:bg-purple-900/30 text-purple-600 dark:text-purple-400 rounded-lg">
            <Clock size={24} />
          </div>
          <div>
            <p className="text-sm text-muted-foreground font-medium">Shipments in Transit</p>
            <h3 className="text-2xl font-bold font-heading">0</h3>
          </div>
        </div>

        <div className="bg-card p-6 rounded-xl border border-border shadow-sm flex items-center space-x-4">
          <div className="p-3 bg-orange-100 dark:bg-orange-900/30 text-orange-600 dark:text-orange-400 rounded-lg">
            <CheckCircle size={24} />
          </div>
          <div>
            <p className="text-sm text-muted-foreground font-medium">Completed Orders</p>
            <h3 className="text-2xl font-bold font-heading">
              {orders.filter(o => o.status === 'DELIVERED').length}
            </h3>
          </div>
        </div>
      </div>
      
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mt-8">
         <div className="bg-card p-6 rounded-xl border border-border shadow-sm">
            <h3 className="text-xl font-heading font-semibold mb-4">Recent Quotations</h3>
            {quotations.slice(0,5).length > 0 ? (
                <div className="space-y-4">
                  {quotations.slice(0,5).map(q => (
                      <div 
                        key={q.id} 
                        className="flex justify-between items-center p-3 hover:bg-muted/50 rounded-lg transition-colors border border-border/50 cursor-pointer"
                        onClick={() => navigate(`/buyer/quotations/${q.id}`)}
                      >
                        <div>
                           <p className="font-medium text-sm">Quotation #{q.id.substring(0,8)}</p>
                           <p className="text-xs text-muted-foreground">{new Date(q.createdAt).toLocaleDateString()}</p>
                        </div>
                        <span className="px-2.5 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-700 dark:bg-blue-900/40 dark:text-blue-300">
                          {q.status}
                        </span>
                      </div>
                  ))}
                </div>
            ) : (
                <p className="text-muted-foreground text-sm">No recent quotations.</p>
            )}
         </div>

         <div className="bg-card p-6 rounded-xl border border-border shadow-sm">
            <h3 className="text-xl font-heading font-semibold mb-4">Active Orders</h3>
            {orders.filter(o => o.status !== 'DELIVERED' && o.status !== 'CANCELLED').slice(0,5).length > 0 ? (
                <div className="space-y-4">
                  {orders.filter(o => o.status !== 'DELIVERED' && o.status !== 'CANCELLED').slice(0,5).map(o => (
                      <div 
                        key={o.id} 
                        className="flex justify-between items-center p-3 hover:bg-muted/50 rounded-lg transition-colors border border-border/50 cursor-pointer"
                        onClick={() => navigate(`/buyer/orders/${o.id}`)}
                      >
                        <div>
                           <p className="font-medium text-sm">Order #{o.id.substring(0,8)}</p>
                           <p className="text-xs text-muted-foreground">{new Date(o.createdAt).toLocaleDateString()}</p>
                        </div>
                        <span className="px-2.5 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-700 dark:bg-green-900/40 dark:text-green-300">
                          {o.status}
                        </span>
                      </div>
                  ))}
                </div>
            ) : (
                <p className="text-muted-foreground text-sm">No active orders.</p>
            )}
         </div>
      </div>
    </div>
  )
}
