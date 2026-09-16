import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { buyerApi } from '../../services/buyerApi';
import { Card, CardContent } from '../../components/ui/card';
import { Badge } from '../../components/ui/badge';
import { Loader2, Search } from 'lucide-react';
import { Input } from '../../components/ui/input';

export default function CatalogPage() {
  const navigate = useNavigate();
  const [fruits, setFruits] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  useEffect(() => {
    fetchCatalog();
  }, []);

  const fetchCatalog = async () => {
    try {
      setLoading(true);
      const data = await buyerApi.getFruits({ size: 100 }); // Pagination can be added later
      setFruits(data.content);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const filteredFruits = fruits.filter(f => 
    f.name.toLowerCase().includes(search.toLowerCase()) || 
    f.category?.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <h2 className="text-3xl font-heading font-bold">Fruit Catalog</h2>
          <p className="text-muted-foreground mt-1">Browse our premium selection of fresh produce.</p>
        </div>
        <div className="relative w-full md:w-72">
          <Search className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
          <Input 
            placeholder="Search fruits..." 
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="pl-9"
          />
        </div>
      </div>

      {loading ? (
        <div className="flex items-center justify-center h-64"><Loader2 className="w-8 h-8 animate-spin" /></div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
          {filteredFruits.map(fruit => (
            <Card 
              key={fruit.id} 
              className="overflow-hidden cursor-pointer hover:shadow-md transition-shadow group border-border/50"
              onClick={() => navigate(`/buyer/catalog/${fruit.id}`)}
            >
              <div className="aspect-video bg-muted/50 flex items-center justify-center p-6 relative">
                {/* Fallback icon if no image */}
                <div className="text-4xl">🍎</div>
                <div className="absolute inset-0 bg-primary/10 opacity-0 group-hover:opacity-100 transition-opacity" />
              </div>
              <CardContent className="p-4">
                <div className="flex justify-between items-start">
                  <div>
                    <h3 className="font-semibold text-lg font-heading">{fruit.name}</h3>
                    <p className="text-sm text-muted-foreground capitalize">{fruit.category || 'Fruit'}</p>
                  </div>
                  <Badge variant="outline" className="bg-primary/5">{fruit.varieties?.length || 0} varieties</Badge>
                </div>
                
                <div className="mt-4 flex flex-wrap gap-2">
                  {fruit.seasons?.slice(0,2).map((s: string) => (
                    <Badge key={s} variant="secondary" className="text-xs">{s}</Badge>
                  ))}
                  {fruit.seasons?.length > 2 && <Badge variant="secondary" className="text-xs">+{fruit.seasons.length - 2}</Badge>}
                </div>
              </CardContent>
            </Card>
          ))}
          {filteredFruits.length === 0 && (
            <div className="col-span-full py-12 text-center text-muted-foreground border rounded-xl border-dashed">
              No fruits found matching your search.
            </div>
          )}
        </div>
      )}
    </div>
  );
}
