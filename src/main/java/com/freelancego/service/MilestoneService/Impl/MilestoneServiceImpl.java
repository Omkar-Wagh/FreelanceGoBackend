package com.freelancego.service.MilestoneService.Impl;

import com.freelancego.exception.UserNotFoundException;
import com.freelancego.model.User;
import com.freelancego.repo.UserRepository;
import com.freelancego.service.MilestoneService.MilestoneService;
import org.springframework.stereotype.Service;

@Service
public class MilestoneServiceImpl implements MilestoneService {

    final private UserRepository userRepository;

    public MilestoneServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Object getMileStone(String name) {
        User user = userRepository.findByEmail(name).orElseThrow(
                () -> new UserNotFoundException("user not found"));

        return null;
    }
}
