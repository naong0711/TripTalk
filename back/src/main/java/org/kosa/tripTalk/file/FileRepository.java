package org.kosa.tripTalk.file;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    // ownerType, ownerId로 전체 파일 조회
    List<File> findByOwnerTypeAndOwnerId(String ownerType, Long ownerId);

    // 썸네일 1개만 조회 (예: product, 게시글 등)
    Optional<File> findFirstByOwnerTypeAndOwnerIdAndIsThumbnail(String ownerType, Long ownerId, int isThumbnail);
}