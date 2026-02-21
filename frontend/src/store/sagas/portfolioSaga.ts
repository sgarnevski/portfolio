import { call, put, takeLatest } from 'redux-saga/effects';
import { PayloadAction } from '@reduxjs/toolkit';
import { portfolioApi } from '../../api/portfolioApi';
import { extractErrorMessage } from '../../utils/extractErrorMessage';
import { CreatePortfolioRequest } from '../../types/portfolio';
import {
  fetchPortfoliosRequest, fetchPortfoliosSuccess, fetchPortfoliosFailure,
  createPortfolioRequest, createPortfolioSuccess, createPortfolioFailure,
  updatePortfolioRequest, updatePortfolioSuccess, updatePortfolioFailure,
  deletePortfolioRequest, deletePortfolioSuccess, deletePortfolioFailure,
  updateCashBalanceRequest, updateCashBalanceSuccess, updateCashBalanceFailure,
} from '../slices/portfolioSlice';

function* handleFetchPortfolios() {
  try {
    const response: Awaited<ReturnType<typeof portfolioApi.getAll>> = yield call(portfolioApi.getAll);
    yield put(fetchPortfoliosSuccess(response.data));
  } catch (error: unknown) {
    const msg = extractErrorMessage(error, 'Failed to fetch portfolios');
    yield put(fetchPortfoliosFailure(msg));
  }
}

function* handleCreatePortfolio(action: PayloadAction<CreatePortfolioRequest>) {
  try {
    const response: Awaited<ReturnType<typeof portfolioApi.create>> = yield call(portfolioApi.create, action.payload);
    yield put(createPortfolioSuccess(response.data));
  } catch (error: unknown) {
    const msg = extractErrorMessage(error, 'Failed to create portfolio');
    yield put(createPortfolioFailure(msg));
  }
}

function* handleUpdatePortfolio(action: PayloadAction<{ id: number; data: CreatePortfolioRequest }>) {
  try {
    const response: Awaited<ReturnType<typeof portfolioApi.update>> = yield call(
      portfolioApi.update, action.payload.id, action.payload.data
    );
    yield put(updatePortfolioSuccess(response.data));
  } catch (error: unknown) {
    const msg = extractErrorMessage(error, 'Failed to update portfolio');
    yield put(updatePortfolioFailure(msg));
  }
}

function* handleDeletePortfolio(action: PayloadAction<number>) {
  try {
    yield call(portfolioApi.delete, action.payload);
    yield put(deletePortfolioSuccess(action.payload));
  } catch (error: unknown) {
    const msg = extractErrorMessage(error, 'Failed to delete portfolio');
    yield put(deletePortfolioFailure(msg));
  }
}

function* handleUpdateCashBalance(action: PayloadAction<{ id: number; cashBalance: number }>) {
  try {
    const response: Awaited<ReturnType<typeof portfolioApi.updateCashBalance>> = yield call(
      portfolioApi.updateCashBalance, action.payload.id, action.payload.cashBalance
    );
    yield put(updateCashBalanceSuccess(response.data));
  } catch (error: unknown) {
    const msg = extractErrorMessage(error, 'Failed to update cash balance');
    yield put(updateCashBalanceFailure(msg));
  }
}

export default function* portfolioSaga() {
  yield takeLatest(fetchPortfoliosRequest.type, handleFetchPortfolios);
  yield takeLatest(createPortfolioRequest.type, handleCreatePortfolio);
  yield takeLatest(updatePortfolioRequest.type, handleUpdatePortfolio);
  yield takeLatest(deletePortfolioRequest.type, handleDeletePortfolio);
  yield takeLatest(updateCashBalanceRequest.type, handleUpdateCashBalance);
}
