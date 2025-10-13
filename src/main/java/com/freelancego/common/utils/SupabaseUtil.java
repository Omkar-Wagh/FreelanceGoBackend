//package com.freelancego.common.utils;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.ByteArrayResource;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Component;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.reactive.function.BodyInserters;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import java.util.Objects;
//import java.util.UUID;
//
//@Component
//public class SupabaseUtil {
//
//    @Value("${supabase.url}")
//    private String supabaseUrl;
//
//    @Value("${supabase.key}")
//    private String supabaseKey;
//
//    @Value("${supabase.public-bucket}")
//    private String publicBucket;
//
//    private final WebClient webClient;
//
//    public SupabaseUtil(@Value("${supabase.url}") String supabaseUrl,
//                        @Value("${supabase.key}") String supabaseKey) {
//        this.webClient = WebClient.builder()
//                .baseUrl(supabaseUrl)
//                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + supabaseKey)
//                .build();
//    }
//
//    /**
//     * Upload file to public bucket
//     * @param file MultipartFile
//     * @return Public URL of uploaded file
//     */
//    public String uploadFile(MultipartFile file, String urlEncodedName) throws Exception {
//        String fileName = UUID.randomUUID() + "_" + urlEncodedName;
//
//        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
//            @Override
//            public String getFilename() {
//                return urlEncodedName;
//            }
//        };
//
//        // Upload file
//        webClient.post()
//                .uri("/storage/v1/object/" + publicBucket + "/" + fileName)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + supabaseKey)
//                .contentType(MediaType.parseMediaType(Objects.requireNonNull(file.getContentType())))
//                .bodyValue(resource)
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//
//        return supabaseUrl + "/storage/v1/object/public/" + publicBucket + "/" + fileName;
//    }
//}


package com.freelancego.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

@Component
public class SupabaseUtil {

    private final WebClient webClient;
    private final String supabaseUrl;
    private final String supabaseKey;
    private final String publicBucket;

    public SupabaseUtil(
            @Value("${supabase.url}") String supabaseUrl,
            @Value("${supabase.key}") String supabaseKey,
            @Value("${supabase.public-bucket}") String publicBucket
    ) {
        this.supabaseUrl = supabaseUrl;
        this.supabaseKey = supabaseKey;
        this.publicBucket = publicBucket;
        this.webClient = WebClient.builder()
                .baseUrl(supabaseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + supabaseKey)
                .build();
    }

    /**
     * Upload file to Supabase public bucket
     * @param file MultipartFile (uploaded file)
     * @return Public URL of uploaded file
     * @throws Exception if upload fails
     */
    public String uploadFile(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null.");
        }

        String originalName = Objects.requireNonNull(file.getOriginalFilename(), "File must have a name");
        String sanitizedName = originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
        String encodedName = URLEncoder.encode(sanitizedName, StandardCharsets.UTF_8);
        String fileName = UUID.randomUUID() + "_" + encodedName;

        // Wrap file bytes
        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };

        // Determine media type safely
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (file.getContentType() != null) {
            try {
                mediaType = MediaType.parseMediaType(file.getContentType());
            } catch (Exception ignored) {
            }
        }

        // Upload file to Supabase storage
        try {
            webClient.post()
                    .uri("/storage/v1/object/" + publicBucket + "/" + fileName)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + supabaseKey)
                    .contentType(mediaType)
                    .body(BodyInserters.fromResource(resource))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response ->
                            response.bodyToMono(String.class)
                                    .flatMap(error -> Mono.error(new RuntimeException("Supabase upload failed: " + error)))
                    )
                    .bodyToMono(String.class)
                    .block(); // blocking for synchronous call

        } catch (WebClientResponseException ex) {
            throw new RuntimeException("Supabase upload error: " + ex.getResponseBodyAsString(), ex);
        } catch (Exception ex) {
            throw new RuntimeException("Unexpected error while uploading to Supabase: " + ex.getMessage(), ex);
        }

        // Return public file URL
        return supabaseUrl + "/storage/v1/object/public/" + publicBucket + "/" + fileName;
    }

}
