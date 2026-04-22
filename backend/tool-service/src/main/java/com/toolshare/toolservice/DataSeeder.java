package com.toolshare.toolservice;

import com.toolshare.toolservice.model.Tool;
import com.toolshare.toolservice.model.ToolCondition;
import com.toolshare.toolservice.repository.ToolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ToolRepository toolRepo;

    public DataSeeder(ToolRepository toolRepo) {
        this.toolRepo = toolRepo;
    }

    @Override
    public void run(String... args) {
        if (toolRepo.count() == 0) {
            // Naveen's tools (userId=2 from user-service seed)
            toolRepo.save(createTool("Bosch Power Drill", "Cordless 18V, comes with bit set", "Power Tools", ToolCondition.GOOD, 2L, "naveen"));
            toolRepo.save(createTool("Extension Ladder 20ft", "Aluminium, supports heavy loads", "Garden", ToolCondition.FAIR, 2L, "naveen"));
            toolRepo.save(createTool("Camping Tent 4-Person", "Waterproof dome tent, used on Manali trip", "Camping", ToolCondition.GOOD, 2L, "naveen"));

            // Kumar's tools (userId=3)
            toolRepo.save(createTool("Circular Saw", "DeWalt 7.25 inch blade", "Power Tools", ToolCondition.NEW, 3L, "kumar"));
            toolRepo.save(createTool("Pressure Washer", "2000 PSI electric, great for driveways", "Garden", ToolCondition.GOOD, 3L, "kumar"));
            toolRepo.save(createTool("Butterfly Mixer Grinder", "750W, 3 jars, perfect for kitchen use", "Kitchen", ToolCondition.GOOD, 3L, "kumar"));

            System.out.println(">> Seeded 6 sample tools");
        }
    }

    private Tool createTool(String name, String desc, String category, ToolCondition condition, Long ownerId, String ownerUsername) {
        Tool t = new Tool();
        t.setName(name);
        t.setDescription(desc);
        t.setCategory(category);
        t.setToolCondition(condition);
        t.setOwnerId(ownerId);
        t.setOwnerUsername(ownerUsername);
        t.setAvailable(true);
        return t;
    }
}
