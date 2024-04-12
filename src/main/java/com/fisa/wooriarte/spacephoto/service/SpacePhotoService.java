package com.fisa.wooriarte.spacephoto.service;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.fisa.wooriarte.spacephoto.domain.SpacePhoto;
import com.fisa.wooriarte.spacephoto.dto.SpacePhotoDTO;
import com.fisa.wooriarte.spacephoto.repository.SpacePhotoRepository;
import com.fisa.wooriarte.spacephoto.utils.S3Service;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SpacePhotoService {
    private final S3Service s3Service;
    private final SpacePhotoRepository spacePhotoRepository;

    public SpacePhotoService(S3Service s3Service, SpacePhotoRepository spacePhotoRepository) {
        this.s3Service = s3Service;
        this.spacePhotoRepository = spacePhotoRepository;
    }

    /**
     * 1. 사진 파일 저장 메서드
     * @param multipartFileList : 사진 파일 리스트
     * @param id : SpaceItemId
     * @return
     * @throws IOException
     */
    public ResponseEntity<?> addFiles(List<MultipartFile> multipartFileList, Long id) throws IOException {
        // 리스트로 들어온 여러 파일들을 하나씩 S3와 DB에 저장
        for (MultipartFile multipartFile : multipartFileList) {
            // 파일명 지정 (겹치지 않도록 UUID와 원본 파일명을 조합)
            String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();

            // S3에 파일 업로드
            String url = s3Service.upload(multipartFile, fileName);

            // S3 키 이름 추출
            String s3KeyName = extractKeyNameFromUrl(url);

            // SpacePhotoDTO 생성
            SpacePhotoDTO spacePhotoDTO = SpacePhotoDTO.builder()
                    .spaceItemId(id)
                    .fileName(fileName)
                    .url(url)
                    .s3KeyName(s3KeyName)
                    .build();

            // SpacePhotoDTO를 엔티티로 변환하여 저장
            SpacePhoto spacePhoto = spacePhotoDTO.toEntity();
            spacePhotoRepository.save(spacePhoto);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * 2. 사진 개별 삭제 메서드
     * @param spacePhotoIds : 삭제할 사진의 spacePhotoId
     */
    @Transactional
    public void deleteFiles(List<Long> spacePhotoIds) {
        for (Long spacePhotoId : spacePhotoIds) {
            // DB에서 사진 정보 가져오기
            SpacePhoto spacePhoto = spacePhotoRepository.findById(spacePhotoId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid space photo ID: " + spacePhotoId));

            // S3 키 추출
            String keyName = extractKeyNameFromUrl(spacePhoto.getUrl());

            // S3에서 파일 삭제
            s3Service.delete(keyName);

            // DB에서 사진 삭제
            spacePhotoRepository.delete(spacePhoto);
        }
    }

    /**
     * 3. SpaceItemId에 해당하는 모든 사진 삭제
     * @param spaceItemId SpaceItem ID
     * @throws IllegalArgumentException 주어진 SpaceItem ID에 해당하는 사진이 없는 경우 예외 발생
     */
    public void deleteAllFiles(Long spaceItemId) {
        List<SpacePhoto> photos = spacePhotoRepository.findBySpaceItemId(spaceItemId);
        if (photos.isEmpty()) {
            throw new IllegalArgumentException("No photos found with space item ID: " + spaceItemId);
        }

        for (SpacePhoto photo : photos) {
            // 각 사진의 S3 키 추출
            String keyName = extractKeyNameFromUrl(photo.getUrl());

            // S3에서 파일 삭제
            s3Service.delete(keyName);
        }

        // DB에서 모든 사진 삭제
        spacePhotoRepository.deleteAll(photos);
    }


    /**
     * 4. DB의 SpacePhotoId를 이용하여 S3 사진을 검색하는 메서드
     * @param spacePhotoId : 검색할 사진이 저장된 DB의 SpacePhotoId
     * @return : Optional을 통해 검색된 사진을 반환
     */
    public List<SpacePhotoDTO> getPhotosBySpaceItemId(Long spaceItemId) {
        List<SpacePhoto> spacePhotos = spacePhotoRepository.findBySpaceItemId(spaceItemId);
        return spacePhotos.stream()
                .map(SpacePhotoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 5. S3 파일 URL에서 키 값 추출
     * @param fileUrl
     * @return
     */
    private String extractKeyNameFromUrl(String fileUrl) {
        // 예: https://example-bucket.s3.amazonaws.com/folder/file.txt
        // 키 이름: folder/file.txt
        String[] parts = fileUrl.split("/");
        int length = parts.length;
        return String.join("/", Arrays.copyOfRange(parts, 3, length));
    }
}