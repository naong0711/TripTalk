package org.kosa.tripTalk.file;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam("ownerType") String ownerType,
            @RequestParam("ownerId") Long ownerId,
            @RequestParam(name = "isThumbnail" ) int isThumbnail) {
        try {
        	// 파일 저장 처리 호출
        	File savedFile = fileService.saveFile(file, ownerType, ownerId, isThumbnail);
           
        	// 성공 메시지와 저장된 파일 ID 반환을 위한 Map 생성
        	Map<String, Object> response = new HashMap<>();
            response.put("message", "파일 업로드 완료");
            response.put("fileId", savedFile.getId());
            
            // HTTP 200 OK 응답과 함께 반환
            return ResponseEntity.ok(response);
        } catch (Exception e) {
        	// 예외 발생 시 HTTP 400 Bad Request와 에러 메시지 반환
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<File>> getFiles(@RequestParam String ownerType, @RequestParam Long ownerId) {
    	// ownerType과 ownerId 기준으로 파일 목록 조회 후 반환
    	return ResponseEntity.ok(fileService.getFilesByOwner(ownerType, ownerId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable Long id) {
        try {
        	// 파일 삭제 처리 호출
            fileService.deleteFile(id);
            // 삭제 성공 메시지 반환
            return ResponseEntity.ok(Map.of("message", "삭제 완료"));
        } catch (Exception e) {
        	// 삭제 실패 시 에러 메시지와 함께 HTTP 400 반환
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}