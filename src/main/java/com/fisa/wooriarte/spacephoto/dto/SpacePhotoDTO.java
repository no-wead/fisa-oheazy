package com.fisa.wooriarte.spacephoto.dto;

import com.fisa.wooriarte.spacephoto.domain.SpacePhoto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class SpacePhotoDTO {

    private Long spacePhotoId;
    private Long spaceItemId;
    private String fileName;
    private String url;
    private String s3KeyName; // S3 키 값을 저장하는 필드 추가

    public SpacePhoto toEntity() {
        return SpacePhoto.builder()
                .spaceItemId(this.getSpaceItemId())
                .fileName(this.fileName)
                .url(this.url)
                .s3KeyName(this.s3KeyName) // s3KeyName 값도 함께 설정
                .build();
    }

    public static SpacePhotoDTO fromEntity(SpacePhoto spacePhoto) {
        return SpacePhotoDTO.builder()
                .spacePhotoId(spacePhoto.getSpacePhotoId())
                .spaceItemId(spacePhoto.getSpaceItemId())
                .fileName(spacePhoto.getFileName())
                .url(spacePhoto.getUrl())
                .s3KeyName(spacePhoto.getS3KeyName()) // s3KeyName 값도 함께 설정
                .build();
    }
}
