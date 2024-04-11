package com.fisa.wooriarte.spacephoto.utils;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class S3Uploader {
    @Autowired
    private AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public List<String> uploadFilesToS3(List<MultipartFile> files, String filePath) {
        List<String> uploadedImageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                File uploadFile = convert(file)
                        .orElseThrow(() -> new IllegalArgumentException("[error]: MultipartFile -> 파일 변환 실패"));

                // S3에 저장된 파일 이름
                String fileName = filePath + "/" + UUID.randomUUID();

                // S3에 파일 업로드
                String uploadImageUrl = putS3(uploadFile, fileName);
                uploadedImageUrls.add(fileName); // 파일 이름 추가
                uploadedImageUrls.add(uploadImageUrl);

                // 로컬 파일 삭제
                removeNewFile(uploadFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // 업로드된 이미지 URL 리스트 반환
        return uploadedImageUrls;
    }


    /**
     * S3로 업로드
     * @param uploadFile : 업로드할 파일
     * @param fileName : 업로드할 파일 이름
     * @return 업로드 경로
     */
    public String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(
                CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    /**
     * S3에 있는 파일 삭제
     * 영어 파일만 삭제 가능 -> 한글 이름 파일은 안됨
     */
    public void deleteS3(String filePath) throws Exception {
        try{
            String key = filePath.substring(56); // 폴더/파일.확장자

            try {
                amazonS3Client.deleteObject(bucket, key);
            } catch (AmazonServiceException e) {
                log.info(e.getErrorMessage());
            }

        } catch (Exception exception) {
            log.info(exception.getMessage());
        }
        log.info("[S3Uploader] : S3에 있는 파일 삭제");
    }

    /**
     * 로컬에 저장된 파일 지우기
     * @param targetFile : 저장된 파일
     */
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("[파일 업로드] : 파일 삭제 성공");
            return;
        }
        log.info("[파일 업로드] : 파일 삭제 실패");
    }

    /**
     * 로컬에 파일 업로드 및 변환
     * @param file : 업로드할 파일
     */
    private Optional<File> convert(MultipartFile file) throws IOException {
        // 로컬에서 저장할 파일 경로 설정
        String dirPath = "C:\\Users\\Admin\\Desktop\\image";

        // 파일 이름에 UUID 추가하여 고유성 보장
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        File convertFile = new File(dirPath, fileName);

        // 파일 생성
        if (convertFile.createNewFile()) {
            // 파일에 바이트 스트림으로 저장
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }

}
