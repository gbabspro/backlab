package com.jokkoapps.jokkoapps.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jokkoapps.jokkoapps.model.Manager;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long>{
	
	Optional<Manager> findByEmail(String email);
	Boolean existsByEmail(String email);
}
