import { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { RootState } from '../../store';
import { addHoldingRequest, updateHoldingRequest, removeHoldingRequest } from '../../store/slices/holdingSlice';
import { fetchCurrenciesRequest } from '../../store/slices/currencySlice';
import { formatCurrency } from '../../utils/formatCurrency';
import { AssetClass, CreateHoldingRequest } from '../../types/holding';
import { Currency } from '../../types/currency';
import { TickerSearchResult } from '../../types/quote';
import TickerAutocomplete from '../common/TickerAutocomplete';

interface Props {
  portfolioId: number;
}

const ASSET_CLASSES: AssetClass[] = ['EQUITY', 'BOND', 'COMMODITY', 'REAL_ESTATE', 'CASH'];

interface EditRowProps {
  defaultValues: CreateHoldingRequest;
  currencies: Currency[];
  onSave: (data: CreateHoldingRequest) => void;
  onCancel: () => void;
  extraCells?: React.ReactNode;
}

function EditRow({ defaultValues, currencies, onSave, onCancel, extraCells }: EditRowProps) {
  const { register, handleSubmit, setValue, watch, formState: { errors } } = useForm<CreateHoldingRequest>({ defaultValues });
  const tickerValue = watch('tickerSymbol');
  const nameValue = watch('name');

  const handleTickerSelect = (result: TickerSearchResult) => {
    setValue('tickerSymbol', result.symbol);
    setValue('name', result.longName || result.shortName);
  };

  return (
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
        <input type="hidden" {...register('tickerSymbol', { required: 'Required' })} />
        <TickerAutocomplete
          value={tickerValue || ''}
          onChange={(val) => setValue('tickerSymbol', val)}
          onSelect={handleTickerSelect}
          placeholder="IWDA.AS"
          className="w-full rounded border border-gray-300 px-2 py-1 text-sm focus:border-blue-500 focus:outline-none"
        />
        {errors.tickerSymbol && <p className="text-red-500 text-xs">{errors.tickerSymbol.message}</p>}
      </td>
      <td className="px-4 py-2">
        <input type="hidden" {...register('name', { required: 'Required' })} />
        <input
          type="text"
          value={nameValue || ''}
          onChange={(e) => setValue('name', e.target.value)}
          placeholder="iShares Core MSCI World"
          className="w-full rounded border border-gray-300 px-2 py-1 text-sm focus:border-blue-500 focus:outline-none"
        />
        {errors.name && <p className="text-red-500 text-xs">{errors.name.message}</p>}
      </td>
      {extraCells}
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
        <button type="button" onClick={handleSubmit(onSave)} className="text-green-600 hover:text-green-800 text-xs mr-2">Save</button>
        <button type="button" onClick={onCancel} className="text-gray-500 hover:text-gray-700 text-xs">Cancel</button>
      </td>
    </tr>
  );
}

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

  const cancelEdit = () => {
    setEditingId(null);
    setAddingNew(false);
  };

  const onAdd = (data: CreateHoldingRequest) => {
    dispatch(addHoldingRequest({ portfolioId, data }));
    setAddingNew(false);
  };

  const onUpdate = (holdingId: number, data: CreateHoldingRequest) => {
    dispatch(updateHoldingRequest({ portfolioId, holdingId, data }));
    setEditingId(null);
  };

  const placeholderCells = (
    <>
      <td className="px-4 py-2 text-right text-gray-400">--</td>
      <td className="px-4 py-2 text-right text-gray-400">--</td>
      <td className="px-4 py-2 text-right text-gray-400">--</td>
      <td className="px-4 py-2 text-right text-gray-400">--</td>
      <td className="px-4 py-2 text-right text-gray-400">--</td>
    </>
  );

  return (
    <div>
      <div className="flex justify-end mb-4">
        <button
          onClick={() => { setEditingId(null); setAddingNew(true); }}
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
                <EditRow
                  key="new"
                  defaultValues={{ tickerSymbol: '', name: '', assetClass: 'EQUITY', currency: '' }}
                  currencies={currencies}
                  onSave={onAdd}
                  onCancel={cancelEdit}
                  extraCells={placeholderCells}
                />
              )}
              {holdings.map((h) => {
                const quote = prices[h.tickerSymbol];
                const price = quote?.regularMarketPrice ?? 0;
                const value = h.quantity * price;
                const change = quote?.regularMarketChangePercent ?? 0;
                const totalCost = h.totalCost ?? 0;
                const profitLoss = totalCost > 0 && value > 0 ? value - totalCost : null;

                if (editingId === h.id) {
                  const editExtraCells = (
                    <>
                      <td className="px-4 py-2 text-right">{h.quantity}</td>
                      <td className="px-4 py-2 text-right">{price > 0 ? formatCurrency(price, h.currency || 'USD') : '--'}</td>
                      <td className="px-4 py-2 text-right font-medium">{value > 0 ? formatCurrency(value, h.currency || 'USD') : '--'}</td>
                      <td className="px-4 py-2 text-right">{totalCost > 0 ? formatCurrency(totalCost, h.currency || 'USD') : '--'}</td>
                      <td className={`px-4 py-2 text-right font-medium ${profitLoss != null ? (profitLoss >= 0 ? 'text-green-600' : 'text-red-600') : ''}`}>
                        {profitLoss != null ? `${profitLoss >= 0 ? '+' : ''}${formatCurrency(profitLoss, h.currency || 'USD')}` : '--'}
                      </td>
                    </>
                  );
                  return (
                    <EditRow
                      key={h.id}
                      defaultValues={{ tickerSymbol: h.tickerSymbol, name: h.name, assetClass: h.assetClass, currency: h.currency }}
                      currencies={currencies}
                      onSave={(data) => onUpdate(h.id, data)}
                      onCancel={cancelEdit}
                      extraCells={editExtraCells}
                    />
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
                        onClick={() => { setAddingNew(false); setEditingId(h.id); }}
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
        </div>
      )}
    </div>
  );
}
