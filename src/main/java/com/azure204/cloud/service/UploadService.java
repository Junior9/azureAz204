package com.azure204.cloud.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure204.cloud.common.KeyVault;

import jakarta.annotation.PostConstruct;

@Service
public class UploadService {

    private BlobServiceClient blobServiceClient;
    private KeyVault keyVault;

    @PostConstruct
    public void init(){
        this.keyVault = new KeyVault();
        this.blobServiceClient = new BlobServiceClientBuilder().connectionString(this.keyVault.getSecret("storageblobConnectString")).buildClient();
    }

    public String uploadBlob(MultipartFile data, String userId, String advertsId) {
        try {
            String blobName = data.getOriginalFilename();
            BlobClient storageBlobClient = this.blobServiceClient.getBlobContainerClient("adverts").getBlobClient(blobName);
            storageBlobClient.upload(data.getInputStream(),data.getSize(), true);
            String url = storageBlobClient.getBlobUrl();
            return url;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

}
