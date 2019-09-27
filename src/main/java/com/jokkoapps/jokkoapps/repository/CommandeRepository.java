package com.jokkoapps.jokkoapps.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jokkoapps.jokkoapps.model.Commande;
import com.jokkoapps.jokkoapps.model.Offre;

public interface CommandeRepository extends JpaRepository<Commande, Long>{

}
