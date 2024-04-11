package com.fisa.wooriarte.spacephoto.DTO;

import com.fisa.wooriarte.spacephoto.domain.SpacePhoto;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SpacePhotoDTO {

    private Long spacePhotoId;
    private String fileName;
    private String link;
    private Long size;
    private String fileType;

    public SpacePhoto toEntity() {
        return SpacePhoto.builder()
                .fileName(this.fileName)
                .link(this.link)
                .size(this.size)
                .fileType(this.fileType)
                .build();
    }
    public static SpacePhotoDTO fromEntity(SpacePhoto spacePhoto) {
        return SpacePhotoDTO.builder()
                .fileName(spacePhoto.getFileName())
                .link(spacePhoto.getLink())
                .size(spacePhoto.getSize())
                .fileType(spacePhoto.getFileType())
                .build();
    }
}
