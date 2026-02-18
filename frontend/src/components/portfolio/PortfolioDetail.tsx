import { useEffect, useState, useMemo } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../../store';
import { fetchHoldingsRequest } from '../../store/slices/holdingSlice';
import { fetchAllocationsRequest } from '../../store/slices/allocationSlice';
import { fetchPricesRequest } from '../../store/slices/priceSlice';
import { deletePortfolioRequest } from '../../store/slices/portfolioSlice';
import HoldingsTable from '../holdings/HoldingsTable';
import AddHoldingModal from '../holdings/AddHoldingModal';
import EditPortfolioModal from './EditPortfolioModal';
import AllocationEditor from '../allocation/AllocationEditor';
import AllocationPieChart from '../allocation/AllocationPieChart';
import RebalancePanel from '../rebalance/RebalancePanel';
import LoadingSpinner from '../common/LoadingSpinner';
import DriftNotification from '../common/DriftNotification';
import { formatCurrency } from '../../utils/formatCurrency';

type Tab = 'holdings' | 'allocation' | 'rebalance';

export default function PortfolioDetail() {
  const { id } = useParams<{ id: string }>();
  const portfolioId = Number(id);
  const dispatch = useDispatch();
  const { portfolios } = useSelector((state: RootState) => state.portfolio);
  const { holdings } = useSelector((state: RootState) => state.holding);
  const { targetAllocations } = useSelector((state: RootState) => state.allocation);
  const prices = useSelector((state: RootState) => state.price.prices);
  const portfolio = portfolios.find((p) => p.id === portfolioId);
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState<Tab>('holdings');
  const [showAddHolding, setShowAddHolding] = useState(false);
  const [showEditPortfolio, setShowEditPortfolio] = useState(false);

  useEffect(() => {
    if (portfolioId) {
      dispatch(fetchHoldingsRequest(portfolioId));
      dispatch(fetchAllocationsRequest(portfolioId));
    }
  }, [dispatch, portfolioId]);

  // Fetch prices when holdings change
  useEffect(() => {
    if (holdings.length > 0) {
      const tickers = holdings.map((h) => h.tickerSymbol);
      dispatch(fetchPricesRequest(tickers));
    }
  }, [dispatch, holdings]);

  // Calculate drift
  const driftInfo = useMemo(() => {
    if (holdings.length === 0 || targetAllocations.length === 0) return null;

    let totalValue = 0;
    const valueByClass: Record<string, number> = {};

    for (const h of holdings) {
      const price = prices[h.tickerSymbol]?.regularMarketPrice ?? 0;
      const value = h.quantity * price;
      totalValue += value;
      valueByClass[h.assetClass] = (valueByClass[h.assetClass] || 0) + value;
    }

    if (totalValue === 0) return null;

    const drifts: { assetClass: string; current: number; target: number; drift: number; driftValue: number }[] = [];
    let maxDrift = 0;

    for (const alloc of targetAllocations) {
      const currentValue = valueByClass[alloc.assetClass] || 0;
      const currentPct = (currentValue / totalValue) * 100;
      const drift = currentPct - alloc.targetPercentage;
      const driftValue = currentValue - (totalValue * alloc.targetPercentage / 100);
      drifts.push({
        assetClass: alloc.assetClass,
        current: currentPct,
        target: alloc.targetPercentage,
        drift,
        driftValue,
      });
      if (Math.abs(drift) > maxDrift) maxDrift = Math.abs(drift);
    }

    return { drifts, maxDrift, totalValue };
  }, [holdings, targetAllocations, prices]);

  if (!portfolio) return <LoadingSpinner />;

  const tabs: { key: Tab; label: string }[] = [
    { key: 'holdings', label: 'Holdings' },
    { key: 'allocation', label: 'Allocation' },
    { key: 'rebalance', label: 'Rebalance' },
  ];

  // Calculate total portfolio value
  const totalValue = holdings.reduce((sum, h) => {
    const price = prices[h.tickerSymbol]?.regularMarketPrice ?? 0;
    return sum + h.quantity * price;
  }, 0);

  // Calculate total cost basis
  const totalCost = holdings.reduce((sum, h) => {
    return sum + (h.averageCostBasis ?? 0) * h.quantity;
  }, 0);

  const totalPnL = totalCost > 0 ? totalValue - totalCost : 0;
  const totalPnLPct = totalCost > 0 ? (totalPnL / totalCost) * 100 : 0;

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <div>
          <div className="flex items-center gap-3">
            <h1 className="text-2xl font-bold">{portfolio.name}</h1>
            <button
              onClick={() => setShowEditPortfolio(true)}
              className="text-gray-400 hover:text-blue-600 text-sm"
              title="Edit portfolio"
            >
              Edit
            </button>
            <button
              onClick={() => {
                if (confirm('Delete this portfolio?')) {
                  dispatch(deletePortfolioRequest(portfolioId));
                  navigate('/');
                }
              }}
              className="text-gray-400 hover:text-red-600 text-sm"
              title="Delete portfolio"
            >
              Delete
            </button>
          </div>
          {portfolio.description && <p className="text-gray-500 mt-1">{portfolio.description}</p>}
        </div>
        <div className="text-right">
          {totalValue > 0 && (
            <>
              <p className="text-2xl font-bold text-blue-600">{formatCurrency(totalValue)}</p>
              {totalCost > 0 && (
                <p className={`text-sm ${totalPnL >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                  {totalPnL >= 0 ? '+' : ''}{formatCurrency(totalPnL)} ({totalPnLPct >= 0 ? '+' : ''}{totalPnLPct.toFixed(2)}%)
                </p>
              )}
            </>
          )}
        </div>
      </div>

      {driftInfo && driftInfo.maxDrift > 3 && (
        <DriftNotification drifts={driftInfo.drifts} onRebalance={() => setActiveTab('rebalance')} />
      )}

      <div className="border-b mb-6">
        <div className="flex gap-4">
          {tabs.map((tab) => (
            <button
              key={tab.key}
              onClick={() => setActiveTab(tab.key)}
              className={`pb-3 px-1 text-sm font-medium border-b-2 transition-colors ${
                activeTab === tab.key
                  ? 'border-blue-600 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>
      </div>

      {activeTab === 'holdings' && (
        <div>
          <div className="flex justify-end mb-4">
            <button
              onClick={() => setShowAddHolding(true)}
              className="bg-blue-600 text-white py-2 px-4 rounded-md text-sm hover:bg-blue-700"
            >
              + Add Holding
            </button>
          </div>
          <HoldingsTable portfolioId={portfolioId} />
          {showAddHolding && (
            <AddHoldingModal portfolioId={portfolioId} onClose={() => setShowAddHolding(false)} />
          )}
        </div>
      )}

      {activeTab === 'allocation' && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <AllocationEditor portfolioId={portfolioId} />
          <AllocationPieChart />
        </div>
      )}

      {activeTab === 'rebalance' && <RebalancePanel portfolioId={portfolioId} />}

      {showEditPortfolio && (
        <EditPortfolioModal portfolio={portfolio} onClose={() => setShowEditPortfolio(false)} />
      )}
    </div>
  );
}
