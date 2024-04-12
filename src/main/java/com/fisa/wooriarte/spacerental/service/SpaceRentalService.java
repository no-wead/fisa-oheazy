package com.fisa.wooriarte.spacerental.service;

import com.fisa.wooriarte.spacerental.repository.SpaceRentalRepository;
import com.fisa.wooriarte.spacerental.dto.SpaceRentalDTO;
import com.fisa.wooriarte.spacerental.domain.SpaceRental;
import com.fisa.wooriarte.util.Encryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class SpaceRentalService {
    private final SpaceRentalRepository spaceRentalRepository;
    private final Encryption encryption;

    @Autowired
    public SpaceRentalService(SpaceRentalRepository spaceRentalRepository, Encryption encryption) {
        this.spaceRentalRepository = spaceRentalRepository;
        this.encryption = encryption;
    }
    /*
    공간대여자 추가
    1. 같은 아이디 사용 확인
        발견시 예외 처리
    2. DB에 저장
     */
    @Transactional
    public boolean addSpaceRental(SpaceRentalDTO spaceRentalDTO) {
        Optional<SpaceRental> optionalSpaceRental = spaceRentalRepository.findBySpaceRentalId(spaceRentalDTO.getId());
        if (optionalSpaceRental.isPresent()) {
            throw new DataIntegrityViolationException("Duplicate User id");
        }
        SpaceRental spaceRental = spaceRentalDTO.toEntity();
        spaceRental.setPwd(encryption.encryptionSHA256(spaceRental.getPwd()));
        spaceRentalRepository.save(spaceRental);
        return true;
    }

    /*
    공간대여자 로그인
    1. id로 유저 검색
        없으면 예외 처리
    2. 비밀번호와 비교
     */
    public boolean loginSpaceRental(String id, String pwd) {
        Optional<SpaceRental> optionalSpaceRental = spaceRentalRepository.findBySpaceRentalId(id);
        return optionalSpaceRental.isPresent() && optionalSpaceRental.get().getPwd().equals(encryption.encryptionSHA256(pwd));
    }

    //공간 대여자 아이디 찾기
    public String getId(String email) {
        SpaceRental spaceRental = spaceRentalRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("가입되지 않은 사용자입니다"));
        return spaceRental.getId();
    }

    //공간 대여자 pw 재설정
    public boolean setPwd(String id, String newPwd) {
        SpaceRental spaceRental = spaceRentalRepository.findBySpaceRentalId(id)
                .orElseThrow(() -> new NoSuchElementException("가입되지 않은 사용자입니다"));
        //비밀번호 검증
        spaceRental.setPwd(encryption.encryptionSHA256(newPwd));
        return true;
    }

    /*
    공간대여자 정보 검색
    1. id로 유저 검색
        없으면 예외 처리
    2. DTO로 변환 후 반환
     */
    public SpaceRentalDTO findBySpaceRentalId(Long spaceRentalId) {
        SpaceRental spaceRental = spaceRentalRepository.findById(spaceRentalId)
                    .orElseThrow(() -> new NoSuchElementException("Fail to search info. No one uses that ID"));
        return SpaceRentalDTO.fromEntity(spaceRental);
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
    public boolean updateSpaceRental(Long spaceRentalId, SpaceRentalDTO spaceRentalDTO) {
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
}