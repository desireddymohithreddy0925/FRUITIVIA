import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
import { Ship, Archive, Clock, AlertCircle } from 'lucide-react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

const logisticsData = [
  { day: '1', shipped: 20, delivered: 15 },
  { day: '2', shipped: 25, delivered: 22 },
  { day: '3', shipped: 18, delivered: 30 },
  { day: '4', shipped: 35, delivered: 15 },
  { day: '5', shipped: 40, delivered: 25 },
  { day: '6', shipped: 22, delivered: 38 },
  { day: '7', shipped: 30, delivered: 28 },
];

export default function LogisticsDashboard() {
  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-3xl font-heading font-bold text-primary">Global Logistics</h2>
        <p className="text-muted-foreground mt-1">Shipment tracking and supply chain management.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <Card className="border-border/50 shadow-sm">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">Active Shipments</CardTitle>
            <Ship className="w-4 h-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-blue-600">84</div>
            <p className="text-xs text-muted-foreground mt-1">In transit</p>
          </CardContent>
        </Card>
        
        <Card className="border-border/50 shadow-sm">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">Delayed</CardTitle>
            <Clock className="w-4 h-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-yellow-600">12</div>
            <p className="text-xs text-yellow-600 dark:text-yellow-400 mt-1">Past ETA</p>
          </CardContent>
        </Card>
        
        <Card className="border-border/50 shadow-sm">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">Customs Hold</CardTitle>
            <AlertCircle className="w-4 h-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-red-600">3</div>
            <p className="text-xs text-red-600 dark:text-red-400 mt-1">Action required</p>
          </CardContent>
        </Card>

        <Card className="border-border/50 shadow-sm">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">Packaging Queue</CardTitle>
            <Archive className="w-4 h-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">45</div>
            <p className="text-xs text-muted-foreground mt-1">Orders awaiting packing</p>
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mt-6">
        <Card className="lg:col-span-2 border-border/50 shadow-sm">
          <CardHeader>
            <CardTitle>Shipping Volume (Last 7 Days)</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="h-[300px] w-full">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={logisticsData} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
                  <XAxis dataKey="day" stroke="#888888" fontSize={12} tickLine={false} axisLine={false} />
                  <YAxis stroke="#888888" fontSize={12} tickLine={false} axisLine={false} />
                  <Tooltip 
                    contentStyle={{ borderRadius: '8px', border: '1px solid var(--border)', backgroundColor: 'var(--card)' }}
                    itemStyle={{ color: 'var(--foreground)' }}
                  />
                  <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="var(--border)" />
                  <Line type="monotone" dataKey="shipped" name="Shipped" stroke="#3b82f6" strokeWidth={3} dot={{ r: 4 }} activeDot={{ r: 6 }} />
                  <Line type="monotone" dataKey="delivered" name="Delivered" stroke="#10b981" strokeWidth={3} dot={{ r: 4 }} activeDot={{ r: 6 }} />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </CardContent>
        </Card>

        <Card className="lg:col-span-1 border-border/50 shadow-sm flex flex-col">
          <CardHeader>
            <CardTitle>Urgent Issues</CardTitle>
          </CardHeader>
          <CardContent className="flex-1">
            <div className="space-y-6">
               <div className="flex items-center justify-between">
                 <div className="flex items-center gap-3">
                   <div className="p-2 bg-red-100 text-red-700 rounded-lg dark:bg-red-900/40 dark:text-red-400">
                     <AlertCircle className="w-5 h-5" />
                   </div>
                   <div>
                     <p className="font-medium text-sm">Shipment #SH-881</p>
                     <p className="text-xs text-muted-foreground">Held at Port of Rotterdam</p>
                   </div>
                 </div>
               </div>
               
               <div className="flex items-center justify-between">
                 <div className="flex items-center gap-3">
                   <div className="p-2 bg-red-100 text-red-700 rounded-lg dark:bg-red-900/40 dark:text-red-400">
                     <AlertCircle className="w-5 h-5" />
                   </div>
                   <div>
                     <p className="font-medium text-sm">Shipment #SH-872</p>
                     <p className="text-xs text-muted-foreground">Missing phytosanitary cert</p>
                   </div>
                 </div>
               </div>

               <div className="flex items-center justify-between">
                 <div className="flex items-center gap-3">
                   <div className="p-2 bg-yellow-100 text-yellow-700 rounded-lg dark:bg-yellow-900/40 dark:text-yellow-400">
                     <Clock className="w-5 h-5" />
                   </div>
                   <div>
                     <p className="font-medium text-sm">Vessel Delayed</p>
                     <p className="text-xs text-muted-foreground">ETA updated +48h (SH-860)</p>
                   </div>
                 </div>
               </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
