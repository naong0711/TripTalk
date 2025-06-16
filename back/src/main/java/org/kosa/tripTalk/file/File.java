package org.kosa.tripTalk.file;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "file")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class File {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "origin_name", nullable = false, length = 255)
    private String originName;

    @Column(name = "stored_name", nullable = false, length = 255)
    private String storedName;

    @Column(name = "file_type", nullable = false, length = 100)
    private String fileType;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @Column(name = "owner_type", nullable = false, length = 30)
    private String ownerType;

    // 작성자 (예: 사용자, 게시글 등 다양한 엔티티의 FK 역할)
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "is_thumbnail")
    private Boolean isThumbnail; // 썸네일 여부 (nullable 허용)
}

