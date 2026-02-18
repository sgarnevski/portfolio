import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../../store';
import { addHoldingRequest, updateHoldingRequest } from '../../store/slices/holdingSlice';
import { fetchCurrenciesRequest } from '../../store/slices/currencySlice';
import { AddHoldingRequest, AssetClass, Holding } from '../../types/holding';

interface Props {
  portfolioId: number;
  onClose: () => void;
  holding?: Holding;
}

const ASSET_CLASSES: AssetClass[] = ['EQUITY', 'BOND', 'COMMODITY', 'REAL_ESTATE', 'CASH'];

export default function AddHoldingModal({ portfolioId, onClose, holding }: Props) {
  const dispatch = useDispatch();
  const { currencies } = useSelector((state: RootState) => state.currency);
  const isEdit = !!holding;

  useEffect(() => {
    if (currencies.length === 0) dispatch(fetchCurrenciesRequest());
  }, [dispatch, currencies.length]);
  const { register, handleSubmit, formState: { errors } } = useForm<AddHoldingRequest>({
    defaultValues: holding ? {
      tickerSymbol: holding.tickerSymbol,
      name: holding.name,
      assetClass: holding.assetClass,
      quantity: holding.quantity,
      averageCostBasis: holding.averageCostBasis ?? undefined,
      initialValue: holding.initialValue ?? undefined,
      currency: holding.currency,
    } : undefined,
  });

  const onSubmit = (data: AddHoldingRequest) => {
    if (Number.isNaN(data.initialValue)) data.initialValue = undefined;
    if (isEdit) {
      dispatch(updateHoldingRequest({ portfolioId, holdingId: holding.id, data }));
    } else {
      dispatch(addHoldingRequest({ portfolioId, data }));
    }
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-lg p-6 w-full max-w-md">
        <h3 className="text-lg font-bold mb-4">{isEdit ? 'Edit Holding' : 'Add Holding'}</h3>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-3">
          <div>
            <label className="block text-sm font-medium text-gray-700">Ticker Symbol</label>
            <input
              {...register('tickerSymbol', { required: 'Required' })}
              placeholder="e.g., IWDA.AS"
              className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
            />
            {errors.tickerSymbol && <p className="text-red-500 text-xs mt-1">{errors.tickerSymbol.message}</p>}
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Name</label>
            <input
              {...register('name', { required: 'Required' })}
              placeholder="e.g., iShares Core MSCI World UCITS ETF"
              className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
            />
            {errors.name && <p className="text-red-500 text-xs mt-1">{errors.name.message}</p>}
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Asset Class</label>
            <select
              {...register('assetClass', { required: 'Required' })}
              className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
            >
              {ASSET_CLASSES.map((ac) => (
                <option key={ac} value={ac}>{ac.replace('_', ' ')}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Quantity (shares)</label>
            <input
              type="number"
              step="0.000001"
              {...register('quantity', { required: 'Required', valueAsNumber: true, min: { value: 0.000001, message: 'Must be positive' } })}
              className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
            />
            {errors.quantity && <p className="text-red-500 text-xs mt-1">{errors.quantity.message}</p>}
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Cost Basis (total cost)</label>
            <input
              type="number"
              step="0.01"
              {...register('initialValue', { valueAsNumber: true })}
              placeholder="e.g., 5000.00"
              className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Currency</label>
            <select
              {...register('currency')}
              className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
            >
              <option value="">Select currency</option>
              {currencies.map((c) => (
                <option key={c.id} value={c.code}>{c.code} — {c.name}</option>
              ))}
            </select>
          </div>
          <div className="flex gap-3 pt-2">
            <button type="submit" className="bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 text-sm">
              {isEdit ? 'Save' : 'Add'}
            </button>
            <button type="button" onClick={onClose} className="bg-gray-200 text-gray-700 py-2 px-4 rounded-md hover:bg-gray-300 text-sm">
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
