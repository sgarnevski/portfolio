import { Holding } from './holding';
import { TargetAllocation } from './allocation';

export interface Portfolio {
  id: number;
  name: string;
  description: string;
  holdings: Holding[];
  targetAllocations: TargetAllocation[];
  createdAt: string;
  updatedAt: string;
}

export interface CreatePortfolioRequest {
  name: string;
  description?: string;
}
