package com.fisa.wooriarte.S3.utils;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Optional;

@Component
@Slf4j
public class S3Service {
    @Autowired
    private AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * 1. 파일 업로드
     * @param multipartFile
     * @param s3FileName
     * @return
     * @throws IOException
     */
    public String upload(MultipartFile multipartFile, String s3FileName) throws IOException {
        // 메타데이터 생성
        ObjectMetadata objMeta = new ObjectMetadata();
        objMeta.setContentLength(multipartFile.getInputStream().available());
        // putObject(버킷명, 파일명, 파일데이터, 메타데이터)로 S3에 객체 등록
        amazonS3.putObject(bucket, s3FileName, multipartFile.getInputStream(), objMeta);
        // 등록된 객체의 url 반환 (decoder: url 안의 한글or특수문자 깨짐 방지)
        return URLDecoder.decode(amazonS3.getUrl(bucket, s3FileName).toString(), "utf-8");
    }

    /**
     * 2. 파일 삭제
     * @param keyName
     */
    public void delete (String keyName) {
        try {
            // deleteObject(버킷명, 키값)으로 객체 삭제
            amazonS3.deleteObject(bucket, keyName);
        } catch (AmazonServiceException e) {
            log.error(e.toString());
        }
    }

    /**
     * 3. 키값으로 DB에서 S3 검색
     * @param photoKey
     * @return
     */
    public Optional<byte[]> getPhoto(String photoKey) {
        try {
            S3Object s3Object = amazonS3.getObject(bucket, photoKey);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            byte[] photoBytes = inputStream.readAllBytes();
            inputStream.close();
            return Optional.of(photoBytes);
        } catch (AmazonS3Exception | IOException e) {
            // S3에서 사진을 찾을 수 없는 경우
            return Optional.empty();
        }
    }
}
