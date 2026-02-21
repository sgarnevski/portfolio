import { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../../store';
import { calculateRebalanceRequest, calculateCashRebalanceRequest } from '../../store/slices/rebalanceSlice';
import { fetchHoldingsRequest } from '../../store/slices/holdingSlice';
import { fetchPortfoliosRequest } from '../../store/slices/portfolioSlice';
import { formatCurrency } from '../../utils/formatCurrency';
import TradeRecommendationTable from './TradeRecommendationTable';
import AllocationDriftTable from './AllocationDriftTable';
import LoadingSpinner from '../common/LoadingSpinner';
import ErrorAlert from '../common/ErrorAlert';

interface Props {
  portfolioId: number;
}

export default function RebalancePanel({ portfolioId }: Props) {
  const dispatch = useDispatch();
  const { result, loading, error } = useSelector((state: RootState) => state.rebalance);
  const portfolio = useSelector((state: RootState) =>
    state.portfolio.portfolios.find((p) => p.id === portfolioId)
  );
  const [cashAmount, setCashAmount] = useState('');
  const [mode, setMode] = useState<'full' | 'cash'>('full');

  const handleRebalance = () => {
    setMode('full');
    dispatch(calculateRebalanceRequest(portfolioId));
  };

  const handleCashRebalance = () => {
    const amount = parseFloat(cashAmount);
    if (amount > 0) {
      setMode('cash');
      dispatch(calculateCashRebalanceRequest({ portfolioId, amount }));
    }
  };

  const handleTradeExecuted = () => {
    dispatch(fetchHoldingsRequest(portfolioId));
    dispatch(fetchPortfoliosRequest());
  };

  return (
    <div className="space-y-6">
      <div className="bg-white rounded-lg shadow-sm border p-6">
        <h3 className="font-semibold text-lg mb-4">Portfolio Rebalancing</h3>
        <div className="flex flex-wrap gap-4 items-end">
          <button
            onClick={handleRebalance}
            disabled={loading}
            className="bg-blue-600 text-white py-2 px-6 rounded-md hover:bg-blue-700 disabled:opacity-50"
          >
            {loading && mode === 'full' ? 'Calculating...' : 'Calculate Rebalance'}
          </button>
          <div className="flex items-end gap-2">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">New Cash to Invest</label>
              <input
                type="number"
                value={cashAmount}
                onChange={(e) => setCashAmount(e.target.value)}
                placeholder="e.g., 5000"
                className="rounded-md border border-gray-300 px-3 py-2 text-sm"
              />
              {(portfolio?.cashBalance ?? 0) > 0 && (
                <p className="text-xs text-gray-500 mt-1">
                  + {portfolio!.cashBalance.toFixed(2)} account cash
                </p>
              )}
            </div>
            <button
              onClick={handleCashRebalance}
              disabled={loading || !cashAmount}
              className="bg-green-600 text-white py-2 px-4 rounded-md hover:bg-green-700 disabled:opacity-50 text-sm"
            >
              {loading && mode === 'cash' ? 'Calculating...' : 'Cash Rebalance'}
            </button>
          </div>
        </div>
      </div>

      {loading && <LoadingSpinner />}
      {error && <ErrorAlert message={error} />}

      {result && (
        <>
          <div className="bg-white rounded-lg shadow-sm border p-6">
            <div className="text-center">
              <p className="text-sm text-gray-500">Total Portfolio Value</p>
              <p className="text-3xl font-bold text-blue-600">
                {formatCurrency(result.totalPortfolioValue, result.currency)}
              </p>
            </div>
          </div>

          {result.allocations.length > 0 && <AllocationDriftTable allocations={result.allocations} currency={result.currency} />}
          {result.trades.length > 0 && <TradeRecommendationTable trades={result.trades} currency={result.currency} portfolioId={portfolioId} onTradeExecuted={handleTradeExecuted} unallocatedCash={result.unallocatedCash} />}
          {result.unallocatedCash != null && result.unallocatedCash > 0 && (
            <div className="bg-blue-50 border border-blue-200 text-blue-700 px-4 py-3 rounded text-center">
              Unallocated cash remaining: <span className="font-bold">{formatCurrency(result.unallocatedCash, result.currency)}</span>
              {' '}(not enough to purchase additional whole shares)
            </div>
          )}
          {result.trades.length === 0 && (
            <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded text-center">
              Portfolio is already balanced! No trades needed.
            </div>
          )}
        </>
      )}
    </div>
  );
}
