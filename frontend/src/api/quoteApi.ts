import axiosClient from './axiosClient';
import { QuoteData, HistoricalDataPoint, TickerSearchResult } from '../types/quote';

export const quoteApi = {
  getQuote: (symbol: string) => axiosClient.get<QuoteData>(`/quotes/${symbol}`),
  getQuotes: (symbols: string[]) =>
    axiosClient.get<QuoteData[]>(`/quotes?symbols=${symbols.join(',')}`),
  searchTickers: (query: string) =>
    axiosClient.get<TickerSearchResult[]>(`/quotes/search?q=${encodeURIComponent(query)}`),
  getHistory: (symbol: string, range: string) =>
    axiosClient.get<HistoricalDataPoint[]>(`/quotes/${encodeURIComponent(symbol)}/history?range=${range}`),
};
