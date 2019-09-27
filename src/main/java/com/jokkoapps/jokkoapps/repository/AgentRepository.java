package com.jokkoapps.jokkoapps.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jokkoapps.jokkoapps.model.Agent;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long>{

	Optional<Agent> findById(Long id);
	List<Agent> findByServiceId(Long serviceId);
}
