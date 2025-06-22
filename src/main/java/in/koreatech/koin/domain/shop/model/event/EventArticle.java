package in.koreatech.koin.domain.shop.model.event;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import in.koreatech.koin.domain.shop.model.shop.Shop;
import in.koreatech.koin.domain.user.model.User;
import in.koreatech.payment.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "event_articles")
@Where(clause = "is_deleted=0")
@SQLDelete(sql = "UPDATE event_articles SET is_deleted = true WHERE id = ?")
@NoArgsConstructor(access = PROTECTED)
public class EventArticle extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Size(max = 255)
    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @OneToMany(mappedBy = "eventArticle", orphanRemoval = true, cascade = ALL)
    private List<EventArticleImage> thumbnailImages = new ArrayList<>();

    @NotNull
    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Column(name = "hit", nullable = false)
    private Integer hit;

    @Size(max = 45)
    @NotNull
    @Column(name = "ip", nullable = false)
    private String ip;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}
