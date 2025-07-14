package org.kosa.tripTalk.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByOwnerTypeAndOwnerId(String ownerType, Long ownerId);

    Optional<File> findFirstByOwnerTypeAndOwnerIdAndIsThumbnail(String ownerType, Long ownerId, int isThumbnail);

    List<File> findByOwnerType(String ownerType);

}