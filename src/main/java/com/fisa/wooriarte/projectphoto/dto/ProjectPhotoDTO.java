package com.fisa.wooriarte.projectphoto.dto;

import com.fisa.wooriarte.projectphoto.domain.ProjectPhoto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectPhotoDTO {

    private Long projectPhotoId;
    private Long projectItemId;
    private String fileName;
    private String url;
    private String s3KeyName; // S3 키 값을 저장하는 필드 추가

    public ProjectPhoto toEntity() {
        return ProjectPhoto.builder()
                .projectItemId(this.getProjectItemId())
                .fileName(this.fileName)
                .url(this.url)
                .s3KeyName(this.s3KeyName) // s3KeyName 값도 함께 설정
                .build();
    }

    public static ProjectPhotoDTO fromEntity(ProjectPhoto projectPhoto) {
        return ProjectPhotoDTO.builder()
                .projectPhotoId(projectPhoto.getProjectPhotoId())
                .projectItemId(projectPhoto.getProjectItemId())
                .fileName(projectPhoto.getFileName())
                .url(projectPhoto.getUrl())
                .s3KeyName(projectPhoto.getS3KeyName()) // s3KeyName 값도 함께 설정
                .build();
    }
}
