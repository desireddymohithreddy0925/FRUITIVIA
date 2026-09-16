import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
import { Activity, Server, Database, Globe, AlertTriangle, ShieldCheck, Cpu } from 'lucide-react';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

const performanceData = [
  { time: '00:00', cpu: 45, memory: 60 },
  { time: '04:00', cpu: 30, memory: 55 },
  { time: '08:00', cpu: 85, memory: 75 },
  { time: '12:00', cpu: 92, memory: 85 },
  { time: '16:00', cpu: 78, memory: 80 },
  { time: '20:00', cpu: 55, memory: 65 },
  { time: '24:00', cpu: 40, memory: 60 },
];

export default function EngineerDashboard() {
  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-3xl font-heading font-bold text-primary">System Operations</h2>
        <p className="text-muted-foreground mt-1">Platform telemetry and infrastructure health.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <Card className="border-border/50 shadow-sm">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">API Gateway</CardTitle>
            <Globe className="w-4 h-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-green-600">99.99%</div>
            <p className="text-xs text-muted-foreground mt-1">Uptime (Last 30 Days)</p>
          </CardContent>
        </Card>
        
        <Card className="border-border/50 shadow-sm">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">Database Clusters</CardTitle>
            <Database className="w-4 h-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">24ms</div>
            <p className="text-xs text-green-600 dark:text-green-400 mt-1">Avg read latency</p>
          </CardContent>
        </Card>
        
        <Card className="border-border/50 shadow-sm">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">Active Tasks</CardTitle>
            <Activity className="w-4 h-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">142</div>
            <p className="text-xs text-muted-foreground mt-1">Background workers</p>
          </CardContent>
        </Card>

        <Card className="border-border/50 shadow-sm">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">System Alerts</CardTitle>
            <AlertTriangle className="w-4 h-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-yellow-600">3</div>
            <p className="text-xs text-muted-foreground mt-1">Non-critical warnings</p>
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mt-6">
        <Card className="lg:col-span-2 border-border/50 shadow-sm">
          <CardHeader>
            <CardTitle>Resource Utilization (Last 24h)</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="h-[300px] w-full">
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={performanceData} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
                  <defs>
                    <linearGradient id="colorCpu" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#8b5cf6" stopOpacity={0.3}/>
                      <stop offset="95%" stopColor="#8b5cf6" stopOpacity={0}/>
                    </linearGradient>
                    <linearGradient id="colorMemory" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#10b981" stopOpacity={0.3}/>
                      <stop offset="95%" stopColor="#10b981" stopOpacity={0}/>
                    </linearGradient>
                  </defs>
                  <XAxis dataKey="time" stroke="#888888" fontSize={12} tickLine={false} axisLine={false} />
                  <YAxis stroke="#888888" fontSize={12} tickLine={false} axisLine={false} tickFormatter={(val) => `${val}%`} />
                  <Tooltip 
                    contentStyle={{ borderRadius: '8px', border: '1px solid var(--border)', backgroundColor: 'var(--card)' }}
                    itemStyle={{ color: 'var(--foreground)' }}
                  />
                  <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="var(--border)" />
                  <Area type="monotone" dataKey="cpu" name="CPU Usage" stroke="#8b5cf6" fillOpacity={1} fill="url(#colorCpu)" strokeWidth={2} />
                  <Area type="monotone" dataKey="memory" name="Memory Usage" stroke="#10b981" fillOpacity={1} fill="url(#colorMemory)" strokeWidth={2} />
                </AreaChart>
              </ResponsiveContainer>
            </div>
          </CardContent>
        </Card>

        <Card className="lg:col-span-1 border-border/50 shadow-sm flex flex-col">
          <CardHeader>
            <CardTitle>Service Status</CardTitle>
          </CardHeader>
          <CardContent className="flex-1">
            <div className="space-y-6">
               <div className="flex items-center justify-between">
                 <div className="flex items-center gap-3">
                   <div className="p-2 bg-green-100 text-green-700 rounded-lg dark:bg-green-900/40 dark:text-green-400">
                     <Server className="w-5 h-5" />
                   </div>
                   <div>
                     <p className="font-medium">Core API Service</p>
                     <p className="text-xs text-muted-foreground">Version 2.4.1</p>
                   </div>
                 </div>
                 <span className="text-sm font-semibold text-green-600">Online</span>
               </div>
               
               <div className="flex items-center justify-between">
                 <div className="flex items-center gap-3">
                   <div className="p-2 bg-green-100 text-green-700 rounded-lg dark:bg-green-900/40 dark:text-green-400">
                     <ShieldCheck className="w-5 h-5" />
                   </div>
                   <div>
                     <p className="font-medium">Auth Service</p>
                     <p className="text-xs text-muted-foreground">JWT Token Issuer</p>
                   </div>
                 </div>
                 <span className="text-sm font-semibold text-green-600">Online</span>
               </div>

               <div className="flex items-center justify-between">
                 <div className="flex items-center gap-3">
                   <div className="p-2 bg-yellow-100 text-yellow-700 rounded-lg dark:bg-yellow-900/40 dark:text-yellow-400">
                     <Cpu className="w-5 h-5" />
                   </div>
                   <div>
                     <p className="font-medium">Batch Processor</p>
                     <p className="text-xs text-muted-foreground">Lag: 45s</p>
                   </div>
                 </div>
                 <span className="text-sm font-semibold text-yellow-600">Degraded</span>
               </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
