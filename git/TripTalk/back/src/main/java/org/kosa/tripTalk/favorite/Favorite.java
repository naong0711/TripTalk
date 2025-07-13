package org.kosa.tripTalk.favorite;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.kosa.tripTalk.product.Product;
import org.kosa.tripTalk.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "favorite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @Column
    private String ownerType;

    @Column
    private LocalDateTime clickDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

}