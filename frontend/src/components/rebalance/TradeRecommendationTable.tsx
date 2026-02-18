import { TradeRecommendation } from '../../types/rebalance';
import { formatCurrency } from '../../utils/formatCurrency';

interface Props {
  trades: TradeRecommendation[];
  currency: string;
}

export default function TradeRecommendationTable({ trades, currency }: Props) {
  return (
    <div className="bg-white rounded-lg shadow-sm border">
      <div className="px-6 py-4 border-b">
        <h3 className="font-semibold text-lg">Trade Recommendations</h3>
      </div>
      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 border-b">
            <tr>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Action</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Ticker</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Name</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Class</th>
              <th className="text-right px-4 py-3 font-medium text-gray-600">Shares</th>
              <th className="text-right px-4 py-3 font-medium text-gray-600">Price</th>
              <th className="text-right px-4 py-3 font-medium text-gray-600">Est. Cost</th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {trades.map((trade, i) => (
              <tr key={i} className="hover:bg-gray-50">
                <td className="px-4 py-3">
                  <span
                    className={`inline-block px-2 py-1 rounded text-xs font-bold ${
                      trade.action === 'BUY'
                        ? 'bg-green-100 text-green-800'
                        : 'bg-red-100 text-red-800'
                    }`}
                  >
                    {trade.action}
                  </span>
                </td>
                <td className="px-4 py-3 font-medium">{trade.tickerSymbol}</td>
                <td className="px-4 py-3 text-gray-600">{trade.name}</td>
                <td className="px-4 py-3">
                  <span className="bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded">
                    {trade.assetClass}
                  </span>
                </td>
                <td className="px-4 py-3 text-right font-medium">{trade.shares}</td>
                <td className="px-4 py-3 text-right">{formatCurrency(trade.currentPrice, currency)}</td>
                <td className="px-4 py-3 text-right font-medium">{formatCurrency(trade.estimatedCost, currency)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
