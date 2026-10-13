import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { billingService, patientService } from '../services/modules';
import { Bill, Patient } from '../types';
import { Receipt, CreditCard, Plus, X } from 'lucide-react';

export const BillingPage: React.FC = () => {
  const { user } = useAuth();
  const [bills, setBills] = useState<Bill[]>([]);
  const [loading, setLoading] = useState(true);
  const [patients, setPatients] = useState<Patient[]>([]);

  // Payment modal state
  const [selectedBill, setSelectedBill] = useState<Bill | null>(null);
  const [showPayModal, setShowPayModal] = useState(false);
  const [payAmount, setPayAmount] = useState<number>(0);
  const [payMethod, setPayMethod] = useState<'CARD' | 'CASH' | 'UPI' | 'INSURANCE'>('CARD');

  // Create Bill modal state (Admin & Receptionist only)
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [newBill, setNewBill] = useState({
    patientId: 1,
    itemType: 'CONSULTATION',
    description: 'Specialist Consultation Fee',
    quantity: 1,
    unitPrice: 120.0,
    notes: 'Inpatient / Outpatient charges'
  });

  const loadBills = async () => {
    try {
      setLoading(true);
      if (user?.role === 'PATIENT') {
        const pid = user.patientId || 1;
        const data = await billingService.getBillsByPatient(pid);
        setBills(data);
      } else {
        const data = await billingService.getBills();
        setBills(data);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadBills();
    if (user?.role === 'ADMIN' || user?.role === 'RECEPTIONIST') {
      patientService.getAll().then(pats => {
        setPatients(pats);
        if (pats.length > 0) {
          setNewBill(prev => ({ ...prev, patientId: pats[0].id }));
        }
      }).catch(() => {});
    }
  }, [user]);

  const handleOpenPay = (b: Bill) => {
    setSelectedBill(b);
    setPayAmount(b.balanceAmount);
    setShowPayModal(true);
  };

  const handlePayment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedBill) return;
    try {
      await billingService.payBill(selectedBill.id, {
        amount: Number(payAmount),
        paymentMethod: payMethod,
        transactionNote: `Settlement via portal TXN-${Date.now().toString().slice(-6)}`
      });
      setShowPayModal(false);
      loadBills();
    } catch (err: any) {
      alert(err.message || 'Payment simulation failed');
    }
  };

  const handleCreateBill = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await billingService.createBill({
        patientId: Number(newBill.patientId),
        notes: newBill.notes,
        items: [
          {
            itemType: newBill.itemType,
            description: newBill.description,
            quantity: Number(newBill.quantity),
            unitPrice: Number(newBill.unitPrice)
          }
        ]
      });
      setShowCreateModal(false);
      loadBills();
    } catch (err: any) {
      alert(err.message || 'Failed to create invoice');
    }
  };

  // Only patient (owning bill) or front-desk cashier/admin can pay bills. Doctors and staff cannot.
  const canPayBill = (bill: Bill) => {
    if (bill.balanceAmount <= 0) return false;
    if (user?.role === 'ADMIN' || user?.role === 'RECEPTIONIST') return true;
    if (user?.role === 'PATIENT' && bill.patientId === (user.patientId || 1)) return true;
    return false;
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Invoices & Billing</h1>
          <p className="page-subtitle">Patient invoices, payment tracking, and balance ledger</p>
        </div>
        {(user?.role === 'ADMIN' || user?.role === 'RECEPTIONIST') && (
          <button onClick={() => setShowCreateModal(true)} className="btn btn-primary">
            <Plus size={16} />
            <span>Create Invoice</span>
          </button>
        )}
      </div>

      <div className="table-container">
        <table>
          <thead>
            <tr>
              <th>Bill Number</th>
              <th>Patient ID</th>
              <th>Total Amount</th>
              <th>Paid Amount</th>
              <th>Balance</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={7} style={{ textAlign: 'center', padding: '30px' }}>Loading invoices...</td></tr>
            ) : bills.length === 0 ? (
              <tr><td colSpan={7} style={{ textAlign: 'center', padding: '30px' }}>No invoices found.</td></tr>
            ) : (
              bills.map(b => (
                <tr key={b.id}>
                  <td style={{ fontWeight: 600 }}>{b.billNumber}</td>
                  <td>Patient #{b.patientId}</td>
                  <td>${b.totalAmount.toFixed(2)}</td>
                  <td>${b.paidAmount.toFixed(2)}</td>
                  <td style={{ fontWeight: 600, color: b.balanceAmount > 0 ? 'var(--danger)' : 'var(--success)' }}>
                    ${b.balanceAmount.toFixed(2)}
                  </td>
                  <td>
                    <span className={`badge ${b.status === 'PAID' ? 'badge-success' : b.status === 'PARTIALLY_PAID' ? 'badge-warning' : 'badge-danger'}`}>
                      {b.status}
                    </span>
                  </td>
                  <td>
                    {canPayBill(b) ? (
                      <button onClick={() => handleOpenPay(b)} className="btn btn-primary btn-sm">
                        <CreditCard size={14} />
                        <span>Pay Bill</span>
                      </button>
                    ) : b.balanceAmount <= 0 ? (
                      <span style={{ fontSize: '0.8rem', color: 'var(--success)', fontWeight: 600 }}>Settled</span>
                    ) : (
                      <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Pending Patient</span>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Pay Bill Modal */}
      {showPayModal && selectedBill && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h3 style={{ fontSize: '1.1rem', fontWeight: 600 }}>Process Payment for {selectedBill.billNumber}</h3>
              <button onClick={() => setShowPayModal(false)} className="btn btn-secondary btn-sm"><X size={16} /></button>
            </div>
            <form onSubmit={handlePayment}>
              <div className="modal-body" style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
                <div style={{ background: '#f8fafc', padding: '12px', borderRadius: '6px', fontSize: '0.875rem' }}>
                  <div>Total Invoiced: <strong>${selectedBill.totalAmount.toFixed(2)}</strong></div>
                  <div>Already Paid: <strong>${selectedBill.paidAmount.toFixed(2)}</strong></div>
                  <div style={{ color: 'var(--danger)' }}>Outstanding Balance: <strong>${selectedBill.balanceAmount.toFixed(2)}</strong></div>
                </div>

                <div className="form-group">
                  <label className="form-label">Payment Amount ($) *</label>
                  <input
                    type="number"
                    step="0.01"
                    min="1"
                    max={selectedBill.balanceAmount}
                    className="form-control"
                    required
                    value={payAmount}
                    onChange={e => setPayAmount(Number(e.target.value))}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Payment Method *</label>
                  <select
                    className="form-control"
                    value={payMethod}
                    onChange={e => setPayMethod(e.target.value as any)}
                  >
                    <option value="CARD">Credit / Debit Card</option>
                    <option value="UPI">UPI / Digital Wallet</option>
                    <option value="CASH">Cash</option>
                    <option value="INSURANCE">Insurance</option>
                  </select>
                </div>
              </div>

              <div className="modal-footer">
                <button type="button" onClick={() => setShowPayModal(false)} className="btn btn-secondary">Cancel</button>
                <button type="submit" className="btn btn-primary">Submit Payment</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Create Invoice Modal (Admin & Receptionist) */}
      {showCreateModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h3 style={{ fontSize: '1.1rem', fontWeight: 600 }}>Create New Invoice</h3>
              <button onClick={() => setShowCreateModal(false)} className="btn btn-secondary btn-sm"><X size={16} /></button>
            </div>
            <form onSubmit={handleCreateBill}>
              <div className="modal-body" style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
                <div className="form-group">
                  <label className="form-label">Patient *</label>
                  <select
                    className="form-control"
                    value={newBill.patientId}
                    onChange={e => setNewBill({ ...newBill, patientId: Number(e.target.value) })}
                  >
                    {patients.map(p => (
                      <option key={p.id} value={p.id}>{p.firstName} {p.lastName} ({p.patientCode})</option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label">Item Category *</label>
                  <select
                    className="form-control"
                    value={newBill.itemType}
                    onChange={e => setNewBill({ ...newBill, itemType: e.target.value })}
                  >
                    <option value="CONSULTATION">CONSULTATION (Physician Exam)</option>
                    <option value="MEDICINE">MEDICINE (Pharmaceutical)</option>
                    <option value="TEST">TEST (Diagnostics / Pathology)</option>
                    <option value="ROOM">ROOM (Inpatient Accommodation)</option>
                    <option value="OTHER">OTHER (Miscellaneous)</option>
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label">Item Description *</label>
                  <input
                    type="text"
                    className="form-control"
                    required
                    value={newBill.description}
                    onChange={e => setNewBill({ ...newBill, description: e.target.value })}
                  />
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                  <div className="form-group">
                    <label className="form-label">Quantity *</label>
                    <input
                      type="number"
                      min="1"
                      className="form-control"
                      required
                      value={newBill.quantity}
                      onChange={e => setNewBill({ ...newBill, quantity: Number(e.target.value) })}
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Unit Price ($) *</label>
                    <input
                      type="number"
                      step="0.01"
                      min="1"
                      className="form-control"
                      required
                      value={newBill.unitPrice}
                      onChange={e => setNewBill({ ...newBill, unitPrice: Number(e.target.value) })}
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label className="form-label">Notes</label>
                  <input
                    type="text"
                    className="form-control"
                    value={newBill.notes}
                    onChange={e => setNewBill({ ...newBill, notes: e.target.value })}
                  />
                </div>
              </div>

              <div className="modal-footer">
                <button type="button" onClick={() => setShowCreateModal(false)} className="btn btn-secondary">Cancel</button>
                <button type="submit" className="btn btn-primary">Generate Bill</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
