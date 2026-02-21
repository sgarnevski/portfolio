import { call, put, takeLatest } from 'redux-saga/effects';
import { PayloadAction } from '@reduxjs/toolkit';
import { currencyApi } from '../../api/currencyApi';
import { extractErrorMessage } from '../../utils/extractErrorMessage';
import { CreateCurrencyRequest } from '../../types/currency';
import {
  fetchCurrenciesRequest, fetchCurrenciesSuccess, fetchCurrenciesFailure,
  createCurrencyRequest, createCurrencySuccess, createCurrencyFailure,
  updateCurrencyRequest, updateCurrencySuccess, updateCurrencyFailure,
  deleteCurrencyRequest, deleteCurrencySuccess, deleteCurrencyFailure,
} from '../slices/currencySlice';

function* handleFetchCurrencies() {
  try {
    const response: Awaited<ReturnType<typeof currencyApi.getAll>> = yield call(currencyApi.getAll);
    yield put(fetchCurrenciesSuccess(response.data));
  } catch (error: unknown) {
    const msg = extractErrorMessage(error, 'Failed to fetch currencies');
    yield put(fetchCurrenciesFailure(msg));
  }
}

function* handleCreateCurrency(action: PayloadAction<CreateCurrencyRequest>) {
  try {
    const response: Awaited<ReturnType<typeof currencyApi.create>> = yield call(currencyApi.create, action.payload);
    yield put(createCurrencySuccess(response.data));
  } catch (error: unknown) {
    const msg = extractErrorMessage(error, 'Failed to create currency');
    yield put(createCurrencyFailure(msg));
  }
}

function* handleUpdateCurrency(action: PayloadAction<{ id: number; data: CreateCurrencyRequest }>) {
  try {
    const response: Awaited<ReturnType<typeof currencyApi.update>> = yield call(
      currencyApi.update, action.payload.id, action.payload.data
    );
    yield put(updateCurrencySuccess(response.data));
  } catch (error: unknown) {
    const msg = extractErrorMessage(error, 'Failed to update currency');
    yield put(updateCurrencyFailure(msg));
  }
}

function* handleDeleteCurrency(action: PayloadAction<number>) {
  try {
    yield call(currencyApi.delete, action.payload);
    yield put(deleteCurrencySuccess(action.payload));
  } catch (error: unknown) {
    const msg = extractErrorMessage(error, 'Failed to delete currency');
    yield put(deleteCurrencyFailure(msg));
  }
}

export default function* currencySaga() {
  yield takeLatest(fetchCurrenciesRequest.type, handleFetchCurrencies);
  yield takeLatest(createCurrencyRequest.type, handleCreateCurrency);
  yield takeLatest(updateCurrencyRequest.type, handleUpdateCurrency);
  yield takeLatest(deleteCurrencyRequest.type, handleDeleteCurrency);
}
