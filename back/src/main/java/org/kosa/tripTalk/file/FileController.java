package org.kosa.tripTalk.file;

import java.util.List;
import org.kosa.tripTalk.file.File;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestPart MultipartFile file,
                                        @RequestParam String ownerType,
                                        @RequestParam Long ownerId,
                                        @RequestParam(required = false) boolean isThumbnail) {
        // 저장 및 DB 등록 로직
        return ResponseEntity.ok("파일 업로드 완료");
    }

    @GetMapping
    public List<File> getFiles(@RequestParam String ownerType, @RequestParam Long ownerId) {
        return fileService.getFilesByOwner(ownerType, ownerId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable Long id) {
        fileService.deleteFile(id);
        return ResponseEntity.ok("삭제 완료");
    }
}