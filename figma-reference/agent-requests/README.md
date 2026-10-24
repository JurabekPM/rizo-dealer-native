# Agent Заявка reference — 2026-10-24

Read-only inspection of RIZO Dealer, page **Main → Applicant**:

- [Visit creation flow](https://www.figma.com/design/HWS4NGlGrmfzliWqqLYX9L/RIZO-Dealer?node-id=14797-246782)
- [Empty request frame](https://www.figma.com/design/HWS4NGlGrmfzliWqqLYX9L/RIZO-Dealer?node-id=676-32456): 375×812, background #F4F4F4; empty white container 375×362, vertical padding24 / horizontal16; original illustration220×220; 24 gap to Add.
- Request edit section node19675-207579; selected product review node14797-247229. Full-width white rounded product cards; thumbnail48; name/price then compact minus/quantity/plus, X at right. Overall total then product-count/add row. Pinned purple Завершить, confirmation Внимание with Нет/Да.
- SearchSelection frames: search with back/clear; selected-product count; name, per-unit price, availability, optional prior price; compact purple plus/count; Количество sheet with Штука suffix and Подтвердить; Продолжить returns to review.
- Saved request/edit: request date + total + saved lines; active-visit Редактировать. Do not fabricate external delivery/warehouse dates for a newly created local document.

`empty-request.svg` is the unmodified full-frame Figma export. `app/src/main/res/drawable-nodpi/request_empty.png` is the unmodified PNG export of illustration instance 382400425_11500110 2 from that frame (220×220), not a generated imitation. Existing return_makita/return_dewalt assets are shared original product photos. The product picker now also uses the original Bosch, XGT and Ryobi images extracted from SearchSelection. The identical Makita/DeWalt originals share existing return_makita/return_dewalt resources. Other demo catalog items retain a neutral placeholder.

Android adaptation keeps native insets and the user's approved stronger AgentVisitDesign typography (name15/20 Semibold, detail13/18 Medium, screenTitle17/22), rather than copying iOS status/home-bar spacing or old thin weights. Existing DealerDesign accent/radii/spacing and softCardPress are reused. No backend, dependency or Admin navigation changes.

## Product picker correction (user requested matching the source before further redesign)

Inspected/exported SearchSelection **13744:262806**, quantity sheet **12512:284426**, selected state **12512:284209**. Export was configured with text preserved to read exact metrics; `picker-measurements.json` records SVG geometry, asset hashes and source-node IDs.

- Header:72 high after excluding the 44-point iOS status area. Back24 at inset16, gap12, search40 high/radius12, input inset12, search/clear24. X remains INSIDE the field and retains focus.
- Selected row: done_all24, gap12, label15/20, top16/bottom12, full inset16 divider1. List content inset16, vertical12. Image tile48/radius12/#F1F1F1, original photo32. Product name15/20, price15/20 with availability13/18 alongside, dot4 and gaps6.
- Normal single-line rows72 high plus divider1; wrapping titles/discount add only their own content height. Text-to-thumbnail gap12. No return-stock/source-document row.
- Add40/radius12/#F4E6FC, original add icon24. Quantity remains a badge over the plus, font11/13 weight600/tracking.06, padding5×2, radius12, white border1; source 130 badge31×17.
- Discount: red badge radius6/padding4×1, detail13/18; prior price black with coral strike1. Current example450000 / previous500000 gives10%.
- Quantity sheet: radius16, scrim50%, handle40×4; title, gap16, input48/radius12/border2, separate unit block64, gap16, Confirm48, bottom16. Product name/normal-stock helper removed; invalid quantity alone shows stock error. Confirm animates sheet closed.
- Picker typography is isolated in RequestPickerDesign: source Regular15/20, Regular13/18; badges Semibold11/13. This user-requested source match overrides the earlier stronger generic weights ONLY here. Other screens retain their approved typography. Android supplies its own status/IME/navigation insets and keyboard.
- Source images are unchanged bytes. Runtime thumbnail decode uses inSampleSize on Dispatchers.IO with density scaling disabled; original large photos are not decoded at full size on the UI thread.
- Catalog's first20 ID/index positions stay stable for restored drafts. XGT and Ryobi append at the end; visual sorting is independent. The fresh Makita demo price is450000 (previous500000); saved order snapshots stay unchanged. There are22 request fixtures; Return's20 items are unchanged.

Validation: assembleDebug and63 unit tests pass. Matching was checked against exported geometry/assets; the phone UI/FPS was not automatically verified because the user prefers personal device checks.

## Subsequent explicit user adjustment — price and title hierarchy

Available products omit the availability label and dot. Only out-of-stock products show Нет в наличии below the price, without an add action. Product title15/20 Medium (max2 lines), numeric price16/22 Semibold with tabular digits, currency/unit13/18 Medium in secondary gray; vertical gap4. Search/header/sheet styles and all quantity/discount behavior stay as before. These are intentional user changes after matching the source; do not revert them to the original SVG.
