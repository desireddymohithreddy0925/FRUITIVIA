import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
import { ClipboardCheck, CheckSquare, AlertTriangle, Box, CheckCircle } from 'lucide-react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

const qcData = [
  { name: 'Mon', passed: 120, failed: 12 },
  { name: 'Tue', passed: 150, failed: 15 },
  { name: 'Wed', passed: 180, failed: 8 },
  { name: 'Thu', passed: 140, failed: 22 },
  { name: 'Fri', passed: 190, failed: 10 },
  { name: 'Sat', passed: 110, failed: 5 },
  { name: 'Sun', passed: 90, failed: 2 },
];

export default function QcDashboard() {
  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-3xl font-heading font-bold text-primary">Quality Control</h2>
        <p className="text-muted-foreground mt-1">Inspection queues and batch grading metrics.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <Card className="border-border/50 shadow-sm">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">Pending Inspections</CardTitle>
            <ClipboardCheck className="w-4 h-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-primary">42</div>
            <p className="text-xs text-muted-foreground mt-1">Batches awaiting QC</p>
          </CardContent>
        </Card>
        
        <Card className="border-border/50 shadow-sm">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">Pass Rate</CardTitle>
            <CheckSquare className="w-4 h-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-green-600">92.4%</div>
            <p className="text-xs text-green-600 dark:text-green-400 mt-1">+1.2% from last week</p>
          </CardContent>
        </Card>
        
        <Card className="border-border/50 shadow-sm">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">Failed Batches</CardTitle>
            <AlertTriangle className="w-4 h-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-red-600">14</div>
            <p className="text-xs text-red-600 dark:text-red-400 mt-1">Requires immediate review</p>
          </CardContent>
        </Card>

        <Card className="border-border/50 shadow-sm">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">Batches Graded</CardTitle>
            <Box className="w-4 h-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">1,204</div>
            <p className="text-xs text-muted-foreground mt-1">Total this month</p>
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mt-6">
        <Card className="lg:col-span-2 border-border/50 shadow-sm">
          <CardHeader>
            <CardTitle>Inspection History (Last 7 Days)</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="h-[300px] w-full">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={qcData} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
                  <XAxis dataKey="name" stroke="#888888" fontSize={12} tickLine={false} axisLine={false} />
                  <YAxis stroke="#888888" fontSize={12} tickLine={false} axisLine={false} />
                  <Tooltip 
                    contentStyle={{ borderRadius: '8px', border: '1px solid var(--border)', backgroundColor: 'var(--card)' }}
                    itemStyle={{ color: 'var(--foreground)' }}
                  />
                  <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="var(--border)" />
                  <Bar dataKey="passed" name="Passed" fill="#10b981" radius={[4, 4, 0, 0]} stackId="a" />
                  <Bar dataKey="failed" name="Failed" fill="#ef4444" radius={[4, 4, 0, 0]} stackId="a" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </CardContent>
        </Card>

        <Card className="lg:col-span-1 border-border/50 shadow-sm flex flex-col">
          <CardHeader>
            <CardTitle>Recent Reports</CardTitle>
          </CardHeader>
          <CardContent className="flex-1">
            <div className="space-y-6">
               <div className="flex items-center justify-between">
                 <div className="flex items-center gap-3">
                   <div className="p-2 bg-green-100 text-green-700 rounded-lg dark:bg-green-900/40 dark:text-green-400">
                     <CheckCircle className="w-5 h-5" />
                   </div>
                   <div>
                     <p className="font-medium text-sm">Batch #B-8291</p>
                     <p className="text-xs text-muted-foreground">Grade A • Export Quality</p>
                   </div>
                 </div>
                 <span className="text-xs text-muted-foreground">2 mins ago</span>
               </div>
               
               <div className="flex items-center justify-between">
                 <div className="flex items-center gap-3">
                   <div className="p-2 bg-red-100 text-red-700 rounded-lg dark:bg-red-900/40 dark:text-red-400">
                     <AlertTriangle className="w-5 h-5" />
                   </div>
                   <div>
                     <p className="font-medium text-sm">Batch #B-8290</p>
                     <p className="text-xs text-muted-foreground">Failed • Brix Level Low</p>
                   </div>
                 </div>
                 <span className="text-xs text-muted-foreground">15 mins ago</span>
               </div>

               <div className="flex items-center justify-between">
                 <div className="flex items-center gap-3">
                   <div className="p-2 bg-green-100 text-green-700 rounded-lg dark:bg-green-900/40 dark:text-green-400">
                     <CheckCircle className="w-5 h-5" />
                   </div>
                   <div>
                     <p className="font-medium text-sm">Batch #B-8289</p>
                     <p className="text-xs text-muted-foreground">Grade B • Domestic</p>
                   </div>
                 </div>
                 <span className="text-xs text-muted-foreground">1 hr ago</span>
               </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
