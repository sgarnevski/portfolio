import { authAxiosClient } from './axiosClient';
import { LoginRequest, RegisterRequest, AuthResponse } from '../types/auth';

export const authApi = {
  login: (data: LoginRequest) => authAxiosClient.post<AuthResponse>('/auth/login', data),
  register: (data: RegisterRequest) => authAxiosClient.post<AuthResponse>('/auth/register', data),
  googleExchange: (idToken: string) => authAxiosClient.post<AuthResponse>('/auth/google', { idToken }),
};
