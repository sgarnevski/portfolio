interface Props {
  message: string;
  onDismiss?: () => void;
}

export default function ErrorAlert({ message, onDismiss }: Props) {
  return (
    <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded relative mb-4">
      <span>{message}</span>
      {onDismiss && (
        <button onClick={onDismiss} className="absolute top-0 right-0 px-4 py-3 text-red-500 hover:text-red-700">
          x
        </button>
      )}
    </div>
  );
}
