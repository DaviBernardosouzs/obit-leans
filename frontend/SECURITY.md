# Frontend security

## Implemented controls

- Dependencies are pinned and locked. Run `npm audit` after dependency updates.
- Content Security Policy restricts scripts, styles, images, connections, objects, and form targets.
- Development and preview servers send MIME-sniffing, framing, referrer, permissions, and opener-policy headers.
- External links opened in new tabs use `noopener noreferrer` and do not send a referrer.
- API responses are type-checked before use; coordinates are range-validated.
- Address input is limited to 240 characters.
- Image responses are limited to 20 MiB and must declare an image content type.
- Blob object URLs are revoked when replaced and on page cleanup.

## Production deployment

The production server or CDN must also send these headers because Vite's `server.headers` and `preview.headers` only apply to local servers:

```text
Content-Security-Policy: default-src 'self'; base-uri 'self'; object-src 'none'; script-src 'self'; style-src 'self'; img-src 'self' blob: data:; connect-src 'self' https://YOUR_API_ORIGIN; form-action 'self'; frame-ancestors 'none'
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
Referrer-Policy: no-referrer
Permissions-Policy: camera=(), microphone=(), geolocation=(), payment=()
Cross-Origin-Opener-Policy: same-origin
```

Replace `YOUR_API_ORIGIN` with the production API origin. Serve both frontend and API over HTTPS in production.
