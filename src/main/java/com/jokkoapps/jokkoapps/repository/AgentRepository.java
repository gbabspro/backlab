package com.jokkoapps.jokkoapps.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jokkoapps.jokkoapps.model.Personnel;

@Repository
public interface AgentRepository extends JpaRepository<Personnel, Long>{

	Optional<Personnel> findById(Long id);
	List<Personnel> findByServiceId(Long serviceId);

	Optional<Personnel> findByEmail(String email);
	Boolean existsByEmail(String email);
}
