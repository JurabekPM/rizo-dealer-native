# Rizo Market filter measurements — inspected 2026-10-28

Source provided by the user: https://www.figma.com/design/5w9dhBHvbuKDAiqSZ1Uuz0/RIZO-Market
Client → Menu -> Debt → Frame (375×944) → Tab Bar → Category → Title.
Frame URL observed: https://www.figma.com/design/5w9dhBHvbuKDAiqSZ1Uuz0/RIZO-Market?node-id=28331-109045

Read from Figma Properties, not estimated from the screenshot:

- Category component: fixed height32px, hug width70px for Статус; radius50px; left/right padding12px; inner gap4px; off fill#F5F5F5.
- Title: SF Pro Text, Semibold600,13px, line18px, tracking−0.08px; color#000000.

Android adaptation uses native sans-serif at13/18sp Semibold with the measured tracking. Optical capsule32dp/12dp sides/radius50dp/off#F5F5F5. Transparent vertical hit area keeps controls at least44dp high and lets larger accessibility fonts grow naturally. Existing dealer purple remains the active fill; local charcoal text keeps the approved softer statement style.

Latest user direction: store icon/name/address and filter row are BOTH pinned. No enterAlways header; no duplicated store context in list. Amount tiles only at top, no За весь период caption. Each operation chip toggles independently; none selected means all. Date chip clears its active date range on a second tap.
