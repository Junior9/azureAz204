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
import com.azure204.cloud.config.AccountSettings;
import com.azure204.cloud.model.Advert;
import com.azure204.cloud.model.User;

@Repository
public class AdvertRepository {

    private CosmosContainer container;
    private CosmosDatabase database;
    private CosmosClient client;
    private final String databaseName = "az204CosmoDb";
    private final String containerName = "advert";
    protected static Logger logger = LoggerFactory.getLogger(UserRepository.class);

    public AdvertRepository(){
        init();
    }

    public void init() {
        ArrayList<String> preferredRegions = new ArrayList<String>();
        preferredRegions.add("West US");

        //  Create sync client
        client = new CosmosClientBuilder()
            .endpoint(AccountSettings.HOST)
            .key(AccountSettings.MASTER_KEY)
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
        container = database.getContainer(containerResponse.getProperties().getId());
    }

    public CosmosPagedIterable<Object> getAdverts() {

        CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();
        queryOptions.setQueryMetricsEnabled(true);

        CosmosPagedIterable<Object> result = container.queryItems(
            "SELECT * FROM c", queryOptions, Object.class);

        return result;
    }

    public CosmosPagedIterable<Object> getAdvertsById(String id) {

        CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();
        queryOptions.setQueryMetricsEnabled(true);

        CosmosPagedIterable<Object> result = container.queryItems(
            "SELECT * FROM c Where c.id = '" + id + "'", queryOptions, Object.class);

        return result;
    }

    public void add(Advert adv) {
        CosmosItemResponse<Advert> respose = container.createItem(adv);
        if(respose.getStatusCode() == StatusCodes.CREATED ||  respose.getStatusCode() == StatusCodes.OK){
            logger.info("[ADVERT CREATED] -- " + adv);
        }else{
            logger.error("[ADVERT NOT CREATED] -- " + adv);
        }
    }


    public void updateAdvert(Advert advert) {
        CosmosItemResponse<Advert> respose =container.upsertItem(advert);
        if(respose.getStatusCode() == StatusCodes.CREATED ||  respose.getStatusCode() == StatusCodes.OK){
            logger.info("[ADVERT UPDATED] -- " + advert);
        }else{
            logger.error("[ADVERT NOT UPDATED] -- " + advert);
        }
    }

    public void delete(String id ) {
        try{
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



}
