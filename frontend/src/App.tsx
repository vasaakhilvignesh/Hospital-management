import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ProtectedRoute } from './components/ProtectedRoute';
import { AppLayout } from './layouts/AppLayout';

import { LoginPage } from './pages/LoginPage';
import { DashboardPage } from './pages/DashboardPage';
import { PatientsPage } from './pages/PatientsPage';
import { DoctorsPage } from './pages/DoctorsPage';
import { StaffPage } from './pages/StaffPage';
import { AppointmentsPage } from './pages/AppointmentsPage';
import { PharmacyPage } from './pages/PharmacyPage';
import { PrescriptionsPage } from './pages/PrescriptionsPage';
import { BillingPage } from './pages/BillingPage';
import { ReportsPage } from './pages/ReportsPage';
import { NotificationsPage } from './pages/NotificationsPage';

export function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />

          <Route element={<ProtectedRoute />}>
            <Route element={<AppLayout />}>
              <Route path="/" element={<DashboardPage />} />
              <Route path="/appointments" element={<AppointmentsPage />} />
              <Route path="/prescriptions" element={<PrescriptionsPage />} />
              <Route path="/billing" element={<BillingPage />} />
              <Route path="/reports" element={<ReportsPage />} />
              <Route path="/notifications" element={<NotificationsPage />} />

              {/* Roles: ADMIN, DOCTOR, RECEPTIONIST, STAFF */}
              <Route element={<ProtectedRoute allowedRoles={['ADMIN', 'DOCTOR', 'RECEPTIONIST', 'STAFF']} />}>
                <Route path="/patients" element={<PatientsPage />} />
                <Route path="/doctors" element={<DoctorsPage />} />
                <Route path="/pharmacy" element={<PharmacyPage />} />
              </Route>

              {/* Roles: ADMIN, RECEPTIONIST */}
              <Route element={<ProtectedRoute allowedRoles={['ADMIN', 'RECEPTIONIST']} />}>
                <Route path="/staff" element={<StaffPage />} />
              </Route>
            </Route>
          </Route>

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
