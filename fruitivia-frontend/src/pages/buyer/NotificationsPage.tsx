import { Card, CardContent } from '../../components/ui/card';
import { Bell } from 'lucide-react';

export default function NotificationsPage() {
  return (
    <div className="space-y-6">
      <h2 className="text-3xl font-heading font-bold">Notifications</h2>
      <p className="text-muted-foreground mt-1">Updates on your quotations and orders.</p>
      
      <Card>
        <CardContent className="flex flex-col items-center justify-center py-24 text-center">
          <div className="relative">
             <Bell className="w-16 h-16 text-muted-foreground opacity-50 mb-4" />
             <div className="absolute top-0 right-0 w-4 h-4 bg-primary rounded-full border-2 border-background" />
          </div>
          <h3 className="text-xl font-medium">You're all caught up!</h3>
          <p className="text-muted-foreground mt-2 max-w-md">
            All notifications have been read. We will notify you when a quotation is priced or an order's shipment status changes.
          </p>
        </CardContent>
      </Card>
    </div>
  );
}
