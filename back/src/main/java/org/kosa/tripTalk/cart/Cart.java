package org.kosa.tripTalk.cart;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import org.kosa.tripTalk.user.User;

@Entity
@Table(name = "cart")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cart {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne @JoinColumn(name = "user_id")
    private User user;

//    @ManyToOne @JoinColumn(name = "product_id")
//    private Product product;

}