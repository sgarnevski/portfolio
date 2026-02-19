import { useState, useEffect, useRef } from 'react';
import {
  createChart,
  createSeriesMarkers,
  CandlestickSeries,
  HistogramSeries,
  ColorType,
} from 'lightweight-charts';
import type { IChartApi, SeriesMarker, Time } from 'lightweight-charts';
import { quoteApi } from '../../api/quoteApi';
import { HistoricalDataPoint } from '../../types/quote';
import { Trade } from '../../types/holding';

interface Props {
  symbol: string;
  trades: Trade[];
}

type Range = '1d' | '1w' | '1m' | '3m' | '6m' | '1y';

const RANGES: { label: string; value: Range }[] = [
  { label: '1D', value: '1d' },
  { label: '1W', value: '1w' },
  { label: '1M', value: '1m' },
  { label: '3M', value: '3m' },
  { label: '6M', value: '6m' },
  { label: '1Y', value: '1y' },
];

export default function PriceChart({ symbol, trades }: Props) {
  const [range, setRange] = useState<Range>('1m');
  const [data, setData] = useState<HistoricalDataPoint[]>([]);
  const [loading, setLoading] = useState(true);
  const chartContainerRef = useRef<HTMLDivElement>(null);
  const chartRef = useRef<IChartApi | null>(null);

  // Fetch data
  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    quoteApi.getHistory(symbol, range).then((res) => {
      if (!cancelled) {
        setData(res.data);
        setLoading(false);
      }
    }).catch(() => {
      if (!cancelled) {
        setData([]);
        setLoading(false);
      }
    });
    return () => { cancelled = true; };
  }, [symbol, range]);

  // Build chart from scratch whenever data, trades, or range change
  useEffect(() => {
    if (loading || !chartContainerRef.current || data.length === 0) return;

    // Tear down previous chart
    if (chartRef.current) {
      chartRef.current.remove();
      chartRef.current = null;
    }

    const container = chartContainerRef.current;
    const useUnixTime = range === '1d' || range === '1w';

    const chart = createChart(container, {
      autoSize: true,
      layout: {
        background: { type: ColorType.Solid, color: '#ffffff' },
        textColor: '#6b7280',
        fontSize: 11,
      },
      grid: {
        vertLines: { color: '#f3f4f6' },
        horzLines: { color: '#f3f4f6' },
      },
      crosshair: { mode: 0 },
      rightPriceScale: { borderVisible: false },
      timeScale: {
        borderVisible: false,
        timeVisible: useUnixTime,
      },
    });

    const candleSeries = chart.addSeries(CandlestickSeries, {
      upColor: '#22c55e',
      downColor: '#ef4444',
      borderDownColor: '#ef4444',
      borderUpColor: '#22c55e',
      wickDownColor: '#ef4444',
      wickUpColor: '#22c55e',
    });

    const volSeries = chart.addSeries(HistogramSeries, {
      priceFormat: { type: 'volume' },
      priceScaleId: 'volume',
    });

    chart.priceScale('volume').applyOptions({
      scaleMargins: { top: 0.8, bottom: 0 },
    });

    // Deduplicate timestamps (rounding can cause dupes)
    const seen = new Set<number>();
    const dedupedData: HistoricalDataPoint[] = [];
    for (const d of data) {
      const t = useUnixTime ? d.timestamp : d.timestamp - (d.timestamp % 86400);
      if (!seen.has(t)) {
        seen.add(t);
        dedupedData.push(d);
      }
    }

    const candleData = dedupedData.map((d) => ({
      time: (useUnixTime ? d.timestamp : d.timestamp - (d.timestamp % 86400)) as Time,
      open: d.open,
      high: d.high,
      low: d.low,
      close: d.close,
    }));

    const volumeData = dedupedData.map((d) => ({
      time: (useUnixTime ? d.timestamp : d.timestamp - (d.timestamp % 86400)) as Time,
      value: d.volume,
      color: d.close >= d.open ? 'rgba(34,197,94,0.3)' : 'rgba(239,68,68,0.3)',
    }));

    candleSeries.setData(candleData);
    volSeries.setData(volumeData);

    // Trade markers
    const markers = createSeriesMarkers(candleSeries);
    const tradeMarkers: SeriesMarker<Time>[] = [];
    const dataTimestamps = dedupedData.map((d) =>
      useUnixTime ? d.timestamp : d.timestamp - (d.timestamp % 86400)
    );

    for (const trade of trades) {
      const tradeTs = new Date(trade.date + 'T00:00:00').getTime() / 1000;
      let closest = dataTimestamps[0];
      let minDiff = Math.abs(tradeTs - closest);
      for (const ts of dataTimestamps) {
        const diff = Math.abs(tradeTs - ts);
        if (diff < minDiff) {
          minDiff = diff;
          closest = ts;
        }
      }
      const maxDiff = useUnixTime ? 86400 : 86400 * 2;
      if (minDiff <= maxDiff) {
        tradeMarkers.push({
          time: closest as Time,
          position: trade.type === 'BUY' ? 'belowBar' : 'aboveBar',
          color: trade.type === 'BUY' ? '#16a34a' : '#dc2626',
          shape: trade.type === 'BUY' ? 'arrowUp' : 'arrowDown',
          text: `${trade.type} ${trade.quantity}@${trade.price.toFixed(2)}`,
        });
      }
    }

    tradeMarkers.sort((a, b) => (a.time as number) - (b.time as number));
    markers.setMarkers(tradeMarkers);

    chart.timeScale().fitContent();
    chartRef.current = chart;

    return () => {
      chart.remove();
      chartRef.current = null;
    };
  }, [loading, data, trades, range]);

  return (
    <div className="bg-white rounded-lg shadow-sm border p-4 mb-4">
      <div className="flex items-center justify-between mb-3">
        <h3 className="text-sm font-semibold text-gray-800">Price History</h3>
        <div className="flex gap-1">
          {RANGES.map((r) => (
            <button
              key={r.value}
              onClick={() => setRange(r.value)}
              className={`px-2 py-1 text-xs rounded ${
                range === r.value
                  ? 'bg-blue-600 text-white'
                  : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
              }`}
            >
              {r.label}
            </button>
          ))}
        </div>
      </div>

      <div style={{ position: 'relative', height: 280 }}>
        <div ref={chartContainerRef} style={{ width: '100%', height: '100%' }} />
        {loading && (
          <div className="absolute inset-0 flex items-center justify-center bg-white/80 z-10">
            <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600" />
          </div>
        )}
        {!loading && data.length === 0 && (
          <div className="absolute inset-0 flex items-center justify-center bg-white z-10 text-gray-400 text-sm">
            No data available
          </div>
        )}
      </div>
    </div>
  );
}
