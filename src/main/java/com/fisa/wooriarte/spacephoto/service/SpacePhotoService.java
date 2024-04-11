package com.fisa.wooriarte.spacephoto.service;

import com.fisa.wooriarte.spacephoto.DTO.SpacePhotoDTO;
import com.fisa.wooriarte.spacephoto.domain.SpacePhoto;
import com.fisa.wooriarte.spacephoto.repository.SpacePhotoRepository;
import com.fisa.wooriarte.spacephoto.utils.S3Uploader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpacePhotoService {
    private final S3Uploader s3Uploader;
    private final SpacePhotoRepository spacePhotoRepository;

    public SpacePhotoService(S3Uploader s3Uploader, SpacePhotoRepository spacePhotoRepository) {
        this.s3Uploader = s3Uploader;
        this.spacePhotoRepository = spacePhotoRepository;
    }

    @Transactional
    public void createPhoto(List<MultipartFile> files) {
        List<String> urls = s3Uploader.uploadFilesToS3(files, "static/space-image");

        for (String url : urls) {
            // 파일 이름 추출
            String fileName = getFileNameFromUrl(url);

            // 파일 크기 추출
            long fileSize = getFileSizeFromUrl(url);

            // 파일 유형 추출
            String fileType = getFileTypeFromUrl(url);

            // SpacePhoto 엔티티 생성
            SpacePhoto spacePhoto = new SpacePhoto();
            SpacePhotoDTO.setFileName(fileName);
            spacePhoto.setLink(url);
            spacePhoto.setSize(fileSize);
            spacePhoto.setFileType(fileType);

            // 데이터베이스에 저장
            spacePhotoRepository.save(spacePhoto);
        }
    }

    private String getFileNameFromUrl(String url) {
        // 파일 URL을 '/'로 분할하여 마지막 세그먼트를 추출
        String[] segments = url.split("/");
        return segments[segments.length - 1];
    }

    private long getFileSizeFromUrl(String url) {
        // 파일 크기를 얻는 로직을 구현
        // 여기서는 가상의 값인 0을 반환합니다.
        return 0;
    }

    private String getFileTypeFromUrl(String url) {
        // 파일 유형을 얻는 로직을 구현
        // 여기서는 가상의 값인 "unknown"을 반환합니다.
        return "unknown";
    }
}
