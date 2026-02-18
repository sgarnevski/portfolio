import { AssetClass } from './holding';

export interface TargetAllocation {
  id: number;
  assetClass: AssetClass;
  targetPercentage: number;
}

export interface SetAllocationRequest {
  allocations: {
    assetClass: AssetClass;
    targetPercentage: number;
  }[];
}
