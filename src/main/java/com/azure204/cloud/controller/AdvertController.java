package com.azure204.cloud.controller;

import java.net.URISyntaxException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.azure.cosmos.implementation.HttpConstants.StatusCodes;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.azure204.cloud.model.Advert;
import com.azure204.cloud.service.AdvertService;
import com.azure204.cloud.service.ChatGtpService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class AdvertController {

    private AdvertService advertService;
    private ChatGtpService chatGtpService;

    @GetMapping("/advert")
    public CosmosPagedIterable<Object> getAdverts(){
        return this.advertService.getAdverts();
    }

    @GetMapping("/advert/{id}")
    public CosmosPagedIterable<Object> getAdvertById(@PathVariable String id){
        return this.advertService.getAdvetById(id);
    }

    @PostMapping("/advert")
    public void add(@RequestBody Advert advert){
        this.advertService.addAdvert(advert);
    }

    @GetMapping("/ai/generate/{msn}")
    public ResponseEntity<Advert> generateTextAI(@PathVariable String msn) throws URISyntaxException{
        Advert advertCreated = this.chatGtpService.generate(msn);
        return ResponseEntity.status(StatusCodes.CREATED).body(advertCreated);
    }

    @PostMapping("/upload/imagine")
    public void uploadImagine(@RequestBody Advert advert){
        this.advertService.addAdvert(advert);
    }

    @PutMapping("/advert")
    public void update(@RequestBody Advert advert){
        this.advertService.update(advert);
    }

    @DeleteMapping("/advert/{id}")
    public void delete(@PathVariable String id){
        this.advertService.delete(id);
    }

}
