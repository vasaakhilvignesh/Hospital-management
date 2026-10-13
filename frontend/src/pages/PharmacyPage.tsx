import React, { useState, useEffect } from 'react';
import { pharmacyService } from '../services/modules';
import { Medicine } from '../types';
import { Pill, AlertTriangle, Plus, PackagePlus, X } from 'lucide-react';

export const PharmacyPage: React.FC = () => {
  const [medicines, setMedicines] = useState<Medicine[]>([]);
  const [lowStock, setLowStock] = useState<Medicine[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'ALL' | 'LOW_STOCK'>('ALL');
  const [showStockModal, setShowStockModal] = useState(false);
  const [selectedMed, setSelectedMed] = useState<Medicine | null>(null);
  const [addQty, setAddQty] = useState(20);

  const loadPharmacyData = async () => {
    try {
      setLoading(true);
      const [allMeds, lowMeds] = await Promise.all([
        pharmacyService.getMedicines(),
        pharmacyService.getLowStockMedicines()
      ]);
      setMedicines(allMeds);
      setLowStock(lowMeds);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPharmacyData();
  }, []);

  const handleUpdateStock = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedMed) return;
    try {
      await pharmacyService.updateStock(selectedMed.id, addQty);
      setShowStockModal(false);
      loadPharmacyData();
    } catch (err: any) {
      alert(err.message || 'Failed to update stock');
    }
  };

  const displayList = activeTab === 'ALL' ? medicines : lowStock;

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Pharmacy & Inventory</h1>
          <p className="page-subtitle">Pharmaceutical medicines, batch inventory, and reorder alerts</p>
        </div>
      </div>

      <div style={{ display: 'flex', gap: '10px', marginBottom: '20px' }}>
        <button
          onClick={() => setActiveTab('ALL')}
          className={`btn ${activeTab === 'ALL' ? 'btn-primary' : 'btn-secondary'}`}
        >
          All Medicines ({medicines.length})
        </button>
        <button
          onClick={() => setActiveTab('LOW_STOCK')}
          className={`btn ${activeTab === 'LOW_STOCK' ? 'btn-danger' : 'btn-secondary'}`}
        >
          <AlertTriangle size={15} />
          <span>Low Stock Alerts ({lowStock.length})</span>
        </button>
      </div>

      <div className="table-container">
        <table>
          <thead>
            <tr>
              <th>Code</th>
              <th>Name / Generic</th>
              <th>Category</th>
              <th>Form / Strength</th>
              <th>Price</th>
              <th>Stock Qty</th>
              <th>Reorder Level</th>
              <th>Expiry</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={9} style={{ textAlign: 'center', padding: '30px' }}>Loading medicines...</td></tr>
            ) : displayList.length === 0 ? (
              <tr><td colSpan={9} style={{ textAlign: 'center', padding: '30px' }}>No medicines found.</td></tr>
            ) : (
              displayList.map(m => (
                <tr key={m.id}>
                  <td style={{ fontWeight: 600 }}>{m.medicineCode}</td>
                  <td>
                    <div><strong>{m.name}</strong></div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{m.genericName}</div>
                  </td>
                  <td><span className="badge badge-info">{m.category}</span></td>
                  <td>{m.dosageForm} ({m.strength})</td>
                  <td>${m.unitPrice.toFixed(2)}</td>
                  <td>
                    <span className={`badge ${m.stockQuantity <= m.reorderLevel ? 'badge-danger' : 'badge-success'}`}>
                      {m.stockQuantity} in stock
                    </span>
                  </td>
                  <td>{m.reorderLevel}</td>
                  <td>{m.expiryDate}</td>
                  <td>
                    <button
                      onClick={() => { setSelectedMed(m); setShowStockModal(true); }}
                      className="btn btn-secondary btn-sm"
                      title="Restock"
                    >
                      <PackagePlus size={14} />
                      <span>Restock</span>
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {showStockModal && selectedMed && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h3 style={{ fontSize: '1.1rem', fontWeight: 600 }}>Restock {selectedMed.name}</h3>
              <button onClick={() => setShowStockModal(false)} className="btn btn-secondary btn-sm"><X size={16} /></button>
            </div>
            <form onSubmit={handleUpdateStock}>
              <div className="modal-body">
                <p style={{ marginBottom: '16px', fontSize: '0.9rem', color: 'var(--text-muted)' }}>
                  Current inventory level: <strong>{selectedMed.stockQuantity} units</strong> (Reorder threshold: {selectedMed.reorderLevel}).
                </p>
                <div className="form-group">
                  <label className="form-label">Units to Add *</label>
                  <input
                    type="number"
                    min="1"
                    className="form-control"
                    required
                    value={addQty}
                    onChange={e => setAddQty(Number(e.target.value))}
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" onClick={() => setShowStockModal(false)} className="btn btn-secondary">Cancel</button>
                <button type="submit" className="btn btn-primary">Add to Inventory</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
