import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { RebalanceResponse } from '../../types/rebalance';

interface RebalanceState {
  result: RebalanceResponse | null;
  loading: boolean;
  error: string | null;
}

const initialState: RebalanceState = {
  result: null,
  loading: false,
  error: null,
};

const rebalanceSlice = createSlice({
  name: 'rebalance',
  initialState,
  reducers: {
    calculateRebalanceRequest: (state, _action: PayloadAction<number>) => {
      state.loading = true;
      state.error = null;
    },
    calculateRebalanceSuccess: (state, action: PayloadAction<RebalanceResponse>) => {
      state.loading = false;
      state.result = action.payload;
    },
    calculateRebalanceFailure: (state, action: PayloadAction<string>) => {
      state.loading = false;
      state.error = action.payload;
    },
    calculateCashRebalanceRequest: (state, _action: PayloadAction<{ portfolioId: number; amount: number }>) => {
      state.loading = true;
      state.error = null;
    },
    clearRebalance: (state) => {
      state.result = null;
      state.error = null;
    },
  },
});

export const {
  calculateRebalanceRequest, calculateRebalanceSuccess, calculateRebalanceFailure,
  calculateCashRebalanceRequest, clearRebalance,
} = rebalanceSlice.actions;
export default rebalanceSlice.reducer;
