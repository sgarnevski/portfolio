import { AllocationComparison } from '../../types/rebalance';
import { formatCurrency } from '../../utils/formatCurrency';
import { formatPercentage } from '../../utils/formatPercentage';

interface Props {
  allocations: AllocationComparison[];
  currency: string;
}

export default function AllocationDriftTable({ allocations, currency }: Props) {
  return (
    <div className="bg-white rounded-lg shadow-sm border">
      <div className="px-6 py-4 border-b">
        <h3 className="font-semibold text-lg">Allocation Drift</h3>
      </div>
      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 border-b">
            <tr>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Asset Class</th>
              <th className="text-right px-4 py-3 font-medium text-gray-600">Current %</th>
              <th className="text-right px-4 py-3 font-medium text-gray-600">Target %</th>
              <th className="text-right px-4 py-3 font-medium text-gray-600">Drift</th>
              <th className="text-right px-4 py-3 font-medium text-gray-600">Current Value</th>
              <th className="text-right px-4 py-3 font-medium text-gray-600">Target Value</th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {allocations.map((a) => (
              <tr key={a.assetClass} className="hover:bg-gray-50">
                <td className="px-4 py-3 font-medium">{a.assetClass.replace('_', ' ')}</td>
                <td className="px-4 py-3 text-right">{formatPercentage(a.currentPercentage)}</td>
                <td className="px-4 py-3 text-right">{formatPercentage(a.targetPercentage)}</td>
                <td className={`px-4 py-3 text-right font-medium ${
                  a.driftPercentage > 0 ? 'text-red-600' : a.driftPercentage < 0 ? 'text-blue-600' : 'text-gray-600'
                }`}>
                  {a.driftPercentage > 0 ? '+' : ''}{formatPercentage(a.driftPercentage)}
                </td>
                <td className="px-4 py-3 text-right">{formatCurrency(a.currentValue, currency)}</td>
                <td className="px-4 py-3 text-right">{formatCurrency(a.targetValue, currency)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
