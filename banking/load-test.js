import http from 'k6/http';

import { htmlReport } from 'https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js';

export const options = {
  vus: 20,
  duration: '30s',
};

export default function () {
  http.post(
    'http://localhost:8080/api/v1/transactions',
    JSON.stringify({
      accountId: 2,
      amount: 1,
      currency: 'EUR',
      direction: 'IN',
      description: 'load test'
    }),
    {
      headers: { 'Content-Type': 'application/json' },
    }
  );
}

export function handleSummary(data) {
  return {
    "report.html": htmlReport(data),
  };
}