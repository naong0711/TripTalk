package org.kosa.tripTalk.file;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

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

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<File>> getFiles(
            @RequestParam String ownerType,
            @RequestParam Long ownerId) {
        return ResponseEntity.ok(fileService.getFilesByOwner(ownerType, ownerId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable Long id) {
        try {
            fileService.deleteFile(id);
            return ResponseEntity.ok(Map.of("message", "삭제 완료"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ 이미지 서빙 API
    @GetMapping("/image/product/{productId}")
    public ResponseEntity<byte[]> getProductThumbnail(@PathVariable Long productId) {
        try {
            File file = fileService.getThumbnailFile("product", productId);
            if (file == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Path path = Paths.get(uploadDir).resolve(file.getStoredName());
            if (!Files.exists(path)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(("파일을 찾을 수 없습니다: " + file.getStoredName()).getBytes());
            }

            byte[] imageBytes = Files.readAllBytes(path);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("서버 오류: " + e.getMessage()).getBytes());
        }
    }
}