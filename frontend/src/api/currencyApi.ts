import axiosClient from './axiosClient';
import { Currency, CreateCurrencyRequest } from '../types/currency';

export const currencyApi = {
  getAll: () =>
    axiosClient.get<Currency[]>('/currencies'),
  create: (data: CreateCurrencyRequest) =>
    axiosClient.post<Currency>('/currencies', data),
  update: (id: number, data: CreateCurrencyRequest) =>
    axiosClient.put<Currency>(`/currencies/${id}`, data),
  delete: (id: number) =>
    axiosClient.delete(`/currencies/${id}`),
};
