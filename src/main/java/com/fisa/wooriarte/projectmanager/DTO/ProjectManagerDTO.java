package com.fisa.wooriarte.projectmanager.DTO;

import com.fisa.wooriarte.projectmanager.domain.ProjectManager;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProjectManagerDTO {
    private Long projectManagerId;
    private Long businessNumber;
    private String id;
    private String pwd;
    private String company;
    private String ceo;
    private String email;
    private String phone;
    private Boolean isDeleted;

    public ProjectManager toEntity() {
        return ProjectManager.builder()
                .projectManagerId(this.projectManagerId)
                .businessNumber(this.businessNumber)
                .id(this.id)
                .pwd(this.pwd)
                .company(this.company)
                .ceo(this.ceo)
                .email(this.email)
                .phone(this.phone)
                .isDeleted(this.isDeleted)
                .build();
    }

    public static ProjectManagerDTO fromEntity(ProjectManager projectManager) {
        return ProjectManagerDTO.builder()
                .projectManagerId(projectManager.getProjectManagerId())
                .businessNumber(projectManager.getBusinessNumber())
                .id(projectManager.getId())
                .company(projectManager.getCompany())
                .ceo(projectManager.getCeo())
                .email(projectManager.getEmail())
                .phone(projectManager.getPhone())
                .isDeleted(projectManager.getIsDeleted())
                .build();
    }
}
