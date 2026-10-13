export type Role = 'ADMIN' | 'DOCTOR' | 'STAFF' | 'RECEPTIONIST' | 'PATIENT';

export interface User {
  id: number;
  username: string;
  fullName: string;
  email: string;
  phone?: string;
  role: Role;
  patientId?: number;
  doctorId?: number;
}

export interface Patient {
  id: number;
  patientCode: string;
  userId?: number;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  gender: string;
  phone: string;
  email: string;
  address: string;
  bloodGroup: string;
  emergencyContact: string;
  createdAt?: string;
}

export interface MedicalHistory {
  id: number;
  patientId: number;
  diagnosis: string;
  treatment: string;
  prescriptions?: string;
  allergies?: string;
  notes?: string;
  recordDate: string;
}

export interface Immunization {
  id: number;
  patientId: number;
  vaccineName: string;
  doseNumber: number;
  dateAdministered: string;
  administeredBy?: string;
  remarks?: string;
}

export interface Doctor {
  id: number;
  doctorCode: string;
  userId?: number;
  fullName: string;
  specialization: string;
  department: string;
  qualification: string;
  experienceYears: number;
  phone: string;
  email: string;
  consultationFee: number;
  status: 'ACTIVE' | 'ON_LEAVE' | 'INACTIVE';
}

export interface Staff {
  id: number;
  staffCode: string;
  userId?: number;
  fullName: string;
  designation: string;
  department: string;
  phone: string;
  email: string;
  joiningDate: string;
  shiftSchedule: string;
  status: 'ACTIVE' | 'ON_LEAVE' | 'INACTIVE';
}

export interface Appointment {
  id: number;
  appointmentCode: string;
  patientId: number;
  doctorId: number;
  appointmentDate: string;
  startTime: string;
  endTime: string;
  reason: string;
  status: 'SCHEDULED' | 'CONFIRMED' | 'COMPLETED' | 'CANCELLED' | 'RESCHEDULED';
  notes?: string;
  createdAt?: string;
}

export interface Medicine {
  id: number;
  medicineCode: string;
  name: string;
  genericName: string;
  category: string;
  dosageForm: string;
  strength: string;
  unitPrice: number;
  stockQuantity: number;
  reorderLevel: number;
  expiryDate: string;
  requiresPrescription: boolean;
  status: 'ACTIVE' | 'DISCONTINUED' | 'OUT_OF_STOCK';
}

export interface PrescriptionItem {
  id?: number;
  medicineId: number;
  medicineName?: string;
  dosage: string;
  frequency: string;
  durationDays: number;
  quantity: number;
  instructions?: string;
}

export interface Prescription {
  id: number;
  prescriptionNumber: string;
  patientId: number;
  doctorId: number;
  appointmentId?: number;
  diagnosis: string;
  status: 'PENDING' | 'DISPENSED' | 'CANCELLED';
  notes?: string;
  items: PrescriptionItem[];
  createdAt?: string;
  dispensedAt?: string;
}

export interface BillItem {
  id?: number;
  itemType: 'CONSULTATION' | 'MEDICINE' | 'TEST' | 'ROOM' | 'OTHER';
  description: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
}

export interface Bill {
  id: number;
  billNumber: string;
  patientId: number;
  subtotal: number;
  discount: number;
  tax: number;
  insuranceCoveredAmount: number;
  totalAmount: number;
  paidAmount: number;
  balanceAmount: number;
  status: 'PENDING' | 'PARTIALLY_PAID' | 'PAID' | 'CANCELLED';
  items?: BillItem[];
  createdAt?: string;
}

export interface Payment {
  id: number;
  paymentCode: string;
  billId: number;
  amount: number;
  paymentMethod: 'CASH' | 'CARD' | 'UPI' | 'INSURANCE';
  status: 'SUCCESS' | 'FAILED' | 'REFUNDED';
  transactionReference: string;
  createdAt: string;
}

export interface Report {
  id: number;
  reportCode: string;
  patientId?: number;
  reportType: 'MEDICAL_SUMMARY' | 'APPOINTMENT_SUMMARY' | 'BILLING_SUMMARY' | 'PRESCRIPTION_SUMMARY' | 'GENERAL';
  title: string;
  description: string;
  reportDate: string;
  status: 'AVAILABLE' | 'ARCHIVED';
  data?: string;
  createdAt?: string;
}

export interface Notification {
  id: number;
  notificationCode: string;
  userId: number;
  patientId?: number;
  type: 'APPOINTMENT' | 'PRESCRIPTION' | 'BILLING' | 'PAYMENT' | 'PHARMACY' | 'GENERAL';
  title: string;
  message: string;
  status: 'UNREAD' | 'READ';
  createdAt: string;
  readAt?: string;
}

export interface DashboardSummary {
  totalPatients: number;
  totalDoctors: number;
  totalAppointments: number;
  pendingAppointments: number;
  completedAppointments: number;
  outstandingBilling: number;
  pendingPrescriptions: number;
  lowStockMedicineCount: number;
}
