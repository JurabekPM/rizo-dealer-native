# GitHub bilan ishlash tartibi

GitHub egasi: `JurabekPM`. Repozitoriy nomi: `rizo-dealer-native`.
Asosiy branch: `main`. Repozitoriy private qilib yaratildi: https://github.com/JurabekPM/rizo-dealer-native . Lokal `origin` shu repozitoriyga ulangan; `main` → `origin/main`ni kuzatadi.

## Har vazifa

1. Ish boshlashdan oldin `git status` va joriy branchni tekshiring. Boshqa agent yoki foydalanuvchining o‘zgarishlarini yo‘qotmang.
2. Bir papkada bir paytda bitta agent kod yozsin. Parallel vazifalar uchun alohida branch/worktree kerak.
3. Faqat vazifaga tegishli o‘zgarishni bajaring; build va zarur testlarni o‘tkazing.
4. `git diff`ni tekshiring. Lokal sozlamalar, tokenlar, signing keys, buildlar yoki begona o‘zgarishlar commitga kirmasin.
5. Foydalanuvchi ushbu loyihadagi yakunlangan o‘zgarishlarni commit qilib, shu GitHub repozitoriysiga push qilishni so‘ragan. Alohida taqiq bo‘lmasa vazifa yakunida shu ishni bajaring.
6. Aniq mazmunli commit nomi yozing; tugallanmagan yoki boshqa odamning o‘zgarishlarini o‘zboshimchalik bilan commit qilmang.
7. Push natijasini tekshirib, qisqa commit ID va havolani bildiring. Push bajarilmasa “GitHubga yuklandi” demang.

## Muhim

- Force push, tarixni qayta yozish, `reset --hard`, loyiha yoki branchlarni o‘chirish oddiy ish tartibiga kirmaydi.
- Hisobga kirish yoki credentials muammosini token/parolni faylga yozish, chatga chiqarish yoki Gitga qo‘shish bilan hal qilmang.
- `.gitignore` build cache, APK, local.properties, .idea, .env va signing materiallarni chiqarib tashlaydi. `gradle-wrapper.jar`, loyiha source, testlar, original resurslar va docs Gitda saqlanadi.
- GitHubga push telefon ma’lumotlarining backupi emas. SharedPreferences qurilmada qoladi.
- Lokal kodni yangi manbadan yangilashda avval `git status`; commit qilinmagan o‘zgarish bo‘lsa merge/rebase bilan ustidan yozishga shoshilmang.
- Git hujjatlari manbani almashtirmaydi. Asosiy qoidalar `AGENTS.md`, `GEMINI.md` va `PROJECT_HANDOFF_UZ.md`da.
