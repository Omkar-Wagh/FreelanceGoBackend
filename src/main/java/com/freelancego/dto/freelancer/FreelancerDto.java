package com.freelancego.dto.freelancer;

import com.freelancego.dto.user.UserDto;

import java.util.List;

public record FreelancerDto(int id, String designation, String bio, String portfolioUrl, List<String> skills, String experienceLevel, String phone, UserDto userDto) {
}
