package org.kosa.tripTalk.product;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import org.kosa.tripTalk.seller.Seller;

@Entity
@Table(name = "product")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne @JoinColumn(name = "seller_id")
    private Seller seller;

//    @ManyToOne @JoinColumn(name = "category_id")
//    private Category category;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private Integer price;

    @Column
    private String address;

}