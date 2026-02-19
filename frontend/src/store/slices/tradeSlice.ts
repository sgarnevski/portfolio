import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { Trade, CreateTradeRequest } from '../../types/holding';

interface TradeState {
  trades: Trade[];
  loading: boolean;
  error: string | null;
}

const initialState: TradeState = {
  trades: [],
  loading: false,
  error: null,
};

const tradeSlice = createSlice({
  name: 'trade',
  initialState,
  reducers: {
    fetchTradesRequest: (state, _action: PayloadAction<{ portfolioId: number; holdingId: number }>) => {
      state.loading = true;
      state.error = null;
    },
    fetchTradesSuccess: (state, action: PayloadAction<Trade[]>) => {
      state.loading = false;
      state.trades = action.payload;
    },
    fetchTradesFailure: (state, action: PayloadAction<string>) => {
      state.loading = false;
      state.error = action.payload;
    },
    addTradeRequest: (state, _action: PayloadAction<{ portfolioId: number; holdingId: number; data: CreateTradeRequest }>) => {
      state.loading = true;
      state.error = null;
    },
    addTradeSuccess: (state, action: PayloadAction<Trade>) => {
      state.loading = false;
      state.trades.unshift(action.payload);
    },
    addTradeFailure: (state, action: PayloadAction<string>) => {
      state.loading = false;
      state.error = action.payload;
    },
    updateTradeRequest: (state, _action: PayloadAction<{ portfolioId: number; holdingId: number; tradeId: number; data: CreateTradeRequest }>) => {
      state.loading = true;
      state.error = null;
    },
    updateTradeSuccess: (state, action: PayloadAction<Trade>) => {
      state.loading = false;
      const idx = state.trades.findIndex((t) => t.id === action.payload.id);
      if (idx !== -1) state.trades[idx] = action.payload;
    },
    updateTradeFailure: (state, action: PayloadAction<string>) => {
      state.loading = false;
      state.error = action.payload;
    },
    deleteTradeRequest: (state, _action: PayloadAction<{ portfolioId: number; holdingId: number; tradeId: number }>) => {
      state.loading = true;
      state.error = null;
    },
    deleteTradeSuccess: (state, action: PayloadAction<number>) => {
      state.loading = false;
      state.trades = state.trades.filter((t) => t.id !== action.payload);
    },
    deleteTradeFailure: (state, action: PayloadAction<string>) => {
      state.loading = false;
      state.error = action.payload;
    },
    clearTrades: (state) => {
      state.trades = [];
    },
  },
});

export const {
  fetchTradesRequest, fetchTradesSuccess, fetchTradesFailure,
  addTradeRequest, addTradeSuccess, addTradeFailure,
  updateTradeRequest, updateTradeSuccess, updateTradeFailure,
  deleteTradeRequest, deleteTradeSuccess, deleteTradeFailure,
  clearTrades,
} = tradeSlice.actions;
export default tradeSlice.reducer;
