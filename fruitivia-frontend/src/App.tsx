import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import AdminDashboard from './pages/admin/AdminDashboard';
import AdminLayout from './layouts/AdminLayout';
import UsersPage from './pages/admin/UsersPage';
import FarmersPage from './pages/admin/FarmersPage';
import BuyersPage from './pages/admin/BuyersPage';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/admin" />} />
        <Route path="/admin" element={<AdminLayout />}>
          <Route index element={<AdminDashboard />} />
          <Route path="users" element={<UsersPage />} />
          <Route path="farmers" element={<FarmersPage />} />
          <Route path="buyers" element={<BuyersPage />} />
        </Route>
        {/* Placeholders for other routes */}
        <Route path="/buyer" element={<div className="p-8">Buyer Dashboard - Coming Soon</div>} />
        <Route path="/engineer" element={<div className="p-8">Engineer Dashboard - Coming Soon</div>} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
