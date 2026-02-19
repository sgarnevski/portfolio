import { call, put, takeLatest } from 'redux-saga/effects';
import { PayloadAction } from '@reduxjs/toolkit';
import { tradeApi } from '../../api/tradeApi';
import { CreateTradeRequest } from '../../types/holding';
import {
  fetchTradesRequest, fetchTradesSuccess, fetchTradesFailure,
  addTradeRequest, addTradeSuccess, addTradeFailure,
  updateTradeRequest, updateTradeSuccess, updateTradeFailure,
  deleteTradeRequest, deleteTradeSuccess, deleteTradeFailure,
} from '../slices/tradeSlice';

function* handleFetchTrades(action: PayloadAction<{ portfolioId: number; holdingId: number }>) {
  try {
    const response: Awaited<ReturnType<typeof tradeApi.getAll>> = yield call(
      tradeApi.getAll, action.payload.portfolioId, action.payload.holdingId
    );
    yield put(fetchTradesSuccess(response.data));
  } catch (error: unknown) {
    const msg = error instanceof Error ? error.message : 'Failed to fetch trades';
    yield put(fetchTradesFailure(msg));
  }
}

function* handleAddTrade(action: PayloadAction<{ portfolioId: number; holdingId: number; data: CreateTradeRequest }>) {
  try {
    const response: Awaited<ReturnType<typeof tradeApi.add>> = yield call(
      tradeApi.add, action.payload.portfolioId, action.payload.holdingId, action.payload.data
    );
    yield put(addTradeSuccess(response.data));
  } catch (error: unknown) {
    const msg = error instanceof Error ? error.message : 'Failed to add trade';
    yield put(addTradeFailure(msg));
  }
}

function* handleUpdateTrade(action: PayloadAction<{ portfolioId: number; holdingId: number; tradeId: number; data: CreateTradeRequest }>) {
  try {
    const response: Awaited<ReturnType<typeof tradeApi.update>> = yield call(
      tradeApi.update, action.payload.portfolioId, action.payload.holdingId, action.payload.tradeId, action.payload.data
    );
    yield put(updateTradeSuccess(response.data));
  } catch (error: unknown) {
    const msg = error instanceof Error ? error.message : 'Failed to update trade';
    yield put(updateTradeFailure(msg));
  }
}

function* handleDeleteTrade(action: PayloadAction<{ portfolioId: number; holdingId: number; tradeId: number }>) {
  try {
    yield call(tradeApi.delete, action.payload.portfolioId, action.payload.holdingId, action.payload.tradeId);
    yield put(deleteTradeSuccess(action.payload.tradeId));
  } catch (error: unknown) {
    const msg = error instanceof Error ? error.message : 'Failed to delete trade';
    yield put(deleteTradeFailure(msg));
  }
}

export default function* tradeSaga() {
  yield takeLatest(fetchTradesRequest.type, handleFetchTrades);
  yield takeLatest(addTradeRequest.type, handleAddTrade);
  yield takeLatest(updateTradeRequest.type, handleUpdateTrade);
  yield takeLatest(deleteTradeRequest.type, handleDeleteTrade);
}
