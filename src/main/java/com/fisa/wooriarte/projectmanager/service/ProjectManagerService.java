package com.fisa.wooriarte.projectmanager.service;

import com.fisa.wooriarte.projectmanager.DTO.ProjectManagerDTO;
import com.fisa.wooriarte.projectmanager.domain.ProjectManager;
import com.fisa.wooriarte.projectmanager.repository.ProjectManagerRepository;
import com.fisa.wooriarte.util.Encryption;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ProjectManagerService {

    private final ProjectManagerRepository projectManagerRepository;
    private final Encryption encryption;

    @Autowired
    public ProjectManagerService(ProjectManagerRepository projectManagerRepository, Encryption encryption) {
        this.projectManagerRepository = projectManagerRepository;
        this.encryption = encryption;
    }
    /*
   프로젝트 매니저 추가
   1. 같은 아이디 사용 확인
       발견시 예외 처리
   2. DB에 저장
    */
    public boolean addProjectManager(ProjectManagerDTO projectManagerDTO) {
        Optional<ProjectManager> optionalProjectManager = projectManagerRepository.findByProjectManagerId(projectManagerDTO.getId());
        if (optionalProjectManager.isPresent()) {
            throw new DataIntegrityViolationException("Duplicate User id");
        }
        ProjectManager projectManager = projectManagerDTO.toEntity();
        projectManager.setPwd(encryption.encryptionSHA256(projectManager.getPwd()));
        projectManagerRepository.save(projectManager);
        return true;
    }

    /*
    프로젝트 매니저 로그인
    1. id로 유저 검색
        없으면 예외 처리
    2. 비밀번호와 비교
     */
    public boolean loginProjectManager(String id, String pwd) {
        Optional<ProjectManager> optionalProjectManager = projectManagerRepository.findByProjectManagerId(id);
        return optionalProjectManager.isPresent() && optionalProjectManager.get().getPwd().equals(encryption.encryptionSHA256(pwd));
    }

    //프로젝트 매니저 아이디 찾기
    public String getId(String email) {
        ProjectManager projectManager = projectManagerRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("가입되지 않은 사용자입니다"));
        return projectManager.getId();
    }

    //프로젝트 매니저 pw 재설정
    public boolean setPwd(String id, String newPwd) {
        ProjectManager projectManager = projectManagerRepository.findByProjectManagerId(id)
                .orElseThrow(() -> new NoSuchElementException("가입되지 않은 사용자입니다"));
        //비밀번호 검증
        projectManager.setPwd(encryption.encryptionSHA256(newPwd));
        return true;
    }

    /*
    프로젝트 매니저 정보 검색
    1. id로 유저 검색
        없으면 예외 처리
    2. DTO로 변환 후 반환
     */
    public Optional<ProjectManagerDTO> findByProjectManagerId(Long projectManagerId) {
        return projectManagerRepository.findById(projectManagerId)
                .map(ProjectManagerDTO::fromEntity);
    }

    /*
    프로젝트 매니저 정보 갱신
    1. id로 유저 검색
        없으면 예외처리
    2. BeanUtils.copyProperties(S, D, ignore)로 같은 컬럼 데이터 갱신
        S:source 복사할 대상(getter 필요)
        D:Destination 내용 저장할 대상(setter 필요)
        ignore: 제외할 내용 선택
            createAt: 생성 시점은 갱신하지 않음
            businessId: 고유 번호는 그대로 유지
     */
    public boolean updateProjectManager(Long projectManagerId, ProjectManagerDTO projectManagerDTO) {
        ProjectManager projectManager = projectManagerRepository.findById(projectManagerId)
                .orElseThrow(() -> new NoSuchElementException("Fail to update. No one uses that ID"));
        projectManager.updateProjectManager(projectManagerDTO);
        projectManagerRepository.save(projectManager);
        return true;
    }

    /*
    프로젝트 매니저 삭제 soft-delete
    1. id로 유저 검색
        없으면 예외처리
    2. delete를 true로 변경
        이미 변경했으면 예외처리
     */
    public boolean deleteProjectManager(Long projectManagerId) {
        ProjectManager projectManager = projectManagerRepository.findById(projectManagerId)
                .orElseThrow(() -> new NoSuchElementException("Fail to delete. No one uses that ID"));
        if(projectManager.getIsDeleted()) {
            throw new DataIntegrityViolationException("Already deleted User");
        }
        projectManager.setIsDeleted();
        return true;
    }
}
