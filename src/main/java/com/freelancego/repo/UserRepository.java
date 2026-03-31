package com.freelancego.repo;

import com.freelancego.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    @Query("""
        SELECT u FROM User u 
        JOIN u.profile p 
        JOIN p.skills s 
        WHERE s IN :skills
        """)
    List<User> findFreelancersBySkills(@Param("skills") List<String> skills);

     Optional<User> findByEmail(String email);
}
