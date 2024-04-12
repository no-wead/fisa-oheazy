package com.fisa.wooriarte.spacephoto.service;

import com.fisa.wooriarte.spacephoto.utils.S3Uploader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpacePhotoService {
    private final S3Uploader s3Uploader;
//    private final SpacePhotoRepository spacePhotoRepository;

    public SpacePhotoService(S3Uploader s3Uploader) {
        this.s3Uploader = s3Uploader;
//        this.spacePhotoRepository = spacePhotoRepository;
    }

    @Transactional
    public void createPhoto(List<MultipartFile> files) {
        List<String> urls = s3Uploader.uploadFilesToS3(files, "static/space-image");
    }
}
