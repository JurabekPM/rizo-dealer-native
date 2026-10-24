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
2. Bir paytda bitta faol Agent viziti. Boshqasi faol bo‘lsa boshlash rad etiladi. Mavjud/yakunlangan sessionni qayta boshlash yo‘q.
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

- Yangi zayavka faqat faol vizitda. Boshlanmagan vizitda «Начать визит» bilan haqiqiy boshlanish vaqti yoziladi; bir faol vizit qoidasi saqlangan. Yakunlangan vizitda yangi zayavka yaratilmaydi.
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
- «Выбрано: N товаров» bosilsa faqat tanlanganlar; «Все товары» orqali qaytiladi. Qidiruv ikkala rejimda ham ishlaydi, matn va tanlov o‘chmaydi. Alohida LazyListState har rejimning scroll joyini saqlaydi. 0 miqdor tanlanganlar ro‘yxatidan chiqaradi, bo‘sh holatda barcha tovarlarga qaytish bor.
- Продолжить ustida noyob pozitsiyalar soni va barcha tanlanganlarning Long UZS jami turadi. Qidiruv/filtr summani kamaytirmaydi. Tanlov bo‘lmasa jami satri yashirin, Продолжить o‘chiq.
- Tanlangan thumbnailga1dp/60% accent chegara; o‘lcham48 va sampled IO decode saqlangan. selected parametri defaultfalse: forma va saqlangan hujjat rasmlari o‘zgarmaydi.
- Bu foydalanuvchi tasdiqlagan Figma asosidan keyingi yaxshilanishlar. Picker Back hanuz tashqi formani o‘zgartirmaydi; faqat Продолжить draftni qo‘llaydi. Build va66 unit test o‘tdi, qurilma UIini foydalanuvchi tekshiradi.

### Picker: sheet o‘rniga tezkor miqdor

Foydalanuvchi faqat mahsulot ro‘yxatida yangi interactionni tasdiqladi; tanlashdan keyingi zayavka formasi keyingi vazifa, o‘zgartirilmadi. + bosish1 dona qo‘shadi. Narx ostida o‘ngga tekislangan128×40dp joy har doim bor: dastlab o‘ngdagi40dp + ko‘rinadi; tanlanganda chapdagi88dp ichida −/son chiqadi. + ayni koordinatada qoladi, nom/narx to‘liq kenglikda va qator balandligi o‘zgarmaydi. Sonning o‘zi bulk RequestQuantitySheetni ochadi; butun qatorga click yo‘q. Eski plus ustidagi badge o‘rniga counter ishlaydi; nozik thumbnail border, tanlanganlar filtri, jami va query saqlangan.

stepRequestPickerQuantity har bosishda oxirgi draftdan yangi list qaytaradi, stock va0 chegaralarini saqlaydi; tez takroriy tap eskirgan row miqdorini qayta ishlatmaydi. Minus0 tanlanganlar ro‘yxatidan chiqaradi va jami yangilanadi. Sheet faqat tasdiqlanganda draftni o‘zgartiradi; tashqi forma hanuz faqat Продолжить bilan yangilanadi. Inline +/− bosishda qidiruv focus/IME o‘z holatida qoladi; bulk sheet yopilgach IME avtomatik qidiruvga qaytmaydi. Search ochilgandagi dastlabki fokus qolgan. RequestProductCard/review, Return va Admin buyurtmalari o‘zgarmagan.

Inline miqdor tekshiruvi: debug build va 68 unit test muvaffaqiyatli, 0 xato. Qurilma UIini foydalanuvchi tekshiradi.
