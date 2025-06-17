package org.kosa.tripTalk.product;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {

	private final ProductService productService;
	
	@PostMapping
    public ResponseEntity<Void> createProduct(@RequestBody ProductRequest request) {
        productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
