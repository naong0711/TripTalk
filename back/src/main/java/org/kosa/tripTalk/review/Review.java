package org.kosa.tripTalk.review;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import org.kosa.tripTalk.product.Product;
import org.kosa.tripTalk.user.User;

@Entity
@Table(name = "review")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne @JoinColumn(name = "product_id")
    private Product product;

    @Column
    private Integer rating;

    @Column
    private String content;

}