import { Card, CardContent } from '../../components/ui/card';
import { ClipboardCheck } from 'lucide-react';

export default function CustomsPage() {
  return (
    <div className="space-y-6">
      <h2 className="text-3xl font-heading font-bold">Customs Clearance</h2>
      <p className="text-muted-foreground mt-1">Review the customs status of your international shipments.</p>
      
      <Card>
        <CardContent className="flex flex-col items-center justify-center py-24 text-center">
          <ClipboardCheck className="w-16 h-16 text-muted-foreground opacity-50 mb-4" />
          <h3 className="text-xl font-medium">No pending customs clearance</h3>
          <p className="text-muted-foreground mt-2 max-w-md">
            Any required actions for customs clearance in your destination country will appear here. Currently, there are no items requiring your attention.
          </p>
        </CardContent>
      </Card>
    </div>
  );
}
