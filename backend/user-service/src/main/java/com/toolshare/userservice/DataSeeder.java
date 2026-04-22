package com.toolshare.userservice;

import com.toolshare.userservice.model.Role;
import com.toolshare.userservice.model.User;
import com.toolshare.userservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Seeds some test data so we don't start with empty DB every time
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public DataSeeder(UserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (userRepo.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@toolshare.com");
            admin.setPassword(encoder.encode("admin123"));
            admin.setFullName("Admin User");
            admin.setRole(Role.ADMIN);
            userRepo.save(admin);

            User naveen = new User();
            naveen.setUsername("naveen");
            naveen.setEmail("naveen.naveen@example.com");
            naveen.setPassword(encoder.encode("naveen123"));
            naveen.setFullName("Naveen Kumar");
            naveen.setPhone("98765-43210");
            naveen.setRole(Role.MEMBER);
            userRepo.save(naveen);

            User kumar = new User();
            kumar.setUsername("kumar");
            kumar.setEmail("kumar.kishan@example.com");
            kumar.setPassword(encoder.encode("kumar123"));
            kumar.setFullName("Kumar Kishan");
            kumar.setPhone("91234-56789");
            kumar.setRole(Role.MEMBER);
            userRepo.save(kumar);

            System.out.println(">> Seeded 3 users (admin, naveen, kumar)");
        }
    }
}
