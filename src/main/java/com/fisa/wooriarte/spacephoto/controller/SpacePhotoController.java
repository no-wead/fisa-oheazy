package com.fisa.wooriarte.spacephoto.controller;

import com.fisa.wooriarte.spacephoto.service.SpacePhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/space-photo")
public class SpacePhotoController {

    private final SpacePhotoService spacePhotoService;

    @Autowired
    public SpacePhotoController(SpacePhotoService spacePhotoService) {
        this.spacePhotoService = spacePhotoService;
    }

    @PostMapping("")
    public String addSpacePhoto(@RequestBody MultipartFile image)  {

        return "fail";
    }
}
