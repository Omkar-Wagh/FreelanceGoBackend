package com.freelancego.service.ClientService;

import com.freelancego.dto.client.ClientDto;
import org.springframework.security.core.Authentication;

import java.util.*;

public interface ClientService {

    public Map<String,Object> createClient(ClientDto clientDto, String username);

    public Map<String,Object> getProfile(int id, Authentication auth);
}