package com.azure204.cloud.common;

import org.springframework.stereotype.Component;

import com.azure.core.exception.ClientAuthenticationException;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;

@Component
public class KeyVault {

    SecretClient secretClient = new SecretClientBuilder()
    .vaultUrl("https://keyaz204vault.vault.azure.net/")
    .credential(new DefaultAzureCredentialBuilder().build())
    .buildClient();


    public String getSecret(String key){

         try {
            KeyVaultSecret secret = secretClient.getSecret(key);
            return secret.getValue();
        } catch (ClientAuthenticationException e) {
            //Handle Exception
            e.printStackTrace();
            return "";
        }
    }

}
