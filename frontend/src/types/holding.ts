export type AssetClass = 'EQUITY' | 'BOND' | 'COMMODITY' | 'REAL_ESTATE' | 'CASH';

export interface Holding {
  id: number;
  tickerSymbol: string;
  name: string;
  assetClass: AssetClass;
  quantity: number;
  averageCostBasis: number | null;
  initialValue: number | null;
  currency: string;
}

export interface AddHoldingRequest {
  tickerSymbol: string;
  name: string;
  assetClass: AssetClass;
  quantity: number;
  averageCostBasis?: number;
  initialValue?: number;
  currency?: string;
}
