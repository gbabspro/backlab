package com.jokkoapps.jokkoapps.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jokkoapps.jokkoapps.model.Service;
import com.jokkoapps.jokkoapps.model.ServiceType;
import com.jokkoapps.jokkoapps.model.User;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long>{
	
    List<Service> findByManagerId(Long managerid);
    Service findByTypeService(ServiceType serviceName); 
    boolean existsByDomaine(String domaine);
}
