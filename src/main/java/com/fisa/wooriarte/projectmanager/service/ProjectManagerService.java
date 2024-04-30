package com.fisa.wooriarte.projectmanager.service;

import com.fisa.wooriarte.jwt.JwtToken;
import com.fisa.wooriarte.jwt.JwtTokenProvider;
import com.fisa.wooriarte.projectmanager.dto.ProjectManagerDto;
import com.fisa.wooriarte.projectmanager.domain.ProjectManager;
import com.fisa.wooriarte.projectmanager.repository.ProjectManagerRepository;
import com.fisa.wooriarte.util.encryption.Encryption;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ProjectManagerService {

    private final ProjectManagerRepository projectManagerRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ProjectManagerService(ProjectManagerRepository projectManagerRepository, AuthenticationManagerBuilder authenticationManagerBuilder,
                                 JwtTokenProvider jwtTokenProvider, Encryption encryption, PasswordEncoder passwordEncoder) {
        this.projectManagerRepository = projectManagerRepository;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }
    /*
   프로젝트 매니저 추가
   1. 같은 아이디 사용 확인
       발견시 예외 처리
   2. DB에 저장
    */
    @Transactional
    public boolean addProjectManager(ProjectManagerDto projectManagerDTO) {
        Optional<ProjectManager> optionalProjectManager = projectManagerRepository.findById(projectManagerDTO.getId());
        if (optionalProjectManager.isPresent()) {
            throw new DataIntegrityViolationException("Duplicate User id");
        }
        ProjectManager projectManager = projectManagerDTO.toEntity();
        projectManager.setPwd(passwordEncoder.encode(projectManagerDTO.getPwd())); // assuming 'encryption' is properly injected
        projectManager.addRole("PROJECT_MANAGER");
        projectManagerRepository.save(projectManager);
        return true;
    }

    /*
    프로젝트 매니저 로그인
    1. id로 유저 검색
        없으면 예외 처리
    2. 비밀번호와 비교
     */
    public ProjectManagerDto loginProjectManager(String id, String pwd) throws Exception {
        Optional<ProjectManager> optionalProjectManager = projectManagerRepository.findById(id);
        if (passwordEncoder.matches(pwd, optionalProjectManager.get().getPwd())){
            return ProjectManagerDto.fromEntity(optionalProjectManager.get());
        } else {
            throw new Exception("로그인 불가");
        }
    }

    @Transactional
    public JwtToken login(String id, String pwd) {
        ProjectManager projectManager = projectManagerRepository.findById(id)
                .orElseThrow(NoSuchElementException::new); // 사용자가 존재하지 않는 경우 예외 발생

        if (!passwordEncoder.matches(pwd, projectManager.getPassword())) {
            // 비밀번호가 일치하지 않는 경우
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        System.out.println(passwordEncoder.matches(pwd, projectManager.getPassword()));
        System.out.println(passwordEncoder.encode(projectManager.getPwd()));
        // 1. Login ID/PW를 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(id, pwd);
        System.out.println(id);
        System.out.println(pwd);
        System.out.println(passwordEncoder.encode(projectManager.getPwd()));
        System.out.println("%%% 111");
        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        Authentication authentication = null;
        try {
            System.out.println("%%% 222");
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            System.out.println("%%% 333");
        } catch (Exception e) {
            e.printStackTrace(); // 예외를 로깅
            throw e; // 필요한 경우, 예외를 다시 던져 처리할 수 있습니다.
        }

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        return jwtToken;
    }


    //프로젝트 매니저 아이디 찾기
    public String getId(String email) {
        ProjectManager projectManager = projectManagerRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("가입되지 않은 사용자입니다"));
        System.out.println("*****************");
        System.out.println(projectManager);
        return projectManager.getId();
    }

    //프로젝트 매니저 pw 재설정
    public boolean setPwd(String id, String newPwd) {
        ProjectManager projectManager = projectManagerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("가입되지 않은 사용자입니다"));
        //비밀번호 검증
        projectManager.setPwd(passwordEncoder.encode(newPwd));
        projectManagerRepository.save(projectManager);
        return true;
    }

    /*
    프로젝트 매니저 정보 검색
    1. id로 유저 검색
        없으면 예외 처리
    2. DTO로 변환 후 반환
     */
    public Optional<ProjectManagerDto> findByProjectManagerId(Long projectManagerId) {
        return projectManagerRepository.findById(projectManagerId)
                .map(ProjectManagerDto::fromEntity);
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
    @Transactional
    public boolean updateProjectManager(Long projectManagerId, ProjectManagerDto projectManagerDTO) {
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

    @Transactional
    public boolean deleteProjectManager(Long projectManagerId) {
        ProjectManager projectManager = projectManagerRepository.findById(projectManagerId)
                .orElseThrow(() -> new NoSuchElementException("Fail to delete. No one uses that ID"));
        if(projectManager.getIsDeleted()) {
            throw new DataIntegrityViolationException("Already deleted User");
        }
        projectManager.setIsDeleted();
        projectManagerRepository.save(projectManager);
        return true;
    }

    public boolean verifyPassword(Long projectManagerId, String pwd) {
        ProjectManager projectManager = projectManagerRepository.findById(projectManagerId)
                .orElseThrow(() -> new NoSuchElementException("가입되지 않은 사용자입니다"));
        return passwordEncoder.matches(projectManager.getPwd(),passwordEncoder.encode(pwd));
    }
}
