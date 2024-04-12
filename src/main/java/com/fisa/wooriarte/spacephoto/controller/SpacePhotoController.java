package com.fisa.wooriarte.spacephoto.controller;

import com.fisa.wooriarte.spacephoto.dto.SpacePhotoDTO;
import com.fisa.wooriarte.spacephoto.service.SpacePhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class SpacePhotoController {

    private final SpacePhotoService spacePhotoService;

    @Autowired
    public SpacePhotoController(SpacePhotoService spacePhotoService) {
        this.spacePhotoService = spacePhotoService;
    }

    /**
     * 1. 사진 파일 추가 - S3, DB
     * @param multipartFileList : 사진 file 리스트
     * @param spaceItemId : 매핑될 spaceItemId
     * @return
     * @throws IOException
     */
    @PostMapping(path = "/{space-item-id}/add-space-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addPhotos(@PathVariable("space-item-id") Long spaceItemId, @RequestPart(value = "file", required = false) List<MultipartFile> multipartFileList) throws IOException {
        System.out.println(spaceItemId);
        if (multipartFileList == null) {
            return ResponseEntity.badRequest().body("No files provided");
        } else {
            return spacePhotoService.addPhotos(multipartFileList, spaceItemId);
        }
    }

    /**
     * 2. 사진 개별 삭제 - S3, DB
     * @param photoIds
     * @return
     */
    @DeleteMapping("/delete-space-photo")
    public ResponseEntity<String> deletePhotos(@RequestParam List<Long> photoIds) {
        try {
            spacePhotoService.deletePhotosBySpaceId(photoIds);
            return ResponseEntity.ok("Photos deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * 3. spaceItemId에 해당하는 모든 사진 삭제 - S3, DB
     * @param spaceItemId
     * @return
     */
    @DeleteMapping("/{space-item-id}/delete-all-space-photo")
    public ResponseEntity<String> deleteAllPhotos(@PathVariable("space-item-id") Long spaceItemId) {
        try {
            spacePhotoService.deleteAllPhotos(spaceItemId);
            return ResponseEntity.ok("All photos with space item ID " + spaceItemId + " deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     *  4. SpaceItemId에 해당하는 모든 사진 출력
     * @param spaceItemId
     * @return photos 객체 정보 출력
     */
    @GetMapping("/{space-item-id}/space-photos")
    public ResponseEntity<List<SpacePhotoDTO>> getPhotosBySpaceItemId(@PathVariable("space-item-id") Long spaceItemId) {
        List<SpacePhotoDTO> photos = spacePhotoService.getPhotosBySpaceItemId(spaceItemId);
        return new ResponseEntity<>(photos, HttpStatus.OK);
    }
}