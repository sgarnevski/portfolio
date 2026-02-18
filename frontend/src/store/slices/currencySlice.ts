import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { Currency, CreateCurrencyRequest } from '../../types/currency';

interface CurrencyState {
  currencies: Currency[];
  loading: boolean;
  error: string | null;
}

const initialState: CurrencyState = {
  currencies: [],
  loading: false,
  error: null,
};

const currencySlice = createSlice({
  name: 'currency',
  initialState,
  reducers: {
    fetchCurrenciesRequest: (state) => {
      state.loading = true;
      state.error = null;
    },
    fetchCurrenciesSuccess: (state, action: PayloadAction<Currency[]>) => {
      state.loading = false;
      state.currencies = action.payload;
    },
    fetchCurrenciesFailure: (state, action: PayloadAction<string>) => {
      state.loading = false;
      state.error = action.payload;
    },
    createCurrencyRequest: (state, _action: PayloadAction<CreateCurrencyRequest>) => {
      state.loading = true;
      state.error = null;
    },
    createCurrencySuccess: (state, action: PayloadAction<Currency>) => {
      state.loading = false;
      state.currencies.push(action.payload);
      state.currencies.sort((a, b) => a.code.localeCompare(b.code));
    },
    createCurrencyFailure: (state, action: PayloadAction<string>) => {
      state.loading = false;
      state.error = action.payload;
    },
    updateCurrencyRequest: (state, _action: PayloadAction<{ id: number; data: CreateCurrencyRequest }>) => {
      state.loading = true;
      state.error = null;
    },
    updateCurrencySuccess: (state, action: PayloadAction<Currency>) => {
      state.loading = false;
      const idx = state.currencies.findIndex((c) => c.id === action.payload.id);
      if (idx !== -1) state.currencies[idx] = action.payload;
      state.currencies.sort((a, b) => a.code.localeCompare(b.code));
    },
    updateCurrencyFailure: (state, action: PayloadAction<string>) => {
      state.loading = false;
      state.error = action.payload;
    },
    deleteCurrencyRequest: (state, _action: PayloadAction<number>) => {
      state.loading = true;
      state.error = null;
    },
    deleteCurrencySuccess: (state, action: PayloadAction<number>) => {
      state.loading = false;
      state.currencies = state.currencies.filter((c) => c.id !== action.payload);
    },
    deleteCurrencyFailure: (state, action: PayloadAction<string>) => {
      state.loading = false;
      state.error = action.payload;
    },
  },
});

export const {
  fetchCurrenciesRequest, fetchCurrenciesSuccess, fetchCurrenciesFailure,
  createCurrencyRequest, createCurrencySuccess, createCurrencyFailure,
  updateCurrencyRequest, updateCurrencySuccess, updateCurrencyFailure,
  deleteCurrencyRequest, deleteCurrencySuccess, deleteCurrencyFailure,
} = currencySlice.actions;
export default currencySlice.reducer;
