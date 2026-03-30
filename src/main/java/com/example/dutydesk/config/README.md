# `config` qovluğu nə üçündür?

Bu qovluq layihənin **ümumi konfiqurasiya** siniflərini saxlayır.
Burada biznes məntiqi yazılmır; məqsəd framework davranışını tənzimləməkdir.

---

## Siniflər və rolu

### 1) `AppConfig`
**Nə üçündür:**
- Ümumi bean-lərin qeydiyyatı üçün mərkəzi config sinfi.

**Necə olmalıdır:**
- `@Configuration` annotasiyası olmalıdır.
- Lazım olan helper bean-lər (`Clock`, `ModelMapper` və s.) burada verilə bilər.
- Environment-ə bağlı dəyərlər `@Value` və ya `@ConfigurationProperties` ilə idarə olunmalıdır.

---

### 2) `CorsConfig`
**Nə üçündür:**
- Frontend ilə backend arasında CORS qaydalarını idarə edir.

**Necə olmalıdır:**
- `@Configuration` olmalıdır.
- İcazəli origin, method, header-lər burada tənzimlənməlidir.
- Production-da `*` yerinə konkret domain-lər verilməlidir.

---

### 3) `WebMvcConfig`
**Nə üçündür:**
- MVC səviyyəsində əlavə davranışlar (interceptor, formatter, locale və s.) üçün.

**Necə olmalıdır:**
- `@Configuration` + `WebMvcConfigurer` implement edə bilər.
- Məsələn, global formatter və interceptor qeydiyyatı burada edilir.

---

### 4) `JacksonConfig`
**Nə üçündür:**
- JSON serializasiya/deserializasiya davranışını standartlaşdırır.

**Necə olmalıdır:**
- `@Configuration` olmalıdır.
- `ObjectMapper` üçün tarix formatı, null handling və naming strategy kimi qaydalar burada verilir.
- API response formatı sabit qalmalıdır.

---

### 5) `OpenApiConfig`
**Nə üçündür:**
- Swagger / OpenAPI sənədləşdirmə konfiqurasiyası.

**Necə olmalıdır:**
- `@Configuration` olmalıdır.
- API title, version, description, security scheme (JWT) burada yazılır.
- Endpoint-lərin test üçün görünən və oxunaqlı olmasını təmin edir.

---

## Qısa qayda
- Config siniflərində biznes logic yazma.
- Environment fərqlərini (`dev`, `prod`) property faylları ilə idarə et.
- Security ilə bağlı parametrləri `SecurityConfig` ilə ziddiyyət yaratmadan ver.
