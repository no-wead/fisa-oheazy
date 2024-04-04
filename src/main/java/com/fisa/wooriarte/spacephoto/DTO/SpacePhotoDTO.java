package com.fisa.wooriarte.spacephoto.DTO;

import com.fisa.wooriarte.spacephoto.domain.SpacePhoto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpacePhotoDTO {
    private Long spacePhotoId;
    //private Long spaceId;
    private String fileName;
    private String link;
    private Integer size;
    private String fileType;

    public SpacePhoto toEntity() {
        return SpacePhoto.builder()
                .spacePhotoId(this.spacePhotoId)
                //.spaceId(this.spaceId)
                .fileName(this.fileName)
                .link(this.link)
                .size(this.size)
                .fileType(this.fileType)
                .build();
    }
}

