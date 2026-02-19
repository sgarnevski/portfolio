import { useState, useRef, useEffect, useCallback } from 'react';
import { quoteApi } from '../../api/quoteApi';
import { TickerSearchResult } from '../../types/quote';

interface Props {
  value: string;
  onChange: (value: string) => void;
  onSelect: (result: TickerSearchResult) => void;
  placeholder?: string;
  className?: string;
}

export default function TickerAutocomplete({ value, onChange, onSelect, placeholder, className }: Props) {
  const [results, setResults] = useState<TickerSearchResult[]>([]);
  const [isOpen, setIsOpen] = useState(false);
  const [activeIndex, setActiveIndex] = useState(-1);
  const containerRef = useRef<HTMLDivElement>(null);
  const debounceRef = useRef<ReturnType<typeof setTimeout>>();

  const search = useCallback(async (query: string) => {
    if (query.length < 1) {
      setResults([]);
      setIsOpen(false);
      return;
    }
    try {
      const response = await quoteApi.searchTickers(query);
      setResults(response.data);
      setIsOpen(response.data.length > 0);
      setActiveIndex(-1);
    } catch {
      setResults([]);
      setIsOpen(false);
    }
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const val = e.target.value.toUpperCase();
    onChange(val);
    clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => search(val), 300);
  };

  const handleSelect = (result: TickerSearchResult) => {
    onChange(result.symbol);
    onSelect(result);
    setIsOpen(false);
    setResults([]);
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (!isOpen) return;
    if (e.key === 'ArrowDown') {
      e.preventDefault();
      setActiveIndex((i) => Math.min(i + 1, results.length - 1));
    } else if (e.key === 'ArrowUp') {
      e.preventDefault();
      setActiveIndex((i) => Math.max(i - 1, 0));
    } else if (e.key === 'Enter' && activeIndex >= 0) {
      e.preventDefault();
      handleSelect(results[activeIndex]);
    } else if (e.key === 'Escape') {
      setIsOpen(false);
    }
  };

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  useEffect(() => {
    return () => clearTimeout(debounceRef.current);
  }, []);

  return (
    <div ref={containerRef} className="relative">
      <input
        type="text"
        value={value}
        onChange={handleChange}
        onKeyDown={handleKeyDown}
        onFocus={() => results.length > 0 && setIsOpen(true)}
        placeholder={placeholder}
        className={className}
        autoComplete="off"
      />
      {isOpen && (
        <ul className="absolute z-50 mt-1 w-72 max-h-60 overflow-auto rounded-md border border-gray-200 bg-white shadow-lg text-sm">
          {results.map((r, i) => (
            <li
              key={r.symbol}
              onMouseDown={() => handleSelect(r)}
              className={`px-3 py-2 cursor-pointer ${i === activeIndex ? 'bg-blue-50' : 'hover:bg-gray-50'}`}
            >
              <div className="flex justify-between items-center">
                <span className="font-medium">{r.symbol}</span>
                <span className="text-xs text-gray-400">{r.exchange}</span>
              </div>
              <div className="text-xs text-gray-500 truncate">
                {r.longName || r.shortName}
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
