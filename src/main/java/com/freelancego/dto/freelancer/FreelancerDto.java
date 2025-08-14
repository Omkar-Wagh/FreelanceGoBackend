package com.freelancego.dto.freelancer;

import java.util.List;

public record FreelancerDto(int id, String bio, String portfolioUrl, List<String> skills, String experienceLevel,String phone) {
}