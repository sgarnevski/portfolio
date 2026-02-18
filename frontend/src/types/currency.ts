export interface Currency {
  id: number;
  code: string;
  name: string;
}

export interface CreateCurrencyRequest {
  code: string;
  name: string;
}
