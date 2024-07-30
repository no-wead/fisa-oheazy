package com.fisa.wooriarte.exhibit.domain;

import com.fisa.wooriarte.exhibit.dto.ExhibitDto;
import com.fisa.wooriarte.matching.domain.Matching;
import com.fisa.wooriarte.ticket.domain.Ticket;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
@DynamicUpdate
public class Exhibit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long exhibitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matching_id")
    private Matching matching;

    @OneToMany(mappedBy = "exhibit", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Ticket> tickets = new ArrayList<>();

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 65535)
    private String intro;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private String artistName;

    @Column(nullable = false)
    private String hostName;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Long soldAmount;

    @Column(nullable = false)
    private City city;

    @Column(nullable = false)
    private Boolean isDeleted;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createAt;

    //티켓 삭제 deleted 컬럼 변경
    public void setIsDeleted() {
        this.isDeleted = !this.isDeleted;
    }

    public void updateExhibit(ExhibitDto exhibitDTO) {

        this.name = exhibitDTO.getName();
        this.intro  = exhibitDTO.getIntro();
        this.startDate = exhibitDTO.getStartDate();
        this.endDate = exhibitDTO.getEndDate();
        this.price  = exhibitDTO.getPrice();
        this.city = City.valueOf(exhibitDTO.getCity());
    }
}
