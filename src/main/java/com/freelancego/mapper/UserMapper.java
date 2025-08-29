package com.freelancego.mapper;

import com.freelancego.dto.user.UserDto;
import com.freelancego.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.Base64;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "imageData", target = "imageData", qualifiedByName = "byteArrayToBase64")
    UserDto toDTO(User user);

    @Mapping(source = "imageData", target = "imageData", qualifiedByName = "base64ToByteArray")
    User toEntity(UserDto dto);

    // Custom converters
    @Named("byteArrayToBase64")
    static String byteArrayToBase64(byte[] bytes) {
        return bytes != null ? Base64.getEncoder().encodeToString(bytes) : null;
    }

    @Named("base64ToByteArray")
    static byte[] base64ToByteArray(String base64) {
        return base64 != null ? Base64.getDecoder().decode(base64) : null;
    }
}
