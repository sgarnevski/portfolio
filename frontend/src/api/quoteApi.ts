import axiosClient from './axiosClient';
import { QuoteData } from '../types/quote';

export const quoteApi = {
  getQuote: (symbol: string) => axiosClient.get<QuoteData>(`/quotes/${symbol}`),
  getQuotes: (symbols: string[]) =>
    axiosClient.get<QuoteData[]>(`/quotes?symbols=${symbols.join(',')}`),
};
