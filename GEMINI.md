# Dealer app Native — Gemini uchun kirish yo‘riqnomasi

Yangilangan: **2026-10-22, Asia/Tashkent**.

Bu mavjud Android loyihasini davom ettirish uchun yo‘riqnoma. Ilovani qaytadan qurish topshirig‘i emas.
Foydalanuvchi bilan **o‘zbekcha**, ilova ichidagi mavjud yozuvlarda esa **ruscha** ishlang.

## Avval o‘qing

1. `AGENTS.md` — qisqa loyiha qoidalari va buzilmasligi kerak bo‘lgan talablar.
2. `docs/PROJECT_HANDOFF_UZ.md` — ekranlar, ma’lumotlar, logika, cheklovlar.
3. `docs/PROJECT_MAP.md` — vazifaga mos kod fayllarini topish uchun xarita.
4. `docs/REGRESSION_CHECKLIST_UZ.md` — o‘zgarishdan keyin aynan nima tekshiriladi.

Hamma kodni har safar o‘qimang: shu hujjatlardan tegishli oqimni toping, so‘ng o‘zgartiriladigan fayl, uning chaqiruvchilari, modeli va testlarini o‘qing.
Bu fayl Android Studio tomonidan avtomatik o‘qilgan deb taxmin qilmang. Kerak bo‘lsa foydalanuvchidan uni chat kontekstiga qo‘shishni so‘rang.

## Ish tartibi

- Avval vazifani tushuning, mavjud ishlashni koddan aniqlang; keyin eng kichik zarur o‘zgarishni qiling.
- Yangi ekran `screens/` ichida alohida `.kt`; umumiy UI `ui/components/`; ma’lumotlar `data/`; qoidalar `model/`.
- Admin va Agent boshqa navigatsiyaga ega. Umumiy komponent o‘zgarsa **ikkala rolga** ta’sirini tekshiring.
- Foydalanuvchi boshqacha demagan joyda Figma/mavjud tasdiqlangan dizaynni saqlang. Rang, o‘lcham, radius, ikonka yoki shriftni taxmin bilan almashtirmang.
- Hozirgi dizaynda aniq nomlar Semibold, yordamchi yozuvlar Medium. Eski Figma izohlaridagi Regular vaznini avtomatik qaytarmang.
- Faqat ishlaydigan card/buttonlarda `softCardPress` ishlating; global ripple/hover qo‘shmang.
- Ro‘yxat scrolli, qidiruv, tab, pager, Back va shared animation kalitlarini saqlang.
- `SharedPreferences` nomlari/JSON maydonlari va store/visit ID larini migratsiyasiz o‘zgartirmang. Ma’lumotlarni tozalash bilan muammoni yashirmang.
- Backend, DI, yangi navigatsiya kutubxonasi, dependencies yoki keng refaktorni alohida topshiriqsiz qo‘shmang.
- Kichik vazifani tez bitiring, lekin build va zarur tekshiruvni o‘tkazib yubormang. Natijani 2–5 qisqa gapda o‘zbekcha ayting.

## Eng muhim joriy holat

- Admin: Визиты / Заявки / Доставки / Инкассация / Меню.
- Agent: Визиты / Магазины / Статистика / История / Меню. Eski Home hozir Статистика.
- Agent vizit vaqti «Начать визит» bosilgan paytdan boshlanadi; 00:00 dan emas. Yakunlanganini qayta boshlatmang.
- Agent yangi qaytarishni faol vizit ichida qiladi. Admin ham shu umumiy qaytarish formasidan foydalanadi.
- Sabab alohida forma kartasi emas: tovarlardan keyin «Возвратить» bosilsa sheet; yakuniy tasdiqdan keyingina saqlash.
- Admin Menu → Возвраты → oy → hujjatlar → details. «Причины возврата» **shu Возвраты ichida**; menyuda alohida emas.
- Agent История → Ревизия: oy kartasida reviziyalar **soni**. Oy ichida do‘kon nomi/manzili/saldo summasi; «Сальдо магазина» yozuvi **yo‘q**. Admin reviziya kartasida yozuv saqlangan.
- Agent История: «История инкассации»; «План заявки» ko‘rinmaydi, «История продаж» qolgan.
- Xarita Agentda ishlaydi, ammo manzil tahriri yo‘q. Faqat Admin Menu → Магазин xaritasida qalamcha bilan tahrir bor. Eski “Agent xaritasi ochilmaydi” talabi eskirgan.

## Tekshirish

Windows PowerShell, loyiha ildizida:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest --offline --no-configuration-cache
```

2026-10-22 topshirish holati: **build muvaffaqiyatli, 57 unit test, 0 xato**, APK Wi-Fi ulangan Samsung SM_S916N ga o‘rnatilgan.
Bu barcha UI oqimlari qurilmada avtomatik tekshirildi yoki FPS o‘lchandi degani emas. Foydalanuvchi ekranlarni o‘zi tekshiradi.
ADB transport raqamini har safar qayta aniqlang; eski 1/8 raqamiga ishonmang.

Kod manba haqiqatidir; hujjat undan farq qilsa avval tekshiring. Yangi topshiriq oldingi talabni almashtirishi mumkin; eskisini bilib-bilmasdan qaytarmang.

## GitHub workflow — 2026-10-22

This project now uses Git. See `docs/GITHUB_WORKFLOW_UZ.md`.
The user authorized committing and pushing completed, task-scoped changes to the project's own GitHub repository after validation. Check `git remote -v`, branch, status and diff first. Do not include unrelated changes, credentials, device settings or build output. Do not force-push or erase history. Report actual push success or the remaining blocker. One writer per checkout; parallel agents use separate worktrees.

## 2026-10-24: Agent Заявка

Agent → Визиты → do‘kon → Заявка yaratish qo‘shilgan; yangi navigation tabi yo‘q. Faqat faol vizitda saqlash/tahrirlash, yakunlanganda read-only. AgentVisitSessions.requests / agent_visit_requests lokal hujjati; save Request resultini qo‘shadi, vizitni yakunlamaydi. Picker tranzaksion, history/sales o‘z record summalari va yaratilgan sanasidan foydalanadi. Tafsilot: handoff §11. Joriy build va 62 test o‘tdi; UI sinovini foydalanuvchi qiladi.

Mahsulot tanlash oynasi keyin Figma eksportidagi aniq qiymatlarga qayta moslandi: ichki X, done_all, narx+mavjudlik, badge, chegirma va asl5 rasm. RequestPickerDesign qidiruv/body uchun Regularni saqlaydi; keyingi foydalanuvchi tuzatishi: mahsulot nomi Medium15/20, sonli narx Semibold16/22, UZS/шт13/18 hozir narx bilan bir xil qora. В наличии yozuvi yo‘q; faqat tugaganda Нет в наличии va + yo‘q. Boshqa ekranlarni o‘zgartirmang. Request katalog22 pozitsiya, eski20 indeks saqlangan. Yangi unit test bilan jami63 ta o‘tdi.

Pickerda mavjud mahsulotlar avval, tugaganlar oxirida. Yuqoridagi done_all/«Выбрано»/«Показать» qatori va selected-only UI filtri foydalanuvchi talabiga ko‘ra olib tashlandi; qidiruv va draft saqlangan. Faqat pastda doimiy son va jami (0 ham) turadi, shu sabab birinchi/oxirgi tanlovda list viewporti sakramaydi. Продолжить cart reviewga o‘tadi; yana Добавить orqali tanlovni davom ettirish mumkin. Thumbnail1dp chegara va butun draftdan Long total saqlangan.

Picker: rasm48/nom max2 qator/chegirma yuqorida; narx va120×40dp counter alohida to‘liq kenglikdagi pastki qatorda (tor title ustunida emas). Narx16/22 va UZS/шт13/18 bir xil qora. Bo‘sh, mavjud cardning istalgan joyi yoki + bosilsa1 dona; tanlanganda faqat counter/son bosiladi. + doim40dp va22dp belgi, joyi o‘zgarmaydi. Fon300ms chapga yoyilib/yig‘iladi, prefix fade/slide draw-only; son40×40 slotda markazda, SizeTransform yo‘q, increase yuqoriga/decrease pastga320ms. Faqat son bulk sheetni ochadi. Step latest draft va stock cap; IME inline tapda o‘zgarmaydi. Post-selection zayavka review va Admin/Return o‘zgarmagan.

Inline miqdor tekshiruvi: debug build va 68 unit test muvaffaqiyatli, 0 xato. Qurilma UIini foydalanuvchi tekshiradi.

## 2026-10-26: Qaytarish mahsulot tanlashi ham yangi uslubda

ReturnProductPickerScreen Agent Visit/Return, Admin Visit/Return va Admin Menu/Returns/Create uchun bitta umumiy oyna. Request bilan Header/Footer/Card/Counter/Thumbnail/QuantitySheet ko‘rinishi qayta ishlatiladi; PickerProduct faqat readonly UI contract, qaytarish stock=available/barcode=id. Hech qachon Request stock/normalizerini Returnga ishlatmang. Return draft+step+total o‘z normalizeridan; bulk maxDigits2 va stockLabel=Можно вернуть. Manba sana/originalQuantity/qoldiq ko‘rinadi. Bo‘sh tanlovni Подтвердить bilan qaytarish saqlangan (cartni tozalash); Request Continue esa0da o‘chiq. Back/dismiss tashqi formani o‘zgartirmaydi. VisitReturnScreen, reason/save/session/history va tanlashdan keyingi formalar o‘zgarmagan. Qurilma UIini foydalanuvchi tekshiradi.

2026-10-26 validation: assembleDebug and 70 unit tests passed, 0 failures. User performs device UI checks.

## 2026-10-26: Внеплановые va visit scroll

Handoff §12 va figma-reference/agent-unplanned/ ni o‘qing. Agent Visits → Внеплановые → Визит/Заявка/Инкассация → physical store tanlash. Startdan oldin yozuv yo‘q; actual Start yangi unplanned-UUID session va agent_unplanned_visits snapshot yozadi. Eski store/session IDni qayta ishlatib ma’lumotni bosmang. Turli do‘konlarda parallel active vizitlar mumkin; finished/no-work qoidalari saqlangan. Optional collectionAmount UZS, active-only; save visitni tugatmaydi. History/physical-store filters/monthly sales yangi IDlarni ham ko‘radi. Agent map readonly.

Visit icons barcha operationlarda bir xil badge; faqat yozilgan results shared. Operation arrows olib tashlangan, menu arrows qolgan. VisitsListState AgentScreen’da, fixture ID tartibi saveable, card enrichment memoized. SharedTransitionLayout har doim shu hierarchyda qoladi;600ms keyin faqat preparedId/shared modifierlar bo‘shaydi. LazyListState lookahead ichida bo‘lgandan keyin hostni olib tashlamang: bu scroll hisobini to‘xtatadi. Oldingi logika va shared ko‘rinishlarni tasodifan qaytarmang. UI sinovi foydalanuvchining o‘zida.

2026-10-26 final validation: assembleDebug and 76 unit tests passed (0 failures, 0 errors); installed to Samsung SM_S916N with install -r. Device UI/FPS checks remain for the user.

## Vizit scroll tuzatishi — 2026-10-26

Handoff §12 oxiridagi sabab va evidencega qarang. Timerdan keyin SharedTransitionLayoutni butunlay olib tashlash noto‘g‘ri edi: LazyListState lookahead holati saqlanib scroll to‘xtab qolgan. Doimiy hostni saqlang, faqat shared ID/modifierlarni600ms keyin bo‘shating; movable list/reparenting yo‘q. Oldingi76 unit test va Samsungda2 isolated Android regression o‘tdi. Oldingi APKda shu test real forward-scroll xatosini takrorladi. Foydalanuvchi ruxsati bilan haqiqiy Agent card/request/Back va Phone Link scroll ham tekshirildi; session/preferences almashtirilmadi.

## 2026-10-27: vizit fotosi va HH:mm:ss

Faol vizitdagi Добавить фото to‘g‘ridan-to‘g‘ri kamerani ochadi; galereya/source sheet olib tashlangan. VisitPhotoAdder: TakePicture/vaqtinchalik per-file grant, saveable pending URI. VisitCameraPhotos filesDir/visit_photos/camera va cheklangan exported=false FileProvider bilan full-resolution rasm saqlaydi. Cancel/empty/errorda result/foto yozilmaydi;6-limit va active-only/history readonly saqlangan. KameraX/dependencies/runtime camera permission qo‘shmang. formatVisitElapsed: doim00:00:00, actual elapsed millis,24soatda wrap yo‘q; finished/history status qatorlarini bu task uchun o‘zgartirmang. Handoff §13.79 unit+3 native storage/contract testi o‘tdi, real kamera UI/shutter sinovi foydalanuvchiga qolgan.

## 2026-10-27: parallel vizitlar va yangi statuslar

Foydalanuvchi global bitta-active cheklovini bekor qildi. Turli do‘konlardagi bir nechta session active bo‘lishi va alohida yakunlanishi mumkin; vaqt/izoh/rasm/result shu visit IDda. Bir physical storega ikki active session yo‘q; finished ID qayta boshlanmaydi. activeVisitIds/activeStoreIds to‘plamlar, singular activeStoreId biznes qoidasi yo‘q. UI: Новый (gray), Активный (blue), Завершено/Попытка saqlangan. NotVisited enum nomi eski fixture/caller mosligi uchun saqlangan, UI Не посещено ko‘rsatmaydi. VisitPhotoAdderda galereya kodi yo‘q, Add to‘g‘ridan-to‘g‘ri kamera; eski photos o‘chirilmaydi.82 unit+5 native (2 isolated parallel-store/session +3 camera storage) o‘tdi. Handoff §14.
