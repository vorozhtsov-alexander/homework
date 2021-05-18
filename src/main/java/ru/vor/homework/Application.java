package ru.vor.homework;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.vor.homework.security.SecurityConfig;
import ru.vor.homework.user.User;
import ru.vor.homework.user.UserRepository;

@SpringBootApplication
public class Application implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) {

		//add default admin user
		User admin = userRepository.findByRole(SecurityConfig.ROLE_ADMIN).block();

		if (admin == null) {
			admin = new User();
			admin.setId(Uuids.timeBased());
			admin.setEmail("admin");
			admin.setLastName("vor");
			admin.setFirstName("alex");
			admin.setPassword(passwordEncoder.encode("admin"));
			admin.setRole(SecurityConfig.ROLE_ADMIN);
			userRepository.save(admin).block();
		}
	}
}
