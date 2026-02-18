import { call, put, takeLatest } from 'redux-saga/effects';
import { PayloadAction } from '@reduxjs/toolkit';
import { holdingApi } from '../../api/holdingApi';
import { AddHoldingRequest } from '../../types/holding';
import {
  fetchHoldingsRequest, fetchHoldingsSuccess, fetchHoldingsFailure,
  addHoldingRequest, addHoldingSuccess, addHoldingFailure,
  updateHoldingRequest, updateHoldingSuccess, updateHoldingFailure,
  removeHoldingRequest, removeHoldingSuccess, removeHoldingFailure,
} from '../slices/holdingSlice';

function* handleFetchHoldings(action: PayloadAction<number>) {
  try {
    const response: Awaited<ReturnType<typeof holdingApi.getAll>> = yield call(holdingApi.getAll, action.payload);
    yield put(fetchHoldingsSuccess(response.data));
  } catch (error: unknown) {
    const msg = error instanceof Error ? error.message : 'Failed to fetch holdings';
    yield put(fetchHoldingsFailure(msg));
  }
}

function* handleAddHolding(action: PayloadAction<{ portfolioId: number; data: AddHoldingRequest }>) {
  try {
    const response: Awaited<ReturnType<typeof holdingApi.add>> = yield call(
      holdingApi.add, action.payload.portfolioId, action.payload.data
    );
    yield put(addHoldingSuccess(response.data));
  } catch (error: unknown) {
    const msg = error instanceof Error ? error.message : 'Failed to add holding';
    yield put(addHoldingFailure(msg));
  }
}

function* handleUpdateHolding(action: PayloadAction<{ portfolioId: number; holdingId: number; data: AddHoldingRequest }>) {
  try {
    const response: Awaited<ReturnType<typeof holdingApi.update>> = yield call(
      holdingApi.update, action.payload.portfolioId, action.payload.holdingId, action.payload.data
    );
    yield put(updateHoldingSuccess(response.data));
  } catch (error: unknown) {
    const msg = error instanceof Error ? error.message : 'Failed to update holding';
    yield put(updateHoldingFailure(msg));
  }
}

function* handleRemoveHolding(action: PayloadAction<{ portfolioId: number; holdingId: number }>) {
  try {
    yield call(holdingApi.delete, action.payload.portfolioId, action.payload.holdingId);
    yield put(removeHoldingSuccess(action.payload.holdingId));
  } catch (error: unknown) {
    const msg = error instanceof Error ? error.message : 'Failed to remove holding';
    yield put(removeHoldingFailure(msg));
  }
}

export default function* holdingSaga() {
  yield takeLatest(fetchHoldingsRequest.type, handleFetchHoldings);
  yield takeLatest(addHoldingRequest.type, handleAddHolding);
  yield takeLatest(updateHoldingRequest.type, handleUpdateHolding);
  yield takeLatest(removeHoldingRequest.type, handleRemoveHolding);
}
