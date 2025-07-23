package com.example.imageSpring.images.repoFileSys;

import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.imageSpring.images.entityFileSys.ImageData;

@Repository
public interface ImageRepository extends JpaRepository<ImageData, Long> {

	Optional<ImageData> findByName(String fileName);
	
	
}
