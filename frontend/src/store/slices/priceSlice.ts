import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { QuoteData } from '../../types/quote';

interface PriceState {
  prices: Record<string, QuoteData>;
  connected: boolean;
  lastUpdated: string | null;
  loading: boolean;
}

const initialState: PriceState = {
  prices: {},
  connected: false,
  lastUpdated: null,
  loading: false,
};

const priceSlice = createSlice({
  name: 'price',
  initialState,
  reducers: {
    fetchPricesRequest: (state, _action: PayloadAction<string[]>) => {
      state.loading = true;
    },
    fetchPricesSuccess: (state, action: PayloadAction<QuoteData[]>) => {
      state.loading = false;
      for (const quote of action.payload) {
        state.prices[quote.symbol] = quote;
      }
      state.lastUpdated = new Date().toISOString();
    },
    fetchPricesFailure: (state) => {
      state.loading = false;
    },
    priceUpdateReceived: (state, action: PayloadAction<{ quotes: QuoteData[]; timestamp: string }>) => {
      for (const quote of action.payload.quotes) {
        state.prices[quote.symbol] = quote;
      }
      state.lastUpdated = action.payload.timestamp;
    },
    wsConnected: (state) => {
      state.connected = true;
    },
    wsDisconnected: (state) => {
      state.connected = false;
    },
  },
});

export const {
  fetchPricesRequest, fetchPricesSuccess, fetchPricesFailure,
  priceUpdateReceived, wsConnected, wsDisconnected,
} = priceSlice.actions;
export default priceSlice.reducer;
