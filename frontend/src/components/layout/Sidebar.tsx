import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Link, useParams } from 'react-router-dom';
import { RootState } from '../../store';
import { fetchPortfoliosRequest } from '../../store/slices/portfolioSlice';

export default function Sidebar() {
  const dispatch = useDispatch();
  const { portfolios } = useSelector((state: RootState) => state.portfolio);
  const { id } = useParams();

  useEffect(() => {
    dispatch(fetchPortfoliosRequest());
  }, [dispatch]);

  return (
    <aside className="w-64 bg-white border-r min-h-screen p-4">
      <div className="flex items-center justify-between mb-4">
        <h3 className="font-semibold text-gray-700">Portfolios</h3>
        <Link to="/portfolios/new" className="text-blue-600 text-sm hover:underline">+ New</Link>
      </div>
      <ul className="space-y-1">
        {portfolios.map((p) => (
          <li key={p.id}>
            <Link
              to={`/portfolios/${p.id}`}
              className={`block px-3 py-2 rounded text-sm ${
                String(p.id) === id ? 'bg-blue-50 text-blue-700 font-medium' : 'text-gray-600 hover:bg-gray-50'
              }`}
            >
              {p.name}
            </Link>
          </li>
        ))}
        {portfolios.length === 0 && (
          <li className="text-sm text-gray-400 px-3 py-2">No portfolios yet</li>
        )}
      </ul>
    </aside>
  );
}
