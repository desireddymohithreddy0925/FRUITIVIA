import React, { useEffect, useState } from 'react';
import { Users, MoreVertical, Edit2, Trash2 } from 'lucide-react';
import api from '../../services/api';

interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
  enabled: boolean;
}

export default function UsersPage() {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // In a real app we'd fetch from API
    // api.get('/admin/users').then(res => setUsers(res.data));
    setTimeout(() => {
      setUsers([
        { id: 1, firstName: 'Raj', lastName: 'Kumar', email: 'raj@fruitivia.com', role: 'ADMIN', enabled: true },
        { id: 2, firstName: 'Amit', lastName: 'Singh', email: 'amit@fruitivia.com', role: 'ENGINEER', enabled: true },
        { id: 3, firstName: 'Priya', lastName: 'Patel', email: 'priya@farmer.com', role: 'FARMER', enabled: true },
      ]);
      setLoading(false);
    }, 500);
  }, []);

  return (
    <div className="p-8 min-h-screen bg-[#F9F9F6]">
      <div className="max-w-5xl mx-auto">
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-2xl font-bold text-[#1B4332] flex items-center gap-2">
              <Users className="w-6 h-6" /> System Users
            </h1>
            <p className="text-[#606C38] text-sm mt-1">Manage platform access and roles</p>
          </div>
          <button className="bg-[#1B4332] text-white px-4 py-2 rounded-lg font-medium hover:bg-[#133024] transition-colors shadow-sm">
            + Add User
          </button>
        </div>

        <div className="bg-white rounded-xl shadow-sm border border-[#E6E8E3] overflow-hidden">
          <table className="w-full text-left text-sm">
            <thead className="bg-[#F0F2EB] text-[#606C38] border-b border-[#E6E8E3]">
              <tr>
                <th className="px-6 py-4 font-semibold tracking-wider">NAME</th>
                <th className="px-6 py-4 font-semibold tracking-wider">EMAIL</th>
                <th className="px-6 py-4 font-semibold tracking-wider">ROLE</th>
                <th className="px-6 py-4 font-semibold tracking-wider">STATUS</th>
                <th className="px-6 py-4 text-right font-semibold tracking-wider">ACTIONS</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#E6E8E3]">
              {loading ? (
                <tr><td colSpan={5} className="p-8 text-center text-gray-400">Loading...</td></tr>
              ) : (
                users.map((user) => (
                  <tr key={user.id} className="hover:bg-[#F9F9F6] transition-colors">
                    <td className="px-6 py-4 font-medium text-[#1B4332]">
                      {user.firstName} {user.lastName}
                    </td>
                    <td className="px-6 py-4 text-gray-600">{user.email}</td>
                    <td className="px-6 py-4">
                      <span className="bg-[#E2E4DC] text-[#606C38] px-2.5 py-1 rounded-md text-xs font-bold tracking-wider">
                        {user.role}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-1.5">
                        <div className={`w-2 h-2 rounded-full ${user.enabled ? 'bg-green-500' : 'bg-red-500'}`}></div>
                        <span className="text-xs font-medium">{user.enabled ? 'Active' : 'Disabled'}</span>
                      </div>
                    </td>
                    <td className="px-6 py-4 text-right">
                      <button className="text-gray-400 hover:text-[#1B4332] p-1.5 transition-colors">
                        <MoreVertical className="w-5 h-5" />
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
