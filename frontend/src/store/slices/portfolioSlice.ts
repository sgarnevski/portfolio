import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { Portfolio, CreatePortfolioRequest } from '../../types/portfolio';

interface PortfolioState {
  portfolios: Portfolio[];
  selectedPortfolioId: number | null;
  loading: boolean;
  error: string | null;
}

const initialState: PortfolioState = {
  portfolios: [],
  selectedPortfolioId: null,
  loading: false,
  error: null,
};

const portfolioSlice = createSlice({
  name: 'portfolio',
  initialState,
  reducers: {
    fetchPortfoliosRequest: (state) => {
      state.loading = true;
      state.error = null;
    },
    fetchPortfoliosSuccess: (state, action: PayloadAction<Portfolio[]>) => {
      state.loading = false;
      state.portfolios = action.payload;
    },
    fetchPortfoliosFailure: (state, action: PayloadAction<string>) => {
      state.loading = false;
      state.error = action.payload;
    },
    selectPortfolio: (state, action: PayloadAction<number | null>) => {
      state.selectedPortfolioId = action.payload;
    },
    createPortfolioRequest: (state, _action: PayloadAction<CreatePortfolioRequest>) => {
      state.loading = true;
      state.error = null;
    },
    createPortfolioSuccess: (state, action: PayloadAction<Portfolio>) => {
      state.loading = false;
      state.portfolios.push(action.payload);
    },
    createPortfolioFailure: (state, action: PayloadAction<string>) => {
      state.loading = false;
      state.error = action.payload;
    },
    updatePortfolioRequest: (state, _action: PayloadAction<{ id: number; data: CreatePortfolioRequest }>) => {
      state.loading = true;
      state.error = null;
    },
    updatePortfolioSuccess: (state, action: PayloadAction<Portfolio>) => {
      state.loading = false;
      const idx = state.portfolios.findIndex((p) => p.id === action.payload.id);
      if (idx !== -1) state.portfolios[idx] = action.payload;
    },
    updatePortfolioFailure: (state, action: PayloadAction<string>) => {
      state.loading = false;
      state.error = action.payload;
    },
    deletePortfolioRequest: (state, _action: PayloadAction<number>) => {
      state.loading = true;
    },
    deletePortfolioSuccess: (state, action: PayloadAction<number>) => {
      state.loading = false;
      state.portfolios = state.portfolios.filter((p) => p.id !== action.payload);
      if (state.selectedPortfolioId === action.payload) {
        state.selectedPortfolioId = null;
      }
    },
    deletePortfolioFailure: (state, action: PayloadAction<string>) => {
      state.loading = false;
      state.error = action.payload;
    },
  },
});

export const {
  fetchPortfoliosRequest, fetchPortfoliosSuccess, fetchPortfoliosFailure,
  selectPortfolio,
  createPortfolioRequest, createPortfolioSuccess, createPortfolioFailure,
  updatePortfolioRequest, updatePortfolioSuccess, updatePortfolioFailure,
  deletePortfolioRequest, deletePortfolioSuccess, deletePortfolioFailure,
} = portfolioSlice.actions;
export default portfolioSlice.reducer;
