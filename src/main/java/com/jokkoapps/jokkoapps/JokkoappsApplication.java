package com.jokkoapps.jokkoapps;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import com.jokkoapps.jokkoapps.model.Role;
import com.jokkoapps.jokkoapps.model.RoleName;
import com.jokkoapps.jokkoapps.model.User;
import com.jokkoapps.jokkoapps.repository.RoleRepository;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EntityScan(basePackageClasses = { 
		JokkoappsApplication.class,
		Jsr310JpaConverters.class 
})
public class JokkoappsApplication implements CommandLineRunner{
	
	private final RoleRepository roleRepository;
	 
	 
    public JokkoappsApplication(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

	@PostConstruct
	void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	public static void main(String[] args) {
		SpringApplication.run(JokkoappsApplication.class, args);
	}

    @Override
    public void run(String... args) throws Exception {
        Long roles = roleRepository.count();

        if (roles <= 0) {
            roleRepository.save(new Role(RoleName.ROLE_USER));
            roleRepository.save(new Role(RoleName.ROLE_AGENT));
            roleRepository.save(new Role(RoleName.ROLE_MANAGER));
        }
    }
}
