package com.jokkoapps.jokkoapps.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jokkoapps.jokkoapps.model.BouttonAppelConf;

public interface BtnRepository extends JpaRepository<BouttonAppelConf, Long>{

	BouttonAppelConf findByServiceId(Long serviceId);
}
