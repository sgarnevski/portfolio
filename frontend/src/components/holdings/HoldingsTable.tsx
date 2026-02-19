import { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { RootState } from '../../store';
import { addHoldingRequest, updateHoldingRequest, removeHoldingRequest } from '../../store/slices/holdingSlice';
import { fetchCurrenciesRequest } from '../../store/slices/currencySlice';
import { formatCurrency } from '../../utils/formatCurrency';
import { Holding, AssetClass, CreateHoldingRequest } from '../../types/holding';

interface Props {
  portfolioId: number;
}

const ASSET_CLASSES: AssetClass[] = ['EQUITY', 'BOND', 'COMMODITY', 'REAL_ESTATE', 'CASH'];

export default function HoldingsTable({ portfolioId }: Props) {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { holdings } = useSelector((state: RootState) => state.holding);
  const prices = useSelector((state: RootState) => state.price.prices);
  const { currencies } = useSelector((state: RootState) => state.currency);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [addingNew, setAddingNew] = useState(false);

  useEffect(() => {
    if (currencies.length === 0) dispatch(fetchCurrenciesRequest());
  }, [dispatch, currencies.length]);

  const { register, handleSubmit, reset, formState: { errors } } = useForm<CreateHoldingRequest>();

  const startEdit = (h: Holding) => {
    setAddingNew(false);
    setEditingId(h.id);
    reset({
      tickerSymbol: h.tickerSymbol,
      name: h.name,
      assetClass: h.assetClass,
      currency: h.currency,
    });
  };

  const startAdd = () => {
    setEditingId(null);
    setAddingNew(true);
    reset({
      tickerSymbol: '',
      name: '',
      assetClass: 'EQUITY',
      currency: '',
    });
  };

  const cancelEdit = () => {
    setEditingId(null);
    setAddingNew(false);
  };

  const onSubmit = (data: CreateHoldingRequest) => {
    if (addingNew) {
      dispatch(addHoldingRequest({ portfolioId, data }));
      setAddingNew(false);
    } else if (editingId !== null) {
      dispatch(updateHoldingRequest({ portfolioId, holdingId: editingId, data }));
      setEditingId(null);
    }
  };

  return (
    <div>
      <div className="flex justify-end mb-4">
        <button
          onClick={startAdd}
          disabled={addingNew}
          className="bg-blue-600 text-white py-2 px-4 rounded-md text-sm hover:bg-blue-700 disabled:opacity-50"
        >
          + Add Holding
        </button>
      </div>
      {holdings.length === 0 && !addingNew ? (
        <p className="text-gray-500 text-center py-8">No holdings yet. Add your first holding.</p>
      ) : (
        <div className="bg-white rounded-lg shadow-sm border overflow-x-auto">
          <form onSubmit={handleSubmit(onSubmit)}>
            <table className="w-full text-sm">
              <thead className="bg-gray-50 border-b">
                <tr>
                  <th className="text-left px-4 py-3 font-medium text-gray-600">Class</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-600">Ticker</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-600">Name</th>
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
                {addingNew && (
                  <tr className="bg-blue-50">
                    <td className="px-4 py-2">
                      <select
                        {...register('assetClass', { required: 'Required' })}
                        className="w-full rounded border border-gray-300 px-2 py-1 text-sm focus:border-blue-500 focus:outline-none"
                      >
                        {ASSET_CLASSES.map((ac) => (
                          <option key={ac} value={ac}>{ac.replace('_', ' ')}</option>
                        ))}
                      </select>
                    </td>
                    <td className="px-4 py-2">
                      <input
                        {...register('tickerSymbol', { required: 'Required' })}
                        placeholder="IWDA.AS"
                        className="w-full rounded border border-gray-300 px-2 py-1 text-sm focus:border-blue-500 focus:outline-none"
                      />
                      {errors.tickerSymbol && <p className="text-red-500 text-xs">{errors.tickerSymbol.message}</p>}
                    </td>
                    <td className="px-4 py-2">
                      <input
                        {...register('name', { required: 'Required' })}
                        placeholder="iShares Core MSCI World"
                        className="w-full rounded border border-gray-300 px-2 py-1 text-sm focus:border-blue-500 focus:outline-none"
                      />
                      {errors.name && <p className="text-red-500 text-xs">{errors.name.message}</p>}
                    </td>
                    <td className="px-4 py-2 text-right text-gray-400">--</td>
                    <td className="px-4 py-2 text-right text-gray-400">--</td>
                    <td className="px-4 py-2 text-right text-gray-400">--</td>
                    <td className="px-4 py-2 text-right text-gray-400">--</td>
                    <td className="px-4 py-2 text-right text-gray-400">--</td>
                    <td className="px-4 py-2 text-right">
                      <select
                        {...register('currency')}
                        className="w-full rounded border border-gray-300 px-2 py-1 text-sm focus:border-blue-500 focus:outline-none"
                      >
                        <option value="">Currency</option>
                        {currencies.map((c) => (
                          <option key={c.id} value={c.code}>{c.code}</option>
                        ))}
                      </select>
                    </td>
                    <td className="px-4 py-2 text-right whitespace-nowrap">
                      <button type="submit" className="text-green-600 hover:text-green-800 text-xs mr-2">Save</button>
                      <button type="button" onClick={cancelEdit} className="text-gray-500 hover:text-gray-700 text-xs">Cancel</button>
                    </td>
                  </tr>
                )}
                {holdings.map((h) => {
                  const isEditing = editingId === h.id;
                  const quote = prices[h.tickerSymbol];
                  const price = quote?.regularMarketPrice ?? 0;
                  const value = h.quantity * price;
                  const change = quote?.regularMarketChangePercent ?? 0;
                  const totalCost = h.totalCost ?? 0;
                  const profitLoss = totalCost > 0 && value > 0 ? value - totalCost : null;

                  if (isEditing) {
                    return (
                      <tr key={h.id} className="bg-blue-50">
                        <td className="px-4 py-2">
                          <select
                            {...register('assetClass', { required: 'Required' })}
                            className="w-full rounded border border-gray-300 px-2 py-1 text-sm focus:border-blue-500 focus:outline-none"
                          >
                            {ASSET_CLASSES.map((ac) => (
                              <option key={ac} value={ac}>{ac.replace('_', ' ')}</option>
                            ))}
                          </select>
                        </td>
                        <td className="px-4 py-2">
                          <input
                            {...register('tickerSymbol', { required: 'Required' })}
                            className="w-full rounded border border-gray-300 px-2 py-1 text-sm focus:border-blue-500 focus:outline-none"
                          />
                        </td>
                        <td className="px-4 py-2">
                          <input
                            {...register('name', { required: 'Required' })}
                            className="w-full rounded border border-gray-300 px-2 py-1 text-sm focus:border-blue-500 focus:outline-none"
                          />
                        </td>
                        <td className="px-4 py-2 text-right">{h.quantity}</td>
                        <td className="px-4 py-2 text-right">{price > 0 ? formatCurrency(price, h.currency || 'USD') : '--'}</td>
                        <td className="px-4 py-2 text-right font-medium">{value > 0 ? formatCurrency(value, h.currency || 'USD') : '--'}</td>
                        <td className="px-4 py-2 text-right">{totalCost > 0 ? formatCurrency(totalCost, h.currency || 'USD') : '--'}</td>
                        <td className={`px-4 py-2 text-right font-medium ${profitLoss != null ? (profitLoss >= 0 ? 'text-green-600' : 'text-red-600') : ''}`}>
                          {profitLoss != null ? `${profitLoss >= 0 ? '+' : ''}${formatCurrency(profitLoss, h.currency || 'USD')}` : '--'}
                        </td>
                        <td className="px-4 py-2 text-right">
                          <select
                            {...register('currency')}
                            className="w-full rounded border border-gray-300 px-2 py-1 text-sm focus:border-blue-500 focus:outline-none"
                          >
                            <option value="">Currency</option>
                            {currencies.map((c) => (
                              <option key={c.id} value={c.code}>{c.code}</option>
                            ))}
                          </select>
                        </td>
                        <td className="px-4 py-2 text-right whitespace-nowrap">
                          <button type="submit" className="text-green-600 hover:text-green-800 text-xs mr-2">Save</button>
                          <button type="button" onClick={cancelEdit} className="text-gray-500 hover:text-gray-700 text-xs">Cancel</button>
                        </td>
                      </tr>
                    );
                  }

                  return (
                    <tr key={h.id} className="hover:bg-gray-50">
                      <td className="px-4 py-3">
                        <span className="bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded">{h.assetClass}</span>
                      </td>
                      <td className="px-4 py-3 font-medium">{h.tickerSymbol}</td>
                      <td className="px-4 py-3 text-gray-600">{h.name}</td>
                      <td className="px-4 py-3 text-right">{h.quantity}</td>
                      <td className="px-4 py-3 text-right">{price > 0 ? formatCurrency(price, h.currency || 'USD') : '--'}</td>
                      <td className="px-4 py-3 text-right font-medium">{value > 0 ? formatCurrency(value, h.currency || 'USD') : '--'}</td>
                      <td className="px-4 py-3 text-right">{totalCost > 0 ? formatCurrency(totalCost, h.currency || 'USD') : '--'}</td>
                      <td className={`px-4 py-3 text-right font-medium ${profitLoss != null ? (profitLoss >= 0 ? 'text-green-600' : 'text-red-600') : ''}`}>
                        {profitLoss != null ? `${profitLoss >= 0 ? '+' : ''}${formatCurrency(profitLoss, h.currency || 'USD')}` : '--'}
                      </td>
                      <td className={`px-4 py-3 text-right ${change >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                        {change !== 0 ? `${change >= 0 ? '+' : ''}${change.toFixed(2)}%` : '--'}
                      </td>
                      <td className="px-4 py-3 text-right whitespace-nowrap">
                        <button
                          type="button"
                          onClick={() => startEdit(h)}
                          className="text-blue-500 hover:text-blue-700 text-xs mr-2"
                        >
                          Edit
                        </button>
                        <button
                          type="button"
                          onClick={() => dispatch(removeHoldingRequest({ portfolioId, holdingId: h.id }))}
                          className="text-red-500 hover:text-red-700 text-xs mr-2"
                        >
                          Delete
                        </button>
                        <button
                          type="button"
                          onClick={() => navigate(`/portfolios/${portfolioId}/holdings/${h.id}/trades`)}
                          className="text-indigo-500 hover:text-indigo-700 text-xs"
                        >
                          Trades
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </form>
        </div>
      )}
    </div>
  );
}
