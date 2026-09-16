import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { buyerApi } from '../../services/buyerApi';
import { Button } from '../../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
import { Badge } from '../../components/ui/badge';
import { Loader2, ArrowLeft, Package, FileText, Anchor, Truck, CheckCircle } from 'lucide-react';

export default function OrderDetailsPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [order, setOrder] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchOrder();
  }, [id]);

  const fetchOrder = async () => {
    try {
      if (!id) return;
      const data = await buyerApi.getOrderDetails(id);
      setOrder(data);
    } catch (err) {
      console.error(err);
      alert('Failed to load order details');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="flex items-center justify-center h-64"><Loader2 className="w-8 h-8 animate-spin" /></div>;
  }

  if (!order) {
    return (
      <div className="text-center py-12">
        <h2 className="text-2xl font-bold">Order not found</h2>
        <Button variant="link" onClick={() => navigate('/buyer/orders')}>Back to Orders</Button>
      </div>
    );
  }

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'PENDING_PAYMENT': return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      case 'CONFIRMED': return 'bg-blue-100 text-blue-800 border-blue-200';
      case 'PROCESSING': return 'bg-purple-100 text-purple-800 border-purple-200';
      case 'SHIPPED': return 'bg-indigo-100 text-indigo-800 border-indigo-200';
      case 'DELIVERED': return 'bg-green-100 text-green-800 border-green-200';
      case 'CANCELLED': return 'bg-red-100 text-red-800 border-red-200';
      default: return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  };

  // Timeline representation (Order -> Packaging -> Documents -> Shipment -> Customs -> Delivery)
  // Mapping order/shipment status to timeline steps
  const timelineSteps = [
    { label: 'Order', icon: Package, status: 'completed' }, // Always completed if order exists
    { label: 'Packaging', icon: Package, status: ['PROCESSING', 'SHIPPED', 'DELIVERED'].includes(order.status) ? 'completed' : 'pending' },
    { label: 'Documents', icon: FileText, status: ['SHIPPED', 'DELIVERED'].includes(order.status) ? 'completed' : 'pending' },
    { label: 'Shipment', icon: Anchor, status: ['SHIPPED', 'DELIVERED'].includes(order.status) ? 'completed' : 'pending' },
    { label: 'Customs', icon: CheckCircle, status: ['DELIVERED'].includes(order.status) ? 'completed' : 'pending' }, // simplify logic
    { label: 'Delivery', icon: Truck, status: ['DELIVERED'].includes(order.status) ? 'completed' : 'pending' },
  ];

  return (
    <div className="space-y-6 max-w-5xl mx-auto">
      <div className="flex items-center space-x-4">
        <Button variant="outline" size="icon" onClick={() => navigate('/buyer/orders')}>
          <ArrowLeft className="h-4 w-4" />
        </Button>
        <div>
          <h2 className="text-3xl font-heading font-bold">Order #{order.id.split('-')[0].toUpperCase()}</h2>
          <div className="flex items-center gap-3 mt-1">
            <p className="text-muted-foreground text-sm">Placed on {new Date(order.createdAt).toLocaleDateString()}</p>
            <Badge variant="outline" className={getStatusColor(order.status)}>
              {order.status.replace('_', ' ')}
            </Badge>
          </div>
        </div>
      </div>

      {/* Timeline Component */}
      <Card>
        <CardHeader>
          <CardTitle>Shipment Timeline</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex flex-col md:flex-row items-center justify-between relative px-4 md:px-12 py-8">
            <div className="absolute left-1/2 md:left-12 top-0 md:top-1/2 bottom-0 md:bottom-auto w-0.5 md:w-auto md:h-0.5 bg-border -translate-x-1/2 md:-translate-y-1/2 md:translate-x-0 md:right-12 z-0 hidden md:block" />
            
            {timelineSteps.map((step, idx) => {
              const Icon = step.icon;
              const isCompleted = step.status === 'completed';
              return (
                <div key={idx} className="flex flex-col items-center relative z-10 w-full md:w-auto mb-8 md:mb-0">
                  <div className={`w-12 h-12 rounded-full flex items-center justify-center border-4 ${isCompleted ? 'bg-primary border-primary text-primary-foreground' : 'bg-card border-border text-muted-foreground'} transition-colors`}>
                    {isCompleted ? <CheckCircle className="w-5 h-5" /> : <Icon className="w-5 h-5" />}
                  </div>
                  <p className={`mt-3 text-sm font-medium ${isCompleted ? 'text-primary' : 'text-muted-foreground'}`}>
                    {step.label}
                  </p>
                </div>
              );
            })}
          </div>
        </CardContent>
      </Card>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Financial Summary</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <div className="flex justify-between items-center font-bold text-lg border-b pb-4">
                  <span>Total Amount</span>
                  <span className="text-primary">{order.currencyCode} {order.totalAmount?.toLocaleString()}</span>
                </div>
                <div className="pt-2">
                  <p className="font-medium">Payment Terms</p>
                  <p className="text-sm text-muted-foreground mt-1 whitespace-pre-wrap">{order.paymentTerms || 'Standard Terms'}</p>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        <div className="lg:col-span-1 space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Documents</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-3">
                <Button variant="outline" className="w-full justify-start" onClick={() => navigate(`/buyer/quotations/${order.quotationId}`)}>
                  <FileText className="w-4 h-4 mr-2" />
                  View Original Quotation
                </Button>
                {/* Placeholder for more documents */}
                <Button variant="outline" className="w-full justify-start" disabled>
                  <FileText className="w-4 h-4 mr-2" />
                  Commercial Invoice (Pending)
                </Button>
                <Button variant="outline" className="w-full justify-start" disabled>
                  <FileText className="w-4 h-4 mr-2" />
                  Bill of Lading (Pending)
                </Button>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
