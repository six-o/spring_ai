package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Files;

@Repository
public interface FilesRepository extends JpaRepository<Files, Long> {
	// JpaRepository 已經提供 save()/findAll()/findById()…
	Optional<Files> findByUser_UidAndName(String userUid, String name);

	List<Files> findAllByUser_Uid(String userUid);
}