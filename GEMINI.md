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

Mahsulot tanlash oynasi keyin Figma eksportidagi aniq qiymatlarga qayta moslandi: ichki X, done_all, narx+mavjudlik, badge, chegirma va asl5 rasm. RequestPickerDesign qidiruv/body uchun Regularni saqlaydi; keyingi foydalanuvchi tuzatishi: mahsulot nomi Medium15/20, sonli narx Semibold16/22, UZS/шт13/18 kulrang. В наличии yozuvi yo‘q; faqat tugaganda Нет в наличии va + yo‘q. Boshqa ekranlarni o‘zgartirmang. Request katalog22 pozitsiya, eski20 indeks saqlangan. Yangi unit test bilan jami63 ta o‘tdi.

Pickerning keyingi tasdiqlangan yaxshilanishlari: mavjud mahsulotlar avval, tugaganlar oxirida; «Выбрано» tanlanganlar/barcha mahsulotlar rejimini almashtiradi. Har rejim o‘z scroll joyini, qidiruv va draft miqdorlarini saqlaydi. Продолжить ustida barcha tanlangan pozitsiyalar soni va price×qty jami, qidiruv natijasigagina cheklanmaydi. Faqat pickerda tanlangan thumbnail1dp xira binafsha chegara oladi; boshqa kartalar o‘zgarmaydi. requestPickerIndexes / requestPickerTotal — RequestCatalog.kt. Build va66 unit test o‘tdi; UI tekshiruvi foydalanuvchida.

Picker miqdori endi inline: + darhol1 dona, tanlanganda narx ostida −/son/+ chiqadi. Faqat sonni bosish bulk miqdor sheetini ochadi; product qatori o‘zi bosilmaydi. Har tap oxirgi draftdan hisoblanadi (stepRequestPickerQuantity),0 tanlovdan chiqaradi, stock cap saqlanadi. Qo‘shishda qidiruv klaviaturasi yig‘iladi; bulk sheet yopilgach qidiruv klaviaturasi majburan qayta ochilmaydi. Keyingi zayavka review oynasi bu vazifada o‘zgartirilmadi.

Inline miqdor tekshiruvi: debug build va 68 unit test muvaffaqiyatli, 0 xato. Qurilma UIini foydalanuvchi tekshiradi.
