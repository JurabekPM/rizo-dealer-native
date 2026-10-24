# Dealer native UI prototype — working instructions

Current behavior reviewed **2026-10-22**. Read `GEMINI.md` and `docs/PROJECT_HANDOFF_UZ.md` before changing code. Use `docs/PROJECT_MAP.md` to locate a flow. Older reference notes are measurements/history, not necessarily current functional requirements.

## Scope and collaboration

- This is a native Kotlin / Jetpack Compose UI/UX prototype with local sample data and SharedPreferences. No dealer backend or real authentication. Keep implementation small.
- Communicate in Uzbek; preserve the existing Russian app labels.
- Implement only the requested task; preserve all other roles, data, behavior and visuals. Do not perform broad redesigns, add dependencies, migrate architecture or introduce backend layers without a task requiring it.
- Read the screen, its callers and model/store before editing. Shared components must be checked in both Admin and Agent.
- MainActivity initializes the app. Each screen belongs in `screens/`; shared UI in `ui/components/`, styles in `ui/theme/`, local data in `data/`, rules in `model/`.
- Do not clear app data, uninstall, rename package/IDs/preferences/JSON keys, or reset sessions to work around a defect. Storage changes require backward compatibility/migration.
- User prefers to perform phone UI checks personally. Do not navigate the prototype for testing unless asked. Installation to the existing Wi-Fi Samsung is authorized; use install -r, inspect the current device/transport first. Read-only inspection of real app/Figma may be used when requested. Never submit real financial actions while inspecting.

## Visual system

- Match the relevant current Figma/matching approved screen unless the user explicitly changes that part. Inspect available source measurements/assets; do not claim pixel-perfect matching from a scaled screenshot.
- Current Visits/History reference: https://www.figma.com/design/HWS4NGlGrmfzliWqqLYX9L/RIZO-Dealer
- Original Admin: https://www.figma.com/design/S9mUxm1q0q8qIqaZViiMzZ/RIZO-Dealer--Copy-
- Original Agent Statistics: https://www.figma.com/design/xP7iPeNT6HO3mvf7Cd1m5S/Untitled
- Local originals/measurements/licenses: `figma-reference/`. Reuse original drawable icons; no substitute glyph icons or invented strokes.
- DealerDesign: accent #9103E4, secondary #84919A, tertiary #9AA6AC, background #F4F4F4, divider #EBEBEB. Insets16, gap8, radius12, icons24, buttons48. Preserve explicit font size/line height/tracking and native system insets.
- Use native sans-serif in DealerDesign/AgentVisitDesign. AgentDesign uses bundled Onest for Statistics/Agent store/menu; do not apply it to Admin globally.
- Latest user correction strengthened typography: AgentVisitDesign name15/20 Semibold, detail13/18 Medium, title20/24 Semibold, navigation11/13 Medium. Do not revert to Regular because an older Figma note says so.
- Keep PrototypeFontWeights system-bold isolation for this visual prototype; do not change phone settings.
- Only actionable elements get softCardPress. No blanket click handlers on static cards, no unsolicited ripple/colored hover. Preserve its lazy draw-only indication implementation.

## Navigation/state/animations

- Admin tabs: Visits, Requests, Deliveries, Collections, Menu. Agent tabs: Visits, Stores, Statistics, History, Menu; Agent enters Visits. Old Home is now Statistics.
- Requests/Deliveries share OrderListScreen and OrderCard.
- ActiveHistoryTabs and HorizontalPager use the same PagerState; indicator follows page offset, tab tap animates the pager. Common shortcut cards remain outside the pager.
- Preserve stable ID-based keys, hoisted LazyListState, query/filter/tab restoration and SharedTransition scopes.
- Visit logo/name/debt/result icons transition into their matching detail rows, not a separate icon header. Store debt list also shares logo/name/debt/ID into details.
- On closing a visit, clear only its detail subtree state (existing removeState) so reopening a card starts in the visit details, not an old Return subpage. List position must remain.
- Debt search collapses into the appbar; tapping opens it inside the appbar instead of expanding the old header. X clears text while search stays open. Tabs remain below appbar.

## Business rules to preserve

- StoreOwesDealer = red with minus; DealerOwesStore = green without minus. Use DebtDirection, not duplicated formatting. USD/UZS remain separate; return/order money uses Long UZS, USD receivables use BigDecimal.
- Store list cards may show debt; Admin Menu/Stores details still show ID under the store name.
- Agent visit begins at the actual Start tap, never midnight. One active visit; finished sessions cannot restart. No-work finish requires a reason; Other requires comment. StoreClosed is an attempted visit. Photos/comments/times/results persist locally.
- Five VisitResults must match status and recorded work. Adding Return does not finish a visit.
- Agent new Return requires an active visit. Both roles reuse VisitReturnScreen. Existing completed returns are read-only.
- Return reason is not a standalone card in the product form: products -> Возвратить -> reason sheet -> final validation/save. Dismissing sheet must not save or add a result.
- Keep 20-item catalog, stock caps, old-draft normalization, quantity sheet, product source-info sheet and transactional picker.
- Reason settings are Admin-only. System reasons can be enabled/disabled, custom ones added/renamed. Historical reasonLabel is a snapshot, not a live lookup.
- Admin Menu -> Returns -> months -> documents -> details. Reason settings live INSIDE Returns, never a separate top-level Menu row.
- Admin Menu -> Revisions uses monthly count and recorded documents from administrator stores.
- Agent History: Revisions, Visit History, Collection History, Returns, Sales History. No Request Plan shortcut. CollectionPlan enum's UI title is now История инкассации; do not restore fake monthly targets.
- Revision month cards show count equal to inner list. Inner cards show store address and saldo value. Agent hides Сальдо магазина label; Admin keeps it via showRevisionBalanceLabel=true.
- Return month/sales subtraction uses record.createdAt, not the visit's start month. Use Asia/Tashkent for timestamp grouping; date-only picker uses StatisticsDates UTC convention.

## Map and phone actions

- Maps work for Agent now, but are read-only. The earlier instruction that Agent map is visual-only is obsolete.
- StoreActionsHost gives shared read-only map and ACTION_DIAL to both roles. Only Admin Menu/Stores map allows location edits via the address pencil.
- Current renderer is MapLibre/WebView/OpenFreeMap, not Yandex SDK. No Yandex API key. Preserve map attribution/licenses.
- Fixed demo dealer point stays fixed. Edit changes the STORE only. GPS button appears only in edit mode, uses foreground permission, and never enables editing on its own.
- Unsaved GPS draft is not sent to OSRM. Existing OSRM approval covers the fixed dealer point and selected saved store coordinates, not arbitrary new services/data.
- Saving store location refreshes route, km and time, and the store list distance; stale replies must not overwrite a newer point. Cancel leaves persistence unchanged.
- Keep smooth store marker/shadow animation, draggable bottom sheet, WebView lifetime and sheet inset/route viewport handling. Do not resize/recreate WebView every drag frame.

## Validation

Baseline: 57 unit tests passed, debug build succeeded and was installed on Samsung SM_S916N on 2026-10-22. This is not a claim that every UI branch/FPS was tested.

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest --offline --no-configuration-cache
```

Use `docs/REGRESSION_CHECKLIST_UZ.md` selectively. Report build/test results accurately and any unverified UI limitations. Documentation-only edits do not require rebuilding the APK. Update handoff docs after behavior changes. Do not overwrite the baseline hash manifest to hide unexpected modifications.

## GitHub workflow — 2026-10-22

This project now uses Git. See `docs/GITHUB_WORKFLOW_UZ.md`.
The user authorized committing and pushing completed, task-scoped changes to the project's own GitHub repository after validation. Check `git remote -v`, branch, status and diff first. Do not include unrelated changes, credentials, device settings or build output. Do not force-push or erase history. Report actual push success or the remaining blocker. One writer per checkout; parallel agents use separate worktrees.

## Agent request flow — 2026-10-24

Agent Visit → Заявка uses AgentVisitRequestScreen / RequestProductPickerScreen / VisitRequestDetailScreen. Local agent_visit_requests storage belongs to AgentVisitSessions.requests. Save is active-visit-only and adds Request without finishing the visit; edits preserve documentId/createdAt. Picker Back must not mutate the cart, empty forms must not create work. Finished visits/orders are read-only. History/Sales use requestRecord snapshots and createdAt for monthly grouping. Admin orders/Return flow are unchanged. See handoff section 11 and figma-reference/agent-requests/README.md.

Picker typography: search/body retain source Regular15/20 and13/18. Latest explicit user adjustment uses productTitle Medium15/20, numeric price Semibold16/22 and muted unit13/18; hide in-stock text, and show Нет в наличии without + only for stock=0. Do not propagate those weights to other screens. Request catalog first20 IDs/indexes remain stable; displayOrder sorts the UI only. XGT/Ryobi append, never reorder persisted draft quantities. RequestQuantitySheet and sampled RequestProductThumbnail are now separate component files. Read figma-reference/agent-requests/README.md for exact metrics.
