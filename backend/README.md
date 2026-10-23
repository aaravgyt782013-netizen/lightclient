# Optional Ar Client API

The backend is intentionally optional and is not required to launch the local UI.

Suggested first endpoints:
- `GET /v1/news` — curated launcher news.
- `GET /v1/updates/launcher` — launcher release metadata.
- `GET /v1/updates/mod?minecraft=1.21.1` — compatible mod release metadata.
- `GET /v1/cosmetics` — original Ar Client cosmetic catalog.

Use HTTPS, strict response schemas, bounded downloads and signed release manifests. Never accept executable URLs from untrusted news content, and never collect Microsoft passwords or access tokens.
