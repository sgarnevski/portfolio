import { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../../store';
import { setAllocationsRequest } from '../../store/slices/allocationSlice';
import { AssetClass } from '../../types/holding';
import ErrorAlert from '../common/ErrorAlert';

interface Props {
  portfolioId: number;
}

const ASSET_CLASSES: AssetClass[] = ['EQUITY', 'BOND', 'COMMODITY', 'REAL_ESTATE', 'CASH'];

export default function AllocationEditor({ portfolioId }: Props) {
  const dispatch = useDispatch();
  const { targetAllocations, error } = useSelector((state: RootState) => state.allocation);
  const [values, setValues] = useState<Record<string, number>>({});

  useEffect(() => {
    const map: Record<string, number> = {};
    for (const a of targetAllocations) {
      map[a.assetClass] = a.targetPercentage;
    }
    setValues(map);
  }, [targetAllocations]);

  const total = Object.values(values).reduce((sum, v) => sum + (v || 0), 0);

  const handleChange = (ac: AssetClass, val: number) => {
    setValues((prev) => ({ ...prev, [ac]: val }));
  };

  const handleSave = () => {
    const allocations = ASSET_CLASSES
      .filter((ac) => (values[ac] || 0) > 0)
      .map((ac) => ({ assetClass: ac, targetPercentage: values[ac] || 0 }));
    dispatch(setAllocationsRequest({ portfolioId, data: { allocations } }));
  };

  return (
    <div className="bg-white rounded-lg shadow-sm border p-6">
      <h3 className="font-semibold text-lg mb-4">Target Allocation</h3>
      {error && <ErrorAlert message={error} />}
      <div className="space-y-3">
        {ASSET_CLASSES.map((ac) => (
          <div key={ac} className="flex items-center gap-4">
            <label className="w-28 text-sm font-medium text-gray-700">{ac.replace('_', ' ')}</label>
            <input
              type="range"
              min="0"
              max="100"
              value={values[ac] || 0}
              onChange={(e) => handleChange(ac, Number(e.target.value))}
              className="flex-1"
            />
            <input
              type="number"
              min="0"
              max="100"
              value={values[ac] || 0}
              onChange={(e) => handleChange(ac, Number(e.target.value))}
              className="w-20 text-right rounded-md border border-gray-300 px-2 py-1 text-sm"
            />
            <span className="text-sm text-gray-500">%</span>
          </div>
        ))}
      </div>
      <div className="mt-4 flex items-center justify-between">
        <span className={`text-sm font-medium ${total === 100 ? 'text-green-600' : 'text-red-600'}`}>
          Total: {total}%
        </span>
        <button
          onClick={handleSave}
          disabled={total !== 100}
          className="bg-blue-600 text-white py-2 px-4 rounded-md text-sm hover:bg-blue-700 disabled:opacity-50"
        >
          Save Allocation
        </button>
      </div>
    </div>
  );
}
