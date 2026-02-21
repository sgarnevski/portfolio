import { useEffect, useRef, useState } from 'react';
import { TradeRecommendation } from '../../types/rebalance';
import { tradeApi } from '../../api/tradeApi';
import { portfolioApi } from '../../api/portfolioApi';
import { formatCurrency } from '../../utils/formatCurrency';

interface Props {
  trades: TradeRecommendation[];
  currency: string;
  portfolioId: number;
  onTradeExecuted: () => void;
  unallocatedCash?: number;
}

type TradeStatus = 'idle' | 'executing' | 'done' | 'error';

export default function TradeRecommendationTable({ trades, currency, portfolioId, onTradeExecuted, unallocatedCash }: Props) {
  const [expandedRows, setExpandedRows] = useState<Set<number>>(new Set());
  const [tradeStatuses, setTradeStatuses] = useState<Record<number, TradeStatus>>({});
  const prevTradesRef = useRef(trades);

  useEffect(() => {
    if (prevTradesRef.current !== trades) {
      setTradeStatuses({});
      setExpandedRows(new Set());
      prevTradesRef.current = trades;
    }
  }, [trades]);

  const toggleRow = (index: number) => {
    setExpandedRows((prev) => {
      const next = new Set(prev);
      if (next.has(index)) {
        next.delete(index);
      } else {
        next.add(index);
      }
      return next;
    });
  };

  const handleExecute = async (trade: TradeRecommendation, index: number) => {
    const today = new Date().toISOString().split('T')[0];
    setTradeStatuses((prev) => ({ ...prev, [index]: 'executing' }));
    try {
      await tradeApi.add(portfolioId, trade.holdingId, {
        date: today,
        type: trade.action,
        quantity: trade.shares,
        price: trade.currentPrice,
      });
      const newStatuses = { ...tradeStatuses, [index]: 'done' as TradeStatus };
      setTradeStatuses(newStatuses);
      // Check if all trades are now done
      const allDone = trades.every((_, i) => (newStatuses[i] || 'idle') === 'done');
      if (allDone && unallocatedCash != null) {
        await portfolioApi.updateCashBalance(portfolioId, unallocatedCash);
      }
      onTradeExecuted();
    } catch {
      setTradeStatuses((prev) => ({ ...prev, [index]: 'error' }));
    }
  };

  const renderButton = (trade: TradeRecommendation, index: number) => {
    const status = tradeStatuses[index] || 'idle';
    switch (status) {
      case 'done':
        return <span className="text-green-600 font-medium text-xs">Done</span>;
      case 'executing':
        return <span className="text-gray-400 text-xs">...</span>;
      case 'error':
        return (
          <button
            onClick={(e) => { e.stopPropagation(); handleExecute(trade, index); }}
            className="bg-red-600 text-white px-3 py-1 rounded text-xs font-medium hover:bg-red-700"
          >
            Retry
          </button>
        );
      default:
        return (
          <button
            onClick={(e) => { e.stopPropagation(); handleExecute(trade, index); }}
            className="bg-blue-600 text-white px-3 py-1 rounded text-xs font-medium hover:bg-blue-700"
          >
            Execute
          </button>
        );
    }
  };

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
              <th className="text-center px-4 py-3 font-medium text-gray-600"></th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {trades.map((trade, i) => (
              <>
                <tr
                  key={i}
                  className={`hover:bg-gray-50 ${trade.lotDetails?.length ? 'cursor-pointer' : ''}`}
                  onClick={() => trade.lotDetails?.length && toggleRow(i)}
                >
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
                    {trade.lotDetails?.length ? (
                      <span className="ml-2 text-gray-400 text-xs">
                        {expandedRows.has(i) ? '\u25B2' : '\u25BC'}
                      </span>
                    ) : null}
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
                  <td className="px-4 py-3 text-center">
                    {renderButton(trade, i)}
                  </td>
                </tr>
                {expandedRows.has(i) && trade.lotDetails && (
                  <tr key={`${i}-lots`}>
                    <td colSpan={8} className="px-4 py-2 bg-gray-50">
                      <div className="ml-8">
                        <p className="text-xs font-semibold text-gray-500 mb-2">HIFO Lot Breakdown</p>
                        <table className="w-full text-xs">
                          <thead>
                            <tr className="text-gray-500">
                              <th className="text-left py-1">Purchase Date</th>
                              <th className="text-right py-1">Qty</th>
                              <th className="text-right py-1">Cost Basis</th>
                              <th className="text-right py-1">Est. Gain</th>
                            </tr>
                          </thead>
                          <tbody>
                            {trade.lotDetails.map((lot, j) => (
                              <tr key={j} className="border-t border-gray-200">
                                <td className="py-1">{lot.purchaseDate}</td>
                                <td className="text-right py-1">{lot.quantity.toFixed(2)}</td>
                                <td className="text-right py-1">{formatCurrency(lot.costBasis, currency)}</td>
                                <td className={`text-right py-1 font-medium ${lot.estimatedGain >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                                  {lot.estimatedGain >= 0 ? '+' : ''}{formatCurrency(lot.estimatedGain, currency)}
                                </td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    </td>
                  </tr>
                )}
              </>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
