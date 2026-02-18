import { useSelector } from 'react-redux';
import { RootState } from '../../store';
import { formatCurrency } from '../../utils/formatCurrency';

export default function PriceTickerBar() {
  const prices = useSelector((state: RootState) => state.price.prices);
  const entries = Object.values(prices);

  if (entries.length === 0) return null;

  return (
    <div className="bg-gray-900 text-white px-4 py-2 rounded-lg overflow-x-auto">
      <div className="flex gap-6 text-sm whitespace-nowrap">
        {entries.map((q) => (
          <span key={q.symbol} className="flex items-center gap-2">
            <span className="font-medium">{q.symbol}</span>
            <span>{formatCurrency(q.regularMarketPrice, q.currency)}</span>
            <span className={q.regularMarketChange >= 0 ? 'text-green-400' : 'text-red-400'}>
              {q.regularMarketChange >= 0 ? '+' : ''}
              {q.regularMarketChangePercent?.toFixed(2) ?? '0.00'}%
            </span>
          </span>
        ))}
      </div>
    </div>
  );
}
