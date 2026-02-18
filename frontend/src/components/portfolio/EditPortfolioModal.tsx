import { useForm } from 'react-hook-form';
import { useDispatch } from 'react-redux';
import { updatePortfolioRequest } from '../../store/slices/portfolioSlice';
import { Portfolio, CreatePortfolioRequest } from '../../types/portfolio';

interface Props {
  portfolio: Portfolio;
  onClose: () => void;
}

export default function EditPortfolioModal({ portfolio, onClose }: Props) {
  const dispatch = useDispatch();
  const { register, handleSubmit, formState: { errors } } = useForm<CreatePortfolioRequest>({
    defaultValues: {
      name: portfolio.name,
      description: portfolio.description || '',
    },
  });

  const onSubmit = (data: CreatePortfolioRequest) => {
    dispatch(updatePortfolioRequest({ id: portfolio.id, data }));
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-lg p-6 w-full max-w-md">
        <h3 className="text-lg font-bold mb-4">Edit Portfolio</h3>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">Name</label>
            <input
              {...register('name', { required: 'Name is required' })}
              className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
            />
            {errors.name && <p className="text-red-500 text-sm mt-1">{errors.name.message}</p>}
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Description</label>
            <textarea
              {...register('description')}
              className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
              rows={3}
            />
          </div>
          <div className="flex gap-3 pt-2">
            <button type="submit" className="bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 text-sm">
              Save
            </button>
            <button type="button" onClick={onClose} className="bg-gray-200 text-gray-700 py-2 px-4 rounded-md hover:bg-gray-300 text-sm">
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
