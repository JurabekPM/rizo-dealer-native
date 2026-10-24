# Agent Заявка reference — 2026-10-24

Read-only inspection of RIZO Dealer, page **Main → Applicant**:

- [Visit creation flow](https://www.figma.com/design/HWS4NGlGrmfzliWqqLYX9L/RIZO-Dealer?node-id=14797-246782)
- [Empty request frame](https://www.figma.com/design/HWS4NGlGrmfzliWqqLYX9L/RIZO-Dealer?node-id=676-32456): 375×812, background #F4F4F4; empty white container 375×362, vertical padding24 / horizontal16; original illustration220×220; 24 gap to Add.
- Request edit section node19675-207579; selected product review node14797-247229. Full-width white rounded product cards; thumbnail48; name/price then compact minus/quantity/plus, X at right. Overall total then product-count/add row. Pinned purple Завершить, confirmation Внимание with Нет/Да.
- SearchSelection frames: search with back/clear; selected-product count; name, per-unit price, availability, optional prior price; compact purple plus/count; Количество sheet with Штука suffix and Подтвердить; Продолжить returns to review.
- Saved request/edit: request date + total + saved lines; active-visit Редактировать. Do not fabricate external delivery/warehouse dates for a newly created local document.

`empty-request.svg` is the unmodified full-frame Figma export. `app/src/main/res/drawable-nodpi/request_empty.png` is the unmodified PNG export of illustration instance 382400425_11500110 2 from that frame (220×220), not a generated imitation. Existing return_makita/return_dewalt assets are shared original product photos. Other catalog entries retain a neutral box placeholder.

Android adaptation keeps native insets and the user's approved stronger AgentVisitDesign typography (name15/20 Semibold, detail13/18 Medium, screenTitle17/22), rather than copying iOS status/home-bar spacing or old thin weights. Existing DealerDesign accent/radii/spacing and softCardPress are reused. No backend, dependency or Admin navigation changes.
