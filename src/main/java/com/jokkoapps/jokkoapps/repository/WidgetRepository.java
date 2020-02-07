package com.jokkoapps.jokkoapps.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jokkoapps.jokkoapps.model.Widget;

public interface WidgetRepository extends JpaRepository<Widget, Long>{
	

	Optional<Widget> findByServiceId(Long serviceId);
	Optional<Widget> findById(Long id);
	
	boolean existsByServiceId(Long serviceId);
}
