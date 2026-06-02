# Workflow Context Summary

Last updated: 2026-06-03

## Current goal

**PromptShield MVP** — OWASP-aligned prompt vulnerability assessor for team system/assistant prompts (internal/external AI ecosystems), with Material 3 security dashboard UI and shareable deployment on Render free tier.

## Project state

- **Product name**: PromptShield (`app.name` in `application.yml`)
- **Stack**: Java 17, Spring Boot 3.3, Thymeleaf, H2 file DB, static CSS (MD3 tokens) + Chart.js CDN
- **Persistence**: H2 at `./data/safeprompt-db` (dev); prod profile uses `/tmp/data/` on Render
- **No LLM second pass** in MVP — rule-based detectors only; `llm` package remains on disk but unused
- **Security score**: `0–100%` where `100 - riskScore` (0 = vulnerable, 100 = ready to use)

### Key routes

| Route | Purpose |
|-------|---------|
| `/` | Dashboard — welcome, owl insight, KPIs, trend chart |
| `/scan` | Prompt composer + ecosystem (INTERNAL/EXTERNAL) + report |
| `/history` | Scan table (desktop) / cards (mobile) with vulnerability tags |
| `/history/{id}` | Full scan detail |
| `/policies` | Detectors + OWASP references |
| `/reports`, `/settings` | Coming soon stubs |
| `/api/v1/prompts/analyze` | JSON API (optional `ecosystem`) |
| `/api/v1/prompts/history` | JSON history with `vulnerabilityTags` |

### Important code locations

- **Web**: `WebPageController`, `WebViewSupport`, `DashboardStats`, `PromptForm`
- **Services**: `PromptSafetyService`, `InsightService`, `VulnerabilityEnricher`
- **Standards**: `VulnerabilityCatalog` (ruleId → OWASP LLM01/LLM07 tags)
- **Schema fix**: `PromptScanSchemaMigrator` (backfills `ecosystem` on startup)
- **Templates**: `templates/dashboard.html`, `scan.html`, `history.html`, `fragments/*`
- **Static**: `static/css/*.css`, `theme.js`, `dashboard.js`, `layout.js`, `table.js`
- **Deploy**: `Dockerfile`, `render.yaml`, `application-prod.yml`, `FUTURE.md`

## Completed steps (cumulative)

1. Created personal Cursor skill `compounding-context` + this project context file
2. Diagnosed H2 file-lock errors when app + tests shared DB; added `AUTO_SERVER=TRUE`, `application-test.yml` + `@ActiveProfiles("test")` for in-memory test DB
3. **Material 3 UI rebuild**: multi-page shell (sidebar + app bar), Inter font, light/dark theme, KPI cards, score ring, Chart.js trend, sortable history table
4. Chart UX: date-only x-axis labels, axis titles “Scan date” / “Security score”
5. **MVP product rebuild**:
   - OWASP-aligned `VulnerabilityCatalog` + enriched `Finding` (`vulnerabilityTag`, `standardRef`)
   - Removed LLM from service, config, UI, API responses
   - `PromptEcosystem` (INTERNAL/EXTERNAL) on scans
   - History: up to 50 scans, vulnerability tag chips, security score %
   - Dashboard: welcome banner, owl insight (`InsightService`), removed latest-scan panel; avg score KPI
   - Mobile: history cards ≤640px, responsive KPI/owl/chart
   - Deploy docs: Render + Docker + `FUTURE.md` (vector DB, news feed, Postgres)
6. **Fixed dashboard 500**: missing/null `ecosystem` on legacy H2 rows — `PromptScanSchemaMigrator`, `@PostLoad`/`@PrePersist`, null-safe `getEcosystem()`; backfilled 20 scans on restart

## Decisions made

- **Personal skill + project context file**: Skill in `~/.cursor/skills/`; state in `.cursor/context/workflow-summary.md`
- **Thymeleaf + static CSS** (no React) for MVP UI
- **OWASP LLM Top 10** as primary standards mapping (not NIST/ISO in v1)
- **H2 for v1** — no MongoDB/vector DB; documented in `FUTURE.md`
- **Render free tier** recommended for shareable MVP (no cloud credits required)
- **Ecosystem column**: JPA nullable + startup SQL backfill for existing DB rows (avoid 500 on dashboard)

## Files touched (recent sessions)

- `src/main/java/com/safeprompt/config/VulnerabilityCatalog.java`, `PromptScanSchemaMigrator.java`, `AnalyzerConfiguration.java`
- `src/main/java/com/safeprompt/service/PromptSafetyService.java`, `InsightService.java`, `VulnerabilityEnricher.java`
- `src/main/java/com/safeprompt/model/*` — `Finding`, `PromptScanResult`, `PromptScanSummary`, `PromptEcosystem`, `OwlInsight`
- `src/main/java/com/safeprompt/persistence/PromptScanEntity.java`, `PromptFindingRepository.java`
- `src/main/resources/templates/*`, `static/css/*`, `static/*.js`
- `src/main/resources/application.yml`, `application-prod.yml`
- `Dockerfile`, `render.yaml`, `FUTURE.md`, `README.md`
- Tests: `WebPageControllerTest`, `VulnerabilityCatalogTest`, updated `PromptSafetyControllerTest`

## Open items

- [ ] Deploy to Render (push GitHub → connect repo per README)
- [ ] Optional: Render PostgreSQL if scan history must survive redeploys
- [ ] Future: vector DB for scan history, prompt-injection news feed (see `FUTURE.md`)
- [ ] Update this file after each substantive work session

## Session log (recent)

### 2026-06-03 — KPI → history links, column filters & visibility

- Dashboard KPI cards link to `/history` with risk query params (`CRITICAL`, `MEDIUM,HIGH`, `LOW`, all scans for avg score)
- History page: per-column filter row, Columns panel (show/hide with localStorage), result count, clear filters
- New `history.js`; updated `table.js` for `data-column`-based sorting
- Tests: dashboard KPI hrefs, history filter markup

- Restarted app multiple times; fixed PowerShell `&&` vs `;` for Maven commands
- Implemented full Material 3 multi-page UI and PromptShield MVP rebuild per plan
- Removed LLM second pass; added OWASP vulnerability catalog, ecosystem field, history tags, owl insight
- User hit Whitelabel 500 on `/` — root cause: legacy `prompt_scans` rows without `ecosystem`
- Fixed with `PromptScanSchemaMigrator` + entity lifecycle hooks; verified HTTP 200 on dashboard
- Tests pass (`mvn test`)

### 2026-06-03 — Create compounding-context skill

- User requested skill to compound workflow context across sessions
- Authored skill + seeded this summary file
