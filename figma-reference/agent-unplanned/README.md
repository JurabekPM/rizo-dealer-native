# Agent unplanned visits

Source: https://www.figma.com/design/HWS4NGlGrmfzliWqqLYX9L/RIZO-Dealer?node-id=579-32035

Reviewed 2026-10-26 through the user's Chrome session. `Unplanned Visit.png` is the original Figma UI export (2705×1290), not a recreated mockup.

The source shows: searchable store list (name/ID/address), disabled active-store hint, store details, empty request, request cart and finished unplanned visit. The user supplied a real-app screenshot for the preceding Внеплановые sheet: Визит / Заявка / Инкассация, radio selection and Отмена / Продолжить. The prototype combines these references.

Reuse DealerDesign/AgentVisitDesign and original drawables. The approved current typography and result badge treatment take precedence over older source text weights. Existing details retain their current five operations and notes; do not rebuild other flows from this export. The collection amount form is a small local prototype extension of the selected Инкассация path; a separate exact Figma collection form was not supplied here.

Cancelling selection writes nothing. Start creates an independent unplanned visit ID and physical-store snapshot, preserving existing visits. One active visit remains enforced. No real financial transaction or backend request is made.
