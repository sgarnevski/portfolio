import axios, { AxiosInstance } from 'axios';

function addInterceptors(client: AxiosInstance) {
  client.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  });

  client.interceptors.response.use(
    (response) => response,
    (error) => {
      if (error.response?.status === 401) {
        localStorage.removeItem('token');
        window.location.href = '/login';
      }
      return Promise.reject(error);
    }
  );
}

const axiosClient = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
});
addInterceptors(axiosClient);

export const authAxiosClient = axios.create({
  baseURL: '/auth-api',
  headers: { 'Content-Type': 'application/json' },
});
addInterceptors(authAxiosClient);

export default axiosClient;
