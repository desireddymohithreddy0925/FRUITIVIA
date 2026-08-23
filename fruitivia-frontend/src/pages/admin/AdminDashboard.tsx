import React from 'react';
import { Leaf, Plus, TrendingUp, AlertTriangle, Plane, Ship, Home, MapPin, Package, FileText, ShoppingCart, CheckCircle2 } from 'lucide-react';

export default function AdminDashboard() {
  return (
    <div className="min-h-screen bg-[#F9F9F6] text-[#1B4332] font-sans pb-24">
      {/* Header */}
      <header className="flex items-center justify-between p-4 bg-white border-b border-[#E6E8E3]">
        <div className="flex items-center gap-2">
          <Leaf className="w-6 h-6 text-[#1B4332]" />
          <span className="text-xl font-bold tracking-tight">Fruitivia</span>
        </div>
        <div className="w-8 h-8 rounded-full bg-[#1B4332] flex items-center justify-center text-white font-medium shadow-sm">
          R
        </div>
      </header>

      <main className="p-4 max-w-3xl mx-auto space-y-6">
        {/* Welcome Section */}
        <div>
          <h1 className="text-2xl font-bold mb-1">Welcome back, Raj</h1>
          <p className="text-[#606C38] text-sm">Mumbai Export Hub</p>
        </div>

        {/* Action Button */}
        <button className="w-full bg-[#1B4332] hover:bg-[#133024] text-white py-3.5 px-4 rounded-xl font-medium flex items-center justify-center gap-2 transition-colors shadow-sm">
          <Plus className="w-5 h-5" />
          NEW EXPORT SHIPMENT
        </button>

        {/* Metrics Grid */}
        <div className="grid grid-cols-2 gap-3">
          <div className="col-span-2 bg-[#F0F2EB] border border-[#E6E8E3] rounded-2xl p-5 relative overflow-hidden">
            <p className="text-xs font-semibold text-[#606C38] tracking-wider mb-2">TOTAL EXPORTS (NOV)</p>
            <div className="flex items-baseline gap-1">
              <span className="text-4xl font-bold">1,248</span>
              <span className="text-sm font-medium text-[#606C38]">MT</span>
            </div>
            <TrendingUp className="absolute top-5 right-5 w-5 h-5 text-[#1B4332]" />
          </div>

          <div className="bg-[#F0F2EB] border border-[#E6E8E3] rounded-2xl p-4 flex flex-col justify-between">
            <p className="text-xs font-semibold text-[#606C38] tracking-wider mb-2">ACTIVE</p>
            <div className="flex items-center gap-2">
              <span className="text-2xl font-bold">12</span>
              <div className="w-2.5 h-2.5 rounded-full bg-[#FFB753]"></div>
            </div>
          </div>

          <div className="bg-[#F0F2EB] border border-[#E6E8E3] rounded-2xl p-4 flex flex-col justify-between">
            <p className="text-xs font-semibold text-[#606C38] tracking-wider mb-2">ALERTS</p>
            <div className="flex items-center gap-2">
              <span className="text-2xl font-bold text-[#D62828]">2</span>
              <AlertTriangle className="w-4 h-4 text-[#D62828]" />
            </div>
          </div>
        </div>

        {/* At a Glance */}
        <section>
          <h2 className="text-xs font-semibold text-[#606C38] tracking-wider mb-3">AT A GLANCE</h2>
          <div className="bg-[#F0F2EB] border border-[#E6E8E3] rounded-2xl overflow-hidden shadow-sm">
            {/* Map Area */}
            <div className="h-40 bg-[#E8EAE3] relative border-b border-[#E6E8E3] flex items-center justify-center overflow-hidden">
               {/* Abstract map lines representation */}
               <svg className="absolute w-[150%] h-[150%] opacity-40 text-[#606C38]" viewBox="0 0 400 200" fill="none" stroke="currentColor" strokeWidth="1">
                 <path d="M 100,100 C 150,50 250,50 300,100 C 350,150 250,180 200,150 C 150,120 120,80 180,60" />
                 <circle cx="100" cy="100" r="3" fill="currentColor" />
                 <circle cx="300" cy="100" r="3" fill="currentColor" />
                 <circle cx="180" cy="60" r="3" fill="currentColor" />
               </svg>
               <div className="absolute bg-white/80 backdrop-blur text-[10px] font-bold px-2 py-1 rounded top-3 left-3 text-[#1B4332]">SHIPPING ROUTES</div>
            </div>
            
            {/* List */}
            <div className="divide-y divide-[#E6E8E3]">
              <div className="p-4 flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-full bg-[#C3E8D5] flex items-center justify-center">
                    <Plane className="w-5 h-5 text-[#1B4332]" />
                  </div>
                  <div>
                    <h3 className="font-medium text-[15px]">New York, USA</h3>
                    <p className="text-sm text-[#606C38] font-mono mt-0.5">Alphonso Mango • 40 MT</p>
                  </div>
                </div>
                <span className="bg-[#1B4332] text-white text-[10px] font-bold px-3 py-1.5 rounded-full tracking-wider">IN TRANSIT</span>
              </div>
              
              <div className="p-4 flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-full bg-[#FCECD5] flex items-center justify-center">
                    <Ship className="w-5 h-5 text-[#D48C28]" />
                  </div>
                  <div>
                    <h3 className="font-medium text-[15px]">Dubai, UAE</h3>
                    <p className="text-sm text-[#606C38] font-mono mt-0.5">Pomegranate • 120 MT</p>
                  </div>
                </div>
                <span className="bg-[#E2E4DC] text-[#606C38] text-[10px] font-bold px-3 py-1.5 rounded-full tracking-wider">PENDING</span>
              </div>
            </div>
          </div>
        </section>

        {/* Recent Activity */}
        <section>
          <div className="flex justify-between items-center mb-3">
            <h2 className="text-xs font-semibold text-[#606C38] tracking-wider">RECENT ACTIVITY</h2>
            <button className="text-xs font-bold text-[#FFB753] tracking-wider">VIEW ALL</button>
          </div>
          
          <div className="space-y-4">
            <div className="flex gap-3">
              <div className="mt-1.5 w-2 h-2 rounded-full bg-[#FFB753] shrink-0"></div>
              <div>
                <p className="text-sm">New Order: 20 MT Cavendish Bananas requested by FreshCo LLC (Russia).</p>
                <p className="text-xs text-[#606C38] font-mono mt-1">2 hours ago</p>
              </div>
            </div>
            
            <div className="flex gap-3">
              <div className="mt-1.5 w-2 h-2 rounded-full bg-[#BCC2B0] shrink-0"></div>
              <div>
                <p className="text-sm">Quality Check Pass: Lot #492 (Grapes) cleared by Nashik sorting facility.</p>
                <p className="text-xs text-[#606C38] font-mono mt-1">5 hours ago</p>
              </div>
            </div>
          </div>
        </section>
      </main>


    </div>
  );
}
