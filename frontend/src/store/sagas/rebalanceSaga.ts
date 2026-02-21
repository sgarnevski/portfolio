import { call, put, takeLatest } from 'redux-saga/effects';
import { PayloadAction } from '@reduxjs/toolkit';
import { rebalanceApi } from '../../api/rebalanceApi';
import { extractErrorMessage } from '../../utils/extractErrorMessage';
import {
  calculateRebalanceRequest, calculateRebalanceSuccess, calculateRebalanceFailure,
  calculateCashRebalanceRequest,
} from '../slices/rebalanceSlice';

function* handleCalculateRebalance(action: PayloadAction<number>) {
  try {
    const response: Awaited<ReturnType<typeof rebalanceApi.calculate>> = yield call(
      rebalanceApi.calculate, action.payload
    );
    yield put(calculateRebalanceSuccess(response.data));
  } catch (error: unknown) {
    const msg = extractErrorMessage(error, 'Failed to calculate rebalance');
    yield put(calculateRebalanceFailure(msg));
  }
}

function* handleCashRebalance(action: PayloadAction<{ portfolioId: number; amount: number }>) {
  try {
    const response: Awaited<ReturnType<typeof rebalanceApi.cashRebalance>> = yield call(
      rebalanceApi.cashRebalance, action.payload.portfolioId, action.payload.amount
    );
    yield put(calculateRebalanceSuccess(response.data));
  } catch (error: unknown) {
    const msg = extractErrorMessage(error, 'Failed to calculate cash rebalance');
    yield put(calculateRebalanceFailure(msg));
  }
}

export default function* rebalanceSaga() {
  yield takeLatest(calculateRebalanceRequest.type, handleCalculateRebalance);
  yield takeLatest(calculateCashRebalanceRequest.type, handleCashRebalance);
}
