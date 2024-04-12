package com.fisa.wooriarte.spacephoto.repository;

import com.fisa.wooriarte.spacephoto.domain.SpacePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpacePhotoRepository extends JpaRepository<SpacePhoto, Long> {

    List<SpacePhoto> findBySpaceItemId(Long spaceItemId);
}

