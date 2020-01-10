package com.jokkoapps.jokkoapps.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jokkoapps.jokkoapps.model.Widget;

public interface WidgetRepository extends JpaRepository<Widget, Long>{
	
	Widget findByServiceId(Long serviceId);
	Optional<Widget> findById(Long id);
}
