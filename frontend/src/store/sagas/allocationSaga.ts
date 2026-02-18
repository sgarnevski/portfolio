import { call, put, takeLatest } from 'redux-saga/effects';
import { PayloadAction } from '@reduxjs/toolkit';
import { allocationApi } from '../../api/allocationApi';
import { SetAllocationRequest } from '../../types/allocation';
import {
  fetchAllocationsRequest, fetchAllocationsSuccess, fetchAllocationsFailure,
  setAllocationsRequest, setAllocationsSuccess, setAllocationsFailure,
} from '../slices/allocationSlice';

function* handleFetchAllocations(action: PayloadAction<number>) {
  try {
    const response: Awaited<ReturnType<typeof allocationApi.get>> = yield call(allocationApi.get, action.payload);
    yield put(fetchAllocationsSuccess(response.data));
  } catch (error: unknown) {
    const msg = error instanceof Error ? error.message : 'Failed to fetch allocations';
    yield put(fetchAllocationsFailure(msg));
  }
}

function* handleSetAllocations(action: PayloadAction<{ portfolioId: number; data: SetAllocationRequest }>) {
  try {
    const response: Awaited<ReturnType<typeof allocationApi.set>> = yield call(
      allocationApi.set, action.payload.portfolioId, action.payload.data
    );
    yield put(setAllocationsSuccess(response.data));
  } catch (error: unknown) {
    const msg = error instanceof Error ? error.message : 'Failed to set allocations';
    yield put(setAllocationsFailure(msg));
  }
}

export default function* allocationSaga() {
  yield takeLatest(fetchAllocationsRequest.type, handleFetchAllocations);
  yield takeLatest(setAllocationsRequest.type, handleSetAllocations);
}
