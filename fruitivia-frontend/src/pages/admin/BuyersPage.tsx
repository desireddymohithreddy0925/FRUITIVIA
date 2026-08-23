import React, { useEffect, useState } from 'react';
import { Globe, MoreVertical, Building2 } from 'lucide-react';
import api from '../../services/api';

interface Buyer {
  id: number;
  user: { firstName: string, lastName: string, email: string };
  companyName: string;
  country: string;
}

export default function BuyersPage() {
  const [buyers, setBuyers] = useState<Buyer[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setTimeout(() => {
      setBuyers([
        { id: 1, user: { firstName: 'John', lastName: 'Smith', email: 'john@freshco.com' }, companyName: 'FreshCo LLC', country: 'USA' },
        { id: 2, user: { firstName: 'Ahmed', lastName: 'Al Maktoum', email: 'ahmed@desertfruits.ae' }, companyName: 'Desert Fruits', country: 'UAE' },
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
              <Globe className="w-6 h-6" /> International Buyers
            </h1>
            <p className="text-[#606C38] text-sm mt-1">Manage B2B import partners and clients</p>
          </div>
          <button className="bg-[#1B4332] text-white px-4 py-2 rounded-lg font-medium hover:bg-[#133024] transition-colors shadow-sm">
            + Add Buyer
          </button>
        </div>

        <div className="bg-white rounded-xl shadow-sm border border-[#E6E8E3] overflow-hidden">
          <table className="w-full text-left text-sm">
            <thead className="bg-[#F0F2EB] text-[#606C38] border-b border-[#E6E8E3]">
              <tr>
                <th className="px-6 py-4 font-semibold tracking-wider">COMPANY</th>
                <th className="px-6 py-4 font-semibold tracking-wider">CONTACT PERSON</th>
                <th className="px-6 py-4 font-semibold tracking-wider">REGION</th>
                <th className="px-6 py-4 text-right font-semibold tracking-wider">ACTIONS</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#E6E8E3]">
              {loading ? (
                <tr><td colSpan={4} className="p-8 text-center text-gray-400">Loading...</td></tr>
              ) : (
                buyers.map((buyer) => (
                  <tr key={buyer.id} className="hover:bg-[#F9F9F6] transition-colors">
                    <td className="px-6 py-4">
                      <div className="font-bold text-[#1B4332] flex items-center gap-2">
                        <Building2 className="w-4 h-4 text-[#606C38]" /> {buyer.companyName}
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      <div className="font-medium text-gray-800">{buyer.user.firstName} {buyer.user.lastName}</div>
                      <div className="text-xs text-gray-500 mt-0.5">{buyer.user.email}</div>
                    </td>
                    <td className="px-6 py-4">
                      <span className="inline-flex items-center gap-1 bg-[#E2E4DC] text-[#1B4332] px-2.5 py-1 rounded-md text-xs font-bold tracking-wider">
                        {buyer.country}
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
