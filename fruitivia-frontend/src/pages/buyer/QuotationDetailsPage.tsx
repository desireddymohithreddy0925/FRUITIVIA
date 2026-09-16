import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { buyerApi } from '../../services/buyerApi';
import { Button } from '../../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle, CardFooter } from '../../components/ui/card';
import { Badge } from '../../components/ui/badge';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '../../components/ui/table';
import { Loader2, ArrowLeft, Check, X } from 'lucide-react';

export default function QuotationDetailsPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [quotation, setQuotation] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);

  useEffect(() => {
    fetchQuotation();
  }, [id]);

  const fetchQuotation = async () => {
    try {
      if (!id) return;
      const data = await buyerApi.getQuotationDetails(id);
      setQuotation(data);
    } catch (err) {
      console.error(err);
      alert('Failed to load quotation details');
    } finally {
      setLoading(false);
    }
  };

  const handleAction = async (action: 'accept' | 'reject') => {
    if (!id || !window.confirm(`Are you sure you want to ${action} this quotation?`)) return;
    try {
      setActionLoading(true);
      if (action === 'accept') {
        await buyerApi.acceptQuotation(id);
      } else {
        await buyerApi.rejectQuotation(id);
      }
      fetchQuotation(); // refresh status
    } catch (err) {
      console.error(err);
      alert(`Failed to ${action} quotation`);
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) {
    return <div className="flex items-center justify-center h-64"><Loader2 className="w-8 h-8 animate-spin" /></div>;
  }

  if (!quotation) {
    return (
      <div className="text-center py-12">
        <h2 className="text-2xl font-bold">Quotation not found</h2>
        <Button variant="link" onClick={() => navigate('/buyer/quotations')}>Back to Quotations</Button>
      </div>
    );
  }

  return (
    <div className="space-y-6 max-w-5xl mx-auto">
      <div className="flex items-center space-x-4">
        <Button variant="outline" size="icon" onClick={() => navigate('/buyer/quotations')}>
          <ArrowLeft className="h-4 w-4" />
        </Button>
        <div>
          <h2 className="text-3xl font-heading font-bold">Quotation #{quotation.id.split('-')[0].toUpperCase()}</h2>
          <div className="flex items-center gap-3 mt-1">
            <p className="text-muted-foreground text-sm">Requested on {new Date(quotation.createdAt).toLocaleDateString()}</p>
            <Badge variant="secondary">{quotation.status}</Badge>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Requested Items</CardTitle>
            </CardHeader>
            <CardContent>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Fruit</TableHead>
                    <TableHead>Variety</TableHead>
                    <TableHead className="text-right">Quantity (kg)</TableHead>
                    <TableHead className="text-right">Unit Price</TableHead>
                    <TableHead className="text-right">Total</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {quotation.items?.map((item: any) => (
                    <TableRow key={item.id}>
                      <TableCell className="font-medium">{item.fruitName}</TableCell>
                      <TableCell>{item.varietyName}</TableCell>
                      <TableCell className="text-right">{item.quantity.toLocaleString()}</TableCell>
                      <TableCell className="text-right">
                        {item.unitPrice ? `${quotation.currencyCode} ${item.unitPrice}` : 'TBD'}
                      </TableCell>
                      <TableCell className="text-right">
                        {item.lineTotal ? `${quotation.currencyCode} ${item.lineTotal.toLocaleString()}` : 'TBD'}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </div>

        <div className="lg:col-span-1 space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Quotation Terms</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex justify-between text-sm">
                <span className="text-muted-foreground">Currency</span>
                <span className="font-medium">{quotation.currencyCode || 'TBD'}</span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-muted-foreground">Items Total</span>
                <span>{quotation.items?.reduce((acc: number, item: any) => acc + (item.lineTotal || 0), 0) > 0 ? `${quotation.currencyCode} ${quotation.items.reduce((acc: number, item: any) => acc + (item.lineTotal || 0), 0).toLocaleString()}` : 'TBD'}</span>
              </div>
              
              {quotation.status !== 'REQUESTED' && (
                <>
                  <div className="border-t my-2 pt-2 space-y-2">
                    <div className="flex justify-between text-sm">
                      <span className="text-muted-foreground">Packaging</span>
                      <span>{quotation.packagingCost || 0}</span>
                    </div>
                    <div className="flex justify-between text-sm">
                      <span className="text-muted-foreground">Transportation</span>
                      <span>{quotation.transportationCost || 0}</span>
                    </div>
                    <div className="flex justify-between text-sm">
                      <span className="text-muted-foreground">Handling</span>
                      <span>{quotation.exportHandlingCost || 0}</span>
                    </div>
                    <div className="flex justify-between text-sm">
                      <span className="text-muted-foreground">Shipping</span>
                      <span>{quotation.shippingCost || 0}</span>
                    </div>
                    <div className="flex justify-between text-sm">
                      <span className="text-muted-foreground">Insurance</span>
                      <span>{quotation.insuranceCost || 0}</span>
                    </div>
                    <div className="flex justify-between text-sm">
                      <span className="text-muted-foreground">Tax</span>
                      <span>{quotation.taxAmount || 0}</span>
                    </div>
                    {quotation.discount > 0 && (
                      <div className="flex justify-between text-sm text-green-600">
                        <span>Discount</span>
                        <span>-{quotation.discount}</span>
                      </div>
                    )}
                  </div>
                  
                  <div className="border-t mt-4 pt-4">
                    <div className="flex justify-between items-center font-bold">
                      <span>Total Amount</span>
                      <span className="text-lg text-primary">{quotation.currencyCode} {quotation.totalAmount?.toLocaleString()}</span>
                    </div>
                  </div>

                  {quotation.paymentTerms && (
                    <div className="mt-4 p-3 bg-muted rounded-md text-sm">
                      <p className="font-medium mb-1">Payment Terms:</p>
                      <p className="text-muted-foreground whitespace-pre-wrap">{quotation.paymentTerms}</p>
                    </div>
                  )}

                  {quotation.validityDate && (
                    <div className="mt-2 text-sm text-center text-muted-foreground">
                      Valid until {new Date(quotation.validityDate).toLocaleDateString()}
                    </div>
                  )}
                </>
              )}
            </CardContent>
            
            {quotation.status === 'SENT' && (
              <CardFooter className="flex gap-4 border-t p-6 bg-muted/20">
                <Button 
                  variant="outline" 
                  className="w-full text-red-600 hover:text-red-700 hover:bg-red-50"
                  onClick={() => handleAction('reject')}
                  disabled={actionLoading}
                >
                  <X className="w-4 h-4 mr-2" /> Reject
                </Button>
                <Button 
                  className="w-full bg-green-600 hover:bg-green-700"
                  onClick={() => handleAction('accept')}
                  disabled={actionLoading}
                >
                  <Check className="w-4 h-4 mr-2" /> Accept
                </Button>
              </CardFooter>
            )}
            
            {quotation.status === 'REQUESTED' && (
              <CardFooter className="border-t p-6 bg-muted/20 justify-center">
                <p className="text-sm text-muted-foreground text-center">
                  Waiting for Fruitivia admin to review and provide pricing.
                </p>
              </CardFooter>
            )}
            
            {quotation.status === 'ACCEPTED' && (
              <CardFooter className="border-t p-6 bg-green-50 justify-center">
                <p className="text-sm text-green-700 text-center font-medium flex items-center">
                  <Check className="w-4 h-4 mr-2" /> Quotation Accepted. Order created.
                </p>
              </CardFooter>
            )}
          </Card>
        </div>
      </div>
    </div>
  );
}
