export interface QuoteData {
  symbol: string;
  shortName: string;
  regularMarketPrice: number;
  regularMarketChange: number;
  regularMarketChangePercent: number;
  currency: string;
  exchangeName: string;
}

export interface StockPriceMessage {
  quotes: QuoteData[];
  timestamp: string;
}

export interface HistoricalDataPoint {
  timestamp: number;
  open: number;
  high: number;
  low: number;
  close: number;
  volume: number;
}

export interface TickerSearchResult {
  symbol: string;
  shortName: string;
  longName: string;
  exchange: string;
  assetType: string;
}
