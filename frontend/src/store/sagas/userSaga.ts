import { call, put, takeLatest } from 'redux-saga/effects';
import { PayloadAction } from '@reduxjs/toolkit';
import { userApi } from '../../api/userApi';
import { extractErrorMessage } from '../../utils/extractErrorMessage';
import { UpdateProfileRequest, ChangePasswordRequest } from '../../types/user';
import {
  fetchProfileRequest, fetchProfileSuccess, fetchProfileFailure,
  updateProfileRequest, updateProfileSuccess, updateProfileFailure,
  changePasswordRequest, changePasswordSuccess, changePasswordFailure,
} from '../slices/userSlice';

function* handleFetchProfile() {
  try {
    const response: Awaited<ReturnType<typeof userApi.getProfile>> = yield call(userApi.getProfile);
    yield put(fetchProfileSuccess(response.data));
  } catch (error: unknown) {
    const msg = extractErrorMessage(error, 'Failed to fetch profile');
    yield put(fetchProfileFailure(msg));
  }
}

function* handleUpdateProfile(action: PayloadAction<UpdateProfileRequest>) {
  try {
    const response: Awaited<ReturnType<typeof userApi.updateProfile>> = yield call(userApi.updateProfile, action.payload);
    yield put(updateProfileSuccess(response.data));
  } catch (error: unknown) {
    const msg = extractErrorMessage(error, 'Failed to update profile');
    yield put(updateProfileFailure(msg));
  }
}

function* handleChangePassword(action: PayloadAction<ChangePasswordRequest>) {
  try {
    yield call(userApi.changePassword, action.payload);
    yield put(changePasswordSuccess());
  } catch (error: unknown) {
    const msg = extractErrorMessage(error, 'Failed to change password');
    yield put(changePasswordFailure(msg));
  }
}

export default function* userSaga() {
  yield takeLatest(fetchProfileRequest.type, handleFetchProfile);
  yield takeLatest(updateProfileRequest.type, handleUpdateProfile);
  yield takeLatest(changePasswordRequest.type, handleChangePassword);
}
