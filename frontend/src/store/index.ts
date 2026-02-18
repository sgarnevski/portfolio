import { configureStore } from '@reduxjs/toolkit';
import createSagaMiddleware from 'redux-saga';
import authReducer from './slices/authSlice';
import portfolioReducer from './slices/portfolioSlice';
import holdingReducer from './slices/holdingSlice';
import allocationReducer from './slices/allocationSlice';
import rebalanceReducer from './slices/rebalanceSlice';
import priceReducer from './slices/priceSlice';
import currencyReducer from './slices/currencySlice';
import userReducer from './slices/userSlice';
import rootSaga from './sagas/rootSaga';

const sagaMiddleware = createSagaMiddleware();

export const store = configureStore({
  reducer: {
    auth: authReducer,
    portfolio: portfolioReducer,
    holding: holdingReducer,
    allocation: allocationReducer,
    rebalance: rebalanceReducer,
    price: priceReducer,
    currency: currencyReducer,
    user: userReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({ thunk: false }).concat(sagaMiddleware),
});

sagaMiddleware.run(rootSaga);

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
