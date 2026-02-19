export type AssetClass = 'EQUITY' | 'BOND' | 'COMMODITY' | 'REAL_ESTATE' | 'CASH';

export interface Holding {
  id: number;
  tickerSymbol: string;
  name: string;
  assetClass: AssetClass;
  quantity: number;
  averageCostBasis: number | null;
  totalCost: number | null;
  currency: string;
  trades: Trade[];
}

export interface Trade {
  id: number;
  date: string;
  type: 'BUY' | 'SELL';
  quantity: number;
  price: number;
  fee: number | null;
  createdAt: string;
}

export interface CreateHoldingRequest {
  tickerSymbol: string;
  name: string;
  assetClass: AssetClass;
  currency?: string;
}

export interface CreateTradeRequest {
  date: string;
  type: 'BUY' | 'SELL';
  quantity: number;
  price: number;
  fee?: number;
}
