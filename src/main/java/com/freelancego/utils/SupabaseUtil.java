package com.freelancego.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Component
public class SupabaseUtil {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.public-bucket}")
    private String publicBucket;

    private final WebClient webClient;

    public SupabaseUtil(@Value("${supabase.url}") String supabaseUrl,
                        @Value("${supabase.key}") String supabaseKey) {
        this.webClient = WebClient.builder()
                .baseUrl(supabaseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + supabaseKey)
                .build();
    }

    /**
     * Upload file to public bucket
     * @param file MultipartFile
     * @return Public URL of uploaded file
     */
    public String uploadFile(MultipartFile file) throws Exception {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        // Upload file
        webClient.post()
                .uri("/storage/v1/object/" + publicBucket + "/" + fileName)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", resource))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Return public URL
        return supabaseUrl + "/storage/v1/object/public/" + publicBucket + "/" + fileName;
    }
}
