import React from 'react';
import { NavLink, Outlet, useLocation } from 'react-router-dom';
import { Leaf, LayoutDashboard, Users, Tractor, Globe, Settings, LogOut } from 'lucide-react';

export default function AdminLayout() {
  const location = useLocation();

  const navItems = [
    { name: 'Dashboard', path: '/admin', icon: LayoutDashboard },
    { name: 'System Users', path: '/admin/users', icon: Users },
    { name: 'Farmers', path: '/admin/farmers', icon: Tractor },
    { name: 'Buyers', path: '/admin/buyers', icon: Globe },
  ];

  return (
    <div className="flex h-screen bg-[#F9F9F6] font-sans text-[#1B4332]">
      {/* Sidebar */}
      <aside className="w-64 bg-white border-r border-[#E6E8E3] flex flex-col shadow-sm z-10">
        <div className="p-6 flex items-center gap-3 border-b border-[#E6E8E3]">
          <div className="bg-[#1B4332] p-1.5 rounded-lg">
             <Leaf className="w-6 h-6 text-[#FFB753]" />
          </div>
          <span className="text-xl font-bold tracking-tight">Fruitivia</span>
        </div>
        
        <nav className="flex-1 p-4 flex flex-col gap-1">
          {navItems.map((item) => {
            const Icon = item.icon;
            const isActive = location.pathname === item.path || (item.path !== '/admin' && location.pathname.startsWith(item.path));
            return (
              <NavLink
                key={item.path}
                to={item.path}
                className={`flex items-center gap-3 px-4 py-3 rounded-xl font-medium transition-colors ${
                  isActive 
                    ? 'bg-[#F0F2EB] text-[#1B4332] shadow-sm' 
                    : 'text-[#606C38] hover:bg-[#F9F9F6] hover:text-[#1B4332]'
                }`}
              >
                <Icon className={`w-5 h-5 ${isActive ? 'text-[#1B4332]' : 'text-[#606C38]'}`} />
                {item.name}
              </NavLink>
            );
          })}
        </nav>
        
        <div className="p-4 border-t border-[#E6E8E3]">
          <button className="flex items-center gap-3 px-4 py-3 w-full rounded-xl font-medium text-[#606C38] hover:bg-[#FCECD5] hover:text-[#D62828] transition-colors">
            <LogOut className="w-5 h-5" />
            Sign Out
          </button>
        </div>
      </aside>

      {/* Main Content Area */}
      <main className="flex-1 overflow-y-auto">
        <Outlet />
      </main>
    </div>
  );
}
