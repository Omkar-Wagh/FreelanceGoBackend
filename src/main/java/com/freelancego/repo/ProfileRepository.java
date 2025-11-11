package com.freelancego.repo;

import com.freelancego.model.Profile;
import com.freelancego.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile,Integer> {
    Optional<Profile> findByUser(User user);

}
