package com.example.imageSpring.images.repoFileSys;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.imageSpring.images.entityFileSys.FileData;

@Repository
public interface FileDataRepo extends JpaRepository<FileData, Integer> {
    List<FileData> findAllByName(String name);
}
