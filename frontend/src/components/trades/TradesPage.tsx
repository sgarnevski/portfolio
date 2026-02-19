import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../../store';
import { fetchHoldingsRequest } from '../../store/slices/holdingSlice';
import {
  fetchTradesRequest,
  addTradeRequest,
  updateTradeRequest,
  deleteTradeRequest,
  clearTrades,
} from '../../store/slices/tradeSlice';
import { Trade, CreateTradeRequest } from '../../types/holding';
import { formatCurrency } from '../../utils/formatCurrency';

export default function TradesPage() {
  const { id, holdingId: holdingIdParam } = useParams<{ id: string; holdingId: string }>();
  const portfolioId = Number(id);
  const holdingId = Number(holdingIdParam);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const { holdings } = useSelector((state: RootState) => state.holding);
  const { trades, loading } = useSelector((state: RootState) => state.trade);
  const holding = holdings.find((h) => h.id === holdingId);

  const [editingId, setEditingId] = useState<number | null>(null);
  const [addingNew, setAddingNew] = useState(false);

  const { register, handleSubmit, reset, formState: { errors } } = useForm<CreateTradeRequest>();

  useEffect(() => {
    if (holdings.length === 0) {
      dispatch(fetchHoldingsRequest(portfolioId));
    }
    dispatch(fetchTradesRequest({ portfolioId, holdingId }));
    return () => {
      dispatch(clearTrades());
    };
  }, [dispatch, portfolioId, holdingId, holdings.length]);

  const startEdit = (t: Trade) => {
    setAddingNew(false);
    setEditingId(t.id);
    reset({
      date: t.date,
      type: t.type,
      quantity: t.quantity,
      price: t.price,
      fee: t.fee ?? undefined,
    });
  };

  const startAdd = () => {
    setEditingId(null);
    setAddingNew(true);
    reset({
      date: new Date().toISOString().split('T')[0],
      type: 'BUY',
      quantity: 0,
      price: 0,
      fee: undefined,
    });
  };

  const cancelEdit = () => {
    setEditingId(null);
    setAddingNew(false);
  };

  const onSubmit = (data: CreateTradeRequest) => {
    const payload: CreateTradeRequest = {
      ...data,
      quantity: Number(data.quantity),
      price: Number(data.price),
      fee: data.fee != null && !isNaN(Number(data.fee)) ? Number(data.fee) : undefined,
    };
    if (addingNew) {
      dispatch(addTradeRequest({ portfolioId, holdingId, data: payload }));
      setAddingNew(false);
    } else if (editingId !== null) {
      dispatch(updateTradeRequest({ portfolioId, holdingId, tradeId: editingId, data: payload }));
      setEditingId(null);
    }
  };

  const currency = holding?.currency || 'USD';

  return (
    <div>
      <div className="flex items-center gap-4 mb-6">
        <button
          onClick={() => navigate(`/portfolios/${portfolioId}`)}
          className="text-blue-600 hover:text-blue-800 text-sm"
        >
          &larr; Back
        </button>
        <div>
          <h1 className="text-2xl font-bold">
            {holding ? `${holding.name} (${holding.tickerSymbol})` : 'Trades'}
          </h1>
          {holding && (
            <p className="text-gray-500 text-sm">
              {holding.quantity} shares | Cost basis: {holding.totalCost != null ? formatCurrency(holding.totalCost, currency) : '--'}
            </p>
          )}
        </div>
      </div>

      <div className="flex justify-end mb-4">
        <button
          onClick={startAdd}
          disabled={addingNew}
          className="bg-blue-600 text-white py-2 px-4 rounded-md text-sm hover:bg-blue-700 disabled:opacity-50"
        >
          + Add Trade
        </button>
      </div>

      {loading && trades.length === 0 ? (
        <p className="text-gray-500 text-center py-8">Loading trades...</p>
      ) : trades.length === 0 && !addingNew ? (
        <p className="text-gray-500 text-center py-8">No trades yet. Add your first trade.</p>
      ) : (
        <div className="bg-white rounded-lg shadow-sm border overflow-x-auto">
          <form onSubmit={handleSubmit(onSubmit)}>
            <table className="w-full text-sm">
              <thead className="bg-gray-50 border-b">
                <tr>
                  <th className="text-left px-4 py-3 font-medium text-gray-600">Date</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-600">Type</th>
                  <th className="text-right px-4 py-3 font-medium text-gray-600">Qty</th>
                  <th className="text-right px-4 py-3 font-medium text-gray-600">Price</th>
                  <th className="text-right px-4 py-3 font-medium text-gray-600">Fee</th>
                  <th className="text-right px-4 py-3 font-medium text-gray-600">Total</th>
                  <th className="px-4 py-3"></th>
                </tr>
              </thead>
              <tbody className="divide-y">
                {addingNew && (
                  <tr className="bg-blue-50">
                    <td className="px-4 py-2">
                      <input
                        type="date"
                        {...register('date', { required: 'Required' })}
                        className="rounded border border-gray-300 px-2 py-1 text-sm focus:border-blue-500 focus:outline-none"
                      />
                      {errors.date && <p className="text-red-500 text-xs">{errors.date.message}</p>}
                    </td>
                    <td className="px-4 py-2">
                      <select
                        {...register('type', { required: 'Required' })}
                        className="rounded border border-gray-300 px-2 py-1 text-sm focus:border-blue-500 focus:outline-none"
                      >
                        <option value="BUY">BUY</option>
                        <option value="SELL">SELL</option>
                      </select>
                    </td>
                    <td className="px-4 py-2">
                      <input
                        type="number"
                        step="0.000001"
                        {...register('quantity', { required: 'Required', valueAsNumber: true, min: { value: 0.000001, message: 'Must be positive' } })}
                        className="w-24 rounded border border-gray-300 px-2 py-1 text-sm text-right focus:border-blue-500 focus:outline-none"
                      />
                      {errors.quantity && <p className="text-red-500 text-xs">{errors.quantity.message}</p>}
                    </td>
                    <td className="px-4 py-2">
                      <input
                        type="number"
                        step="0.01"
                        {...register('price', { required: 'Required', valueAsNumber: true, min: { value: 0.01, message: 'Must be positive' } })}
                        className="w-24 rounded border border-gray-300 px-2 py-1 text-sm text-right focus:border-blue-500 focus:outline-none"
                      />
                      {errors.price && <p className="text-red-500 text-xs">{errors.price.message}</p>}
                    </td>
                    <td className="px-4 py-2">
                      <input
                        type="number"
                        step="0.01"
                        {...register('fee', { valueAsNumber: true })}
                        placeholder="0.00"
                        className="w-20 rounded border border-gray-300 px-2 py-1 text-sm text-right focus:border-blue-500 focus:outline-none"
                      />
                    </td>
                    <td className="px-4 py-2 text-right text-gray-400">--</td>
                    <td className="px-4 py-2 text-right whitespace-nowrap">
                      <button type="submit" className="text-green-600 hover:text-green-800 text-xs mr-2">Save</button>
                      <button type="button" onClick={cancelEdit} className="text-gray-500 hover:text-gray-700 text-xs">Cancel</button>
                    </td>
                  </tr>
                )}
                {trades.map((t) => {
                  const isEditing = editingId === t.id;
                  const fee = t.fee ?? 0;
                  const total = t.quantity * t.price + fee;

                  if (isEditing) {
                    return (
                      <tr key={t.id} className="bg-blue-50">
                        <td className="px-4 py-2">
                          <input
                            type="date"
                            {...register('date', { required: 'Required' })}
                            className="rounded border border-gray-300 px-2 py-1 text-sm focus:border-blue-500 focus:outline-none"
                          />
                        </td>
                        <td className="px-4 py-2">
                          <select
                            {...register('type', { required: 'Required' })}
                            className="rounded border border-gray-300 px-2 py-1 text-sm focus:border-blue-500 focus:outline-none"
                          >
                            <option value="BUY">BUY</option>
                            <option value="SELL">SELL</option>
                          </select>
                        </td>
                        <td className="px-4 py-2">
                          <input
                            type="number"
                            step="0.000001"
                            {...register('quantity', { required: 'Required', valueAsNumber: true, min: { value: 0.000001, message: 'Must be positive' } })}
                            className="w-24 rounded border border-gray-300 px-2 py-1 text-sm text-right focus:border-blue-500 focus:outline-none"
                          />
                        </td>
                        <td className="px-4 py-2">
                          <input
                            type="number"
                            step="0.01"
                            {...register('price', { required: 'Required', valueAsNumber: true, min: { value: 0.01, message: 'Must be positive' } })}
                            className="w-24 rounded border border-gray-300 px-2 py-1 text-sm text-right focus:border-blue-500 focus:outline-none"
                          />
                        </td>
                        <td className="px-4 py-2">
                          <input
                            type="number"
                            step="0.01"
                            {...register('fee', { valueAsNumber: true })}
                            className="w-20 rounded border border-gray-300 px-2 py-1 text-sm text-right focus:border-blue-500 focus:outline-none"
                          />
                        </td>
                        <td className="px-4 py-2 text-right">{formatCurrency(total, currency)}</td>
                        <td className="px-4 py-2 text-right whitespace-nowrap">
                          <button type="submit" className="text-green-600 hover:text-green-800 text-xs mr-2">Save</button>
                          <button type="button" onClick={cancelEdit} className="text-gray-500 hover:text-gray-700 text-xs">Cancel</button>
                        </td>
                      </tr>
                    );
                  }

                  return (
                    <tr key={t.id} className="hover:bg-gray-50">
                      <td className="px-4 py-3">{t.date}</td>
                      <td className="px-4 py-3">
                        <span className={`text-xs px-2 py-1 rounded ${t.type === 'BUY' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                          {t.type}
                        </span>
                      </td>
                      <td className="px-4 py-3 text-right">{t.quantity}</td>
                      <td className="px-4 py-3 text-right">{formatCurrency(t.price, currency)}</td>
                      <td className="px-4 py-3 text-right">{fee > 0 ? formatCurrency(fee, currency) : '--'}</td>
                      <td className="px-4 py-3 text-right font-medium">{formatCurrency(total, currency)}</td>
                      <td className="px-4 py-3 text-right whitespace-nowrap">
                        <button
                          type="button"
                          onClick={() => startEdit(t)}
                          className="text-blue-500 hover:text-blue-700 text-xs mr-2"
                        >
                          Edit
                        </button>
                        <button
                          type="button"
                          onClick={() => dispatch(deleteTradeRequest({ portfolioId, holdingId, tradeId: t.id }))}
                          className="text-red-500 hover:text-red-700 text-xs"
                        >
                          Delete
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
