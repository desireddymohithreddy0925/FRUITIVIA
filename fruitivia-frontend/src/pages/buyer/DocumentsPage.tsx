import { Card, CardContent } from '../../components/ui/card';
import { BookOpen } from 'lucide-react';

export default function DocumentsPage() {
  return (
    <div className="space-y-6">
      <h2 className="text-3xl font-heading font-bold">Documents</h2>
      <p className="text-muted-foreground mt-1">Access all your trade and export documents.</p>
      
      <Card>
        <CardContent className="flex flex-col items-center justify-center py-24 text-center">
          <BookOpen className="w-16 h-16 text-muted-foreground opacity-50 mb-4" />
          <h3 className="text-xl font-medium">Document Vault</h3>
          <p className="text-muted-foreground mt-2 max-w-md">
            Commercial invoices, certificates of origin, and phytosanitary certificates are securely attached to their respective orders. Please navigate to the specific Order Details page to download them.
          </p>
        </CardContent>
      </Card>
    </div>
  );
}
