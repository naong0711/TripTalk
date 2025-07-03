package org.kosa.tripTalk.file;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    // 🔹 일반 업로드 (상품 대표 이미지 등)
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam("ownerType") String ownerType,
            @RequestParam("ownerId") Long ownerId,
            @RequestParam("isThumbnail") int isThumbnail) {
        try {
            File savedFile = fileService.saveFile(file, ownerType, ownerId, isThumbnail);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "파일 업로드 완료");
            response.put("fileId", savedFile.getId());
            response.put("url", savedFile.getImageUrl());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 🔹 ckEditor 이미지 업로드용
    @PostMapping("/upload/editor")
    public ResponseEntity<?> uploadForEditor(@RequestParam("upload") MultipartFile file) {
        try {
            File savedFile = fileService.saveFile(file, "editor", 0L, 0);
            Map<String, Object> response = new HashMap<>();
            response.put("url", savedFile.getImageUrl());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 🔹 ownerType/ownerId 기준 이미지 서빙 (여러 개 중 첫 번째 반환)
    @GetMapping("/image/{ownerType}/{ownerId}")
    public ResponseEntity<byte[]> serveImage(
            @PathVariable("ownerType") String ownerType,
            @PathVariable("ownerId") Long ownerId) {
        try {
            List<File> files = fileService.getFilesByOwner(ownerType, ownerId);
            if (files == null || files.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            File file = files.get(0); // 첫 번째 파일을 대표 이미지로 사용
            if (!Objects.equals(file.getOwnerType(), ownerType)) {
                return ResponseEntity.notFound().build();
            }

            Path path = Paths.get(uploadDir).resolve(file.getStoredName());
            if (!Files.exists(path)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("파일 없음".getBytes());
            }

            byte[] imageBytes = Files.readAllBytes(path);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(file.getFileType()));
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("서버 오류: " + e.getMessage()).getBytes());
        }
    }

    // 🔹 상품 ID만으로 대표 썸네일 서빙 (프론트 요청에 맞춤)
    @GetMapping("/image/product/{productId}")
    public ResponseEntity<byte[]> serveThumbnailByProductId(@PathVariable("productId") Long productId) {
        try {
            File thumbnail = fileService.getThumbnailFile("product", productId);
            if (thumbnail == null) {
                return ResponseEntity.notFound().build();
            }

            Path path = Paths.get(uploadDir).resolve(thumbnail.getStoredName());
            if (!Files.exists(path)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("파일 없음".getBytes());
            }

            byte[] imageBytes = Files.readAllBytes(path);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(thumbnail.getFileType()));
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("서버 오류: " + e.getMessage()).getBytes());
        }
    }

    // 🔹 특정 owner의 전체 파일 리스트 조회
    @GetMapping
    public ResponseEntity<List<File>> getFiles(
            @RequestParam String ownerType,
            @RequestParam Long ownerId) {
        return ResponseEntity.ok(fileService.getFilesByOwner(ownerType, ownerId));
    }

    // 🔹 파일 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable Long id) {
        try {
            fileService.deleteFile(id);
            return ResponseEntity.ok(Map.of("message", "삭제 완료"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}