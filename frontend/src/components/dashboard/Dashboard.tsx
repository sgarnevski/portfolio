import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import { RootState } from '../../store';
import { fetchPortfoliosRequest } from '../../store/slices/portfolioSlice';
import { formatCurrency } from '../../utils/formatCurrency';
import PriceTickerBar from './PriceTickerBar';
import LoadingSpinner from '../common/LoadingSpinner';

export default function Dashboard() {
  const dispatch = useDispatch();
  const { portfolios, loading } = useSelector((state: RootState) => state.portfolio);
  const prices = useSelector((state: RootState) => state.price.prices);

  useEffect(() => {
    dispatch(fetchPortfoliosRequest());
  }, [dispatch]);

  if (loading) return <LoadingSpinner />;

  return (
    <div>
      <h1 className="text-2xl font-bold mb-6">Dashboard</h1>
      <PriceTickerBar />
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mt-6">
        {portfolios.map((p) => {
          const totalValue = p.holdings.reduce((sum, h) => {
            const price = prices[h.tickerSymbol]?.regularMarketPrice ?? 0;
            return sum + h.quantity * price;
          }, 0);
          return (
            <Link
              key={p.id}
              to={`/portfolios/${p.id}`}
              className="bg-white rounded-lg shadow-sm border p-6 hover:shadow-md transition-shadow"
            >
              <h3 className="font-semibold text-lg">{p.name}</h3>
              <p className="text-sm text-gray-500 mt-1">{p.description || 'No description'}</p>
              <p className="text-2xl font-bold mt-4 text-blue-600">
                {totalValue > 0 ? formatCurrency(totalValue) : '--'}
              </p>
              <p className="text-sm text-gray-400 mt-1">{p.holdings.length} holdings</p>
            </Link>
          );
        })}
        {portfolios.length === 0 && (
          <div className="col-span-full text-center py-12 text-gray-500">
            <p className="text-lg">No portfolios yet</p>
            <Link to="/portfolios/new" className="text-blue-600 hover:underline mt-2 inline-block">
              Create your first portfolio
            </Link>
          </div>
        )}
      </div>
    </div>
  );
}
