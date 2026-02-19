import { call, put, takeLatest } from 'redux-saga/effects';
import { PayloadAction } from '@reduxjs/toolkit';
import { authApi } from '../../api/authApi';
import {
  loginRequest, loginSuccess, loginFailure,
  registerRequest, registerSuccess, registerFailure,
  googleLoginRequest, googleLoginSuccess, googleLoginFailure,
  logout,
} from '../slices/authSlice';

function* handleLogin(action: PayloadAction<{ username: string; password: string }>) {
  try {
    const response: Awaited<ReturnType<typeof authApi.login>> = yield call(authApi.login, action.payload);
    const { token, username, userId } = response.data;
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify({ id: userId, username }));
    yield put(loginSuccess({ token, username, userId }));
  } catch (error: unknown) {
    const msg = error instanceof Error ? error.message : 'Login failed';
    yield put(loginFailure(msg));
  }
}

function* handleRegister(action: PayloadAction<{ username: string; email: string; password: string }>) {
  try {
    const response: Awaited<ReturnType<typeof authApi.register>> = yield call(authApi.register, action.payload);
    const { token, username, userId } = response.data;
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify({ id: userId, username }));
    yield put(registerSuccess({ token, username, userId }));
  } catch (error: unknown) {
    const msg = error instanceof Error ? error.message : 'Registration failed';
    yield put(registerFailure(msg));
  }
}

function* handleGoogleLogin(action: PayloadAction<string>) {
  try {
    const response: Awaited<ReturnType<typeof authApi.googleExchange>> = yield call(authApi.googleExchange, action.payload);
    const { token, username, userId } = response.data;
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify({ id: userId, username }));
    yield put(googleLoginSuccess({ token, username, userId }));
  } catch (error: unknown) {
    const msg = error instanceof Error ? error.message : 'Google sign-in failed';
    yield put(googleLoginFailure(msg));
  }
}

function* handleLogout() {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
}

export default function* authSaga() {
  yield takeLatest(loginRequest.type, handleLogin);
  yield takeLatest(registerRequest.type, handleRegister);
  yield takeLatest(googleLoginRequest.type, handleGoogleLogin);
  yield takeLatest(logout.type, handleLogout);
}
