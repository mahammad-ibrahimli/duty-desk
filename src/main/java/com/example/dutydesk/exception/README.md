# `exception` qovluğu nə üçündür?

Bu qovluq tətbiqin xəta idarəetmə qatıdır.
Məqsəd: xətaları **mənalı**, **standart** və frontend üçün **oxunaqlı** formatda qaytarmaq.

---

## Siniflər və rolu

### 1) `BadRequestException`
**Nə üçündür:**
- Yanlış input, qayda pozuntusu və ya uyğun olmayan request üçün.

**Necə olmalıdır:**
- `RuntimeException`-dan törəməlidir.
- Mesaj qəbul edən constructor olmalıdır.
- `400 BAD_REQUEST` ilə map edilməlidir.

---

### 2) `ConflictException`
**Nə üçündür:**
- Mövcud vəziyyətlə konflikt olduqda (məs: artıq mövcud email).

**Necə olmalıdır:**
- `RuntimeException`-dan törəməlidir.
- `409 CONFLICT` ilə qaytarılmalıdır.

---

### 3) `UnauthorizedException`
**Nə üçündür:**
- Kimlik doğrulama və ya token problemi olduqda.

**Necə olmalıdır:**
- `RuntimeException`-dan törəməlidir.
- `401 UNAUTHORIZED` ilə map edilməlidir.

---

### 4) `ResourceNotFoundException`
**Nə üçündür:**
- Soruşulan resurs tapılmadıqda (user, shift, handover və s.).

**Necə olmalıdır:**
- `RuntimeException`-dan törəməlidir.
- `404 NOT_FOUND` ilə map edilməlidir.

---

### 5) `ValidationException`
**Nə üçündür:**
- Bir və ya bir neçə sahə validasiya qaydasını pozduqda.

**Necə olmalıdır:**
- `RuntimeException`-dan törəməlidir.
- Xəta detalları (`field -> message`) daşıya bilər.
- `422 UNPROCESSABLE_ENTITY` (və ya komanda qərarına görə `400`) qaytarıla bilər.

---

### 6) `GlobalExceptionHandler`
**Nə üçündür:**
- Bütün exception-ları mərkəzləşdirilmiş formada tutmaq və standart response qaytarmaq.

**Necə olmalıdır:**
- `@RestControllerAdvice` olmalıdır.
- `@ExceptionHandler` metodları ilə yuxarıdakı custom exception-ları ayrıca idarə etməlidir.
- Son fallback üçün `Exception.class` handler olmalıdır (`500 INTERNAL_SERVER_ERROR`).
- Response formatı sabit olmalıdır: `success`, `error.code`, `error.message`, `error.details`.

---

## Qısa qayda
- Controller içində `try/catch` ilə hər şeyi tutma, custom exception at.
- HTTP status mapping-ləri bir yerdə (`GlobalExceptionHandler`) olsun.
- Frontend üçün xətalar həmişə eyni JSON formatda qaytarılsın.
