# OrbitLens frontend

React + TypeScript + Vite interface for the OrbitLens Spring Boot API.

## Development

The backend must be running on `http://localhost:8081`.

```bash
cp .env.example .env
npm install
npm run dev
```

Open `http://localhost:5173`.

`VITE_API_BASE_URL` identifies the backend target. During development, requests use the Vite `/api` proxy automatically, avoiding a cross-origin request. Production builds use the explicit URL, so Spring Boot must allow the deployed frontend origin for `GET` requests to `/api/**`.

## Production build

```bash
npm run build
npm run preview
```

The production bundle is written to `dist/`.
