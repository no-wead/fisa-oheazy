package com.fisa.wooriarte.spacephoto.domain;

import com.fisa.wooriarte.spacephoto.DTO.SpacePhotoDTO;
import io.lettuce.core.dynamic.annotation.CommandNaming;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class SpacePhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long spacePhotoId;

    //private Long spaceId;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String link;

    @Column(nullable = false)
    private Integer size;

    @Column(nullable = false)
    private String fileType;

    public SpacePhotoDTO toDTO() {
        return SpacePhotoDTO.builder()
                .spacePhotoId(this.spacePhotoId)
                //.spaceId(this.spaceId)
                .fileName(this.fileName)
                .link(this.link)
                .size(this.size)
                .fileType(this.fileType)
                .build();
    }
}

