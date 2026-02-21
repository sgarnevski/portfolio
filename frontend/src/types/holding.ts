export type AssetClass = 'EQUITY' | 'BOND' | 'COMMODITY' | 'REAL_ESTATE' | 'CASH';

export interface Lot {
  tradeId: number;
  purchaseDate: string;
  originalQuantity: number;
  remainingQuantity: number;
  costBasisPerShare: number;
}

export interface Holding {
  id: number;
  tickerSymbol: string;
  name: string;
  assetClass: AssetClass;
  quantity: number;
  averageCostBasis: number | null;
  totalCost: number | null;
  realizedPnL: number | null;
  currency: string;
  trades: Trade[];
  lots: Lot[];
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
