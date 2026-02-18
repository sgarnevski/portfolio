import { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../../store';
import { removeHoldingRequest } from '../../store/slices/holdingSlice';
import { formatCurrency } from '../../utils/formatCurrency';
import { Holding } from '../../types/holding';
import AddHoldingModal from './AddHoldingModal';

interface Props {
  portfolioId: number;
}

export default function HoldingsTable({ portfolioId }: Props) {
  const dispatch = useDispatch();
  const { holdings } = useSelector((state: RootState) => state.holding);
  const prices = useSelector((state: RootState) => state.price.prices);
  const [editingHolding, setEditingHolding] = useState<Holding | null>(null);

  if (holdings.length === 0) {
    return <p className="text-gray-500 text-center py-8">No holdings yet. Add your first ETF.</p>;
  }

  return (
    <>
      <div className="bg-white rounded-lg shadow-sm border overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 border-b">
            <tr>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Ticker</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Name</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Class</th>
              <th className="text-right px-4 py-3 font-medium text-gray-600">Qty</th>
              <th className="text-right px-4 py-3 font-medium text-gray-600">Price</th>
              <th className="text-right px-4 py-3 font-medium text-gray-600">Value</th>
              <th className="text-right px-4 py-3 font-medium text-gray-600">Cost Basis</th>
              <th className="text-right px-4 py-3 font-medium text-gray-600">P/L</th>
              <th className="text-right px-4 py-3 font-medium text-gray-600">Change</th>
              <th className="px-4 py-3"></th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {holdings.map((h) => {
              const quote = prices[h.tickerSymbol];
              const price = quote?.regularMarketPrice ?? 0;
              const value = h.quantity * price;
              const change = quote?.regularMarketChangePercent ?? 0;
              const profitLoss = (h.initialValue != null && value > 0) ? value - h.initialValue : null;
              return (
                <tr key={h.id} className="hover:bg-gray-50">
                  <td className="px-4 py-3 font-medium">{h.tickerSymbol}</td>
                  <td className="px-4 py-3 text-gray-600">{h.name}</td>
                  <td className="px-4 py-3">
                    <span className="bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded">{h.assetClass}</span>
                  </td>
                  <td className="px-4 py-3 text-right">{h.quantity}</td>
                  <td className="px-4 py-3 text-right">{price > 0 ? formatCurrency(price, h.currency || 'USD') : '--'}</td>
                  <td className="px-4 py-3 text-right font-medium">{value > 0 ? formatCurrency(value, h.currency || 'USD') : '--'}</td>
                  <td className="px-4 py-3 text-right">{h.initialValue != null ? formatCurrency(h.initialValue, h.currency || 'USD') : '--'}</td>
                  <td className={`px-4 py-3 text-right font-medium ${profitLoss != null ? (profitLoss >= 0 ? 'text-green-600' : 'text-red-600') : ''}`}>
                    {profitLoss != null ? `${profitLoss >= 0 ? '+' : ''}${formatCurrency(profitLoss, h.currency || 'USD')}` : '--'}
                  </td>
                  <td className={`px-4 py-3 text-right ${change >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                    {change !== 0 ? `${change >= 0 ? '+' : ''}${change.toFixed(2)}%` : '--'}
                  </td>
                  <td className="px-4 py-3 text-right">
                    <button
                      onClick={() => setEditingHolding(h)}
                      className="text-blue-500 hover:text-blue-700 text-xs mr-2"
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => dispatch(removeHoldingRequest({ portfolioId, holdingId: h.id }))}
                      className="text-red-500 hover:text-red-700 text-xs"
                    >
                      Remove
                    </button>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
      {editingHolding && (
        <AddHoldingModal
          portfolioId={portfolioId}
          holding={editingHolding}
          onClose={() => setEditingHolding(null)}
        />
      )}
    </>
  );
}
