package com.tutorial.projectservice.repository;


import com.tutorial.projectservice.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends MongoRepository<Project,String> {

    Optional<Project> findByRoom(String room);

}
