package com.freelancego.service.ClientService;

import com.freelancego.dto.client.ClientDto;
import com.freelancego.dto.client.JobDto;
import com.freelancego.enums.Role;
import com.freelancego.model.Bid;
import com.freelancego.model.Client;
import com.freelancego.model.Job;
import com.freelancego.model.User;
import com.freelancego.repo.ClientRepository;
import com.freelancego.repo.JobRepository;
import com.freelancego.repo.UserRepository;
import com.freelancego.service.UserService.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ClientService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private JobRepository jobRepository;


    public ResponseEntity<?> createClient(ClientDto clientDto, String username) {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }

        Client existingClient = clientRepository.findByUser(user);
        if (existingClient != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Freelancer profile already exists"));
        }
        Client newClient = new Client();
        newClient.setUser(user);
        newClient.setCompanyName(clientDto.companyName());
        newClient.setCompanyUrl(clientDto.companyUrl());
        newClient.setBio(clientDto.bio());
        newClient.setPhone(clientDto.phone());
        clientRepository.save(newClient);
        user.setRole(Role.CLIENT);
        userRepository.save(user);
        String token = jwtService.generateToken(user.getEmail(),Role.CLIENT.name());
        return ResponseEntity.ok(
                Map.of(
                        "message", "Client profile created successfully",
                        "id", newClient.getId(),
                        "token", token
                )
        );
    }

    public ResponseEntity<?> createPost(JobDto jobDto,String auth) {
        Job newJob = new Job();
        User user = userRepository.findByEmail(auth);
        Client client = clientRepository.findByUser(user);
        String requiredSkills = String.join(",", jobDto.requiredSkills());
        newJob.setJobTitle(jobDto.jobTitle());
        newJob.setJobDescription(jobDto.jobDescription());
        newJob.setExperienceLevel(Role.valueOf(jobDto.ExperienceLevel()));
        newJob.setRequiredSkills(requiredSkills);
        newJob.setRequirement(jobDto.requirement());
        newJob.setProjectStartTime(jobDto.projectStartTime());
        newJob.setProjectEndTime(jobDto.projectEndTime());
        newJob.setClient(client);
        jobRepository.save(newJob);
        return ResponseEntity.ok(Map.of("message","job created successfully"));
    }

    public ResponseEntity<?> getPostByClient(String name) {
        User user = userRepository.findByEmail(name);
        Client client = clientRepository.findByUser(user);
        List<Job> job = jobRepository.getPostByClient(client);
        List<JobDto> jobDto = new ArrayList<>();
        for(Job jobs : job){
            String[] skills = jobs.getRequiredSkills().split(",");
            JobDto jobDto1 = new JobDto(
                    jobs.getJobTitle(),skills,jobs.getExperienceLevel().name(),
                    jobs.getJobDescription(),jobs.getRequirement(),jobs.getProjectStartTime(),
                    jobs.getProjectEndTime(),jobs.getBudget()
                    );
            jobDto.add(jobDto1);
        }
        return ResponseEntity.ok(Map.of("posts",jobDto));
    }
}

/*
    private String jobTitle;
    private String requiredSkills;
    private Role ExperienceLevel;
    private String jobDescription;
    @Lob
    private String requirement;
    private ZoneOffset projectStartTime;
    private ZoneOffset projectEndTime;
    private Double budget;
    @ManyToOne
    private Client client;

    @OneToMany(mappedBy = "job")
    private List<Bid> bids;
 */