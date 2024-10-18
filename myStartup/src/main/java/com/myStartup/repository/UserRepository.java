package com.myStartup.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myStartup.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);
}