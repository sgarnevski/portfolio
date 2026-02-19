import axiosClient from './axiosClient';
import { Holding, CreateHoldingRequest } from '../types/holding';

export const holdingApi = {
  getAll: (portfolioId: number) =>
    axiosClient.get<Holding[]>(`/portfolios/${portfolioId}/holdings`),
  add: (portfolioId: number, data: CreateHoldingRequest) =>
    axiosClient.post<Holding>(`/portfolios/${portfolioId}/holdings`, data),
  update: (portfolioId: number, holdingId: number, data: CreateHoldingRequest) =>
    axiosClient.put<Holding>(`/portfolios/${portfolioId}/holdings/${holdingId}`, data),
  delete: (portfolioId: number, holdingId: number) =>
    axiosClient.delete(`/portfolios/${portfolioId}/holdings/${holdingId}`),
};
