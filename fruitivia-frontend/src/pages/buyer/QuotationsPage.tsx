import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { buyerApi } from '../../services/buyerApi';
import { Button } from '../../components/ui/button';
import { Card, CardContent } from '../../components/ui/card';
import { Badge } from '../../components/ui/badge';
import { Loader2, Plus, FileText, Filter } from 'lucide-react';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../../components/ui/select';

export default function QuotationsPage() {
  const navigate = useNavigate();
  const [quotations, setQuotations] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState<string>('ALL');

  useEffect(() => {
    fetchQuotations();
  }, [statusFilter]);

  const fetchQuotations = async () => {
    try {
      setLoading(true);
      const statusParam = statusFilter === 'ALL' ? undefined : statusFilter;
      const data = await buyerApi.getQuotations(0, 50, statusParam);
      setQuotations(data.content);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'REQUESTED': return 'bg-blue-100 text-blue-800 border-blue-200';
      case 'SENT': return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      case 'ACCEPTED': return 'bg-green-100 text-green-800 border-green-200';
      case 'REJECTED': return 'bg-red-100 text-red-800 border-red-200';
      case 'EXPIRED': return 'bg-gray-100 text-gray-800 border-gray-200';
      default: return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <h2 className="text-3xl font-heading font-bold">Quotations</h2>
          <p className="text-muted-foreground mt-1">Manage your B2B quotation requests and offers.</p>
        </div>
        <div className="flex gap-4 w-full md:w-auto">
          <Select value={statusFilter} onValueChange={setStatusFilter}>
            <SelectTrigger className="w-[180px]">
              <Filter className="w-4 h-4 mr-2" />
              <SelectValue placeholder="Filter by status" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="ALL">All Statuses</SelectItem>
              <SelectItem value="REQUESTED">Requested</SelectItem>
              <SelectItem value="SENT">Sent to Buyer</SelectItem>
              <SelectItem value="ACCEPTED">Accepted</SelectItem>
              <SelectItem value="REJECTED">Rejected</SelectItem>
              <SelectItem value="EXPIRED">Expired</SelectItem>
            </SelectContent>
          </Select>
          <Button onClick={() => navigate('/buyer/quotations/request')}>
            <Plus className="w-4 h-4 mr-2" />
            New Request
          </Button>
        </div>
      </div>

      {loading ? (
        <div className="flex items-center justify-center h-64"><Loader2 className="w-8 h-8 animate-spin" /></div>
      ) : (
        <div className="space-y-4">
          {quotations.map(q => (
            <Card 
              key={q.id} 
              className="cursor-pointer hover:border-primary/50 transition-colors"
              onClick={() => navigate(`/buyer/quotations/${q.id}`)}
            >
              <CardContent className="p-6">
                <div className="flex flex-col md:flex-row justify-between md:items-center gap-4">
                  <div className="flex items-start gap-4">
                    <div className="p-3 bg-primary/10 text-primary rounded-lg">
                      <FileText className="w-6 h-6" />
                    </div>
                    <div>
                      <h3 className="font-semibold text-lg">Quotation #{q.id.split('-')[0].toUpperCase()}</h3>
                      <p className="text-sm text-muted-foreground">
                        Requested on {new Date(q.createdAt).toLocaleDateString()}
                      </p>
                      <div className="mt-2 text-sm">
                        <span className="font-medium">{q.items?.length || 0} items</span> 
                        {q.totalAmount && (
                          <span className="ml-4 font-medium text-green-700 dark:text-green-400">
                            Total: {q.currencyCode} {q.totalAmount.toLocaleString()}
                          </span>
                        )}
                      </div>
                    </div>
                  </div>
                  
                  <div className="flex flex-col items-end gap-2">
                    <Badge variant="outline" className={getStatusColor(q.status)}>
                      {q.status}
                    </Badge>
                    {q.validityDate && q.status === 'SENT' && (
                      <p className="text-xs text-muted-foreground">
                        Valid until {new Date(q.validityDate).toLocaleDateString()}
                      </p>
                    )}
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
          
          {quotations.length === 0 && (
            <div className="py-12 text-center border rounded-xl border-dashed bg-muted/20">
              <FileText className="w-12 h-12 text-muted-foreground mx-auto mb-4 opacity-50" />
              <h3 className="text-lg font-medium">No quotations found</h3>
              <p className="text-sm text-muted-foreground mt-1">You don't have any quotations matching the current filters.</p>
              <Button variant="outline" className="mt-4" onClick={() => navigate('/buyer/quotations/request')}>
                Request your first quotation
              </Button>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
