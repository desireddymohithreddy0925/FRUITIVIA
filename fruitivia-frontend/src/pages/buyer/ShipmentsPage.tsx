import { Card, CardContent } from '../../components/ui/card';
import { Ship } from 'lucide-react';

export default function ShipmentsPage() {
  return (
    <div className="space-y-6">
      <h2 className="text-3xl font-heading font-bold">Shipments</h2>
      <p className="text-muted-foreground mt-1">Track your active shipments in transit.</p>
      
      <Card>
        <CardContent className="flex flex-col items-center justify-center py-24 text-center">
          <Ship className="w-16 h-16 text-muted-foreground opacity-50 mb-4" />
          <h3 className="text-xl font-medium">Global tracking overview</h3>
          <p className="text-muted-foreground mt-2 max-w-md">
            Please track individual shipments through the "My Orders" page. Global shipment mapping and tracking will be available in the next release.
          </p>
        </CardContent>
      </Card>
    </div>
  );
}
