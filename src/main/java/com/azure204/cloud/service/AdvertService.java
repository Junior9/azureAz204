package com.azure204.cloud.service;

import org.springframework.stereotype.Service;

import com.azure.cosmos.util.CosmosPagedIterable;
import com.azure204.cloud.model.Advert;
import com.azure204.cloud.repositories.AdvertRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AdvertService {

    private AdvertRepository advertRepository;

    public CosmosPagedIterable<Object> getAdverts(){
        return this.advertRepository.getAdverts();
    }

    public CosmosPagedIterable<Object> getAdvetById(String id) {
        return this.advertRepository.getAdvertsById(id);
    }

    public void addAdvert(Advert advert){
        this.advertRepository.add(advert);
    }

    public void update(Advert advert) {
        this.advertRepository.updateAdvert(advert);
    }

    public void delete(String id) {
        this.advertRepository.delete(id);
    }

}
