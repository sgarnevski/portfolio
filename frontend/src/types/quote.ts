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
