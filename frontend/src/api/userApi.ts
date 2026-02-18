import axiosClient from './axiosClient';
import { UserProfile, UpdateProfileRequest, ChangePasswordRequest } from '../types/user';

export const userApi = {
  getProfile: () =>
    axiosClient.get<UserProfile>('/users/me'),
  updateProfile: (data: UpdateProfileRequest) =>
    axiosClient.put<UserProfile>('/users/me', data),
  changePassword: (data: ChangePasswordRequest) =>
    axiosClient.post('/users/me/change-password', data),
};
