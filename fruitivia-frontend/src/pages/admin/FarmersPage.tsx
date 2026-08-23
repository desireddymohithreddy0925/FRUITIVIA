import React, { useEffect, useState } from 'react';
import { Tractor, MoreVertical, MapPin } from 'lucide-react';
import api from '../../services/api';

interface Farmer {
  id: number;
  user: { firstName: string, lastName: string, email: string };
  phoneNumber: string;
  farms: number;
}

export default function FarmersPage() {
  const [farmers, setFarmers] = useState<Farmer[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setTimeout(() => {
      setFarmers([
        { id: 1, user: { firstName: 'Ram', lastName: 'Das', email: 'ram@farm.in' }, phoneNumber: '+91 9876543210', farms: 2 },
        { id: 2, user: { firstName: 'Kisan', lastName: 'Rao', email: 'kisan@farm.in' }, phoneNumber: '+91 9123456789', farms: 1 },
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
              <Tractor className="w-6 h-6" /> Farmers Network
            </h1>
            <p className="text-[#606C38] text-sm mt-1">Manage partner farmers and their agricultural lands</p>
          </div>
          <button className="bg-[#1B4332] text-white px-4 py-2 rounded-lg font-medium hover:bg-[#133024] transition-colors shadow-sm">
            + Onboard Farmer
          </button>
        </div>

        <div className="bg-white rounded-xl shadow-sm border border-[#E6E8E3] overflow-hidden">
          <table className="w-full text-left text-sm">
            <thead className="bg-[#F0F2EB] text-[#606C38] border-b border-[#E6E8E3]">
              <tr>
                <th className="px-6 py-4 font-semibold tracking-wider">FARMER NAME</th>
                <th className="px-6 py-4 font-semibold tracking-wider">CONTACT</th>
                <th className="px-6 py-4 font-semibold tracking-wider">FARMS</th>
                <th className="px-6 py-4 text-right font-semibold tracking-wider">ACTIONS</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#E6E8E3]">
              {loading ? (
                <tr><td colSpan={4} className="p-8 text-center text-gray-400">Loading...</td></tr>
              ) : (
                farmers.map((farmer) => (
                  <tr key={farmer.id} className="hover:bg-[#F9F9F6] transition-colors">
                    <td className="px-6 py-4">
                      <div className="font-medium text-[#1B4332]">{farmer.user.firstName} {farmer.user.lastName}</div>
                      <div className="text-xs text-gray-500 mt-0.5">{farmer.user.email}</div>
                    </td>
                    <td className="px-6 py-4 text-gray-600 font-mono text-xs">{farmer.phoneNumber}</td>
                    <td className="px-6 py-4">
                      <span className="inline-flex items-center gap-1 bg-[#FFF3E0] text-[#D48C28] px-2 py-1 rounded-md text-xs font-bold">
                        <MapPin className="w-3 h-3" /> {farmer.farms} Registered
                      </span>
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
