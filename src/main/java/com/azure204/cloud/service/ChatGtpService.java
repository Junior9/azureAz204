package com.azure204.cloud.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.azure204.cloud.common.KeyVault;
import com.azure204.cloud.dto.RequestChatGTP;
import com.azure204.cloud.dto.ResponseChatGTP;
import com.azure204.cloud.model.Advert;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ChatGtpService {

    private Environment env;
    private KeyVault keyVaultSecrets;
    private final String URL = "https://";


    public Advert generate(String msn) throws URISyntaxException{

        RestTemplate restTemplate = new RestTemplate();
        String openAIKey =   keyVaultSecrets.getSecret("openiasecretkey");

        StringBuilder sbuilderBearer = new StringBuilder().append("Bearer ").append(openAIKey);

        URI uri = new URI("https://api.openai.com/v1/chat/completions");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", sbuilderBearer.toString());

        List<RequestChatGTP.Message> messages = new ArrayList<>();

        String prompt = "";

        messages.add(new RequestChatGTP.Message("user",msn));

        RequestChatGTP request = RequestChatGTP.builder()
            .messages(messages)
            .n(1)
            .max_tokens(900)
            //.temperature("1")
            .model("gpt-4-1106-preview")
            .build();

        HttpEntity<Object> entity = new HttpEntity<>(request, headers);
        ResponseEntity<ResponseChatGTP> result = restTemplate.postForEntity(uri, entity, ResponseChatGTP.class);
        

        String title = "Static title";
        String description = "";
        StringBuilder sbuilder = new StringBuilder();

        if(result.getStatusCode().is2xxSuccessful()){
            
            result.getBody().getChoices().forEach(r -> {sbuilder.append(r); });
        }

        description = sbuilder.toString();

        return Advert.builder().title(title).description(description).build();
    }

}
