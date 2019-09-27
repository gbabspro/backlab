package com.jokkoapps.jokkoapps.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jokkoapps.jokkoapps.model.Offre;
import com.jokkoapps.jokkoapps.model.OffreName;

public interface OffreRepository extends JpaRepository<Offre, Long>{
	Offre findByName(OffreName offreName);
}
