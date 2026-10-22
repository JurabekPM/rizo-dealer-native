# Agent Home design reference

Source: https://www.figma.com/design/xP7iPeNT6HO3mvf7Cd1m5S/Untitled?node-id=21-115
Downloaded through the Figma SVG export with text preserved. Baseline: light Super-courier, 360 x 640; native Android insets replace the mock status bar.

- Onest, bundled static instances at 400/500/600/700, generated from the original variable font with FontTools. Separate resources prevent Android sharing variation state between weights and also support API 24+. Official source: https://github.com/google/fonts/tree/main/ofl/onest ; license: Onest-OFL.txt.
- Surface #F4F4F4, cards/header/nav white, text #211F26, secondary #84919A, purple #9103E4, pale purple #F4E6FC, negative #F76659, divider #EBEBEB.
- Header 56; main inset 16; vertical padding 12; card gap 8; radius 12; metric padding 12; hero padding 16.
- Hero label 15 normal, value 36 bold with tracking -0.72, percentage 16 semibold, badge 12 semibold. Metric label 13 normal, value 20 bold, supporting text 12 normal. Bottom labels 11 medium. AgentDesign keeps explicit Android line heights and disables extra font padding.
- Exported card bounds: hero 328 x 173.59; first grid row cards 160 x 120; second row 160 x 136; third row approximately 160 x 142. Equal-height grid rows accommodate wrapped supporting text.
- Original icons are exported as strokes: 22 x 22 at 1.83333 stroke, chevrons 18/20; home navigation is 61 high including its 1-point divider. Icon masks and source origins are in icon-origins.json.
- Values under the clipped lower section use the same component variants (revision counter, active/total stores and store-debt row) as the supplied board. This stage implements Home only; the other agent destinations remain visual.
