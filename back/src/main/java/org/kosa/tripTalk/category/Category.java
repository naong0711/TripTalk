package org.kosa.tripTalk.category;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "category")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column
    private String description;

    @Column
    private String iconUrl;

}
