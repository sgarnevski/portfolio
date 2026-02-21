import axios from 'axios';

export function extractErrorMessage(error: unknown, fallback: string): string {
  if (axios.isAxiosError(error)) {
    const serverMsg = error.response?.data?.error;
    if (typeof serverMsg === 'string') return serverMsg;
  }
  if (error instanceof Error) return error.message;
  return fallback;
}
