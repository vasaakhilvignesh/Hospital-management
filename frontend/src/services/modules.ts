import { request } from './api';
import {
  User, Patient, MedicalHistory, Immunization, Doctor, Staff,
  Appointment, Medicine, Prescription, Bill, Payment, Report,
  Notification, DashboardSummary
} from '../types';

export const authService = {
  login: (credentials: { username: string; password: string }) =>
    request<{ token: string; user?: User }>('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify(credentials)
    }),
  register: (data: any) =>
    request<User>('/api/auth/register', {
      method: 'POST',
      body: JSON.stringify(data)
    }),
  getMe: () => request<User>('/api/auth/me'),
};

export const patientService = {
  getAll: () => request<Patient[]>('/api/patients'),
  getById: (id: number) => request<Patient>(`/api/patients/${id}`),
  getByCode: (code: string) => request<Patient>(`/api/patients/code/${code}`),
  getMe: () => request<Patient>('/api/patients/me'),
  registerPatient: (data: any) =>
    request<Patient>('/api/patients/register', {
      method: 'POST',
      body: JSON.stringify(data)
    }),
  search: (query: string) => request<Patient[]>(`/api/patients/search?query=${encodeURIComponent(query)}`),
  create: (data: Partial<Patient>) => request<Patient>('/api/patients', { method: 'POST', body: JSON.stringify(data) }),
  update: (id: number, data: Partial<Patient>) => request<Patient>(`/api/patients/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  delete: (id: number) => request<void>(`/api/patients/${id}`, { method: 'DELETE' }),

  getHistory: (patientId: number) => request<MedicalHistory[]>(`/api/patients/${patientId}/history`),
  addHistory: (patientId: number, data: Partial<MedicalHistory>) =>
    request<MedicalHistory>(`/api/patients/${patientId}/history`, { method: 'POST', body: JSON.stringify(data) }),

  getImmunizations: (patientId: number) => request<Immunization[]>(`/api/patients/${patientId}/immunizations`),
  addImmunization: (patientId: number, data: Partial<Immunization>) =>
    request<Immunization>(`/api/patients/${patientId}/immunizations`, { method: 'POST', body: JSON.stringify(data) }),
};

export const doctorService = {
  getAll: () => request<Doctor[]>('/api/doctors'),
  getById: (id: number) => request<Doctor>(`/api/doctors/${id}`),
  getMe: () => request<Doctor>('/api/doctors/me'),
  search: (query: string) => request<Doctor[]>(`/api/doctors/search?query=${encodeURIComponent(query)}`),
  getAvailability: (id: number) => request<any[]>(`/api/doctors/${id}/availability`),
  create: (data: Partial<Doctor>) => request<Doctor>('/api/doctors', { method: 'POST', body: JSON.stringify(data) }),
  update: (id: number, data: Partial<Doctor>) => request<Doctor>(`/api/doctors/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
};

export const staffService = {
  getAll: () => request<Staff[]>('/api/staff'),
  getById: (id: number) => request<Staff>(`/api/staff/${id}`),
  search: (query: string) => request<Staff[]>(`/api/staff/search?query=${encodeURIComponent(query)}`),
  create: (data: Partial<Staff>) => request<Staff>('/api/staff', { method: 'POST', body: JSON.stringify(data) }),
};

export const appointmentService = {
  getAll: () => request<Appointment[]>('/api/appointments'),
  getById: (id: number) => request<Appointment>(`/api/appointments/${id}`),
  getByPatient: (patientId: number) => request<Appointment[]>(`/api/appointments/patient/${patientId}`),
  getByDoctor: (doctorId: number) => request<Appointment[]>(`/api/appointments/doctor/${doctorId}`),
  create: (data: any) => request<Appointment>('/api/appointments', { method: 'POST', body: JSON.stringify(data) }),
  updateStatus: (id: number, status: string) =>
    request<Appointment>(`/api/appointments/${id}/status`, {
      method: 'PATCH',
      body: JSON.stringify({ status }),
    }),
  reschedule: (id: number, data: any) =>
    request<Appointment>(`/api/appointments/${id}/reschedule`, { method: 'PATCH', body: JSON.stringify(data) }),
  cancel: (id: number) => request<Appointment>(`/api/appointments/${id}/cancel`, { method: 'PATCH' }),
  getAvailableSlots: (doctorId: number, date: string) =>
    request<{ startTime: string; endTime: string; available: boolean }[]>(
      `/api/appointments/available-slots?doctorId=${doctorId}&date=${date}`
    ),
};

export const pharmacyService = {
  getMedicines: () => request<Medicine[]>('/api/medicines'),
  getLowStockMedicines: () => request<any[]>('/api/inventory/low-stock'),
  getMedicineById: (id: number) => request<Medicine>(`/api/medicines/${id}`),
  createMedicine: (data: Partial<Medicine>) => request<Medicine>('/api/medicines', { method: 'POST', body: JSON.stringify(data) }),
  updateStock: (id: number, quantity: number) =>
    request<any>('/api/inventory', {
      method: 'POST',
      body: JSON.stringify({
        medicineId: id,
        batchNumber: `BATCH-${Date.now().toString().slice(-5)}`,
        quantity: quantity,
        reorderLevel: 20,
        expiryDate: '2028-01-01',
        unitPurchasePrice: 5.0
      })
    }),

  getPrescriptions: () => request<Prescription[]>('/api/prescriptions'),
  getPrescriptionById: (id: number) => request<Prescription>(`/api/prescriptions/${id}`),
  getPrescriptionsByPatient: (patientId: number) => request<Prescription[]>(`/api/prescriptions/patient/${patientId}`),
  createPrescription: (data: any) => request<Prescription>('/api/prescriptions', { method: 'POST', body: JSON.stringify(data) }),
  dispensePrescription: (id: number) => request<Prescription>(`/api/prescriptions/${id}/dispense`, { method: 'PATCH' }),
};

export const billingService = {
  getBills: () => request<Bill[]>('/api/bills'),
  getBillById: (id: number) => request<Bill>(`/api/bills/${id}`),
  getBillsByPatient: (patientId: number) => request<Bill[]>(`/api/bills/patient/${patientId}`),
  createBill: (data: any) => request<Bill>('/api/bills', { method: 'POST', body: JSON.stringify(data) }),
  payBill: (billId: number, data: { amount: number; paymentMethod: string; transactionNote?: string }) =>
    request<Payment>('/api/payments', {
      method: 'POST',
      body: JSON.stringify({
        billId,
        amount: data.amount,
        paymentMethod: data.paymentMethod,
        transactionNote: data.transactionNote || 'Payment recorded via portal'
      })
    }),
  getPaymentsByBill: (billId: number) => request<Payment[]>(`/api/payments/bill/${billId}`),
};

export const reportService = {
  getReports: () => request<Report[]>('/api/reports'),
  getReportById: (id: number) => request<Report>(`/api/reports/${id}`),
  getReportsByPatient: (patientId: number) => request<Report[]>(`/api/reports/patient/${patientId}`),
  getDashboardSummary: () => request<DashboardSummary>('/api/reports/dashboard/summary'),
  generateReport: (data: { patientId?: number; reportType: string; title: string; description: string }) =>
    request<Report>('/api/reports', { method: 'POST', body: JSON.stringify(data) }),
  archiveReport: (id: number) => request<Report>(`/api/reports/${id}/archive`, { method: 'PATCH' }),
};

export const notificationService = {
  getNotifications: () => request<Notification[]>('/api/notifications'),
  getByUser: (userId: number) => request<Notification[]>(`/api/notifications/user/${userId}`),
  getUnreadCount: () => request<{ unreadCount: number }>('/api/notifications/unread-count'),
  markAsRead: (id: number) => request<Notification>(`/api/notifications/${id}/read`, { method: 'PATCH' }),
  markAllAsRead: () => request<void>('/api/notifications/read-all', { method: 'PATCH' }),
};
