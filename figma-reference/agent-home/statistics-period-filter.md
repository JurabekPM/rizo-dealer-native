# Statistics date filter

New prototype control designed to match the existing exported Agent home reference.
Reviewed figma-frame.svg, README.md, Onest weights, AgentDesign and original schedule vector.
Reuse white/F4F4F4 surfaces, purple9103E4, inset16, radius12, gap8, explicit Onest metrics.
Place the 44-point segmented control directly under the 56-point app bar. The 48-point
date/calendar row below opens a day picker or date-range picker. Keep both pinned above
the existing metric list. Calendar colors and typography are scoped to this dialog.
This is a new design; no claim of a pre-existing exact Figma date-filter component.

Date-only UTC values match Android pickers; today is resolved in Asia/Tashkent.
Weeks run Monday–Sunday; months honor leap years. Inclusive custom intervals retain both endpoints.
Local dated demo records change all activity metrics; future days add only plans.
Store-debt balance remains today's balance, labeled На сегодня, with its existing details link.
