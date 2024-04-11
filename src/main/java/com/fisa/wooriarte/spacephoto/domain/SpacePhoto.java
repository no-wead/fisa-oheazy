package com.fisa.wooriarte.spacephoto.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpacePhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long spacePhotoId;

//    @JoinColumn(nullable = false)
//    private SpaceItem spaceItem;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String link;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private String fileType;
}

