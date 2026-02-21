import { AssetClass } from './holding';

export interface AllocationComparison {
  assetClass: AssetClass;
  currentPercentage: number;
  targetPercentage: number;
  driftPercentage: number;
  currentValue: number;
  targetValue: number;
}

export interface LotSaleDetail {
  tradeId: number;
  purchaseDate: string;
  quantity: number;
  costBasis: number;
  estimatedGain: number;
}

export interface TradeRecommendation {
  holdingId: number;
  tickerSymbol: string;
  name: string;
  assetClass: AssetClass;
  action: 'BUY' | 'SELL';
  shares: number;
  currentPrice: number;
  estimatedCost: number;
  currentWeight: number;
  targetWeight: number;
  lotDetails?: LotSaleDetail[];
}

export interface RebalanceResponse {
  portfolioId: number;
  totalPortfolioValue: number;
  currency: string;
  allocations: AllocationComparison[];
  trades: TradeRecommendation[];
  unallocatedCash?: number;
  calculatedAt: string;
}
