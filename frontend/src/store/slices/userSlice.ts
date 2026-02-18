import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { UserProfile, UpdateProfileRequest, ChangePasswordRequest } from '../../types/user';

interface UserState {
  profile: UserProfile | null;
  loading: boolean;
  error: string | null;
  passwordChangeSuccess: boolean;
}

const initialState: UserState = {
  profile: null,
  loading: false,
  error: null,
  passwordChangeSuccess: false,
};

const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    fetchProfileRequest: (state) => {
      state.loading = true;
      state.error = null;
    },
    fetchProfileSuccess: (state, action: PayloadAction<UserProfile>) => {
      state.loading = false;
      state.profile = action.payload;
    },
    fetchProfileFailure: (state, action: PayloadAction<string>) => {
      state.loading = false;
      state.error = action.payload;
    },
    updateProfileRequest: (state, _action: PayloadAction<UpdateProfileRequest>) => {
      state.loading = true;
      state.error = null;
    },
    updateProfileSuccess: (state, action: PayloadAction<UserProfile>) => {
      state.loading = false;
      state.profile = action.payload;
    },
    updateProfileFailure: (state, action: PayloadAction<string>) => {
      state.loading = false;
      state.error = action.payload;
    },
    changePasswordRequest: (state, _action: PayloadAction<ChangePasswordRequest>) => {
      state.loading = true;
      state.error = null;
      state.passwordChangeSuccess = false;
    },
    changePasswordSuccess: (state) => {
      state.loading = false;
      state.passwordChangeSuccess = true;
    },
    changePasswordFailure: (state, action: PayloadAction<string>) => {
      state.loading = false;
      state.error = action.payload;
    },
    clearUserError: (state) => {
      state.error = null;
      state.passwordChangeSuccess = false;
    },
  },
});

export const {
  fetchProfileRequest, fetchProfileSuccess, fetchProfileFailure,
  updateProfileRequest, updateProfileSuccess, updateProfileFailure,
  changePasswordRequest, changePasswordSuccess, changePasswordFailure,
  clearUserError,
} = userSlice.actions;
export default userSlice.reducer;
