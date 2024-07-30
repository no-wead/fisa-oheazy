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

@RequestMapping("/api/space-photos")
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
    @PostMapping(path = "/{space-item-id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addPhotos(@PathVariable("space-item-id") Long spaceItemId, @RequestPart(value = "file", required = false) List<MultipartFile> multipartFileList) throws IOException {
        if (multipartFileList == null) {
            return ResponseEntity.badRequest().body("No files provided");
        } else {
            return spacePhotoService.addPhotos(multipartFileList, spaceItemId);
        }
    }

    /**
     * 2. 사진 개별 삭제 - S3, DB
     * @param photoIds : 삭제할 photoIds
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
     * @param spaceItemId : 삭제할 사진을 가지고 있는 spaceItemId
     * @return
     */
    @DeleteMapping("/{space-item-id}")
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
     * @param spaceItemId : 출력할 사진을 가지고 있는 spaceItemId
     * @return photos 객체 정보 출력
     */
    @GetMapping("/{space-item-id}")
    public ResponseEntity<List<SpacePhotoDTO>> getPhotosBySpaceItemId(@PathVariable("space-item-id") Long spaceItemId) {
        List<SpacePhotoDTO> photos = spacePhotoService.getPhotosBySpaceItemId(spaceItemId);
        return new ResponseEntity<>(photos, HttpStatus.OK);
    }

    /**
     * 5. 공간 사진 수정 - S3, DB : 해당 공간의 사진 데이터 삭제 후 덮어씌우기
     * @param spaceItemId : 수정할 spaceItemId
     * @param multipartFileList : 수정에서 삽입될 사진 파일 리스트
     * @return
     * @throws IOException
     */
    @PutMapping(path = "/{space-item-id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> editPhotos(@PathVariable("space-item-id") Long spaceItemId, @RequestPart(value = "file", required = false) List<MultipartFile> multipartFileList) throws IOException {
        if (multipartFileList == null) {
            return ResponseEntity.badRequest().body("No files provided");
        } else {
            // 먼저 해당 공간 아이템의 모든 사진 삭제
            spacePhotoService.deleteAllPhotos(spaceItemId);

            // 새로운 사진 파일 추가
            return spacePhotoService.addPhotos(multipartFileList, spaceItemId);
        }
    }
}