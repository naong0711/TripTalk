package org.kosa.tripTalk.product;

import org.kosa.tripTalk.common.dto.PageRequestDTO;
import org.kosa.tripTalk.common.dto.Search;
import org.kosa.tripTalk.file.FileService;
import org.kosa.tripTalk.user.User;
import org.kosa.tripTalk.seller.SellerService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;
    private final FileService fileService;
    private final SellerService sellerService;

    // ✅ 상품 등록 + 썸네일 이미지 포함 처리
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestParam("product") String productJson,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal User user  // ✅ 인증된 유저 주입
    ) {
        try {
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            System.out.println("📦 받은 JSON: " + productJson);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            ProductRequestDTO dto = objectMapper.readValue(productJson, ProductRequestDTO.class);
            System.out.println("✅ 파싱 완료: " + dto.getTitle());

            Long userId = user.getId();  // 🔑 로그인한 유저 ID
            Long sellerId = sellerService.getSellerIdByUserId(userId);  // 🔑 유저 → 판매자 매핑

            dto.setSellerId(sellerId);  // ✅ sellerId 직접 주입

            Long productId = productService.create(dto);
            System.out.println("💾 상품 저장 성공: " + productId);

            if (file != null && !file.isEmpty()) {
                fileService.saveFile(file, "product", productId, 1);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(productId);

        } catch (Exception e) {
            e.printStackTrace(); // 로그 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류: " + e.getMessage());
        }
    }

    // ✅ 상품 단건 조회
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable("productId") Long id) {
        ProductResponseDTO response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    // ✅ 상품 수정
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequestDTO requestDTO) {
        Product updated = productService.update(id, requestDTO);
        return ResponseEntity.ok(ProductResponseDTO.from(updated));
    }

    // ✅ 전체 상품 조회 (페이징 + 검색)
    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @ModelAttribute PageRequestDTO pageRequestDTO,
            @ModelAttribute Search search
    ) {
        System.out.println("요청 받은 페이지: " + pageRequestDTO.getPage() + ", 사이즈: " + pageRequestDTO.getSize() + ", 정렬: " + pageRequestDTO.getSort());

        Pageable pageable = pageRequestDTO.toPageable();
        Page<ProductResponseDTO> result = productService.getAllProducts(pageable, search);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(
        @RequestParam String location,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime checkIn,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime checkOut,
        @RequestParam int people
    ) {
        try {
            List<Product> results = productService.search(location, checkIn, checkOut, people);

            List<ProductResponseDTO> dtoList = results.stream()
                .map(ProductResponseDTO::from)
                .collect(Collectors.toList());

            return ResponseEntity.ok(dtoList);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }
    @GetMapping("/locations")
    public ResponseEntity<List<String>> getLocations() {
        return ResponseEntity.ok(productService.getAvailableLocations());
    }
}   
      