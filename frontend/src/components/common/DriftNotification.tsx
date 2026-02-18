import { formatCurrency } from '../../utils/formatCurrency';

interface DriftItem {
  assetClass: string;
  current: number;
  target: number;
  drift: number;
  driftValue: number;
}

interface Props {
  drifts: DriftItem[];
  onRebalance: () => void;
}

export default function DriftNotification({ drifts, onRebalance }: Props) {
  const significantDrifts = drifts.filter((d) => Math.abs(d.drift) > 2);

  if (significantDrifts.length === 0) return null;

  return (
    <div className="bg-yellow-50 border border-yellow-300 rounded-lg p-4 mb-6">
      <div className="flex items-start justify-between">
        <div>
          <h3 className="font-semibold text-yellow-800 text-sm">Portfolio Drift Detected</h3>
          <p className="text-yellow-700 text-xs mt-1">
            Your portfolio allocation has drifted from target. Consider rebalancing.
          </p>
          <div className="mt-3 space-y-1">
            {significantDrifts.map((d) => (
              <div key={d.assetClass} className="flex items-center gap-3 text-xs">
                <span className="font-medium text-yellow-800 w-28">{d.assetClass.replace('_', ' ')}</span>
                <span className="text-gray-600">
                  {d.current.toFixed(1)}% (target: {d.target.toFixed(1)}%)
                </span>
                <span className={`font-medium ${d.drift > 0 ? 'text-red-600' : 'text-green-600'}`}>
                  {d.drift > 0 ? '+' : ''}{d.drift.toFixed(1)}% ({d.driftValue > 0 ? '+' : ''}{formatCurrency(d.driftValue)})
                </span>
              </div>
            ))}
          </div>
        </div>
        <button
          onClick={onRebalance}
          className="bg-yellow-600 text-white px-3 py-1 rounded text-xs hover:bg-yellow-700 whitespace-nowrap"
        >
          Rebalance Now
        </button>
      </div>
    </div>
  );
}
