package com.tutorial.projectservice.service;

import com.tutorial.common.model.Project;
import com.tutorial.projectservice.constant.DefaultProjectValues;
import com.tutorial.projectservice.exception.GeneralException;
import com.tutorial.projectservice.repository.ProjectRepository;
import com.tutorial.projectservice.util.ProjectConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectConverter projectConverter;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectConverter projectConverter) {
        this.projectRepository = projectRepository;
        this.projectConverter = projectConverter;
    }

    public Project getProjectByID(String id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new GeneralException("Not found!", HttpStatus.NOT_FOUND));
    }

    @Cacheable(value = "projects", key = "#room")
    public Project getProjectByRoom(String room) {
        return projectRepository.findFirstByRoom(room)
                .orElseThrow(() -> new GeneralException("Not found!", HttpStatus.NOT_FOUND));
    }

    public boolean existsByRoom(String room) {
        return projectRepository.findFirstByRoom(room).isPresent();
    }

    public Project create(Project project) {
        if (existsByRoom(project.getRoom()))
            throw new GeneralException("Already exists!", HttpStatus.CONFLICT);

        log.info("Project created for room {}", project.getRoom());
        return projectRepository.save(project);
    }

    public Project getOrCreateByDefaultValues(String room) {
        Optional<Project> existing = projectRepository.findFirstByRoom(room);
        if (existing.isPresent()) {
            log.info("Project already exists for room {}", room);
            return existing.get();
        }

        Project project = Project.builder()
                .room(room)
                .html(DefaultProjectValues.HTML)
                .css(DefaultProjectValues.CSS)
                .js(DefaultProjectValues.JS)
                .version(0L) // 👈 ADD
                .build();


        log.info("Default project created for room {}", room);
        return projectRepository.save(project);
    }

    @CachePut(value = "projects", key = "#project.room")
    public Project update(Project project) {
        try {

            return projectRepository
                    .findFirstByRoom(project.getRoom())
                    .map(existing -> {

                        // 🚫 DROP STALE SAVE
                        if (project.getVersion() <= existing.getVersion()) {
                            log.warn("Stale save ignored for room {}", project.getRoom());
                            return existing;
                        }

                        if (project.getHtml() != null)
                            existing.setHtml(project.getHtml());

                        if (project.getCss() != null)
                            existing.setCss(project.getCss());

                        if (project.getJs() != null)
                            existing.setJs(project.getJs());

                        existing.setVersion(project.getVersion()); // 👈 IMPORTANT

                        log.info("Project updated for room {}", project.getRoom());
                        return projectRepository.save(existing);
                    })


                    .orElseGet(() -> {
                        project.setHtml(
                                project.getHtml() == null ? DefaultProjectValues.HTML : project.getHtml());
                        project.setCss(
                                project.getCss() == null ? DefaultProjectValues.CSS : project.getCss());
                        project.setJs(
                                project.getJs() == null ? DefaultProjectValues.JS : project.getJs());

                        log.info("Project created for room {}", project.getRoom());
                        return projectRepository.save(project);
                    });
        }
        catch (org.springframework.dao.DuplicateKeyException e) {
            log.warn("Duplicate insert ignored for room {}", project.getRoom());
            return projectRepository.findFirstByRoom(project.getRoom()).get();
        }
    }


    public String projectToJsonString(Project project) {
        return projectConverter.convertToJsonString(project);
    }
}
