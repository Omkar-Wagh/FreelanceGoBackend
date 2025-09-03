package com.freelancego.dto.client;

import com.freelancego.dto.user.UserDto;

public record ClientDto(int id, String companyName, String companyUrl, String bio, String phone, UserDto userDto) {
}
