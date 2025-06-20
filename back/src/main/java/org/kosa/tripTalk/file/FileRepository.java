package org.kosa.tripTalk.file;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByOwnerTypeAndOwnerId(String ownerType, Long ownerId);
}