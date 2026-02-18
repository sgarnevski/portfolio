import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { TargetAllocation, SetAllocationRequest } from '../../types/allocation';

interface AllocationState {
  targetAllocations: TargetAllocation[];
  loading: boolean;
  error: string | null;
}

const initialState: AllocationState = {
  targetAllocations: [],
  loading: false,
  error: null,
};

const allocationSlice = createSlice({
  name: 'allocation',
  initialState,
  reducers: {
    fetchAllocationsRequest: (state, _action: PayloadAction<number>) => {
      state.loading = true;
      state.error = null;
    },
    fetchAllocationsSuccess: (state, action: PayloadAction<TargetAllocation[]>) => {
      state.loading = false;
      state.targetAllocations = action.payload;
    },
    fetchAllocationsFailure: (state, action: PayloadAction<string>) => {
      state.loading = false;
      state.error = action.payload;
    },
    setAllocationsRequest: (state, _action: PayloadAction<{ portfolioId: number; data: SetAllocationRequest }>) => {
      state.loading = true;
      state.error = null;
    },
    setAllocationsSuccess: (state, action: PayloadAction<TargetAllocation[]>) => {
      state.loading = false;
      state.targetAllocations = action.payload;
    },
    setAllocationsFailure: (state, action: PayloadAction<string>) => {
      state.loading = false;
      state.error = action.payload;
    },
    clearAllocations: (state) => {
      state.targetAllocations = [];
    },
  },
});

export const {
  fetchAllocationsRequest, fetchAllocationsSuccess, fetchAllocationsFailure,
  setAllocationsRequest, setAllocationsSuccess, setAllocationsFailure,
  clearAllocations,
} = allocationSlice.actions;
export default allocationSlice.reducer;
