import { Card, CardContent } from '../../components/ui/card';
import { CreditCard } from 'lucide-react';

export default function PaymentsPage() {
  return (
    <div className="space-y-6">
      <h2 className="text-3xl font-heading font-bold">Payments</h2>
      <p className="text-muted-foreground mt-1">Manage your invoices and payment history.</p>
      
      <Card>
        <CardContent className="flex flex-col items-center justify-center py-24 text-center">
          <CreditCard className="w-16 h-16 text-muted-foreground opacity-50 mb-4" />
          <h3 className="text-xl font-medium">B2B Invoicing Only</h3>
          <p className="text-muted-foreground mt-2 max-w-md">
            All payments are processed offline according to the agreed terms in your quotation (e.g., Letter of Credit, Wire Transfer). The status will be updated by our finance team once funds are cleared.
          </p>
        </CardContent>
      </Card>
    </div>
  );
}
