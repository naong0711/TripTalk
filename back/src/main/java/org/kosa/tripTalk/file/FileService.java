package org.kosa.tripTalk.file;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public File saveFile(MultipartFile multipartFile, String ownerType, Long ownerId, int isThumbnail) throws IOException {
        if (!multipartFile.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }

        if (multipartFile.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("10MB 이하의 파일만 업로드 가능합니다.");
        }

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalName = multipartFile.getOriginalFilename();
        String storedName = UUID.randomUUID() + "_" + originalName;
        Path fullPath = uploadPath.resolve(storedName);
        Files.copy(multipartFile.getInputStream(), fullPath, StandardCopyOption.REPLACE_EXISTING);

        if (isThumbnail == 1) {
            List<File> existingThumbnails = fileRepository.findByOwnerTypeAndOwnerId(ownerType, ownerId);
            for (File f : existingThumbnails) {
                if (f.getIsThumbnail() == 1) {
                    f.setIsThumbnail(0);
                    fileRepository.save(f);
                }
            }
        }

        File file = File.builder()
                .originName(originalName)
                .storedName(storedName)
                .fileType(multipartFile.getContentType())
                .fileSize(multipartFile.getSize())
                .uploadedAt(LocalDateTime.now())
                .ownerType(ownerType)
                .ownerId(ownerId)
                .isThumbnail(isThumbnail)
                .build();
        
        System.out.println("+++++++++++++++++++++++++++++++++"+file);

        return fileRepository.save(file);
    }

    public List<File> getFilesByOwner(String ownerType, Long ownerId) {
        return fileRepository.findByOwnerTypeAndOwnerId(ownerType, ownerId);
    }

    public File getThumbnailFile(String ownerType, Long ownerId) {
        return fileRepository.findByOwnerTypeAndOwnerId(ownerType, ownerId).stream()
                .filter(f -> f.getIsThumbnail() == 1)
                .findFirst()
                .orElse(null);
    }

    public void deleteFile(Long fileId) throws IOException {
        File file = fileRepository.findById(fileId).orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));
        Path fullPath = Paths.get(uploadDir).resolve(file.getStoredName());
        Files.deleteIfExists(fullPath);
        fileRepository.delete(file);
    }
}

