import axiosClient from './axiosClient';
import { RebalanceResponse } from '../types/rebalance';

export const rebalanceApi = {
  calculate: (portfolioId: number) =>
    axiosClient.get<RebalanceResponse>(`/portfolios/${portfolioId}/rebalance`),
  cashRebalance: (portfolioId: number, amount: number) =>
    axiosClient.post<RebalanceResponse>(`/portfolios/${portfolioId}/rebalance/cash`, { amount }),
};
