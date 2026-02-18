import axiosClient from './axiosClient';
import { LoginRequest, RegisterRequest, AuthResponse } from '../types/auth';

export const authApi = {
  login: (data: LoginRequest) => axiosClient.post<AuthResponse>('/auth/login', data),
  register: (data: RegisterRequest) => axiosClient.post<AuthResponse>('/auth/register', data),
};
