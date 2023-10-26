package com.cydeo.controller;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.entity.ResponseWrapper;
import com.cydeo.service.ProjectService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/project")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    @RolesAllowed({"Manager","Admin"})
    public ResponseEntity<ResponseWrapper> getProjects(){
        List<ProjectDTO> projectDTOList = projectService.listAllProjects();
        return ResponseEntity.ok(new ResponseWrapper("Projects are successfully retrieved",projectDTOList, HttpStatus.OK));
    }

    @GetMapping("/{code}")
    @RolesAllowed("Manager")
    public ResponseEntity<ResponseWrapper> getProjectByCode(@PathVariable("code") String code){
        //@PathVariable = "catch from the uri
        ProjectDTO projectDTO = projectService.getByProjectCode(code);
        return ResponseEntity.ok(new ResponseWrapper("Project is successfully retrieved",projectDTO,HttpStatus.OK));
    }

    @PostMapping
    @RolesAllowed("Manager")
    public ResponseEntity<ResponseWrapper> createProject(@RequestBody ProjectDTO project){
        //when creating a project, I shouldn't pass completeTaskCounts and unfinishedTaskCounts!
        //go to ProjectDTO --> @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        projectService.save(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseWrapper("Project is successfully created",HttpStatus.CREATED));
    }

    @PutMapping
    @RolesAllowed("Manager")
    public ResponseEntity<ResponseWrapper> updateProject(@RequestBody ProjectDTO project){
        projectService.update(project);
        return ResponseEntity.ok(new ResponseWrapper("Project is successfully updated",project,HttpStatus.OK));
    }

    @DeleteMapping("/{projectCode}")
    @RolesAllowed("Manager")
    public ResponseEntity<ResponseWrapper> deleteProject(@PathVariable("projectCode") String code){
        projectService.delete(code);
        return ResponseEntity.ok(new ResponseWrapper("Project is successfully deleted",HttpStatus.OK));
    }

//    @GetMapping("/manager/project-status")
//    @RolesAllowed("Manager")
//    public ResponseEntity<ResponseWrapper> getProjectByManager(){
//        List<ProjectDTO> projects = projectService.listAllProjectDetails();
//        return ResponseEntity.ok(new ResponseWrapper("Projects are successfully retrieved",projects,HttpStatus.OK));
//    }

    @PutMapping ("/manager/complete/{projectCode}")    //manager will be updating one of the fields (change status to COMPLETE)
    @RolesAllowed("Manager")
    public ResponseEntity<ResponseWrapper> managerCompleteProject(@PathVariable("projectCode") String code){
        projectService.complete(code);
        return ResponseEntity.ok(new ResponseWrapper("Project is successfully completed",HttpStatus.OK));
    }

}
