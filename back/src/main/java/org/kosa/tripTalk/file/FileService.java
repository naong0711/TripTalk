package org.kosa.tripTalk.file;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;

    public File saveFile(File file) {
        file.setUploadedAt(LocalDateTime.now());
        return fileRepository.save(file);
    }

    public List<File> getFilesByOwner(String ownerType, Long ownerId) {
        return fileRepository.findByOwnerTypeAndOwnerId(ownerType, ownerId);
    }

    public void deleteFile(Long fileId) {
        fileRepository.deleteById(fileId);
        // 실제 파일 시스템에서도 삭제해야 함
    }
}