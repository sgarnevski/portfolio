import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { Holding, AddHoldingRequest } from '../../types/holding';

interface HoldingState {
  holdings: Holding[];
  loading: boolean;
  error: string | null;
}

const initialState: HoldingState = {
  holdings: [],
  loading: false,
  error: null,
};

const holdingSlice = createSlice({
  name: 'holding',
  initialState,
  reducers: {
    fetchHoldingsRequest: (state, _action: PayloadAction<number>) => {
      state.loading = true;
      state.error = null;
    },
    fetchHoldingsSuccess: (state, action: PayloadAction<Holding[]>) => {
      state.loading = false;
      state.holdings = action.payload;
    },
    fetchHoldingsFailure: (state, action: PayloadAction<string>) => {
      state.loading = false;
      state.error = action.payload;
    },
    addHoldingRequest: (state, _action: PayloadAction<{ portfolioId: number; data: AddHoldingRequest }>) => {
      state.loading = true;
      state.error = null;
    },
    addHoldingSuccess: (state, action: PayloadAction<Holding>) => {
      state.loading = false;
      state.holdings.push(action.payload);
    },
    addHoldingFailure: (state, action: PayloadAction<string>) => {
      state.loading = false;
      state.error = action.payload;
    },
    updateHoldingRequest: (state, _action: PayloadAction<{ portfolioId: number; holdingId: number; data: AddHoldingRequest }>) => {
      state.loading = true;
      state.error = null;
    },
    updateHoldingSuccess: (state, action: PayloadAction<Holding>) => {
      state.loading = false;
      const idx = state.holdings.findIndex((h) => h.id === action.payload.id);
      if (idx !== -1) state.holdings[idx] = action.payload;
    },
    updateHoldingFailure: (state, action: PayloadAction<string>) => {
      state.loading = false;
      state.error = action.payload;
    },
    removeHoldingRequest: (state, _action: PayloadAction<{ portfolioId: number; holdingId: number }>) => {
      state.loading = true;
    },
    removeHoldingSuccess: (state, action: PayloadAction<number>) => {
      state.loading = false;
      state.holdings = state.holdings.filter((h) => h.id !== action.payload);
    },
    removeHoldingFailure: (state, action: PayloadAction<string>) => {
      state.loading = false;
      state.error = action.payload;
    },
    clearHoldings: (state) => {
      state.holdings = [];
    },
  },
});

export const {
  fetchHoldingsRequest, fetchHoldingsSuccess, fetchHoldingsFailure,
  addHoldingRequest, addHoldingSuccess, addHoldingFailure,
  updateHoldingRequest, updateHoldingSuccess, updateHoldingFailure,
  removeHoldingRequest, removeHoldingSuccess, removeHoldingFailure,
  clearHoldings,
} = holdingSlice.actions;
export default holdingSlice.reducer;
