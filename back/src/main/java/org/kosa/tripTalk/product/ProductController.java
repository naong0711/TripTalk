package org.kosa.tripTalk.product;

import org.kosa.tripTalk.common.dto.PageRequestDTO;
import org.kosa.tripTalk.common.dto.Search;
import org.kosa.tripTalk.file.FileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.jsonwebtoken.io.IOException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;
    private final FileService fileService;

    // ✅ 상품 등록 + 썸네일 이미지 포함 처리
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
    		@RequestParam("product") String productJson,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        try {
            System.out.println("📦 받은 JSON: " + productJson);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            ProductRequestDTO dto = objectMapper.readValue(productJson, ProductRequestDTO.class);
            System.out.println("✅ 파싱 완료: " + dto.getTitle());

            Long productId = productService.create(dto);
            System.out.println("💾 상품 저장 성공: " + productId);

            if (file != null && !file.isEmpty()) {
                fileService.saveFile(file, "product", productId, 1);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(productId);

        } catch (Exception e) {
            e.printStackTrace(); // 터미널 로그에 전체 예외 스택 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류: " + e.getMessage());
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable("productId") Long id) {
        ProductResponseDTO response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequestDTO requestDTO) {
        Product updated = productService.update(id, requestDTO);
        return ResponseEntity.ok(ProductResponseDTO.from(updated));
    }

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
}
