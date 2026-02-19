import axiosClient from './axiosClient';
import { Trade, CreateTradeRequest } from '../types/holding';

export const tradeApi = {
  getAll: (portfolioId: number, holdingId: number) =>
    axiosClient.get<Trade[]>(`/portfolios/${portfolioId}/holdings/${holdingId}/trades`),
  add: (portfolioId: number, holdingId: number, data: CreateTradeRequest) =>
    axiosClient.post<Trade>(`/portfolios/${portfolioId}/holdings/${holdingId}/trades`, data),
  update: (portfolioId: number, holdingId: number, tradeId: number, data: CreateTradeRequest) =>
    axiosClient.put<Trade>(`/portfolios/${portfolioId}/holdings/${holdingId}/trades/${tradeId}`, data),
  delete: (portfolioId: number, holdingId: number, tradeId: number) =>
    axiosClient.delete(`/portfolios/${portfolioId}/holdings/${holdingId}/trades/${tradeId}`),
};
