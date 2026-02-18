import { AssetClass } from './holding';

export interface AllocationComparison {
  assetClass: AssetClass;
  currentPercentage: number;
  targetPercentage: number;
  driftPercentage: number;
  currentValue: number;
  targetValue: number;
}

export interface TradeRecommendation {
  tickerSymbol: string;
  name: string;
  assetClass: AssetClass;
  action: 'BUY' | 'SELL';
  shares: number;
  currentPrice: number;
  estimatedCost: number;
  currentWeight: number;
  targetWeight: number;
}

export interface RebalanceResponse {
  portfolioId: number;
  totalPortfolioValue: number;
  currency: string;
  allocations: AllocationComparison[];
  trades: TradeRecommendation[];
  calculatedAt: string;
}
