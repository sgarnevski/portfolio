import { useSelector } from 'react-redux';
import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { RootState } from '../../store';

const COLORS = ['#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6'];

export default function AllocationPieChart() {
  const { targetAllocations } = useSelector((state: RootState) => state.allocation);

  if (targetAllocations.length === 0) {
    return (
      <div className="bg-white rounded-lg shadow-sm border p-6 flex items-center justify-center text-gray-500">
        Set target allocations to see chart
      </div>
    );
  }

  const data = targetAllocations.map((a) => ({
    name: a.assetClass.replace('_', ' '),
    value: a.targetPercentage,
  }));

  return (
    <div className="bg-white rounded-lg shadow-sm border p-6">
      <h3 className="font-semibold text-lg mb-4">Target Allocation</h3>
      <ResponsiveContainer width="100%" height={300}>
        <PieChart>
          <Pie
            data={data}
            cx="50%"
            cy="50%"
            innerRadius={60}
            outerRadius={100}
            dataKey="value"
            label={({ name, value }) => `${name} ${value}%`}
          >
            {data.map((_, index) => (
              <Cell key={index} fill={COLORS[index % COLORS.length]} />
            ))}
          </Pie>
          <Tooltip formatter={(value: number) => `${value}%`} />
          <Legend />
        </PieChart>
      </ResponsiveContainer>
    </div>
  );
}
