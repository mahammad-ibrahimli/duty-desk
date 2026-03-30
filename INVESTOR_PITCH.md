## DutyDesk – Rəqəmsal Növbə və Əməkdaş İdarəetmə Platforması

DutyDesk, növbə ilə işləyən komandaların (call-center, müştəri dəstəyi, təhlükəsizlik, istehsalat və s.) **növbə cədvəllərini, davamiyyətini və təhvil–təslim prosesini** mərkəzləşdirilmiş şəkildə idarə edən veb platformadır. Məqsəd, menecerlər üçün idarəetməni sadələşdirmək, əməkdaşlar üçün isə daha şəffaf və ədalətli iş qrafiki təmin etməkdir.

### Problemin təsviri
- **Əl ilə planlaşdırma**: Növbə cədvəlləri çox vaxt Excel və ya kağız üzərindən aparılır, bu isə tez-tez səhvlərə, ziddiyyətli qrafiklərə və itirilən məlumatlara səbəb olur.
- **Şəffaflığın olmaması**: Əməkdaşlar öz növbələrini, dəyişiklikləri və statuslarını rahat şəkildə izləyə bilmirlər.
- **Təhvil–təslimdə boşluqlar**: Növbə dəyişəndə şifahi və ya qeyri-strukturlaşdırılmış məlumat ötürülməsi, sonrakı növbədə itkilərə və xidmət keyfiyyətinin düşməsinə gətirir.
- **Analitika çatışmazlığı**: Menecerlərin komanda yükünü, aktiv növbələri, gecikmələri və resurs istifadəsini real vaxt izləməsi çətindir.

DutyDesk bu problemləri **vahid, təhlükəsiz və analitik yönümlü platforma** ilə həll edir.

---

## Əsas funksionallıq (Biznes baxışı)

- **Rollar və səlahiyyətlər**
  - **Admin / Supervisor**: Komandaları yaradır, əməkdaşları əlavə edir, rolları (ADMIN, SUPERVISOR, EMPLOYEE) təyin edir, qrafikləri planlayır və sistem üzərindən bütün statistikaya çıxış əldə edir.
  - **Employee (işçi)**: Öz növbələrini, davamiyyət tarixçəsini, təhvil–təslim qeydlərini görür və şəxsi məlumatlarını idarə edir.

- **Növbə planlaşdırma və cədvəl**
  - Həftəlik və aylıq növbə cədvəllərinin avtomatik və ya manual yaradılması.
  - Komanda üzrə **gündüz / axşam / gecə** növbələrinin təyin edilməsi.
  - Növbələrin **SCHEDULED, ACTIVE, CANCELLED** kimi statuslarla idarə edilməsi.

- **Davamiyyət və check-in/out**
  - İşçinin növbəyə **daxil olması (check-in)** və **çıxışı (check-out)**, statuslarla birlikdə qeyd olunur.
  - Gecikmələr, tamamlanmamış növbələr və digər hallar haqqında məlumat toplamaq mümkündür.

- **Təhvil–təslim (Handover) modulu**
  - Növbə sonu görülən işlər, açıq qalan tapşırıqlar və kritik qeydlər strukturlaşdırılmış formada növbəti əməkdaşa ötürülür.
  - **HandoverStatus** vasitəsilə təhvil–təslim prosesinin tamamlanma vəziyyəti izlənilir.

- **Admin panel və dashboard**
  - Ümumi **Dashboard**: aktiv növbələrin sayı, sistemdəki istifadəçi sayı, gözləyən təhvil–təslimlər və s.
  - Komanda üzrə statistikalar: hər komanda üçün əməkdaş sayı, aktiv növbə sayı, komanda məlumatları.
  - İstifadəçilərin filtrelənməsi: rol, komanda, status, axtarış sözü ilə istifadəçilərin siyahılanması.

- **Axtarış və filtr funksiyaları**
  - E-poçt, komanda, status, tarix intervalı və s. üzrə növbələrin və istifadəçilərin filtr olunması.
  - Həftə formatında (`YYYY-Www`) cədvəllərin alınması (məsələn, 2026-W11).

---

## Texniki memarlıq və istifadə olunan texnologiyalar

- **Backend stack**
  - **Java 21** – Müasir, performanslı JVM platforması.
  - **Spring Boot 3.3.5**
    - `spring-boot-starter-web` – RESTful API-lər üçün.
    - `spring-boot-starter-data-jpa` – ORM və database əməliyyatları üçün.
    - `spring-boot-starter-security` – autentifikasiya və avtorizasiya üçün.
    - `spring-boot-starter-actuator` – monitorinq və sağlamlıq (health) endpoint-ləri üçün.
    - `spring-boot-starter-validation` – request səviyyəsində data validasiyası üçün.
  - **Spring Security + JWT (io.jsonwebtoken)** – Token əsaslı autentifikasiya (stateless API-lər üçün).
  - **Spring Session JDBC** – Server tərəfli sessiya idarəçiliyi (ehtiyac olduqda).
  - **Springdoc OpenAPI** – API-lərin avtomatik sənədləşdirilməsi və Swagger UI.

- **Database və migration**
  - **PostgreSQL** – Əsas relasion verilənlər bazası.
  - **Spring Data JPA** – Repository-lər (`UserRepository`, `ShiftRepository`, `TeamRepository`, `HandoverRepository` və s.) ilə entity-lərin idarə olunması.
  - **Flyway** – Schema migration-lar üçün; mərhələli, nəzarətli dəyişikliklər.

- **Digər texnologiyalar**
  - **Lombok** – Entity və DTO-larda boilerplate kodun (getter/setter, builder və s.) azaldılması.
  - **DevTools** – Development mərhələsində auto-reload və daha sürətli dövr üçün.

- **Layihə quruluşu (qısa)**
  - `controller` – `AdminController`, `AuthController`, `UserController` və s. – REST endpoint-lər.
  - `service` və `service.impl` – Biznes məntiqi (məsələn, `ShiftService`, `HandoverService`, `TeamService`).
  - `repository` – JPA repository-lər, database əməliyyatları.
  - `entities` – `User`, `Team`, `Shift`, `ShiftNote`, `PasswordResetToken` və s. domen modelləri.
  - `dto.request` / `dto.response` – API üçün request/response modelləri (məsələn, `CreateUserRequest`, `GenerateScheduleRequest`, `AdminDashboardResponse`).
  - `enums` – `Role`, `ShiftType`, `ShiftStatus`, `HandoverStatus` və s.
  - `config` və `securty` – `SecurityConfig`, `JwtService`, `JwtAuthenticationFilter`, `AppConfig`, `JacksonConfig`, `DataInitializer` və s. konfiqurasiya və infrastruktur sinifləri.

---

## Təhlükəsizlik və data keyfiyyəti

- **Autentifikasiya**
  - İstifadəçilər Spring Security və JWT əsasında sistemə login olur.
  - `JwtAuthenticationFilter` request-lərdə token-i yoxlayır və istifadəçini kontekstə əlavə edir.

- **Avtorizasiya və rollar**
  - `@PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR')")` vasitəsilə admin endpoint-lərə yalnız səlahiyyətli istifadəçilər keçə bilir.
  - `Role` enum-u sistemdəki səlahiyyət modelləşdirməsini təmin edir.

- **Validasiya**
  - `jakarta.validation` annotasiyaları ilə request-lər sahə səviyyəsində yoxlanılır.
  - Məsələn, `CreateUserRequest`-də:
    - `@Email` – e-poçt formatı üçün.
    - `@NotBlank` / `@NotNull` – məcburi sahələr üçün.
    - `@Pattern("^\\+994\\d{9}$")` – telefon nömrəsinin `+994XXXXXXXXX` formatında olmasını məcbur edir.

---

## Miqyaslana bilmə və gələcək inkişaf istiqamətləri

- **Miqyaslana bilən backend**
  - Stateless REST API + JWT autentifikasiyası horizontal miqyaslanmanı (bir neçə instansiya) asanlaşdırır.
  - PostgreSQL və Flyway ilə böyük həcmli verilənlərlə işləmək mümkündür.

- **Gələcəkdə əlavə oluna biləcək funksiyalar**
  - Mobil tətbiq (Android/iOS) üçün public API-lərin genişləndirilməsi.
  - Növbə optimizasiyası üçün **rule-based** və ya **AI əsaslı** planlaşdırma modulu.
  - Komanda performansı haqqında daha dərin dashboard və BI inteqrasiyaları.
  - Xarici sistemlərlə inteqrasiya (HRM, payroll, ticketing və s.).

---

## İnvestor üçün dəyər təklifi

- **Bazar ehtiyacı**: Növbə ilə işləyən bizneslərdə (xüsusilə xidmət sektorunda) əməliyyatların rəqəmsallaşdırılmasına böyük ehtiyac var.
- **ROI potensialı**:
  - Planlama və koordinasiyada itən iş saatlarının azaldılması.
  - Daha düzgün resurs paylaşımı hesabına işçi xərclərinin optimallaşdırılması.
  - Təhvil–təslimdə itən məlumatlara görə yaranan xidmət keyfiyyəti itkisinə mane olmaq.
- **Texniki baza**: Müasir, geniş istifadə olunan enterprise texnologiyalar üzərində qurulduğu üçün:
  - Komanda tapmaq və böyütmək nisbətən asandır.
  - Sistem uzunmüddətli inkişaf üçün dayanıqlıdır.

DutyDesk, növbə mədəniyyətini daha **şəffaf, ölçülə bilən və idarə oluna bilən** hala gətirərək həm menecerlərə, həm də əməkdaşlara real dəyər təqdim etməyi hədəfləyir.

