package backend.contents.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "contents")
public class ContentsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Long viewCount = 0L;

    private LocalDateTime createdDate;

    @Column(nullable = false, length = 50)
    private String createdBy;

    private LocalDateTime lastModifiedDate;

    @Column(length = 50)
    private String lastModifiedBy;

    @PrePersist
    public void onCreate() {
        var now = LocalDateTime.now();
        this.createdDate = now;
        this.lastModifiedDate = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.lastModifiedDate = LocalDateTime.now();
    }
}
