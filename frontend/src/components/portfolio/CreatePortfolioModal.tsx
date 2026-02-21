import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { RootState } from '../../store';
import { createPortfolioRequest } from '../../store/slices/portfolioSlice';
import { fetchCurrenciesRequest } from '../../store/slices/currencySlice';
import { CreatePortfolioRequest } from '../../types/portfolio';
import ErrorAlert from '../common/ErrorAlert';

export default function CreatePortfolioModal() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { loading, error } = useSelector((state: RootState) => state.portfolio);
  const currencies = useSelector((state: RootState) => state.currency.currencies);
  const { register, handleSubmit, formState: { errors } } = useForm<CreatePortfolioRequest>({
    defaultValues: { baseCurrency: 'USD' },
  });

  useEffect(() => {
    if (currencies.length === 0) dispatch(fetchCurrenciesRequest());
  }, [dispatch, currencies.length]);

  const onSubmit = (data: CreatePortfolioRequest) => {
    dispatch(createPortfolioRequest(data));
    navigate('/');
  };

  return (
    <div className="max-w-lg mx-auto">
      <h2 className="text-xl font-bold mb-6">Create New Portfolio</h2>
      {error && <ErrorAlert message={error} />}
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 bg-white p-6 rounded-lg shadow-sm border">
        <div>
          <label className="block text-sm font-medium text-gray-700">Name</label>
          <input
            {...register('name', { required: 'Name is required' })}
            className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
            placeholder="e.g., UCITS ETF Portfolio"
          />
          {errors.name && <p className="text-red-500 text-sm mt-1">{errors.name.message}</p>}
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700">Description</label>
          <textarea
            {...register('description')}
            className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
            rows={3}
            placeholder="Optional description"
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700">Drift Threshold (%)</label>
          <input
            type="number"
            step="0.5"
            min="0"
            max="100"
            defaultValue={5}
            {...register('driftThreshold', { valueAsNumber: true, min: 0, max: 100 })}
            className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
          />
          <p className="text-xs text-gray-500 mt-1">Drift notification triggers when any asset class exceeds this threshold</p>
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700">Base Currency</label>
          <select
            {...register('baseCurrency')}
            className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
          >
            {currencies.map((c) => (
              <option key={c.id} value={c.code}>{c.code} — {c.name}</option>
            ))}
          </select>
        </div>
        <div className="flex gap-3">
          <button
            type="submit"
            disabled={loading}
            className="bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 disabled:opacity-50"
          >
            {loading ? 'Creating...' : 'Create Portfolio'}
          </button>
          <button
            type="button"
            onClick={() => navigate('/')}
            className="bg-gray-200 text-gray-700 py-2 px-4 rounded-md hover:bg-gray-300"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
}
