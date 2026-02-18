import axiosClient from './axiosClient';
import { TargetAllocation, SetAllocationRequest } from '../types/allocation';

export const allocationApi = {
  get: (portfolioId: number) =>
    axiosClient.get<TargetAllocation[]>(`/portfolios/${portfolioId}/allocations`),
  set: (portfolioId: number, data: SetAllocationRequest) =>
    axiosClient.put<TargetAllocation[]>(`/portfolios/${portfolioId}/allocations`, data),
};
