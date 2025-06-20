# 📄 공통 기능 및 예외 처리 사용 가이드 (with Product 예시)

---

## 📁 기반 구조 설명

본 문서는 프로젝트 전반에서 사용하는 공통 기능들(페이직, 검색 조건, QueryDSL 동적 코드, 예외 처리 등)의 사용법을 설명합니다


---

## 📆 1. 공통 페이징 요청 DTO (`PageRequestDTO`)

### ✅ 역할

- 페이징 번호, 크기, 정렬 조건을 관리
- `Pageable` 객체로 변환

### ✨ 사용 예시 – `ProductController`

```java
@GetMapping
public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
    PageRequestDTO pageRequestDTO,
    Search search
) {
    Pageable pageable = pageRequestDTO.toPageable();
    Page<ProductResponseDTO> result = productService.getAllProducts(pageable, search);
    return ResponseEntity.ok(result);
}
```

---

## 🔍 2. 공통 검색 DTO (`Search`)

### ✅ 역할

- 검색 키와 값 관리
- `searchKey`, `searchValue` 유효성 검사 포함

### ✨ 사용 예시 – `ProductPredicateBuilder`

```java
public class ProductPredicateBuilder {
    public static BooleanExpression build(QProduct product, Search search) {
        if (search == null || !search.isValid()) return null;

        return switch (search.getSearchKey()) {
            case "title"       -> PredicateBuilder.applyKeyword(product.title, search.getSearchValue());
            case "description" -> PredicateBuilder.applyKeyword(product.description, search.getSearchValue());
            case "address"     -> PredicateBuilder.applyKeyword(product.address, search.getSearchValue());
            default            -> null;
        };
    }
}
```

> ✔ 해당 조건은 `ProductRepositoryImpl`에서 QueryDSL 쿼리로 사용됨

---

## 🧠 3. QueryDSL 정렬 유틸 (`QuerydslUtils`)

### ✅ 역할

- Spring의 `Sort` → QueryDSL의 `OrderSpecifier[]`로 변환

### ✨ 사용 예시 – `ProductRepositoryImpl`

```java
.selectFrom(product)
.where(condition)
.offset(pageable.getOffset())
.limit(pageable.getPageSize())
.orderBy(QuerydslUtils.toOrders(pageable.getSort(), product))
.fetch();
```

---

## 🔧 4. QueryDSL 조건 유틸 (`PredicateBuilder`)

### ✅ 역할

- 다양한 조건 코드 메서드 제공
- 다른 조건들 추가 가능 (필요시 요청)

### ✨ 제공 기능

```java
applyKeyword(field, keyword);   // like 검색
between(field, start, end);     // 범위 검색
```

---

## 🚨 5. 전역 예외 처리 (`GlobalExceptionHandler`)

### ✅ 기본 구조

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    ...
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ...
}
```

### ✨ 사용 예시 – `ProductService`

```java
User sellerUser = userRepository.findById(request.getSellerId())
    .orElseThrow(() -> new NotFoundException("판매자 유저가 존재하지 않습니다."));
```

> ✔ 유효하지 않은 ID로 조회 시 `404 NOT_FOUND` 예외 자동 반환

---

## 🧪 6. 유효성 검증 (`@Valid`, `@NotNull`, `@NotBlank`)

### ✅ 역할

- 컨트롤러에서 요청 DTO에 대한 자동 검증
- 실패 시 `MethodArgumentNotValidException` 발생 → 400 오류 반환

### ✨ 사용 예시 – `ProductController`

```java
@PostMapping
public ResponseEntity<Void> createProduct(@RequestBody @Valid ProductRequestDTO request) {
    productService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
}
```

### ✨ DTO 예시 – `ProductRequestDTO`

```java
@NotBlank(message = "제목은 필수입니다.")
private String title;

@Min(value = 1000, message = "가격은 최소 1000원 이상이어야 합니다.")
private Integer price;
```

---

## 🔟 마무리

| 공통 기능  | 관련 클래스                       | Product 예시                    |
| ------ | ---------------------------- | ----------------------------- |
| 페이직 처리 | `PageRequestDTO`             | `getAllProducts()`            |
| 검색 조건  | `Search`, `PredicateBuilder` | `ProductPredicateBuilder`     |
| 정렬 변환  | `QuerydslUtils`              | `ProductRepositoryImpl`       |
| 동적 조건  | `PredicateBuilder`           | `title`, `description` 검색     |
| 예외 처리  | `GlobalExceptionHandler`     | `NotFoundException`, `@Valid` |
| DTO 검증 | `@NotBlank`, `@Min` 등        | `ProductRequestDTO`           |

---

> **📀 이 문서는 팀에서 공통 기능을 활용할 때 기본 참고자료로 사용되며, 새로운 도메인을 개발할 때도 동일한 구조를 따라가면 좋습니다.**

