import { eventChannel, EventChannel } from 'redux-saga';
import { take, put, call, fork, cancel, cancelled, takeLatest } from 'redux-saga/effects';
import { Task } from 'redux-saga';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { PayloadAction } from '@reduxjs/toolkit';
import { quoteApi } from '../../api/quoteApi';
import {
  fetchPricesRequest, fetchPricesSuccess, fetchPricesFailure,
  priceUpdateReceived, wsConnected, wsDisconnected,
} from '../slices/priceSlice';
import { loginSuccess, logout } from '../slices/authSlice';

interface WsEvent {
  type: 'connected' | 'disconnected' | 'prices';
  payload?: unknown;
}

function createWebSocketChannel(): EventChannel<WsEvent> {
  return eventChannel<WsEvent>((emitter) => {
    const client = new Client({
      webSocketFactory: () => new SockJS('/ws'),
      onConnect: () => {
        emitter({ type: 'connected' });
        client.subscribe('/topic/prices', (message) => {
          const data = JSON.parse(message.body);
          emitter({ type: 'prices', payload: data });
        });
      },
      onDisconnect: () => {
        emitter({ type: 'disconnected' });
      },
      reconnectDelay: 5000,
    });
    client.activate();
    return () => {
      client.deactivate();
    };
  });
}

function* watchPriceChannel() {
  const channel: EventChannel<WsEvent> = yield call(createWebSocketChannel);
  try {
    while (true) {
      const event: WsEvent = yield take(channel);
      if (event.type === 'prices') {
        yield put(priceUpdateReceived(event.payload as { quotes: never[]; timestamp: string }));
      } else if (event.type === 'connected') {
        yield put(wsConnected());
      } else if (event.type === 'disconnected') {
        yield put(wsDisconnected());
      }
    }
  } finally {
    const isCancelled: boolean = yield cancelled();
    if (isCancelled) {
      channel.close();
      yield put(wsDisconnected());
    }
  }
}

function* handleFetchPrices(action: PayloadAction<string[]>) {
  try {
    if (action.payload.length === 0) return;
    const response: Awaited<ReturnType<typeof quoteApi.getQuotes>> = yield call(quoteApi.getQuotes, action.payload);
    yield put(fetchPricesSuccess(response.data));
  } catch {
    yield put(fetchPricesFailure());
  }
}

function* watchWebSocket() {
  while (true) {
    yield take(loginSuccess.type);
    const task: Task = yield fork(watchPriceChannel);
    yield take(logout.type);
    yield cancel(task);
  }
}

export default function* priceSaga() {
  yield fork(watchWebSocket);
  yield takeLatest(fetchPricesRequest.type, handleFetchPrices);
}
