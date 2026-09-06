# OrbitLens API

OrbitLens is a Spring Boot API that geocodes an address, obtains NASA GIBS Earth-observation imagery, reads current GP orbital elements from CelesTrak, propagates satellites with SGP4, and predicts geometric passes above an observer.

## Architecture

The code is split into controller, service, client, DTO, and domain layers. `observation` orchestrates the existing services without duplicating their responsibilities. The MVP is stateless: it has no database, authentication, users, polling scheduler, or startup downloads.

## Requirements and execution

- Java 21+
- Internet access at runtime for Nominatim, NASA GIBS, and CelesTrak

Run tests and start the API on port 8081:

```bash
./mvnw test
./mvnw spring-boot:run
```

Configuration is in `src/main/resources/application.properties`. `NOMINATIM_USER_AGENT` may override the non-personal default `OrbitLens/1.0`. Connection/read timeouts, service base URLs, image limit, geographic BBOX radius, and pass-window limit are configurable there. No secrets or `.env` file are required.

## Endpoints

```bash
curl 'http://localhost:8081/api/geocoding?address=Ipatinga'
curl 'http://localhost:8081/api/imagery?address=Ipatinga&layer=MODIS_Terra_CorrectedReflectance_TrueColor&width=800&height=800&time=2026-09-05&transparent=false' --output imagery.png
curl 'http://localhost:8081/api/satellites/25544'
curl 'http://localhost:8081/api/satellites/25544/position?at=2026-09-06T18:30:00Z'
curl 'http://localhost:8081/api/satellites/25544/passes?latitude=-19.4777807&longitude=-42.5270802&hours=24&minimumElevation=10&from=2026-09-06T18:30:00Z'
curl 'http://localhost:8081/api/observations?address=Ipatinga&catalogNumber=25544'
```

`at` and `from` default to the current UTC instant. Observer altitude is optional as `altitudeMeters` and defaults to zero. Passes are geometric (elevation above the requested threshold); they do not claim optical visibility. The search is limited to 168 hours. No pass is represented by an empty array, and `nextPass` is empty in the integrated response.

Angles are degrees, orbital period is minutes, semimajor axis/mean approximate altitude/satellite altitude are kilometers, and observer altitude is meters. The approximate altitude derived from mean motion is a mean orbital metric, not instantaneous altitude. The imagery BBOX uses WMS 1.1.1 EPSG:4326 order `minLongitude,minLatitude,maxLongitude,maxLatitude`. Its current radius is in geographic degrees, so it is not the same physical distance at every latitude.

## External services and cache

- Nominatim `/search` with `format=jsonv2`, `limit=1`, and an identifiable configurable User-Agent.
- NASA GIBS WMS 1.1.1 returns PNG mosaics of Earth observations. It is not a live camera and the endpoint rejects empty/XML/non-PNG responses.
- CelesTrak GP JSON is queried by NORAD catalog number. Successful results are cached in memory with Caffeine for two hours (maximum 500 entries); failures are not cached and there is no background polling.

Orbital elements are current published model inputs, not live video or onboard telemetry. SGP4 propagation uses Orekit 13.1.7. The leap-second history needed for UTC is packaged under `orekit-data`; the application performs no silent data download. Earth orientation uses Orekit's simplified mode when precise EOP files are unavailable, which is appropriate for this MVP but limits high-precision geolocation.

## Development and limitations

CORS is restricted to Vite on localhost/127.0.0.1 ports 5173 and 5174. Automated tests use mocks or local mock HTTP handling and never call external services. External availability, imagery layer/date coverage, GP-element age, simplified Earth-orientation data, the 30-second pass scan step (crossings refined below one second), and lack of optical-visibility modeling are current limitations.

Potential next steps include packaged current EOP data for higher precision, illumination/night calculations, configurable imagery presets, API schema generation, and operational rate limiting/metrics.
