package org.kosa.tripTalk.travellog;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.kosa.tripTalk.bookmark.BookMark;
import org.kosa.tripTalk.category.Category;
import org.kosa.tripTalk.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "travellog")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TravelLog {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String title;

    @Lob
    @Column
    private String content;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column
    private LocalDateTime createdAt;
    
    @Column(name = "temp_key")
    private String tempKey;
    
    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<BookMark> bookmarks = new ArrayList<>();
    

}