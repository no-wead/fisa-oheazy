package com.fisa.wooriarte.projectItem.controller;

import com.fisa.wooriarte.projectItem.dto.SpaceRentalDto;
import com.fisa.wooriarte.projectItem.repository.ProjectItemRepository;
import com.fisa.wooriarte.projectItem.service.ProjectItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/project-item")
@RestController
public class ProjectItemController {

    private static final Logger log = LoggerFactory.getLogger(ProjectItemController.class);

    private final ProjectItemService projectItemService;
    private final ProjectItemRepository projectItemRepository;

    @Autowired
    public ProjectItemController(ProjectItemService projectItemService, ProjectItemRepository projectItemRepository) {
        this.projectItemService = projectItemService;
        this.projectItemRepository = projectItemRepository;
    }

    @GetMapping("")
    public ResponseEntity<?> getAllProjectItemInfo() {
        try {
            List<SpaceRentalDto> items = projectItemService.findAll();
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            log.error("Failed to retrieve all project items", e);
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to retrieve project item information."));
        }
    }

    @GetMapping("/project-manager/{project-manager-id}")
    public ResponseEntity<List<SpaceRentalDto>> getProjectItemByProjectManagerId(@PathVariable("project-manager-id") Long projectManagerId) {
        List<SpaceRentalDto> projectItemDtoList = projectItemService.findByProjectManagerId(projectManagerId);
        return ResponseEntity.ok(projectItemDtoList);
    }


    @PostMapping("")
    public ResponseEntity<?> addProjectItem(@RequestBody SpaceRentalDto projectItemDTO) {
        try {
            projectItemService.addProjectItem(projectItemDTO);
            return ResponseEntity.ok(Map.of("message", "Project item successfully added."));
        } catch (Exception e) {
            log.error("Failed to add project item", e);
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to add project item."));
        }
    }

    @GetMapping("/{project-item-id}")
    public ResponseEntity<?> getProjectItemInfo(@PathVariable(name = "project-item-id") Long projectItemId) {
        try {
            return projectItemService.findByProjectItemId(projectItemId)
                    .map(item -> ResponseEntity.ok(item))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Failed to retrieve project item with id: {}", projectItemId, e);
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to retrieve project item details."));
        }
    }

    @PutMapping("/{project-item-id}")
    public ResponseEntity<?> updateProjectItem(@PathVariable(name = "project-item-id") Long projectItemId, @RequestBody SpaceRentalDto projectItemDTO) {
        try {
            projectItemService.updateProjectItem(projectItemId, projectItemDTO);
            return ResponseEntity.ok(Map.of("message", "Project item successfully updated."));
        } catch (Exception e) {
            log.error("Failed to update project item with id: {}", projectItemId, e);
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to update project item."));
        }
    }

    @DeleteMapping("/{project-item-id}")
    public ResponseEntity<?> deleteProjectItem(@PathVariable(name = "project-item-id") Long projectItemId) {
        try {
            projectItemService.deleteProjectItem(projectItemId);
            return ResponseEntity.ok(Map.of("message", "Project item successfully deleted."));
        } catch (Exception e) {
            log.error("Failed to delete project item with id: {}", projectItemId, e);
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to delete project item."));
        }
    }


}
