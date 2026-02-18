import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../../store';
import {
  fetchCurrenciesRequest,
  createCurrencyRequest,
  updateCurrencyRequest,
  deleteCurrencyRequest,
} from '../../store/slices/currencySlice';
import LoadingSpinner from '../common/LoadingSpinner';
import ErrorAlert from '../common/ErrorAlert';

export default function CurrencyManager() {
  const dispatch = useDispatch();
  const { currencies, loading, error } = useSelector((state: RootState) => state.currency);

  const [code, setCode] = useState('');
  const [name, setName] = useState('');
  const [editingId, setEditingId] = useState<number | null>(null);

  useEffect(() => {
    dispatch(fetchCurrenciesRequest());
  }, [dispatch]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const trimmedCode = code.trim().toUpperCase();
    const trimmedName = name.trim();
    if (!trimmedCode || !trimmedName) return;

    if (editingId !== null) {
      dispatch(updateCurrencyRequest({ id: editingId, data: { code: trimmedCode, name: trimmedName } }));
    } else {
      dispatch(createCurrencyRequest({ code: trimmedCode, name: trimmedName }));
    }
    resetForm();
  };

  const startEdit = (currency: { id: number; code: string; name: string }) => {
    setEditingId(currency.id);
    setCode(currency.code);
    setName(currency.name);
  };

  const resetForm = () => {
    setEditingId(null);
    setCode('');
    setName('');
  };

  return (
    <div>
      {error && <ErrorAlert message={error} />}

      <form onSubmit={handleSubmit} className="flex gap-3 mb-6 items-end">
        <div>
          <label className="block text-sm font-medium text-gray-700">Code</label>
          <input
            value={code}
            onChange={(e) => setCode(e.target.value)}
            placeholder="EUR"
            maxLength={3}
            className="mt-1 block w-24 rounded-md border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700">Name</label>
          <input
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Euro"
            className="mt-1 block w-64 rounded-md border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
          />
        </div>
        <button type="submit" className="bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 text-sm">
          {editingId !== null ? 'Save' : 'Add'}
        </button>
        {editingId !== null && (
          <button type="button" onClick={resetForm} className="bg-gray-200 text-gray-700 py-2 px-4 rounded-md hover:bg-gray-300 text-sm">
            Cancel
          </button>
        )}
      </form>

      {loading && <LoadingSpinner />}

      <table className="w-full border-collapse">
        <thead>
          <tr className="border-b text-left text-sm text-gray-500">
            <th className="py-2 pr-4">Code</th>
            <th className="py-2 pr-4">Name</th>
            <th className="py-2">Actions</th>
          </tr>
        </thead>
        <tbody>
          {currencies.map((c) => (
            <tr key={c.id} className="border-b text-sm">
              <td className="py-2 pr-4 font-mono">{c.code}</td>
              <td className="py-2 pr-4">{c.name}</td>
              <td className="py-2 space-x-2">
                <button
                  onClick={() => startEdit(c)}
                  className="text-blue-600 hover:underline text-sm"
                >
                  Edit
                </button>
                <button
                  onClick={() => dispatch(deleteCurrencyRequest(c.id))}
                  className="text-red-600 hover:underline text-sm"
                >
                  Delete
                </button>
              </td>
            </tr>
          ))}
          {currencies.length === 0 && !loading && (
            <tr>
              <td colSpan={3} className="py-4 text-gray-400 text-sm">No currencies yet</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
