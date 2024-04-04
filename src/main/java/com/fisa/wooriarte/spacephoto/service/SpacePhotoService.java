package com.fisa.wooriarte.spacephoto.service;

import com.fisa.wooriarte.spacephoto.repository.SpacePhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SpacePhotoService {

    private final SpacePhotoRepository spacePhotoRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Autowired
    public SpacePhotoService(SpacePhotoRepository spacePhotoRepository) {
        this.spacePhotoRepository = spacePhotoRepository;
    }

    public boolean addSpacePhoto(MultipartFile file) {



        return true;
    }
}
