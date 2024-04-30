package com.fisa.wooriarte.spacerental.service;

import com.fisa.wooriarte.jwt.JwtToken;
import com.fisa.wooriarte.jwt.JwtTokenProvider;
import com.fisa.wooriarte.spacerental.repository.SpaceRentalRepository;
import com.fisa.wooriarte.spacerental.dto.SpaceRentalDto;
import com.fisa.wooriarte.spacerental.domain.SpaceRental;
import com.fisa.wooriarte.util.encryption.Encryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class SpaceRentalService {
    private final SpaceRentalRepository spaceRentalRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final Encryption encryption;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SpaceRentalService(SpaceRentalRepository spaceRentalRepository, AuthenticationManagerBuilder authenticationManagerBuilder, JwtTokenProvider jwtTokenProvider, Encryption encryption, PasswordEncoder passwordEncoder) {
        this.spaceRentalRepository = spaceRentalRepository;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.encryption = encryption;
        this.passwordEncoder = passwordEncoder;
    }
    /*
    공간대여자 추가
    1. 같은 아이디 사용 확인
        발견시 예외 처리
    2. DB에 저장
     */
    @Transactional
    public boolean addSpaceRental(SpaceRentalDto spaceRentalDTO) {
        Optional<SpaceRental> optionalSpaceRental = spaceRentalRepository.findById(spaceRentalDTO.getId());
        if (optionalSpaceRental.isPresent()) {
            throw new DataIntegrityViolationException("Duplicate User id");
        }
        SpaceRental spaceRental = spaceRentalDTO.toEntity();
        spaceRental.setPwd(passwordEncoder.encode(spaceRentalDTO.getPwd()));
        spaceRental.addRole("SPACE_RENTAL");
        spaceRentalRepository.save(spaceRental);
        return true;
    }

    /*
    공간대여자 로그인
    1. id로 유저 검색
        없으면 예외 처리
    2. 비밀번호와 비교
     */
    public SpaceRentalDto loginSpaceRental(String id, String pwd) throws Exception{
        Optional<SpaceRental> optionalSpaceRental = spaceRentalRepository.findById(id);
        if(passwordEncoder.matches(pwd, optionalSpaceRental.get().getPwd())){
            return SpaceRentalDto.fromEntity(optionalSpaceRental.get());
        }else{
            throw new Exception("로그인 불가 ");
        }
    }

    @Transactional
    public JwtToken login(String id, String pwd) {
        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(id, pwd);
        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행

        Authentication authentication = null;
        try {
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (Exception e) {
            e.printStackTrace(); // 예외를 로깅
            throw e; // 필요한 경우, 예외를 다시 던져 처리할 수 있습니다.
        }
        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);
        return jwtToken;
    }

    //공간 대여자 아이디 찾기
    public String getId(String email) {
        SpaceRental spaceRental = spaceRentalRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("가입되지 않은 사용자입니다"));
        return spaceRental.getId();
    }

    //공간 대여자 pw 재설정
    public boolean setPwd(String id, String newPwd) {
        SpaceRental spaceRental = spaceRentalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("가입되지 않은 사용자입니다"));
        //비밀번호 검증
        spaceRental.setPwd(encryption.encrypt(newPwd));
        return true;
    }

    /*
    공간대여자 정보 검색
    1. id로 유저 검색
        없으면 예외 처리
    2. DTO로 변환 후 반환
     */
    public SpaceRentalDto findBySpaceRentalId(Long spaceRentalId) {
        SpaceRental spaceRental = spaceRentalRepository.findById(spaceRentalId)
                    .orElseThrow(() -> new NoSuchElementException("Fail to search info. No one uses that ID"));
        return SpaceRentalDto.fromEntity(spaceRental);
    }

    /*
    공간대여자 정보 갱신
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
    public boolean updateSpaceRental(Long spaceRentalId, SpaceRentalDto spaceRentalDTO) {
        SpaceRental spaceRental = spaceRentalRepository.findById(spaceRentalId)
                .orElseThrow(() -> new NoSuchElementException("Fail to update. No one uses that ID"));
        spaceRental.updateSpaceRental(spaceRentalDTO);
        spaceRentalRepository.save(spaceRental);
        return true;
    }

    /*
    공간대여자 삭제 soft-delete
    1. id로 유저 검색
        없으면 예외처리
    2. delete를 true로 변경
        이미 변경했으면 예외처리
     */
    @Transactional
    public boolean deleteSpaceRental(Long spaceRentalId) {
        SpaceRental spaceRental = spaceRentalRepository.findById(spaceRentalId)
                .orElseThrow(() -> new NoSuchElementException("Fail to delete. No one uses that ID"));
        if(spaceRental.getIsDeleted()) {
            throw new DataIntegrityViolationException("Already deleted User");
        }
        spaceRental.setIsDeleted();
        spaceRentalRepository.save(spaceRental);
        return true;
    }

    public boolean verifyPassword(Long spaceRentalId, String pwd) {
        SpaceRental spaceRental = spaceRentalRepository.findById(spaceRentalId)
                .orElseThrow(() -> new NoSuchElementException("가입되지 않은 사용자입니다"));
        return passwordEncoder.matches(pwd, spaceRental.getPwd());
    }
}