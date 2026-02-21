import axiosClient from './axiosClient';
import { Portfolio, CreatePortfolioRequest } from '../types/portfolio';

export const portfolioApi = {
  getAll: () => axiosClient.get<Portfolio[]>('/portfolios'),
  getById: (id: number) => axiosClient.get<Portfolio>(`/portfolios/${id}`),
  create: (data: CreatePortfolioRequest) => axiosClient.post<Portfolio>('/portfolios', data),
  update: (id: number, data: CreatePortfolioRequest) => axiosClient.put<Portfolio>(`/portfolios/${id}`, data),
  delete: (id: number) => axiosClient.delete(`/portfolios/${id}`),
  updateCashBalance: (id: number, cashBalance: number) =>
    axiosClient.patch<Portfolio>(`/portfolios/${id}/cash-balance`, { cashBalance }),
};
