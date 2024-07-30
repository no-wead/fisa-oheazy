package com.fisa.wooriarte.projectphoto.repository;

import com.fisa.wooriarte.projectItem.domain.ProjectItem;
import com.fisa.wooriarte.projectphoto.domain.ProjectPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectPhotoRepository extends JpaRepository<ProjectPhoto, Long> {
    List<ProjectPhoto> findByProjectItem(Optional<ProjectItem> projectItem);
}

