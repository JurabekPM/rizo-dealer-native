# Dealer app Native: loyiha topshirish hujjati

**Holat: 2026-10-22.** Bu ilova Rizo Dealer uchun native Android UI/UX prototipidir. Asosiy maqsad: mavjud mahsulotga qo‘shiladigan ekran va ishlashlarni telefonda ko‘rib sinash. Dealer serveriga ulanish, haqiqiy autentifikatsiya yoki ishlab chiqarish hisobi yo‘q. Namunaviy ma’lumotlar va qurilmaning lokal saqlashidan foydalaniladi.

Bu hujjat mavjud funksiyalarni himoya qilish va keyingi vazifaga kerakli faylni tez topish uchun yozilgan. Ishlamaydigan vizual tugmani avtomatik “tayyor funksiya” deb hisoblamang; chaqiruvchini tekshiring.

## 1. Muhit va kirish nuqtalari

- Loyiha: `C:\Users\turgu\AndroidStudioProjects\DealerappNative`.
- Package/applicationId: `com.example.dealerappnative`.
- Kod ildizi: `app/src/main/java/com/example/dealerappnative/`.
- Jetpack Compose, Material3, Kotlin Compose plugin 2.2.10, Compose BOM 2026.02.01, AGP 9.4.1.
- minSdk 24, target/compile SDK 37, Java source/target 11. Build uchun Android Studio bundled JBR ishlatiladi.
- `gradle/libs.versions.toml`, `app/build.gradle.kts` — haqiqiy versiyalar. Ularni vazifasiz yangilamang.
- `MainActivity` → `DealerEntry` → rol tanlash → Admin `DealerApp` yoki Agent `AgentScreen`.
- `previewTab` intent parametri faqat Admin boshlang‘ich tabini belgilaydi; rol tanlashni chetlab o‘tmaydi.
- `VisitReturnsProvider` rol shoxlanishidan tashqarida turadi; qaytarishlar va sabab sozlamalari umumiy.
- `StoreActionsHost` xarita/telefon amallarini composition orqali beradi. `LocalStoreActions`ni chetlab o‘tib navbatdagi soxta map tugmasini qilmang.

## 2. Ekranlar va foydalanuvchi yo‘llari

### Administrator

Pastki navigatsiya tartibi `navigation/DealerDestination.kt`da:

| Ekran | Asosiy fayllar / xulq |
|---|---|
| Визиты | `AdminVisitsFlowScreen`, `VisitsScreen`, `VisitDetailScreen`; ro‘yxat/details shared transition, qaytishda scroll saqlanadi |
| Заявки | `RequestsScreen`; tablar, qidiruv, reviziya/oylik reja shortcutlari, order kartalari |
| Доставки | `DeliveriesScreen`; Заявкиdan keyin; Active/History, alohida «Новый» tabi yo‘q |
| Инкассация | `CollectionsScreen`; USD qarz, status, tur, manzil va undirilgan summa; kamida bir nechta demo kartalar |
| Меню | `MenuScreen`; magazinlar, reviziyalar va qaytarishlar uchun ishlaydigan yo‘llar |

Заявки va Доставки `OrderListScreen`/`OrderCard`ni ulashadi. Ularning dizaynini ajratib yubormang. Заявки ichida «Маршруты» yo‘q, u Visitda qolgan. Umumiy shortcutlar pager tashqarisida; faqat pastki ro‘yxat almashadi.

Menu → Магазин: `StoresScreen` → `StoreDetailScreen`. Ro‘yxatda qarz va km, detailsda esa do‘kon nomi tagida **ID** turadi. Xarita shu yo‘ldan ochilganda tahrirlash mumkin.

Menu → Ревизии: `AdminRevisionsScreen` → `MonthlyOperationsScreen` → oy → hujjat → `AgentHistoryOperationScreen`. Admin uchun shu umumiy UI ishlatilgani uning Agent ma’lumotlarini o‘qishi degani emas: `adminRevisionHistoryEntries()` Admin do‘konlari va mavjud namunaviy sanalaridan yig‘adi.

Menu → Возвраты: `AdminReturnsScreen` → oy → qaytarishlar → `AdminReturnDetailScreen`. Oy summasi aynan ichidagi hujjatlar jami. Yuqoridagi «Причины возврата» kartasi `ReturnReasonSettingsScreen`ni ochadi; bu endi Menu ichida alohida band emas. «Создать» → `AdminReturnCreateScreen`da do‘kon tanlash → umumiy `VisitReturnScreen`.

Admin return details tartibi: do‘kon nomi/ID va map/call → timeline → hujjat raqami/tovarlar/dostavshik/jami → mahsulotlar → sabab/izoh. Demo hujjatlarda tasdiqlash/yakunlash vaqtlari bor. Yangi lokal hujjatda bu tashqi tasdiqlar qayd etilmagan: faqat rasmiylashtirish vaqtini ko‘rsatadi. Real qayd bo‘lmasa yangi “tasdiqlandi” hodisasini yasamang.

### Agent

Tartib `navigation/AgentDestination.kt`da: **Визиты, Магазины, Статистика, История, Меню**. Default Visits. Oldingi Home fayli endi `AgentStatisticsScreen`; eski hujjatlardagi AgentHomeScreen joriy fayl emas.

- Visits: `AgentVisitsScreen` → `AgentVisitDetailScreen`. Admin bilan umumiy `VisitStoreCard`, natija ikonlari va transitions. Agent o‘zining `AgentVisitSessions`ini ishlatadi.
- Магазины: `AgentStoresScreen` mavjud `StoresScreen`/lokal manzillarni ulashadi; `AgentStoreDetailScreen` o‘z detailsini chizadi. Undagi eski «Начать визит» vizual tugmasini ishlaydigan Visit session oqimi bilan adashtirmang.
- Статистика: `AgentStatisticsScreen`, kun/hafta/oy/istalgan oraliq filtri. Namuna ko‘rsatkichlar; hisobot serveri emas.
- Statistika qarz kartasi → `AgentStoreDebtsScreen`: barcha/qarzdor/qarzsiz, qidiruv, ko‘p do‘konlar, animatsiyali yig‘iluvchi header. Yuqoriga scroll: katta summa/search yig‘iladi, tablar appbar tagida qoladi. Search appbarda icon bo‘ladi; bosilganda **appbar ichida** title o‘rniga ochiladi. X faqat matnni tozalaydi, qidiruvni yopmaydi. Qaytishda filter/query/scroll saqlanadi.
- История: `AgentHistoryScreen`, faqat yakunlangan sessionlar. Alohida visit detailsi o‘z vaqti, natijasi, izohi, rasmlari bilan ochiladi.
- Menyu: profil/sinxronizatsiya/jadval/magazinlar/sozlamalar/bildirishnoma/about/exit. Ba’zi satrlar hanuz vizual — ularni bajargan deb yozmang.

Agent История yuqori bo‘limlari: **Ревизия, История визитов, История инкассации, Возвраты, История продаж**. «План заявки» foydalanuvchi talabi bilan olib tashlangan. `HistorySection.RequestPlan` eski saved holat/testlar uchun data enumda qolgan; dispatcher uni Salesga yo‘naltiradi. Uni UIga qaytarmang. `CollectionPlan` enum nomi ham compatibility uchun saqlangan, lekin hozir UI matni «История инкассации» va oqim haqiqiy qayd etilgan collection hujjatlariga asoslanadi.

Reviziya: oy kartasida **summa emas, son**. Oy ichidagi ro‘yxat shu son bilan bir manbadan (`historyMonthlyDocuments`) olinadi. Store kartasida nom ostida **manzil**, boshqa agentlar ismi yoki ID emas. Qarz summasi qoladi, Agentda «Сальдо магазина» matni **olib tashlangan**. Admin shu umumiy komponentni `showRevisionBalanceLabel = true` bilan chaqiradi. Detailsdagi saldo yozuvi bundan alohida, o‘chirilmagan.

Agent qaytarish arxivi `AgentHistoryArchiveScreen`: oy → return hujjatlar. Sotuvlar `AgentSalesHistoryScreen`/`AgentHistorySales`: oy → do‘kon → hujjatlar; qaytarishlar sof sotuv hisobiga ta’sir qiladi. Bu hisoblarni UIda alohida soxta son bilan almashtirmang.

## 3. Vizit logikasi — buzilmasligi kerak

`model/AgentVisitSession.kt` va `data/AgentVisitSessions.kt`:

1. Yangi session faqat «Начать визит» bosilganda `System.currentTimeMillis()` bilan boshlanadi. Sana 00:00iga bog‘lamang, tunda avtomatik reset yo‘q.
2. Bir paytda turli do‘konlarda bir nechta faol Agent viziti mumkin. Bittasining boshlanishi/yakunlanishi boshqasiga ta’sir qilmaydi. Mavjud/yakunlangan sessionni qayta boshlash yo‘q.
3. Button boshlanganidan keyin «Завершить визит»; izoh va rasm joylari bor. Rasmlar native photo picker/local URI, eng ko‘pi 6 ta.
4. Ish/natija bo‘lmasa yakunlash sababi kerak. Other bo‘lsa bo‘sh bo‘lmagan izoh kerak. «Магазин закрыт» yakunlanganda attempted/cancelled ko‘rinishi hosil qiladi.
5. Natijali sample kartalar allaqachon yakunlangan sessionlar bilan ochiladi. Ularni “yangi vizit”ga aylantirmang. Sample vaqt/rasmlar `CompletedAgentVisitSamples`da.
6. `VisitResult`ning besh turi: Delivery, Request, Revision, Collection, Return. Status va ikonlar sessiondagi haqiqiy resultsga mos bo‘lsin. Ish qilinmagan turga natija ikonini qo‘shmang.
7. Return qo‘shish sessionga Returnni qo‘shadi, **vizitni yakunlamaydi**.

`AgentVisitSessions` SharedPreferences: `agent_visit_sessions`. `startedAt`, `finishedAt`, `comment`, `photos`, `results`, `reason`; demo seed flag ham bor. Ularni nomini almashtirish yoki data clear qilib tashlash mumkin emas.

## 4. Qaytarish: umumiy forma va sabablar

Fayllar: `VisitReturnScreen`, `ReturnProductPickerScreen`, `ReturnProductComponents`, `ReturnQuantitySheet`, `ReturnProductInfoSheet`, `ReturnReasonSheet`, `ReturnReasonSettingsScreen`.

- Admin va Agent **bitta** umumiy return formasini ishlatadi. Agent yangi returnni faqat faol session ichida qiladi; yakunlangan vizitda yangi return formasi ochilmaydi. Returni bo‘lsa o‘qish rejimidagi hujjat ochiladi; bo‘lmasa “Возвраты не оформлялись”.
- Katalog `ReturnCatalog.kt`: 20 ta demo mahsulot. Birinchi 4 ta pozitsiya va narxlar avvalgi holatga mos saqlangan.
- `normalizeReturnQuantities`: eski 4 elementli draftni yangi katalogga kengaytiradi, miqdorni mavjud qoldiq oralig‘ida ushlab turadi. Indeksga ko‘r-ko‘rona kirish crash keltirishi mumkin.
- Store header doim ko‘rinadi; ID/map/call. Jami summa yuqorida, pastda «Возвратить». Tanlangan mahsulotlarda X, qoldiq, bitta kulrang minus/count/plus bloki, ⓘ.
- ⓘ: joriy qoldiq, manba buyurtma raqami/sanasi va asl miqdor. Bu ma’lumotni tanlangan return miqdori bilan almashtirmang. Miqdor bosilsa raqam kiritish sheeti.
- Pickerda tanlash draft: tasdiqlashgacha tashqi cart o‘zgarmaydi. Back bekor qiladi. Minus 0ga tushsa mahsulot cartdan chiqadi.
- `Возвратить` tovar borligida ochiq. Bosilganda **sabab sheeti** ochiladi. Forma ichida alohida sabab kartasi yo‘q.
- Sheet dismiss/Back = return yozilmaydi va Agent result qo‘shilmaydi. Yakuniy tasdiq: sabab validatsiyasi → sessionga addReturn (Agent) → `VisitReturnStore.save` → forma yopiladi.
- Standart/custom sabab + ixtiyoriy qo‘shimcha izoh; «Другое» + majburiy izoh. Izoh maydoni doim ko‘rinadi. Standart sababga qo‘shimcha izoh yozish uni majburan Otherga almashtirmaydi.
- `canManageReasons = true` faqat Admin yo‘llarida. Agent standartlarni qo‘shish/nomlash/o‘chirish UIini ko‘rmaydi.
- 5 ta system sababi, Admin custom sabablari, enable/disable. System sabablarini fizik o‘chirish/nomlash yo‘q; custom nomini o‘zgartirish bor. 2–100 belgili, takrorlanmaydigan nom, «Другое» rezerv.
- Oldingi hujjatning `reasonLabel`i snapshot: keyin Admin nomni almashtirsa yoki sababni o‘chirsa eski hujjat o‘zgarmaydi.

Saqlash: `visit_returns` — JSON record har bir **visit/store ID** kalitida: documentId, createdAt, items(name,price,quantity), comment, reasonId, reasonLabel. `return_reason_settings` → `reasons` JSON ro‘yxati. JSON eski recordlarda reason bo‘lmaganda ham o‘qiladi.

**Cheklov:** hozir bir visit IDga bitta return record. `save` shu kalitni almashtiradi. “Bir do‘konga cheksiz return hujjati” funksiyasi bajarilmagan; uni yashirincha taxmin qilmang. Admin Create mavjud returnli do‘konlarni yangi forma tanlovidan chiqaradi. Shu cheklovni o‘zgartirish alohida model/migratsiya vazifasi.

`AdminReturnDocuments` Admin fixture returns + lokal saqlangan hujjatlarni birlashtiradi; mavjud saqlangan record o‘sha do‘kon demo recordini almashtiradi. Saqlangan Agent returnlari ham Admin arxivida ko‘rinishi mumkin. Demo hujjatlarning hisoblarini device SharedPreferencesga “real” qilib yozmaydi.

## 5. Summa, status va sanalar

- `DebtDirection.StoreOwesDealer`: do‘kon dilerga qarz → **qizil minus**. `DealerOwesStore`: diler do‘konga qarz → **yashil, minussiz**. Faqat rangni almashtirib, belgini unutish mumkin emas.
- Visit status Active ko‘k; Completed yashil; NotVisited yumshoq qizil; Attempted alohida. Statusni bir manbadan results bilan moslashtiring.
- Vizit cardida masofa manzil yonida, pastki qatorda; uzun manzil ellipsis bo‘lib, kmni bosib ketmaydi.
- `historyMoney(Long)` va return/order amallari UZS butun sonlar. USD qarzlar bor joyda `BigDecimal` va alohida format ishlatiladi. Valyutalarni qo‘shib yubormang, UI rasmdagi USDni ko‘rib joriy UZS modelini yashirin konvertatsiya qilmang.
- Statistika date-only sanalar UTC-midnight kalendar picker convention; haqiqiy timestamp/oy guruhlash Asia/Tashkent. `StatisticsDates` va `HistoryMonth.at`ni ishlating; host timezonega bog‘lamang.
- `historyReturnTotal` qaytarishni **record.createdAt** oyiga kiritadi, vizit boshlangan oyga emas. Sales hisobida ham shu farq bor.
- Reviziya oy soni va document list bir xil filter/helperga tayanadi. Faqat natija yozilgan, yakunlangan sessionlar historyga kiradi.

## 6. Dizayn va animatsiyalar

Asosiy manbalar:

- Hozirgi Dealer/Agent Visits/History Figma: https://www.figma.com/design/HWS4NGlGrmfzliWqqLYX9L/RIZO-Dealer
- Dastlabki Admin: https://www.figma.com/design/S9mUxm1q0q8qIqaZViiMzZ/RIZO-Dealer--Copy-
- Statistika/Agentning ilk dizayni: https://www.figma.com/design/xP7iPeNT6HO3mvf7Cd1m5S/Untitled
- Original lokal SVG, font, ikon, litsenziya va o‘lchovlar: `figma-reference/`. Bu katalogdagi eski izohlar tarixiy holatni aks ettirishi mumkin; funksiyalar uchun bu hujjat va joriy kod ustun.

`DealerDesign`: #9103E4 accent, #84919A secondary, #9AA6AC tertiary, #F4F4F4 fon, #EBEBEB divider; inset16, sectiongap8, radius12, icon24, button48. Default title17/22, body15/20, detail13/18, nav11/13. Native Android insets; iOS status/homebar bo‘shliqlarini ko‘chirmang.

`AgentVisitDesign`: native sans-serif. Joriy title20/24 Semibold, name15/20 Semibold, detail13/18 Medium, screenTitle17/22 Semibold, button15/20 Semibold, nav11/13 Medium. Bu vaznlar foydalanuvchining “juda ingichka” tuzatishidan keyingi holat.

`AgentDesign`: Statistika/Agent store/menu uchun bundled Onest 400/500/600/700 va #211F26. Uni butun Admin themega almashtirmang.

`PrototypeFontWeights` Samsung system bold sozlamasidan prototipning o‘lchovlarini izolyatsiya qiladi. Telefon sozlamasini o‘zgartirmaydi; uni “keraksiz” deb o‘chirib yubormang. Productionga o‘tishda alohida accessibility qarori kerak.

- `ActiveHistoryTabs`: HorizontalPagerning o‘sha PagerStateidan progress olib oq indikatorni siljitadi. Tab tap va swipe bir xil state; ikki mustaqil selectedTab yuritilmaydi. Press ripple/hover yo‘q.
- `SoftCardPress`: faqat callbackli elementlarda; lazy IndicationNode, draw-only scale. Idle kartaga animatsiya state bermaydi, clickni kutdirib navbat qilmaydi. Barcha statik kartalarga blanket clickable qo‘shmang.
- `VisitResultsTransition`/`VisitResultsRow`: do‘kon logo/nom/qarz va 5 result ikonining shared transition kalitlari. Resultlar detailsda o‘z ish satriga tushadi, yuqorida alohida panelga emas. Cardda ikonlar ixcham, qisman ustma-ust, detailsda kattaroq va asl ko‘rinishi bir xil.
- `AgentStoreSharedTransition`: Statistika qarzlar list/details logo/nom/qarz/ID.
- `VisitListViewport` hamda hoisted list states birinchi scroll va backdagi joyni saqlashga xizmat qiladi. Ularni soddalashtirib olib tashlamang.
- `AdminVisitsFlowScreen` va `AgentVisitsScreen` closeVisitda faqat yopilgan details subtree stateini `removeState` qiladi. Shu bilan cardga qayta kirishda eski Return ichki oynasi ochilib qolmaydi. Ro‘yxat scroll/filteri tashqarida saqlanadi.

## 7. Xarita, manzil va qo‘ng‘iroq

Hozir **Yandex SDK emas**: lokal MapLibre GL JS/WebView + OpenFreeMap Bright, `assets/map/` va `StoreMapView`. API kalit yo‘qligi uchun tanlangan. Litsenziya/attributionni olib tashlamang.

- `DealerMapDefaults`: Tashkentdagi bitta fixed demo diler nuqtasi (41.31115,69.27970). Har doim yo‘l shu nuqtadan do‘kongacha. Bu joriy telefon GPSi emas.
- `StoreRouteService`: OSRM driving, masofa/vaqt/geometriya **bitta javobdan**. Foydalanuvchi fixed demo diler va tanlangan store koordinatalari uchun OSRMni ma’qullagan. Bu barcha yangi servislar yoki boshqa shaxsiy ma’lumotlarni uzatishga umumiy ruxsat emas.
- GPS editing draft saqlanmaguncha OSRMga yuborilmaydi. Xarita tile/geocoder uchun internet kerak bo‘lishi mumkin.
- Umumiy `StoreActionsHost` ochgan map **read-only**, ikkala rolda ham. Pastki sheet drag bilan yig‘iladi/kengayadi, shop nom/manzil/km/vaqtni ko‘rsatadi. Dial `ACTION_DIAL`: raqam terish ilovasi ochiladi, avtomatik telefon qilmaydi.
- Faqat **Admin Menu → Магазин → details → Открыть карту** yo‘lida location editing. Qalamcha tahrirni yoqadi, current-location button emas. Agentda qalamcha/edit yo‘q.
- Editingda bitta fixed center shop pin tepaga-pastga animatsiyalanadi, soyasi alohida. GPS button faqat editingda animatsiya bilan ko‘rinadi; foreground permission bilan telefon joyiga olib boradi. Fixed diler nuqtasini o‘zgartirmaydi.
- Mapni surish shopning yangi nuqtasini tanlaydi. Geocoder manzilni to‘ldiradi; plus code, oblast/country/postcode `compactStoreAddress` orqali olib tashlanadi; xatoda qo‘lda yozish bor.
- Save → store.id bo‘yicha `prototype_store_locations`; old route tozalanadi; yangi route hisoblanadi; kechikkan eski javob koordinata mos kelmasa yozilmaydi; Stores ro‘yxat km ham yangilanadi. Cancel persist qilmaydi.
- `DraggableMapSheet`: native BottomSheetScaffold, sheet drag uchun WebViewni har frame qayta yaratish yoki balandligini o‘zgartirish yo‘q. Sheet inset `window.setStoreSheetInset`ga uzatiladi; route viewport va attributionni sheet yopib qolmasin.

## 8. Davom ettirish tartibi va tekshiruv

1. Vazifani shu oqim xaritasiga joylang; tegishli screen → caller → model/store → shared component → testlarni o‘qing.
2. Keng refaktorsiz o‘zgartiring. Umumiy komponent bo‘lsa Admin va Agent ishlatilishini `rg` bilan toping.
3. App ma’lumotlarini tozalamang, package ID/SharedPrefs kalitlarini o‘zgartirmang. Yangi dependency/backend kerak bo‘lmasa qo‘shmang.
4. `docs/REGRESSION_CHECKLIST_UZ.md`ning tegishli bo‘limini tekshiring. Code/logika uchun meaningful tests; oddiy matn o‘zgarishi uchun ortiqcha test yaratish shart emas.
5. Build/test natijasini aniq yozing. So‘ng ulanish bo‘lsa `adb install -r` bilan yangilang. O‘zbilarmon uninstall/clear qilinmaydi.
6. Foydalanuvchi UI sinovini o‘zi qiladi degan afzallikni hurmat qiling. Phone Link/real appni ko‘rish so‘ralsa faqat ko‘rish; haqiqiy moliyaviy operatsiya yubormang. UI/FPSni o‘lchamasangiz o‘lchandi demang.
7. O‘zgarishdan keyin shu hujjatdagi tegishli holatni yangilang. Hamma manba kodni chatga nusxalamang.

## 9. Yig‘ish, telefon va baseline

```powershell
# Loyiha ildizida
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest --offline --no-configuration-cache

# Ulangan qurilmani avval tekshiring; transport ID har safar o‘zgarishi mumkin
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" devices -l
# Faqat ro‘yxatdan aniqlangan transport raqami bilan:
# adb -t <transport_id> install -r app\build\outputs\apk\debug\app-debug.apk
```

So‘nggi holat: **57 tests, 0 failures; assembleDebug muvaffaqiyatli; Samsung SM_S916Nga o‘rnatilgan**. Hujjat tayyorlash paytida app kodiga o‘zgarish kiritilmaydi. Bu unit tests UI/performance testlar o‘rnini bosmaydi.

Handoff yaratilganda Git yo‘q edi; 2026-10-22 kuni Git ishga tushirildi. Joriy remote/branch uchun `git remote -v` va `git status`, tartib uchun `docs/GITHUB_WORKFLOW_UZ.md`ni o‘qing. `docs/HANDOFF_BASELINE_SHA256.json` va `scripts/Compare-HandoffBaseline.ps1` snapshotdagi muhim fayllarni tekshiradi; bu avtomatik rollback emas. Original source ZIP foydalanuvchiga alohida beriladi. Zarur bo‘lsa arxivni **yangi papkaga** ochib solishtiring; joriy loyihani ko‘r-ko‘rona ustidan yozmang. ZIP telefon SharedPreferences ma’lumotlarini, SDKni yoki build cachelarni saqlamaydi.

## 10. Bilinadigan cheklovlar / keyingi ish uchun eslatma

- Agentning sessioni hozir store/visit ID bo‘yicha bitta; ko‘p kunlik real visit ID modeli yoki multi-agent backend yo‘q.
- Return modeli har visit IDga bitta record. Mavjudni ustidan yozish xavfini tushunmasdan ko‘p hujjat funksiyasini qo‘shmang.
- Demo tovarlar, narxlar, tarix, ayrim statistika ko‘rsatkichlari real ERP hisoblari emas. Barcha ekran sumlari bir global moliyaviy balansga bog‘langan deb taxmin qilmang.
- Catalogdagi ikki original Figma mahsulot rasmi paketlangan; boshqa mahsulotlarda neutral placeholder bo‘lishi mumkin. Loyiha manbalarida bo‘lmagan rasmni “asl rasm” deb ko‘rsatmang.
- Internet bo‘lmasa route/geocoder/map ishlamasligi mumkin; fallback mavjud UIini olib tashlamang. OSRM production SLA bermaydi.
- Mavjud ba’zi report/menu/buttonlar dizayn maketi xolos. Masalan store detailsdagi vizual «Начать визит»ni Agent Visitsdagi haqiqiy session boshqaruvi bilan adashtirmang.
- Hozir alohida bajarilmagan topshiriq yo‘q: keyingi o‘zgarish foydalanuvchining yangi vazifasidan boshlanadi. Ushbu cheklovlarni ruxsatsiz kengaytirish “tozalash” hisoblanmaydi.

## 11. Agent Заявка — 2026-10-24

Agent → Визиты → do‘kon → Заявка endi ishlaydi. Alohida bottom-navigation bandi qo‘shilmadi; Admin Заявки va umumiy Return oqimi o‘zgarmadi.

- Yangi zayavka faqat faol vizitda. Boshlanmagan vizitda «Начать визит» bilan haqiqiy boshlanish vaqti yoziladi; turli do‘konlarda parallel vizit mumkin. Yakunlangan vizitda yangi zayavka yaratilmaydi.
- Figma bo‘sh holati/asl illustratsiya → Добавить → mahsulot qidirish (nom/barcode), miqdor sheeti → Продолжить → umumiy summa, tovarlar soni, X va minus/count/plus → Завершить → Внимание / Да.
- Picker mustaqil draft: Back tashqi formani o‘zgartirmaydi. Sheet miqdori 0..katalog stock oralig‘ida, stock 0 bo‘lsa tanlanmaydi. 0 miqdor cartdan chiqaradi. Bekor qilish/bo‘sh forma result yoki hujjat yozmaydi.
- `RequestCatalog.kt` — 20 demo pozitsiya, buyurtma uchun alohida stock; returnning do‘kondagi qoldig‘i yoki manba hujjati order UIiga ko‘chirilmagan. Asl mavjud mahsulot rasmlari qayta ishlatiladi, qolganida neutral placeholder.
- `VisitRequestStore`: yangi `agent_visit_requests` SharedPreferences, visit ID → documentId, createdAt, items(productId,name,price,quantity). Bir vizitga bitta zayavka. Narx/nom/miqdor snapshot, UZS Long. Faol vizitda «Редактировать» bilan shu hujjat yangilanadi; documentId/createdAt saqlanadi. Vizit yakunlangach read-only.
- `AgentVisitSessions.saveRequest` faqat faol sessionga Request resultini qo‘shadi, vizitni yakunlamaydi va boshqa natija/izoh/rasm/vaqtni saqlaydi. Vizit qatorida haqiqiy items/total tavsifi chiqadi.
- AgentHistoryEntry.requestRecord optional. Vizit yakunlangach history/sales shu record mahsulot va summalarini oladi; demo mahsulotlar bilan almashtirmaydi. Sales oyiga record.createdAt (Asia/Tashkent) bo‘yicha tushadi. Haqiqiy qayd bo‘lmagan sklad/qabul/dostavka timeline hodisalari qo‘shilmadi.
- O‘rganilgan Figma va aniq komponentlar: `figma-reference/agent-requests/README.md`.

Tekshiruv: debug yig‘ildi, **62 unit test, 0 failures** (yangi order validatsiyasi, aniq total, history/sales va oy chegarasi). Telefon UIini foydalanuvchi tekshiradi. Oldindan tahrirlangan PendingAgentVisitSampleData va VisitSampleConsistencyTest fayllari aynan saqlandi va bu vazifaning commitiga kiritilmadi.

### 2026-10-24: mahsulot tanlash Figma bilan qayta moslandi

RequestProductPickerScreen endi SearchSelection eksportining o‘lcham/rang/ikon/raster manbalarini ishlatadi. RequestPickerComponents va RequestPickerDesign faqat shu oynaga tegishli: Regular15/20 va13/18 manbaga mos; boshqa ekranlarda kuchaytirilgan typography saqlangan. Header72/search40, ichki X, selected done_all, rasm48/32, narx+mavjudlik bir qator, badge va chegirma; compact RequestQuantitySheet alohida faylda.

RequestProductThumbnail original rasmlarni IOda sampled decode qiladi. Beshta asl mahsulot rasmi bor; ikkita bir xil asset avvalgi Return resursidan qayta ishlatiladi. RequestCatalog dastlabki20 ID/indeksni saqlaydi, XGT/Ryobi yangi22tagacha kengaytiradi; displayOrder saqlanadigan draft indekslarini o‘zgartirmaydi. Yangi Makita demo450000/old500000; mavjud request snapshotlari o‘zgarmaydi. Return katalogi o‘zgarmagan.

63 unit test va build o‘tdi; UI/FPSni telefonda avtomatik tekshirildi deb hisoblamang. `figma-reference/agent-requests/README.md` va picker-measurements.json — aniq manbalar.

### Picker: foydalanuvchi so‘ragan narx/nom ajratilishi

Figma asosidan keyingi ruxsat etilgan o‘zgarish: bor mahsulotlarda В наличии va ajratuvchi nuqta olib tashlandi. Faqat stock=0 bo‘lsa narx ostida Нет в наличии; shu qatorda + va bosish ishlamaydi. Mahsulot nomi15/20 Medium (2 qator/ellipsis), narx16/22 Semibold, valyuta/birlik13/18 Medium kulrang; matn oralig‘i4. Oddiy bir qator nomli mavjud mahsulot qatori48px kontent ichida qoladi. Discount, quantity badge, picker draft/stock cap, katalog va saqlangan hujjatlar o‘zgarmagan. Manbadagi eski В наличии qatorini avtomatik qaytarmang.

### Picker: tanlanganlarni tekshirish va ixcham jami

- Mavjud mahsulotlar yuqorida, stock=0 pastda; har guruh ichida avvalgi displayOrder. Katalog/draft indekslari va saqlangan mahsulot IDlari o‘zgarmaydi.
- Yuqoridagi «Выбрано»/done_all/«Показать» va selected-only UI filtri olib tashlangan. Bitta LazyListState va qidiruv/draft qoladi; tanlanganlarni Продолжить orqali tashqi cartda tekshirish va Добавить bilan qayta kirish mumkin.
- Продолжить ustida noyob pozitsiyalar soni va barcha tanlanganlarning Long UZS jami turadi. Qidiruv/filtr summani kamaytirmaydi. Tanlov0 bo‘lsa ham son/jami satri qoladi (list balandligi sakramaydi), Продолжить o‘chiq.
- Tanlangan thumbnailga1dp/60% accent chegara; o‘lcham48 va sampled IO decode saqlangan. selected parametri defaultfalse: forma va saqlangan hujjat rasmlari o‘zgarmaydi.
- Bu foydalanuvchi tasdiqlagan Figma asosidan keyingi yaxshilanishlar. Picker Back hanuz tashqi formani o‘zgartirmaydi; faqat Продолжить draftni qo‘llaydi. Build va66 unit test o‘tdi, qurilma UIini foydalanuvchi tekshiradi.

### Picker: sheet o‘rniga tezkor miqdor

Pickerning joriy dizayni: yuqorida48dp rasm + max2 qator nom va chegirma; pastda to‘liq kenglikdagi narx/counter qatori. Narx16/22 Semibold va birlik13/18 bir xil qora, birlik o‘lchami kichikroq. Counter120×40dp (3 ta40dp slot),22dp original +/derived − va radius12. + joyi/default kattaligi o‘zgarmaydi; tanlovda fon300ms chapga yoyiladi, minus/son fade+slide bilan ochilib/yopiladi. Draw/graphicsLayer progressi card/listni har frame qayta o‘lchamaydi. Son AnimatedContent320ms, SizeTransform=null, wrap stack markazda40×40dp klip ichida; 200ms va fillMaxSize/SizeTransformdagi eski siljish qaytarilmasin. Bo‘sh mavjud card butunlay bosiladi va1 dona qo‘shadi; tanlanganida body statik, counterlar ishlaydi. Stock0 card bosilmaydi. Draft son/summa darhol, animatsiyani kutmay yangilanadi. Yuqori selected qatori yo‘q, hisob pastda doim bor. Son bulk sheetni ochadi; keyingi review oynasi bu vazifada o‘zgarmagan.

stepRequestPickerQuantity har bosishda oxirgi draftdan yangi list qaytaradi, stock va0 chegaralarini saqlaydi; tez takroriy tap eskirgan row miqdorini qayta ishlatmaydi. Minus0 tanlovdan chiqaradi va jami yangilanadi. Sheet faqat tasdiqlanganda draftni o‘zgartiradi; tashqi forma hanuz faqat Продолжить bilan yangilanadi. Inline +/− bosishda qidiruv focus/IME o‘z holatida qoladi; bulk sheet yopilgach IME avtomatik qidiruvga qaytmaydi. Search ochilgandagi dastlabki fokus qolgan. RequestProductCard/review, Return va Admin buyurtmalari o‘zgarmagan.

Inline miqdor tekshiruvi: debug build va 68 unit test muvaffaqiyatli, 0 xato. Qurilma UIini foydalanuvchi tekshiradi.

### 2026-10-26: umumiy picker uslubi qaytarishlarda

Uchta kirish yo‘li (Agent Visit→Return, Admin Visit→Return, Admin Menu→Returns→Create→store) bitta ReturnProductPickerScreen ishlatadi. Uning header/footeri, rasm48/title max2/price+animated counter, qidiruv X, selection border va bulk sheeti yangilangan Request uslubida. Return cardida Можно вернуть va oldingi Заявка sanasi/originalQuantity ham bor.

PickerProduct — faqat readonly UI contract. RequestCatalogProductning konstruktor/ID/indeks/narxlari o‘zgarmagan; ReturnCatalogProduct.stock getter availablega, id getter barcodega teng. Hech qaysi catalog fixtures yoki saqlangan JSON almashtirilmagan. RequestQuantitySheet default4 digit; Return picker maxDigits2/stockLabel=Можно вернуть bilan0..available tekshiradi. Formadagi eski ReturnQuantitySheet va source-info sheet o‘z holida.

stepReturnPickerQuantity har tapda oxirgi lokal draftdan, faqat Return qoldig‘igacha hisoblaydi; returnPickerTotal Long jami to‘liq draftdan. Back/dismiss tranzaksion, faqat Подтвердить tashqi tanlovga qo‘llaydi. Returnda empty draft confirm avvalgidek mumkin (barcha oldingi mahsulotni olib tashlash uchun); Request Continue0da o‘chiq. Bu save emas: VisitReturnScreenning Возвратить→reason sheet→validation→store.save oqimi, active-visit/read-only cheklovlari va tarix o‘zgarmadi. Unit tests qoldiq ajratilishi, total va draft izolyatsiyasini qamraydi; qurilma UIini foydalanuvchi tekshiradi.

2026-10-26 validation: assembleDebug and 70 unit tests passed, 0 failures. User performs device UI checks.

## 12. Vizitlar va rejadan tashqari oqim — 2026-10-26

- Admin/Agent Visit details va Agent History detailsdagi operation chevronlari olib tashlangan. Besh turdagi operation uchun bir xil VisitResultBadge: list28/icon20, details32/icon24. Faqat haqiqatan yozilgan result shared transitionda qatnashadi; tugallanmagan qatorning accessibility matni ish bajarilganini aytmaydi. Boshqa menu/report chevronlari saqlangan.
- AgentScreen Visits uchun haqiqiy LazyListStateni saqlaydi. Fixture ID tartibi rememberSaveable; session/return/request/unplanned snapshotlari o‘zgarmaganda cardlar qayta yig‘ilmaydi. SharedTransitionLayout doimiy; Backdan600ms keyin faqat preparedId tozalanadi (440ms bounds +160ms buffer). Avvalgi hostni olib tashlash optimizatsiyasi scrollni buzishi aniqlanib bekor qilingan. Logo/nom/qarz/results shared va eski Back scrolli saqlangan.
- Agent Visits pastida Внеплановые. Real-app rasmdagi uch radio (Визит / Заявка / Инкассация), so‘ng Figma579:32035 store picker. Search nom/ID; faol physical store o‘chiq va izohli. Boshqa do‘konda faol vizit bo‘lishi Startni bloklamaydi; tanlangan physical storedagi active dublikat bloklanadi.
- Tanlov paytida faqat vaqtinchalik draft. Начать визит actual vaqt bilan yangi unplanned-UUID session yozadi; agent_unplanned_visits physical store snapshotini alohida saqlaydi. Eski store/visit/session/return/request IDlar o‘zgarmaydi. Oldingi vizit tugagan bo‘lsa shu do‘konga yana yangi ID bilan kirish mumkin. Boshlamasdan Back hech narsa yozmaydi.
- VisitStore.sourceStore optional, faqat yangi unplanned uchun. Xarita/telefon/ID shu physical store orqali; shared key va session visit UUID orqali. Agent xaritasi read-only.
- Заявка tanlovi mavjud AgentVisitRequestScreenni, Инкассация esa kichik lokal UZS amount formasini ochadi; ikkisi Startni shu yangi visitga bog‘laydi. Positive Long collectionAmount optional JSON maydoni; eski JSONsiz ham o‘qiladi. Saqlash Collection resultini qo‘shadi, vizitni tugatmaydi. Return/request va notes/times saqlanadi; finish qoidalari o‘z holida.
- Yakunlangan unplanned vizitlar Agent Historyga kiradi, unplannedOnly va physical store filtrlari ishlaydi. Order/return/collection hujjatlari o‘z snapshotlaridan. Monthly sales bir do‘kondagi turli visit ID hujjatlarini physical ID orqali jamlaydi; Request/Return oy sanasi avvalgidek record.createdAt.
- Figma source eksporti: figma-reference/agent-unplanned/. Collection summasi formasi aniq collection Figma eksportiga teng deb da’vo qilinmaydi; mavjud uslubdagi lokal prototype davomidir.

Qurilmada UI tekshiruvini foydalanuvchi qiladi. Qotish sababi nazoratli scenario/FPS sinovi bilan tasdiqlanmagan; yuqoridagilar ro‘yxat holati va shared host umrini tuzatuvchi kod o‘zgarishlari. Oldindan tahrirlangan PendingAgentVisitSampleData.kt va VisitSampleConsistencyTest.kt tegilmaydi va commitga kirmaydi.

2026-10-26 final validation: assembleDebug and 76 unit tests passed (0 failures, 0 errors); installed to Samsung SM_S916N with install -r. Device UI/FPS checks remain for the user.

### 2026-10-26: detailsdan qaytganda scroll to‘xtashi — tasdiqlangan tuzatish

Sabab foundation1.10.4 manbasidan tekshirildi: LazyListState.hasLookaheadOccurred true bo‘lgandan keyin u qayta false bo‘lmaydi. SharedTransitionLayoutni olib tashlab shu state bilan oddiy layoutga o‘tish applyMeasureResultni faqat approach natijasiga yozdiradi; asosiy scroll pozitsiyasi/chegaralari yangilanmay qoladi. Timer bilan hostni tezroq olib tashlash muammoni hal qilmaydi.

VisitResultsTransition endi bitta barqaror SharedTransitionLayout + AnimatedContent ishlatadi. movableContentOf va idle/listni boshqa parentga ko‘chirish olib tashlangan.600ms cleanup faqat tanlangan shared IDni o‘chiradi; hostni emas. Faqat tanlangan card shared modifierlari faol, boshqa cardlarda ular yo‘q. Logo/nom/qarz/result kalitlari,440ms bounds, viewport/query/pager va business logic saqlangan. Umumiy host Admin/Agentga xizmat qiladi.

Tekshiruv Samsung SM_S916N’da, foydalanuvchining aniq telefon sinovi ruxsati bilan bajarildi. Oldingi61a542e APKga yangi regression harnessi qo‘yilganda scrollingSurvivesRepeatedDetailWorkAndBack haqiqiy xatoni ushladi: Forward scroll stalled: (11,246) -> (11,246). Yangi APKda shu testning3 sikli va hoistedStateScrollsAfterLeavingAndReturningToVisitsTab o‘tdi (2/2). Harness faqat80ta synthetic card/in-memory ish holati, actual shared komponent va LazyListState ishlatadi; user session/preferences/financial data yo‘q.

Telefondagi haqiqiy Agent prototipida yakunlangan card → mavjud Заявка → Back → Back ham bajarildi; Phone Link orqali list pastga/yuqoriga siljishi ko‘rildi. Qo‘shimcha FPS benchmark qilinmadi. Phone Link blackscreen qatlami oddiy ADB pointer eventlarini yutgani sabab native window scroll ishlatildi; bu app sozlamasi o‘zgartirilmadi. Test harness eski Espresso3.5.1 ning yangi Android InputManager incompatibilitysini chetlash uchun public ActivityScenario/instrumentation sync + list dispatchRawDelta ishlatadi, dependencies yangilanmagan.

assembleDebug, assembleDebugAndroidTest va76 unit test (0 failures) o‘tdi. Yangi APK install-r bilan telefonda. Oldingi ikki unrelated dirty fayl aynan saqlangan.

## 13. Faol vizit: kamera va soatli hisoblagich — 2026-10-27

- AgentVisitNotesning mavjud96dp «Добавить фото» cardi VisitPhotoAdder orqali bevosita tashqi kamerani ochadi. Galereya tanlovi va source sheet foydalanuvchi ko‘rsatmasiga ko‘ra olib tashlangan; oldin saqlangan gallery URIlar hamon o‘qiladi. Faqat faol vizit, umumiy limit6; completed/history read-only.
- Kamera tashqi kamera ilovasini ActivityResultContracts.TakePicture orqali ochadi. VisitTakePictureContract aniq content URI uchun vaqtinchalik read/write grant + ClipData beradi. Yangi dependencies yoki CAMERА/storage runtime permission qo‘shilmagan; bu CameraX/in-app preview emas.
- VisitCameraPhotos full-resolution JPEGni filesDir/visit_photos/camera ichida saqlaydi. Manifestdagi exported=false FileProvider faqat shu katalogni ochadi; root/files wildcard yo‘q. Context package+.visit_photos authority. Kamera cache emas, shu sabab app qayta ochilganda ham photoUris string bilan o‘qiladi. Pending URI rememberSaveable(session.startedAt), ActivityResult qaytishiga mos.
- Kamera cancel/error/empty-or-invalid image holati evidence qo‘shmaydi; faqat o‘zi yaratgan visit_*.jpg vaqtinchalik fayli tozalanadi. Qaytishda active session va6-limit qayta tekshiriladi. Kamera mavjud bo‘lmasa tushunarli toast; AddPhoto ochiq holatda qotib qolmaydi. Foto o‘chirish va mavjud JSON/session/result/timer manbasi o‘zgarmagan.
- AgentVisitTiming faol hisoblagichi har doim HH:mm:ss (00:00:00,00:01:05,01:00:00). model/VisitDuration.kt formati Locale.ROOT,24soatda aylanmaydi. Actual startedAt/finishedAt asosiy manba bo‘lib qoladi; tugagan vizitning status/vaqt qatori va history dizayni o‘zgarmagan.

Validation: assembleDebug, assembleDebugAndroidTest,79 unit test (0 failures). Samsung SM_S916N’da3 camera-storage/contract testi o‘tdi: secure provider/grants, generated JPEG URI restore/read+native thumbnail, cancel/empty file cleanup. Test real kamerani/galereyani ochmaydi va faqat yangi test rasmlarini ishlatadi. Haqiqiy kamera shutteri/source sheet UI foydalanuvchi sinovi uchun; avtomatik end-to-end capture tekshirildi deb hisoblamang. install-r bilan APK telefonga qo‘yildi. Boshqa oldindan tahrirlangan2 fayl saqlangan.

## 14. Parallel vizitlar, Новый/Активный, faqat kamera — 2026-10-27

- User global bitta-faol-vizit cheklovini bekor qildi. canStartAgentVisit endi faqat shu ID mavjudligi/oldingi recorded resultsni tekshiradi. AgentVisitSessions.activeVisitIds/activeStoreIds barcha faol session/physical storelarni qaytaradi. Turli do‘konlarda istalgan bir necha active session bo‘ladi; finished ID o‘zgarmaydi, qayta boshlatilmaydi.
- Same-store active duplicate cheklovi saqlangan: rejasiz picker barcha faol physical storelarni o‘chiradi (avval faqat bittasini); startUnplanned/start ham shu dublikatni tekshiradi. Boshqa do‘kon faol bo‘lgani uchun umumiy warning/toast yo‘q. Demo faqat dastlabki seed sifatida qolgan, global limit emas.
- agentVisitStatus shu sessiondan olinadi: boshlanmagan Новый, active Активный, finished Завершено, StoreClosed Попытка. Active cardda result bor bo‘lsa ham actual session active ekanligi ustun. UI enum label/color: New gray84919A, Active blue2563EB; NotVisited ichki nomi caller/fixture mosligi uchun o‘zgarmagan. Admin/Agent umumiy status labelni ishlatadi.
- Har bir sessionning startedAt, HH:mm:ss, notes/photos/requests/collection/returns mustaqil. Bittasini finish qilish qolganlarini tugatmaydi; finished/no-work sabab va6-foto qoidalari saqlangan. Tarixga faqat finished session kiradi.
- VisitPhotoAdder gallery launcher/source sheetni olib tashladi. Добавить фото darhol TakePicture. Mavjud rasmlar/session JSON/FileProvider/capture-cancel qoidalari o‘zgarmagan. Real kamera shutteri test uchun ishlatilmadi.

Validation: assembleDebug/assembleDebugAndroidTest,82 unit test PASS. Samsungda5 native test PASS (AgentParallelVisitsTest2 +VisitCameraPhotosTest3). Parallel testlar UUID prefixli o‘z SharedPreferencesi bilan actual AgentVisitSessionsni ishlatadi: A/B/unplanned-C birga start; B finish; A/C active; notes/photos/amount/start restore; same-store duplicate rad, boshqa store ruxsat. Test faqat o‘z pref namespaceini tozalaydi, user ma’lumotlariga tegmaydi. APK install-r bilan yangilangan. Oldindan dirty PendingAgentVisitSampleData.kt va VisitSampleConsistencyTest.kt tegilmagan.


## 15. Admin diler ma’lumotlarini tahrirlash — 2026-10-28

User requested Admin → Menu top pencil. Source inspected read-only in Chrome: https://rizopost-staging.up.railway.app/legal → Edit legal entity. New native mobile adaptation, not a claim of a mobile Figma export.

- DealerApp opens screens/DealerProfileScreen.kt, hides bottom navigation and keeps Menu scroll on return. The person name/phone in Menu and Agent profile are unchanged.
- Company/INN or PINFL, interaction, activity, currency, tariff → country/region/district/address and separate map picker → logo/photo/UzQR → seven-day working hours → price visibility. Typography follows DealerDesign/AgentVisitDesign, original icons and softCardPress only on actions.
- model/DealerProfile.kt owns immutable draft/validation and working hours. Empty tax ID is allowed; a provided ID must be ASCII9/14 digits. Overnight hours are allowed, equal times rejected. Copy-to-all copies the first enabled day’s times, preserving closed days.
- data/DealerProfilePreferences.kt stores one profile JSON in NEW administrator_dealer_profile/profile. Currency/tariff/price visibility here are profile metadata, not global accounting/subscription changes. Save alone persists; Back cancels. Saver restores the draft through rotation; list state is hoisted across map entry. Invalid data cannot replace the saved record.
- INN is manual entry in this offline prototype: no tax-registry lookup or writes to the Chrome service. Non-Uzbekistan regions and districts without a local option list are editable text. Prototype defaults are fictional coordinates/address, not copied private account details.
- DealerProfileMedia + DealerProfileImages use OpenDocument for THESE business images, validate image/5MB and retain read access, bounded thumbnail decode on IO. Nothing is uploaded. This does not restore the removed gallery option for VISIT photos, which remain camera-only.
- DealerProfileLocationScreen reuses StoreMapView/marker, changes only this profile’s draft coordinates/address. No OSRM/GPS, route/account-store edits or store-location preference writes. Existing Android geocoder handles a moved point; address is also editable. Back cancels point selection; select applies only to draft, Save persists the profile.
- New files: model/DealerProfile.kt; data/DealerProfilePreferences.kt, DealerProfileImages.kt; screens/DealerProfileScreen.kt, DealerProfileLocationScreen.kt; ui/components/DealerProfileFields.kt, DealerProfileMedia.kt, DealerProfileWorkingHours.kt. Small caller edits only in MenuScreen/DealerApp.
- Tests: DealerProfileTest (4 validation/hours cases); DealerProfilePersistenceTest (2 isolated Android JSON/storage cases, no UI or user data). Check final validation result below; do not infer that UI was exercised from a successful build.

2026-10-28 final validation: assembleDebug + assembleDebugAndroidTest + 86 unit tests passed (0 failures/errors); Samsung SM_S916N app/test install-r succeeded. DealerProfilePersistenceTest OK (2 isolated device tests), no prototype UI navigation or user data edits. Visual/UI checks remain with the user.


## 16. Ikki tabli Admin profil — 2026-10-28

Latest user override replaces the single legal editor entry in §15. Admin Menu entire top profile row (including pencil) → screens/AdminProfileScreen.kt. Header «Профиль»; «Физические лица» (personal, default) / «Юридические лица» (dealer). White segmented indicator follows live HorizontalPager offset, 280ms click animation, no ripple/hover. Tabs remain pinned above each independently scrolling form.

- screens/AdminPersonalProfileScreen.kt: self name, phone, optional birth date (native date picker), gender, country/region/district. Role and associated company are read-only. No password, blocked/access/permission flags or other users. Chrome /clients Edit user was inspected read-only for labels; no web user record was changed or private source values copied into defaults.
- screens/DealerProfileScreen.kt now renders the EXISTING legal form inside the host; fields, images, work hours and map remain. Shared option lists moved to data/DealerProfileOptions.kt. Original DealerProfile key/JSON are retained.
- Both immutable drafts/Savers, pager and two LazyListStates are hoisted in AdminProfileScreen. Switching tabs/map and rotation keep drafts/scroll. Back before Save cancels both. One Save validates BOTH forms, moves to the invalid tab if needed, then commits both JSON values using ONE SharedPreferences.Editor commit. No half-saved tab on validation failure.
- DealerProfilePreferences still uses administrator_dealer_profile/profile for legal; NEW administrator key in the same namespace for model/AdminPersonalProfile. saveProfiles commits both atomically. Legacy save(dealer) preserves the personal key. Older legal-only data gets existing Menu name/phone defaults; no reset or migration required. Agent/visit/store/accounting prefs remain separate.
- Successful save refreshes name/phone in the Admin Menu immediately (displayAdminPhone preserves the familiar formatting) and closes the editor. Agent menu/profile is unchanged. Admin role/authorization remains the prototype role selection, not real authentication.
- AdminPersonalProfile validates full name, international phone, optional ISO birthday (real calendar, 1900..today, Tashkent date), gender and region. Phone keyboard and normalized storage; no SMS or calls. DealerProfileField adds an optional phone keyboard mode without changing other callers.
- DealerApp/MenuScreen callers, ui/components/AdminProfileTabs, model/AdminPersonalProfile, data/AdminPersonalProfileJson and new forms comprise this scoped change. Tests add 3 personal validation cases and 2 native atomic-save/backward-compatibility cases in DealerProfilePersistenceTest (now4). UI remains for user testing.

2026-10-28 two-tab validation: debug/app-test builds and 89 unit tests passed (0 failures/errors). Samsung SM_S916N app/test install-r Success; DealerProfilePersistenceTest OK (4 tests, isolated preferences only). No real prototype UI/profile was edited during these tests; visual checks remain with the user.


## 17. Ko‘p jismoniy shaxs — 2026-10-28, latest user correction

User clarified that Физические лица contains MANY people and supplied ten roles. This supersedes §16's personal-tab direct form and global two-draft Save. The two-tab host remains; legal form is intact.

- Personal tab → AdminPeopleScreen list of10 fictional samples (current administrator +9 other roles), searchable by name/phone/role, stable-ID lazy cards, current self marked Вы, fixed Добавить сотрудника. Each card opens AdminPersonEditScreen; new UUID for each addition, blank name/partial phone never persist before Save.
- A person editor reuses AdminPersonalProfileScreen, with an editable role for other people. Role choices match supplied source: Администратор, Бухгалтер, Мерчендайзер, Главный кассир, Кассир, Консультант, Супер-курьер, Курьер (товары), Курьер-инкассатор, Торговый агент. Current admin role stays readonly; associated legal entity stays readonly. No live authentication, web permission changes or messages/invitations.
- Each person's Save commits the people directory + current-admin compatibility snapshot atomically and returns to the list. Back cancels only that person's draft. Legal draft survives list/editor/map/tab changes and has its own Save; person saves do NOT commit/discard a pending legal draft. This avoids an extra Done + global Save step for people.
- model/DealerPerson.kt: stable IDs, upsert, directory validation (one current-admin ID, known roles, valid profiles, unique IDs/normalized phones). data/DealerPeopleData.kt: sample data and JSON. No source-page private staff names/phones copied to samples.
- DealerProfilePreferences adds people JSON in EXISTING administrator_dealer_profile. When absent, readPeople migrates old administrator snapshot into current-administrator and adds demo colleagues in memory. Saving legal alone preserves all people. saveProfiles legacy API keeps the current directory record in sync. Editing another employee never changes the Menu identity; editing current admin refreshes Menu name/phone only. Agent role/app/session data remain unchanged.
- AdminProfileScreen hoists saved directory, query, two list states and selected/new-person ID. New person draft and legal draft survive rotation; cancel leaves saved data. List search/scroll returns on closing the editor. Existing DealerProfile map/media/hours and camera-only Visit photo behavior remain.
- Tests add DealerPeopleTest3 (samples/roles/identity/upsert/validation) +2 isolated device directory tests; DealerProfilePersistenceTest now6. Earlier self-form/atomic legal API tests remain for compatibility. Final validation below.

2026-10-28 directory validation: assembleDebug/assembleDebugAndroidTest and92 unit tests passed (0 failures/errors). Samsung SM_S916N install-r Success;6 isolated DealerProfilePersistenceTest cases passed, covering old legal/self JSON, directory reload/edit/add/duplicate rejection and identity isolation. No real prototype UI or user records were modified during tests.


## 18. Profile design + view before edit — 2026-10-28 (latest)

- User asked for a lighter outer/inner design and mandatory view-first for BOTH people and legal entity. Admin people cards now open AdminPersonDetailsScreen (text, no form); legal tab opens DealerProfileDetailsScreen. Pencil in ProfilePageHeader alone enables editing. New-person creation still opens a blank form directly.
- Existing-person Back during edit cancels draft and returns to its saved details; Back from details returns to directory at previous query/scroll. Successful person save now returns to its updated DETAILS, not the directory, retaining the selected stable ID. Current-admin identity refresh remains isolated. A new-person Back before save creates nothing.
- Legal view has its own savedDealer snapshot + LazyListState; editor keeps a separate draft/edit list. Back in editor discards unsaved draft into view; map Back only cancels map selection. Save updates saved snapshot and returns to legal view. View mode mounts no text fields, switches, image pickers or map edit callbacks.
- ProfileDesign is LOCAL typography: medium15 names, regular13 support, label12, charcoal text/neutral roles. Smaller initials avatars, discreet Вы badge, inset compact cards, search38 and top Добавить action remove the large bottom button. Tabs retain live white indicator/no-hover behavior. Profile-only section/input/media/working-hours typography refined, not global DealerDesign/AgentVisitDesign.
- Read-only detail components show ALL existing profile information including personal contact/birthday/gender/region/company, and dealer tax ID/interaction/currency/tariff/price visibility/address/hours/images. Blank data is explicit; images in view mode have no upload actions. Standard native insets, original drawable icons and softCardPress remain.
- New sources: ui/theme/ProfileDesign.kt; ui/components/ProfileDetailsComponents.kt (header/avatar/badge/identity/readonly values/media); screens/AdminPersonDetailsScreen.kt, DealerProfileDetailsScreen.kt. AdminPersonEditScreen is now the view/edit host; AdminProfileScreen gates legal editing. Persistence schemas, identities, role rules and Agent/visit/map flows are unchanged. No new dependencies.
- This UI task does not introduce redundant implementation-mirroring unit tests. Existing build/model regression tests are used; actual interactive UI checking remains with the user.

2026-10-28 profile visual/view-first validation: assembleDebug and92 existing unit tests passed (0 failures/errors). Installed app to Samsung SM_S916N with install-r. No schemas/business rules changed; no additional redundant tests or new device/UI test claims. Interactive visual checks remain with the user.


## 19. Xodim fotosi + collapsing people header — 2026-10-28 (latest)

- Two authorized requests completed together: add/replace a person's photo, and refine physical-people AppBar/search/actions scroll behavior. View-first/pencil-only editing from §18 remains.
- AdminPersonalProfile appends OPTIONAL photoUri=""; AdminPersonalProfileJson writes/reads it with an empty fallback for all old records. Same existing people/administrator preferences/IDs, no resets. Photos are per-person; editing another person cannot replace current admin, another employee or the legal photo.
- Editor-mounted rememberPersonPhotoPicker (PersonPhotoPicker.kt) uses OpenDocument image/*, existing local5MB/image validation and persistable read access; async work off main. It lives outside recycled LazyColumn rows, retains pending owner across rotation, guards callbacks by person ID. User selection updates draft only; Save is disabled during validation; Back cancel does not persist. Read-only mode has no photo action. List/details use the shared circular ProfileAvatar; saving current-admin photo also updates Menu avatar. Visit photos remain CAMERA-ONLY; no backend uploads/camera/storage permissions added.
- Physical list top title is left aligned. Root AdminProfileScreen hoists rememberTopAppBarState + enterAlwaysScrollBehavior and a single nestedScroll connection (no per-card listeners/shared lookahead changes). Collapse is enabled only for the current people list, disabled for toolbar input and child/legal screens. State/query/list restoration remain stable after details.
- AdminPeopleHeader.kt: title-to-inline-search Crossfade, right search icon fade/scale driven by header offset; expanded search collapses in first48dp, count/Add row in next44dp. Reverse scroll reveals the short actions row first without requiring the list to reach the top. Native snap260ms. Tabs are always a pinned sibling, never part of the92dp collapsing region. Opening toolbar search finishes collapse smoothly, types INSIDE AppBar; X clears while keeping it open; Back closes input first. Query filtering is memoized by people/query, not scroll frames.
- AdminPeopleScreen now renders only the keyed list; search/actions live outside HorizontalPager below fixed tabs. Darkened ProfileDesign text/secondary; names Semibold15, support Medium13, other dimensions unchanged. ProfilePageHeader left alignment is profile-local, not a global appbar redesign.
- Storage regression adds one isolated device test for person-photo roundtrip, legacy no-photo defaults, other-person/self/legal isolation (DealerProfilePersistenceTest now7). No gallery or real user images are touched in tests. Existing92 unit tests retained; UI/animation inspection remains for the user.

2026-10-28 photo/header validation: debug/app-test builds and92 unit tests passed (0 failures/errors). Samsung SM_S916N install-r Success; DealerProfilePersistenceTest OK (7 tests) including photo JSON/ID isolation and legacy compatibility. Tests use only UUID-prefixed preferences and fake photo URIs. Actual picker interaction, header animation/UI and user photos remain for user testing; no prototype UI was navigated automatically.


## 20. Admin Menu cleanup - 2026-10-28

User removed the Information about dealer row from MenuScreen.menuItems. Profile row/pencil still opens the existing personal/legal tabs. Other menu entries, Agent screens, data and drawable assets are unchanged.


## 21. Do‘kon o‘zaro hisob-kitob tarixi — 2026-10-28

- Admin/Agent store and visit details now expose «История взаиморасчётов» immediately after saldo; completed History visits also expose it. No «Запросить сверку» action/tab, no financial writes. Payment titles are «Оплата №…» or «Оплата»; client name and status are separate detail fields, never concatenated.
- StoreActionsHost opens StoreStatementScreen as a shared full-screen read-only dialog. The underlying details/SharedTransition/list remain mounted; saveable filters/expansion/scroll keyed by physical store ID. StatementStoreSaver preserves the full store separately from the unchanged map saver. All existing map/dial/edit-location rules remain.
- data/StoreStatementData reads existing SharedPreferences directly OFF main, without constructing AgentVisitSessions (which would seed/write sessions). Reads agent_visit_requests, visit_returns, agent_visit_sessions and explicit agent_unplanned_visits UUID→storeId links. Identity is never inferred from a name. Local returns preserve reasonLabel/comment snapshots; order/return prices stay exact UZS. Old collection sessions have no payment timestamp; displayed date explicitly says «Дата визита», using startedAt.
- Per-store deterministic sample history supplies orders/payments/returns for prototype preview. Local saved documents are added read-only. No balance/running-total is derived; USD/UZS stay separate. Requests are neutral, labelled «Создана»/«Ожидает выполнения», not posted negative sales. Orders debit red; payments/returns credit green. Current store saldo and all persisted schemas are untouched.
- Filters: operation type, currency, inclusive date/day/range (Tashkent timestamp grouping, StatisticsDates date-picker convention). Filter sheet uses draft until Apply; Reset resets draft. Expandable rows show date/status/client/products/comment. Stable-ID keyed LazyColumn, compact native typography, original icons and soft press only on actions.
- New sources: model/StoreStatement.kt; data/StoreStatementData.kt, StoreStatementSamples.kt; screens/StoreStatementScreen.kt; ui/components/StoreStatementEntry.kt, StoreStatementCard.kt, StoreStatementFilters.kt. StoreStatementTest covers money/signs/currency/date boundaries/physical links; StoreStatementReadTest isolates native JSON reads in UUID-prefixed prefs, verifies corrupted-record skipping and no writes. Actual UI checking remains with the user.

2026-10-28 statement validation: assembleDebug/assembleDebugAndroidTest and 96 unit tests passed (0 failures/errors). Samsung SM_S916N app/test install-r Success; StoreStatementReadTest OK (2 tests). Native tests used only UUID-prefixed preferences; no prototype UI, real records, photos or maps were operated. UI checks remain with the user.


## 22. Statement design refinement — 2026-10-28

User asked for a softer, clearer design based on screenshots. StatementDesign is local charcoal/medium-secondary typography: names15 Semibold, amounts16 Semibold, body13 Medium, dates12 Medium, section labels12 Semibold. No global font/theme changes. Store + calendar period form one compact card INSIDE the keyed history list, so context scrolls away naturally. Count/filter summary is clear; expanded rows now show full document title if truncated in the list.

Filter sheet: light handle, compact grouped choices, subtle animated selected fill/border, top Reset, fixed full-width Apply footer, independently scrollable body for small screens/large fonts. No duplicated “all period” field: explicit Указать даты opens existing date/range picker. Draft/apply/dismiss, IDs, date/currency/type logic, read-only data and underlying visit/store state stay unchanged. No new dependencies or redundant UI-mirroring tests. User performs visual checks.

2026-10-28 statement design validation: assembleDebug and96 unit tests passed (0 failures/errors). Updated app installed with install-r on Samsung SM_S916N. No new data/schema or financial logic changes; no automatic UI navigation. Visual checking remains with the user.
