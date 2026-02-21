import { Holding } from './holding';
import { TargetAllocation } from './allocation';

export interface Portfolio {
  id: number;
  name: string;
  description: string;
  holdings: Holding[];
  targetAllocations: TargetAllocation[];
  driftThreshold: number;
  cashBalance: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreatePortfolioRequest {
  name: string;
  description?: string;
  driftThreshold?: number;
  cashBalance?: number;
}
