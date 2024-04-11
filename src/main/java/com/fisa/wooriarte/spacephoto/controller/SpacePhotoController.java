package com.fisa.wooriarte.spacephoto.controller;

import com.fisa.wooriarte.spacephoto.service.SpacePhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class SpacePhotoController {

    public final SpacePhotoService spacePhotoService;
    @Autowired
    public SpacePhotoController(SpacePhotoService spacePhotoService){
        this.spacePhotoService = spacePhotoService;
    }

    /**
     * 그룹(팀) 생성
     * @param files
     * @return
     */
//    @PostMapping(path = "/image", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
//    public ResponseEntity createImage(
////            @RequestPart(value = "id") Long imageId,
//            @RequestPart(value = "file", required = false) MultipartFile file
//    ){
//        s3Service.createImage(file);
//        return new ResponseEntity(null, HttpStatus.OK);
//    }
    @PostMapping(path = "/image", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity createPhoto(
            @RequestPart(value = "files") List<MultipartFile> files
    ){
        spacePhotoService.createPhoto(files);
        return new ResponseEntity(null, HttpStatus.OK);
    }
}