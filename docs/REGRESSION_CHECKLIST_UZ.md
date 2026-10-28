# O‘zgarishdan keyingi regressiya tekshiruvi

2026-10-22 baseline: build OK, 57 unit test, 0 failures. Har kichik vazifada hamma bandni qayta bajarish shart emas: **ta’sirlangan oqim + umumiy komponent ishlatilgan ikkinchi rol** tekshiriladi. Build/test o‘tishi telefondagi ko‘rinish va silliqlikni isbotlamaydi.

## Har vazifa

- [ ] Foydalanuvchi so‘ramagan boshqa ekran yoki ma’lumot o‘zgarmadi.
- [ ] Mavjud IDs, package, SharedPreferences nomlari va JSON eski formatlari saqlandi.
- [ ] Faqat callbackli card/button animatsiyalanadi; statik card clickable emas.
- [ ] Uzun nom/summa/manzil tor ekranda bir-birini siqmadi; ellipsis kerakli joyda.
- [ ] Back va appbar back bir xil to‘g‘ri darajaga qaytadi; modal avval yopiladi.
- [ ] Query, filter, pager va ro‘yxat scrolli kerakli joyda saqlandi.
- [ ] `assembleDebug` o‘tdi; logika ta’sirlangan bo‘lsa unit testlar o‘tdi.
- [ ] O‘rnatishda `install -r`; uninstall yoki `pm clear` yo‘q.
- [ ] Yakunda bajarilgan ish va tekshirilmagan cheklovlar aniq aytildi.

## Rollar va navigatsiya

- [ ] Kirish rol so‘raydi. Adminning 5 tabi va Agentning 5 tabi o‘z tartibida.
- [ ] Agent default Visits; eski Home Statistika bo‘lib qolgan.
- [ ] Agentning read-only mapida qalamcha yo‘q; Admin editable xaritasi saqlangan.
- [ ] Bottom bar details ochilganda yashirinadi va qaytganda tiklanadi.

## Vizit / result / shared transition

- [ ] Natijasiz yangi vizit → Начать визит → real vaqt ishlay boshlaydi.
- [ ] Boshqa faol vizit bor paytda yangi vizit rad etiladi.
- [ ] Yakunlangan demo card yana boshlanmaydi; oldingi vaqt/izoh/fotolari saqlanadi.
- [ ] Boshlanganida button Завершитьga almashadi; natijasiz yakunlash sabab so‘raydi.
- [ ] Other uchun comment shart; comment maydoni doim ko‘rinadi.
- [ ] StoreClosed attempt holati; boshqa sabablarni avtomatik Cancelledga aylantirmaslik.
- [ ] Return qo‘shilganda session faol qoladi, faqat Return resulti qo‘shiladi.
- [ ] Logo/nom/qarz/result ikonlari o‘z satriga smooth o‘tadi va backda o‘z joyiga qaytadi.
- [ ] Orqaga qaytgan list avvalgi scroll joyida; birinchi scroll qotmaydi.
- [ ] Vizitga yangi card tap → asosiy details; eski Return ichki oynasi qayta ochilmaydi.

## Qaytarish — ikkala rol

- [ ] Admin uchun forma ishlaydi; Agent uchun avval faol vizit kerak.
- [ ] 20 mahsulotli picker qidiradi; mavjud qoldiqdan ortiq son qabul qilmaydi.
- [ ] Picker Back eski cartni o‘zgartirmaydi; Confirm tanlovni qo‘llaydi.
- [ ] − / count / +, X, qoldiq va ⓘ ishlaydi; 0 mahsulotdan chiqaradi.
- [ ] ⓘ asl order raqami/sanasi/asl miqdorni ko‘rsatadi; tanlangan quantityga aralashmaydi.
- [ ] Eski qisqa quantities state bilan yangi katalog crash qilmaydi.
- [ ] Jami summa yuqorida, pastda Возвратить. Alohida sabab kartasi yo‘q.
- [ ] Возвратить → sabab sheeti. Dismiss → **hech narsa saqlanmaydi**.
- [ ] Standart sabab + optional comment; Other + required comment.
- [ ] Disabled sabab yangi hujjatga tanlanmaydi; eski reasonLabel snapshot saqlanadi.
- [ ] Agentda sabab sozlamalari yo‘q; Admin system/customni yoqib-o‘chira oladi.
- [ ] Yakuniy confirmdan keyin record/items/reason/comment va Agent result mos.
- [ ] Bir visitga bitta record cheklovi buzilib, mavjud hujjat bilmay ustidan yozilmadi.

## Admin Menu

- [ ] Ревизии → oy → revision list → details.
- [ ] Возвраты → oy → shu oy hujjatlari → har hujjatning o‘z detailsi.
- [ ] Причины возврата faqat **Возвраты ichida**; Menu alohida band yo‘q.
- [ ] Oy summasi aynan shu oy record.createdAt bo‘yicha jamlangan.
- [ ] Saved record demo bilan takrorlanmaydi; saved Agent return ham topiladi.
- [ ] Returns Create → store tanlash → umumiy forma; bekor qilish hujjat yaratmaydi.
- [ ] Yangi lokal returnga bo‘lmagan qabul/yakun hodisasi qo‘shilmagan.
- [ ] Details: store ID, map/call, timeline, RN raqam, quantity, employee, summa, mahsulot, sabab.

## Agent History

- [ ] Faqat finished sessionlar historyda; active session chiqmaydi.
- [ ] «История инкассации» bor; «План заявки» yo‘q; Sales qolgan.
- [ ] Revision oy kartasidagi son ichida ochilgan documentlar soniga teng.
- [ ] Revision store nomi tagida manzil, agentlar ismi emas.
- [ ] Agent revision cardida saldo **summa bor, label yo‘q**; Admin labeli yo‘qolmagan.
- [ ] History visit detailsi har entryning o‘z vaqt/rasm/comment/resultsini ko‘rsatadi.
- [ ] Return oy hisobi record.createdAt; vizit boshqa oyda boshlangan bo‘lsa ham to‘g‘ri.
- [ ] Sales umumiy sum/order/return manbasi bir-biriga mos, valyutalar aralashmagan.

## Search / pager / qarz

- [ ] Active/History oq indikator pager offsetiga ergashadi; tap va swipe bir xil state.
- [ ] Tab pressda rangli ripple/hover qaytmadi.
- [ ] Umumiy shortcutlar pager bilan almashib ketmaydi.
- [ ] Do‘kon dilerga qarz → qizil minus; diler do‘konga qarz → yashil minussiz.
- [ ] Agent receivables qarzsiz store uchun 0 USD; negative dealer balance receivablega qo‘shilmaydi.
- [ ] Debt list collapse: tabs appbar ostida; search toolbar ichida title o‘rnida ochiladi.
- [ ] Search X matnni tozalaydi va qidiruv ochiq qoladi.
- [ ] Statistika Day/Week/Month/Range hamda custom date bir-biriga mos.

## Xarita

- [ ] Har do‘kon o‘z nom/manzil/nuqtasi/km/vaqtiga ega; diler fixed nuqta.
- [ ] Faqat Admin Menu/Stores qalamchasi tahrirlaydi; Agent va umumiy map read-only.
- [ ] GPS button faqat editda, ishga tushishi editni o‘zi yoqmaydi.
- [ ] GPS nuqta draft; savegacha OSRMga yuborilmaydi.
- [ ] Manzil tanlangan nuqtaga mos; plus-code/oblast/country ortiqcha yozuvlari tozalangan.
- [ ] Save → yangi route/km/time + Stores km; eski kechikkan response qayta yozmaydi.
- [ ] Cancel persist qilmaydi; fixed diler nuqtasiga zarar yo‘q.
- [ ] Shop marker va soyasi qisilmay smooth animatsiyalanadi.
- [ ] Bottom sheet drag smooth, map har frame qayta yaratilmaydi, attribution yo‘qolmagan.
- [ ] Internet/GPS rad etilganda crash emas, tushunarli mavjud fallback.
- [ ] Pozvonit ACTION_DIAL, avtomatik real qo‘ng‘iroq emas.

## Baseline solishtirish

```powershell
.\scripts\Compare-HandoffBaseline.ps1
```

Bu 2026-10-22 hujjat topshirish paytidagi source SHA-256 holati bilan solishtiradi. Keyinchalik asosli yangi o‘zgarishlar chiqishi normal. Har bir farq xato degani emas, lekin vazifaga aloqasiz farqlar tekshiriladi. Baseline manifestni testni “yashil” qilish uchun avtomatik yangilamang.

## Agent Заявка — yangi oqim (2026-10-24)

- Boshlanmagan vizitda Заявка → Начать визит; boshqa do‘kon viziti faol bo‘lsa ham boshlansin. Yakunlangan vizit qayta ochilmasin.
- Faol vizit → Заявка → Добавить → nom/barcode qidirish → qty sheet → Продолжить. Picker Back cartni o‘zgartirmasin; X qidiruv matnini tozalasin.
- 0/stockdan ko‘p/noto‘g‘ri miqdor tekshirilsin, stock 0 mahsulot tanlanmasin. Minus0 yoki X mahsulotni olib tashlasin. Total = price×qty jami.
- Завершить → Нет/dismiss hujjat/result yozmasin. Да request yozsin va vizit davom etsin. Qayta kirishda o‘z nom/narx/qty/total chiqsin; faol vizitda Редактировать ishlasin.
- App qayta ochilganda request saqlansin. Vizit yakunlangach request read-only, История/История продаж ayni mahsulot/summani ko‘rsatsin; oy record.createdAt bo‘yicha.
- Returnlar/sabab sheetlari, Admin Заказы, shared transition va listga Back scrolli saqlansin.

Avtomatik holat: assembleDebug va 62 unit test muvaffaqiyatli. Yuqoridagi UI bandlari foydalanuvchining qurilmadagi tekshiruvi uchun; avtomatik bajarilgan deb belgilanmagan.

Figma picker tuzatishi: klaviatura bilan header72/search40, X maydon ichida; selected done_all va count;48/32 rasm, plus40 va17 baland badge; narx soni aniqroq Semibold, valyuta/birlik kulrang; mavjud tovarlarda В наличии yo‘q, tugaganda Нет в наличии va + yo‘q;10% belgi va eski narx strike saqlangan. Miqdor sheetida oddiy holatda nom/qoldiq satrlari yo‘q, suffix ajratilgan; confirm yumshoq yopilsin. Stock cap va Back draft qoidasi saqlansin. 63 unit test o‘tdi; bular qurilmadagi vizual tekshiruv o‘rnini bosmaydi.

- Pickerda mavjud tovarlar avval, tugaganlar oxirida. Yuqori done_all/Выбрано/Показать bo‘lmasin; hisob pastda0 bo‘lsa ham bir xil balandlikda qolsin. Продолжить → review → Добавить tanlovni saqlasin.
- Tanlangan rasmida nozik chegara; Продолжить ustida pozitsiyalar soni va to‘liq jami. Qidiruvda tovar yashirilsa ham jami o‘zgarmasin; Back tashqi cartga qo‘llamasin.

- Pickerda bo‘sh mavjud cardning istalgan joyi yoki40dp + darhol1 dona qo‘shsin, double increment bo‘lmasin. Nom to‘liq joyda, narx/birlik bir xil qora; pastki full-width qatorda120×40 counter. Son markazda320ms up/down, fon/prefix300ms ochilib-yopilsin. + joyi va list/card balandligi tanlovda o‘zgarmasin; tez taps/stock cap/zero va bulk sheet tekshirilsin.
- Qidiruv/jami/Back qoidalari saqlansin; bulk sheet cancel miqdorni o‘zgartirmasin, tasdiq yangilasin. Tanlashdan keyingi review oynasi o‘zgarmasin.

## 2026-10-26: umumiy qaytarish picker

- Agent faol Visit→Return, Admin Visit→Return va Admin Menu→Returns→Create→store orqali yangi umumiy tanlash ochilsin.
- Card/+1, markazdagi son/bulk sheet, animatsiya, qidiruv X, footer count/total ishlasin. Returndagi qoldiq/date/original qty saqlansin; Requestning kattaroq stocki Returnga o‘tmasin, bulk2 digit/0..available.
- Back va sheet cancel tashqi draftni o‘zgartirmasin; Подтвердить0 ham oldingi tanlovni tozalasin. Tanlash save qilmasin; Возвратитьdan keyingina reason sheet va final save. Completed return read-only, Agent active-visit talabi va Admin sabab boshqaruvi saqlansin.

## Vizit va Внеплановые — 2026-10-26

- [ ] Admin/Agent visit details: besh icon bir xil badge, faqat mavjud results shared; ortiqcha operation arrows yo‘q. Menu arrows saqlangan.
- [ ] Agent Visitsni pastga scroll → Stores/Statistics/History → Visits: o‘sha query/tartib/viewport. Detail→Back, tez qayta ochish va map roundtrip shared/scrollni buzmasin.
- [ ] Внеплановые: Continue tanlovsiz o‘chiq. Har uch tur→store list; name/ID search. Faol store o‘chiq va hintli. Cancel/Back yozuv yaratmasin.
- [ ] Boshqa do‘kon faol bo‘lsa Start ruxsat etilsin. Har yangi vizit actual Start vaqti bilan boshlansin; eski vizitlar/suratlar/izoh/hujjatlar saqlansin.
- [ ] Request/Collection tanlovi tegishli formaga; Startdan keyingina saqlash. Positive UZS collection result qo‘shadi, session active qoladi. No-work finish reason sheet o‘z holida.
- [ ] Finished unplanned Historyda, unplanned filter va physical store filterda ko‘rinsin; aynan o‘z summasi/surati/izohi; qayta start yo‘q. Takroriy do‘kon orderlari salesda bir do‘konga jami, order/return createdAt oyi bo‘yicha.
- [ ] Restartda unplanned metadata/session/record saqlansin. Map/call aynan o‘sha physical storega, Agent map readonly.

2026-10-26 final validation: assembleDebug and 76 unit tests passed (0 failures, 0 errors); installed to Samsung SM_S916N with install -r. Device UI/FPS checks remain for the user.

## Detailsdan Back scroll xatosi — tasdiqlangan (2026-10-26)

- [x] Oldingi APKda Android regression: Forward scroll stalled (11,246) -> (11,246).
- [x] Yangi APKda synthetic shared list3×detail/work/Back, oldingi viewport va yetarli forward/backward scroll: PASS.
- [x] Yangi APKda hoisted state bilan Visitsdan chiqish/qaytish va scroll: PASS.
- [x] Samsungdagi haqiqiy Agent card → mavjud Заявка → ikki Back, Phone Link scroll down/up.
- [x] assembleDebug + assembleDebugAndroidTest +76 unit tests;2 Android regressions.

SharedTransitionLayoutni idle paytida olib tashlash regressiyani qaytaradi.600ms timer faqat preparedIdni tozalashi kerak. Barcha UI/FPS holatlari qamralgan deb hisoblamang; vizit ma’lumotlari test uchun o‘zgartirilmasin.

## Kamera va elapsed HH:mm:ss — 2026-10-27

- [x]79 unit tests; elapsed0,59sec,1min,1h,25h,negative,midnight and frozen finish.
- [x] Samsungda3 storage/contract test: temporary URI grants/non-exported provider, test JPEG restore/read+thumbnail, cancelled/empty capture cleanup.
- [ ] Faol visit → Добавить фото → bevosita kamera; kamera confirmdan keyin o‘sha visitda preview, qayta kirish/app restartda saqlanishi.
- [ ] Kamera Back/cancel hech narsa qo‘shmasin; unavailable camera toast va qayta urinish.
- [ ] Galereya tanlovi ochilmasin, eski saqlangan URIlar o‘qilsin; kamera6-limit, completed/historyda add/remove yo‘q.
- [ ] Kamera paytida Activity/process recreationdan keyin pending URI o‘sha visitga qaytsin; actual timer kamera vaqtini ham hisoblasin, doim HH:mm:ss.

Bu yangi camera UI sinovlari hali avtomatik bajarilmagan; real kamera shutterini user ishlatadi. Shared transition/scroll host o‘z holida qoladi.

## Parallel vizit va camera-only — 2026-10-27

- [x]82 unit tests; actual lifecycle/status labels va parallel start/no-restart.
- [x] Samsungda2 isolated parallel-session/persistence +3 capture-storage/contract testi.
- [ ] Agentda ikki turli do‘konni boshlash: ikkisi Активный, har birining timer/izoh/fotosi alohida. Birini tugatish boshqasiga ta’sir qilmasin; historyga faqat finished yozuv.
- [ ] Boshlanmaganlar Новый, Не посещено yo‘q; boshlangani Активный, finished/attempt statuslari o‘z holida.
- [ ] Unplanned picker barcha active physical storelarni bloklasin; boshqa do‘konlar ishlasin. Finished ID qayta boshlanmasin.
- [ ] Добавить фото bevosita kamera, galereya/source sheet yo‘q; eski foto ko‘rinsin, cancel/6-limit saqlansin.


## Admin diler tahriri

- Admin → Menu top pencil opens profile; bottom bar hidden, Back restores Menu scroll. Agent unchanged.
- Edit/save/reopen retains company/options/address/map point/images/weekdays/price toggle; Back without Save does not change stored profile. Rotation preserves draft.
- INN9/PINFL14 digits, blank optional; missing name/address and equal working hours rejected; overnight allowed. Apply-to-all keeps closed days closed.
- Logo/photo/UzQR choose/replace ≤5MB, gallery cancellation leaves prior image. Visit Add photo stays camera-only.
- Map select applies to draft; map Back cancels; final form Back cancels all. No store location, route, debt, visit or Agent state changes.
