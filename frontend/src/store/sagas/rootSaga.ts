import { all, fork } from 'redux-saga/effects';
import authSaga from './authSaga';
import portfolioSaga from './portfolioSaga';
import holdingSaga from './holdingSaga';
import allocationSaga from './allocationSaga';
import rebalanceSaga from './rebalanceSaga';
import priceSaga from './priceSaga';
import currencySaga from './currencySaga';
import userSaga from './userSaga';

export default function* rootSaga() {
  yield all([
    fork(authSaga),
    fork(portfolioSaga),
    fork(holdingSaga),
    fork(allocationSaga),
    fork(rebalanceSaga),
    fork(priceSaga),
    fork(currencySaga),
    fork(userSaga),
  ]);
}
