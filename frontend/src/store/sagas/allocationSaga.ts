import { call, put, select, takeLatest } from 'redux-saga/effects';
import { PayloadAction } from '@reduxjs/toolkit';
import { allocationApi } from '../../api/allocationApi';
import { extractErrorMessage } from '../../utils/extractErrorMessage';
import { SetAllocationRequest } from '../../types/allocation';
import { Holding } from '../../types/holding';
import {
  fetchAllocationsRequest, fetchAllocationsSuccess, fetchAllocationsFailure,
  setAllocationsRequest, setAllocationsSuccess, setAllocationsFailure,
} from '../slices/allocationSlice';
import { clearRebalance, calculateRebalanceRequest } from '../slices/rebalanceSlice';
import { fetchPricesRequest } from '../slices/priceSlice';
import { RootState } from '../index';

function* handleFetchAllocations(action: PayloadAction<number>) {
  try {
    const response: Awaited<ReturnType<typeof allocationApi.get>> = yield call(allocationApi.get, action.payload);
    yield put(fetchAllocationsSuccess(response.data));
  } catch (error: unknown) {
    const msg = extractErrorMessage(error, 'Failed to fetch allocations');
    yield put(fetchAllocationsFailure(msg));
  }
}

function* handleSetAllocations(action: PayloadAction<{ portfolioId: number; data: SetAllocationRequest }>) {
  try {
    const response: Awaited<ReturnType<typeof allocationApi.set>> = yield call(
      allocationApi.set, action.payload.portfolioId, action.payload.data
    );
    yield put(setAllocationsSuccess(response.data));

    // Clear stale rebalance result and recalculate with new allocations
    yield put(clearRebalance());
    yield put(calculateRebalanceRequest(action.payload.portfolioId));

    // Re-fetch prices so drift notification recalculates with fresh data
    const holdings: Holding[] = yield select((state: RootState) => state.holding.holdings);
    if (holdings.length > 0) {
      const tickers = holdings.map((h) => h.tickerSymbol);
      yield put(fetchPricesRequest(tickers));
    }
  } catch (error: unknown) {
    const msg = extractErrorMessage(error, 'Failed to set allocations');
    yield put(setAllocationsFailure(msg));
  }
}

export default function* allocationSaga() {
  yield takeLatest(fetchAllocationsRequest.type, handleFetchAllocations);
  yield takeLatest(setAllocationsRequest.type, handleSetAllocations);
}
