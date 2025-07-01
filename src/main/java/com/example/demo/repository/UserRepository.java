package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	// 這裡不需要額外的方法，JpaRepository 提供了 save、findById、existsById…
}