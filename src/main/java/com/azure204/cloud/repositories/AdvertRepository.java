package com.azure204.cloud.repositories;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.implementation.HttpConstants.StatusCodes;
import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.models.CosmosContainerResponse;
import com.azure.cosmos.models.CosmosDatabaseResponse;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.azure204.cloud.common.KeyVault;
import com.azure204.cloud.model.Advert;

import lombok.AllArgsConstructor;



@Repository
@AllArgsConstructor
public class AdvertRepository {

    private final String databaseName = "cosmodbaz204exam";
    private final String containerName = "adverts";
    private KeyVault keyVaultSecrets;
    protected static Logger logger = LoggerFactory.getLogger(AdvertRepository.class);


    public CosmosPagedIterable<Object> getAdverts() {

        CosmosContainer container = getContainer();
        CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();
        queryOptions.setQueryMetricsEnabled(true);

        CosmosPagedIterable<Object> result = container.queryItems(
            "SELECT * FROM c", queryOptions, Object.class);

        return result;
    }

    public CosmosPagedIterable<Object> getAdvertsById(String id) {

        CosmosContainer container = getContainer();
        CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();
        queryOptions.setQueryMetricsEnabled(true);

        CosmosPagedIterable<Object> result = container.queryItems(
            "SELECT * FROM c Where c.id = '" + id + "'", queryOptions, Object.class);

        return result;
    }

    public Advert add(Advert advert) {
        CosmosContainer container = getContainer();
        CosmosItemResponse<Advert> respose = container.createItem(advert);
        respose.getItem();
        if(respose.getStatusCode() == StatusCodes.CREATED ||  respose.getStatusCode() == StatusCodes.OK){
            logger.info("[ADVERT CREATED] -- " + advert);
            return respose.getItem();
        }else{
            logger.error("[ADVERT NOT CREATED] -- " + advert);
            return null; 
        }
    }


    public void updateAdvert(Advert advert) {
        CosmosContainer container = getContainer();
        CosmosItemResponse<Advert> respose =container.upsertItem(advert);
        if(respose.getStatusCode() == StatusCodes.CREATED ||  respose.getStatusCode() == StatusCodes.OK){
            logger.info("[ADVERT UPDATED] -- " + advert);
        }else{
            logger.error("[ADVERT NOT UPDATED] -- " + advert);
        }
    }

    public void delete(String id ) {
        try{
            CosmosContainer container = getContainer();
            CosmosItemResponse<Object> respose = container.deleteItem(id, new PartitionKey(id), new CosmosItemRequestOptions());

            if(respose.getStatusCode() == StatusCodes.NO_CONTENT){
                logger.info("[ADVERT DELETED] id : -- " + id);
            }else{
                logger.error("[ADVERT NOT DELETED]  id -- " + id);
            }
        }catch(Exception e){
            logger.error("[ADVERT NOT DELETED]  id -- " + id + " Error : " + e.getMessage());
        }
        
    }

    private CosmosContainer getContainer(){

        CosmosDatabase database;
        CosmosClient client;
        keyVaultSecrets = new KeyVault();
        ArrayList<String> preferredRegions = new ArrayList<String>();
        preferredRegions.add("West US");

        String cosmoDbkey  = keyVaultSecrets.getSecret("cosmodb204key");
        String cosmoDbHost  = keyVaultSecrets.getSecret("cosmodbhost");


        //  Create sync client
        client = new CosmosClientBuilder()
            .endpoint(cosmoDbHost)
            .key(cosmoDbkey)
            .preferredRegions(preferredRegions)
            .userAgentSuffix("CosmosDBJavaQuickstart")
            .consistencyLevel(ConsistencyLevel.EVENTUAL)
            .buildClient();

        CosmosDatabaseResponse databaseResponse = client.createDatabaseIfNotExists(databaseName);
        database = client.getDatabase(databaseResponse.getProperties().getId());
        
           //  Create container if not exists
        CosmosContainerProperties containerProperties =
            new CosmosContainerProperties(containerName, "/partitionKey");

        CosmosContainerResponse containerResponse = database.createContainerIfNotExists(containerProperties);
  
        return database.getContainer(containerResponse.getProperties().getId());
    }



}
