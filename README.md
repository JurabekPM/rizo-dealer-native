# Dealer app Native — Rizo Dealer prototipi

Native Kotlin / Jetpack Compose UI/UX prototip. Admin va Agent alohida navigatsiyaga ega; ma’lumotlar lokal namuna va SharedPreferences bilan ishlaydi. Dealer backend va haqiqiy login yo‘q.

GitHub: [JurabekPM/rizo-dealer-native](https://github.com/JurabekPM/rizo-dealer-native) — **private**, asosiy branch `main`.

## Yangi developer yoki Gemini uchun

1. [GEMINI.md](GEMINI.md) — tez kirish va asosiy qoidalar.
2. [Loyiha topshirish hujjati](docs/PROJECT_HANDOFF_UZ.md) — hozirgi ekranlar, logika, saqlash va cheklovlar.
3. [Kod xaritasi](docs/PROJECT_MAP.md) — qaysi vazifada qaysi fayl o‘qiladi.
4. [Regressiya ro‘yxati](docs/REGRESSION_CHECKLIST_UZ.md) — o‘zgarishda nimalar saqlanishi kerak.
5. [Geminiga yuboriladigan tayyor matn](docs/GEMINI_START_PROMPT_UZ.txt).

`GEMINI.md` avtomatik o‘qildi deb taxmin qilmang: Android Studio Gemini chatiga shu fayllarni kontekst qilib qo‘shing yoki tayyor matnni yuboring. Kodni o‘qish huquqi bo‘lmasa Gemini taxmin qilmasdan aytishi kerak.

## Tez ishga tushirish

Loyihani Android Studio’da oching, SDK/JBRni mavjud konfiguratsiyadan oling va appni ishga tushiring. Kirishda rol tanlanadi.

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest --offline --no-configuration-cache
```

Oxirgi ilova holati: **2026-10-22, build OK, 57 unit test, 0 failures**, Samsung SM_S916Nga o‘rnatilgan. Telefon UI tekshiruvini foydalanuvchi o‘zi bajaradi.

## Joriy ekranlar

- Admin: Визиты, Заявки, Доставки, Инкассация, Меню; Menu’da Magazin, oyma-oy Reviziyalar va Qaytarishlar.
- Agent: Визиты, Магазины, Статистика, История, Меню.
- Visit: haqiqiy start/finish timestamp, comments/photos, besh result va shared animatsiyalar.
- Qaytarish: umumiy mahsulot/miqdor tanlovi, Возвратитьdan keyin sabab sheeti, lokal yozuvlar, Admin sabab sozlamalari.
- Xarita: ishlaydigan read-only map/call; faqat Admin Menu/Storesda store location tahriri, OSRM route/km/time, drag bottom sheet.

Original dizayn resurslari va litsenziyalar `figma-reference/`, drawables va `app/src/main/assets/map/` ichida. Oldingi funksional tarix o‘rniga yuqoridagi joriy hujjatni o‘qing.

## O‘zgarishlarni solishtirish

```powershell
.\scripts\Compare-HandoffBaseline.ps1
```

Bu source fayllarni topshirish paytidagi SHA-256 manifest bilan solishtiradi; ma’lumotlarni o‘zgartirmaydi. Zaxira ZIP alohida topshiriladi. U source uchun; telefon ichidagi SharedPreferences nusxasi emas.

## GitHub va keyingi o‘zgarishlar

Loyiha Git bilan kuzatiladi. [GitHub ish tartibi](docs/GITHUB_WORKFLOW_UZ.md) — scope tekshiruvi, commit va push qoidalari. Lokal sozlamalar/buildlar/credentials repozitoriyga kiritilmaydi. Bir papkada bir vaqtda bitta agent kod yozadi.
