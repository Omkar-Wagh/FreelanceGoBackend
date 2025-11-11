package com.freelancego.service.ClientService;

import com.freelancego.dto.client.ClientDto;

import java.util.*;

public interface ClientService {

    public Map<String,Object> createClient(ClientDto clientDto, String username);

}