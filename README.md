# Mechanical Keyboard Pro

هيكل مشروع Android (Kotlin + Jetpack Compose + MVVM) لتطبيق يحوّل الهاتف إلى
كيبورد ميكانيكي احترافي يتصل بجهاز الحاسوب.

## كيف تفتحه
1. افتح المجلد `MechanicalKeyboardPro` كـ Project في Android Studio (Koala أو أحدث).
2. اترك Gradle يزامن المكتبات (Compose BOM 2024.06، navigation-compose، datastore...).
3. شغّل على جهاز/محاكي بـ Android 8.0 (API 26) فأعلى.
4. أول تشغيل يعرض شاشة Splash المتحركة → شاشة الاتصال → شاشة الكيبورد الرئيسية
   (زر "متابعة" في شاشة الاتصال ينقلك مباشرة للتجربة قبل ربط النقل الفعلي).

## بنية المجلدات (كما في المواصفات)

```
app/src/main/java/com/mkpro/keyboard/
├── core/
│   ├── connection/   # KeyboardTransport (واجهة مجردة) + BluetoothHid/Wifi + ConnectionManager
│   ├── keyboard/     # KeyModel + StandardLayout (QWERTY)
│   ├── layers/       # Layer + LayerManager (تبديل فوري)
│   ├── macro/        # Macro/MacroStep + MacroEngine (تسجيل/تنفيذ)
│   ├── profiles/     # Profile + ProfileManager (تبديل تلقائي حسب التطبيق)
│   ├── rgb/          # RgbController (Static/Breathing/Wave/Rainbow...)
│   └── settings/     # AppSettings + SettingsRepository (DataStore)
├── domain/
│   ├── model/        # CustomLayout (مخرجات مصمم الكيبورد المخصص لاحقًا)
│   └── usecase/       # منطق يجمع أكثر من core/ manager
├── data/local/        # نقطة تخزين محلي (Room لاحقًا) خلف واجهة ثابتة
├── ui/
│   ├── theme/         # ألوان RGB + Typography + Material3 dark theme
│   ├── navigation/    # NavGraph: Splash → Connection → Keyboard
│   ├── components/    # KeyCap, CommandBar (قابلة لإعادة الاستخدام)
│   └── screens/       # splash/ connection/ keyboard/ (كل شاشة + ViewModel خاص بها)
└── common/            # ثوابت مشتركة
```

## المبدأ المعماري
- **لا منطق أعمال داخل الـ UI**: كل شاشة تقرأ StateFlow من ViewModel، والـ ViewModel
  يقرأ فقط من core/domain managers.
- **طبقة الاتصال مجردة**: `KeyboardTransport` هي العقد الوحيد؛ إضافة USB HID أو
  WebSocket أو TCP/UDP لاحقًا = تنفيذ الواجهة وتسجيلها في `ConnectionManager`،
  بدون أي تعديل على الشاشات.
- **الطبقات (Layers) بيانات وليست UI**: `KeyboardScreen` يعرض أي `List<List<KeyModel>>`
  يُعطى له، لذلك إضافة طبقة Function/Gaming/Programming لاحقًا = تعريف صفوف
  مفاتيح جديدة فقط.

## الحالة الحالية مقابل المواصفات الكاملة
تم بناء الأساس الحقيقي القابل للتشغيل: Splash المتحرك، التنقل الكامل بين
الشاشات، الكيبورد القياسي التفاعلي مع CommandBar، وكل الـ core managers
كواجهات ونماذج بيانات جاهزة للتوصيل. الأجزاء المعلّمة بـ `TODO` (تنفيذ
Bluetooth HID الفعلي عبر `BluetoothHidDevice`، USB HID، مصمم الكيبورد
بالسحب والإفلات، محرك الذكاء الاصطناعي للاقتراحات، نظام الثيمات القابلة
للتنزيل) هي الخطوة التالية الطبيعية فوق هذا الهيكل — وكل واحدة منها
تدخل في مجلدها المخصص أعلاه دون الحاجة لإعادة هيكلة الباقي.
