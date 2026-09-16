import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '../../components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle, CardFooter } from '../../components/ui/card';
import { Input } from '../../components/ui/input';
import { Label } from '../../components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../../components/ui/select';
import { buyerApi } from '../../services/buyerApi';
import { Plus, Trash2, Loader2, ArrowLeft } from 'lucide-react';

export default function RequestQuotationPage() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [fruits, setFruits] = useState<any[]>([]);
  const [items, setItems] = useState([{ fruitId: '', varietyId: '', quantity: 1000 }]);

  useEffect(() => {
    // Fetch active catalog
    buyerApi.getFruits({ size: 100 }).then(data => {
      setFruits(data.content);
    }).catch(console.error);
  }, []);

  const handleAddItem = () => {
    setItems([...items, { fruitId: '', varietyId: '', quantity: 1000 }]);
  };

  const handleRemoveItem = (index: number) => {
    setItems(items.filter((_, i) => i !== index));
  };

  const handleItemChange = (index: number, field: string, value: string | number) => {
    const newItems = [...items];
    (newItems[index] as any)[field] = value;
    if (field === 'fruitId') {
      newItems[index].varietyId = ''; // reset variety when fruit changes
    }
    setItems(newItems);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setLoading(true);
      
      // Validate
      if (items.some(i => !i.fruitId || !i.varietyId || i.quantity <= 0)) {
        alert("Please complete all fields with valid quantities.");
        setLoading(false);
        return;
      }
      
      const res = await buyerApi.requestQuotation({ items });
      navigate(`/buyer/quotations/${res.id}`);
    } catch (err) {
      console.error(err);
      alert("Failed to request quotation.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6 max-w-4xl mx-auto">
      <div className="flex items-center space-x-4">
        <Button variant="outline" size="icon" onClick={() => navigate('/buyer/quotations')}>
          <ArrowLeft className="h-4 w-4" />
        </Button>
        <div>
          <h2 className="text-3xl font-heading font-bold">Request Quotation</h2>
          <p className="text-muted-foreground mt-1">Select fruits and quantities for your new B2B quotation.</p>
        </div>
      </div>

      <form onSubmit={handleSubmit}>
        <Card>
          <CardHeader>
            <CardTitle>Items</CardTitle>
            <CardDescription>Add one or more fruit varieties to your request.</CardDescription>
          </CardHeader>
          <CardContent className="space-y-6">
            {items.map((item, index) => {
              const selectedFruit = fruits.find(f => f.id === item.fruitId);
              const varieties = selectedFruit?.varieties || [];

              return (
                <div key={index} className="flex flex-col md:flex-row gap-4 items-end p-4 border rounded-lg bg-muted/20">
                  <div className="flex-1 space-y-2">
                    <Label>Fruit</Label>
                    <Select value={item.fruitId} onValueChange={(val) => handleItemChange(index, 'fruitId', val)}>
                      <SelectTrigger>
                        <SelectValue placeholder="Select fruit" />
                      </SelectTrigger>
                      <SelectContent>
                        {fruits.map(f => (
                          <SelectItem key={f.id} value={f.id}>{f.name}</SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>

                  <div className="flex-1 space-y-2">
                    <Label>Variety</Label>
                    <Select disabled={!item.fruitId} value={item.varietyId} onValueChange={(val) => handleItemChange(index, 'varietyId', val)}>
                      <SelectTrigger>
                        <SelectValue placeholder="Select variety" />
                      </SelectTrigger>
                      <SelectContent>
                        {varieties.map((v: any) => (
                          <SelectItem key={v.id} value={v.id}>{v.name}</SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>

                  <div className="w-full md:w-32 space-y-2">
                    <Label>Quantity (kg)</Label>
                    <Input 
                      type="number" 
                      min="100" 
                      value={item.quantity} 
                      onChange={(e) => handleItemChange(index, 'quantity', parseInt(e.target.value) || 0)} 
                    />
                  </div>

                  {items.length > 1 && (
                    <Button type="button" variant="destructive" size="icon" onClick={() => handleRemoveItem(index)}>
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  )}
                </div>
              );
            })}

            <Button type="button" variant="outline" onClick={handleAddItem} className="w-full">
              <Plus className="h-4 w-4 mr-2" /> Add Item
            </Button>
          </CardContent>
          <CardFooter className="flex justify-end bg-muted/50 border-t p-6">
            <Button type="submit" disabled={loading}>
              {loading && <Loader2 className="h-4 w-4 mr-2 animate-spin" />}
              Submit Request
            </Button>
          </CardFooter>
        </Card>
      </form>
    </div>
  );
}
