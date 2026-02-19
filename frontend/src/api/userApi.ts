import { authAxiosClient } from './axiosClient';
import { UserProfile, UpdateProfileRequest, ChangePasswordRequest } from '../types/user';

export const userApi = {
  getProfile: () =>
    authAxiosClient.get<UserProfile>('/auth/me'),
  updateProfile: (data: UpdateProfileRequest) =>
    authAxiosClient.put<UserProfile>('/auth/me', data),
  changePassword: (data: ChangePasswordRequest) =>
    authAxiosClient.post('/auth/me/change-password', data),
};
