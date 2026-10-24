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

- Boshlanmagan vizitda Заявка → Начать визит; boshqa vizit faol bo‘lsa boshlamasin. Yakunlangan vizit qayta ochilmasin.
- Faol vizit → Заявка → Добавить → nom/barcode qidirish → qty sheet → Продолжить. Picker Back cartni o‘zgartirmasin; X qidiruv matnini tozalasin.
- 0/stockdan ko‘p/noto‘g‘ri miqdor tekshirilsin, stock 0 mahsulot tanlanmasin. Minus0 yoki X mahsulotni olib tashlasin. Total = price×qty jami.
- Завершить → Нет/dismiss hujjat/result yozmasin. Да request yozsin va vizit davom etsin. Qayta kirishda o‘z nom/narx/qty/total chiqsin; faol vizitda Редактировать ishlasin.
- App qayta ochilganda request saqlansin. Vizit yakunlangach request read-only, История/История продаж ayni mahsulot/summani ko‘rsatsin; oy record.createdAt bo‘yicha.
- Returnlar/sabab sheetlari, Admin Заказы, shared transition va listga Back scrolli saqlansin.

Avtomatik holat: assembleDebug va 62 unit test muvaffaqiyatli. Yuqoridagi UI bandlari foydalanuvchining qurilmadagi tekshiruvi uchun; avtomatik bajarilgan deb belgilanmagan.

Figma picker tuzatishi: klaviatura bilan header72/search40, X maydon ichida; selected done_all va count;48/32 rasm, plus40 va17 baland badge; narx soni aniqroq Semibold, valyuta/birlik kulrang; mavjud tovarlarda В наличии yo‘q, tugaganda Нет в наличии va + yo‘q;10% belgi va eski narx strike saqlangan. Miqdor sheetida oddiy holatda nom/qoldiq satrlari yo‘q, suffix ajratilgan; confirm yumshoq yopilsin. Stock cap va Back draft qoidasi saqlansin. 63 unit test o‘tdi; bular qurilmadagi vizual tekshiruv o‘rnini bosmaydi.

- Pickerda mavjud tovarlar avval, tugaganlar oxirida. «Выбрано» ↔ «Все товары» qidiruv/draftni yo‘qotmasin, har rejim scrolli saqlansin; qty0 tanlanganlardan chiqsin.
- Tanlangan rasmida nozik chegara; Продолжить ustida pozitsiyalar soni va to‘liq jami. Qidiruvda tovar yashirilsa ham jami o‘zgarmasin; Back tashqi cartga qo‘llamasin.

- Picker + darhol1 dona qo‘shsin, sheet ochilmasin; narx yonidagi108×32dp −/son/+ ixcham tursin, + joyidan ko‘chmasin. Son ko‘payishda yuqoriga, kamayishda pastga almashsin, tez taplar va4 xonali miqdor klip ichida to‘g‘ri ko‘rinsin. Faqat sonni bosish sheet ochsin. Tez taplar hisobdan tushmasin, stockdan oshmasin, minus0 chiqarib tashlasin.
- Qidiruv/selected filter/jami/Back qoidalari saqlansin; bulk sheet cancel miqdorni o‘zgartirmasin, tasdiq yangilasin. Tanlashdan keyingi review oynasi o‘zgarmasin.
