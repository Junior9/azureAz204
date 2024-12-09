package com.azure204.cloud.repositories;

import java.util.ArrayList;

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
import com.azure204.cloud.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class UserRepository {

    private CosmosContainer container;
    private CosmosDatabase database;
    private CosmosClient client;
    private final String databaseName = "az204CosmoDb";
    private final String containerName = "user";
    private KeyVault keyVaultSecrets;
    protected static Logger logger = LoggerFactory.getLogger(UserRepository.class);

    public UserRepository(){
        init();
    }


    public void init() {
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
        container = database.getContainer(containerResponse.getProperties().getId());
    }



    public CosmosPagedIterable<Object> getUsers() {

        CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();
        queryOptions.setQueryMetricsEnabled(true);

        CosmosPagedIterable<Object> result = container.queryItems(
            "SELECT * FROM c", queryOptions, Object.class);

        return result;
    }

    public CosmosPagedIterable<Object> getUserById(String id) {

        CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();
        queryOptions.setQueryMetricsEnabled(true);

        CosmosPagedIterable<Object> result = container.queryItems(
            "SELECT * FROM c WHERE c.id = '" + id + "'", queryOptions, Object.class);

        return result;
    }

    public void add(User user) {
        CosmosItemResponse<User> respose = container.createItem(user);
        if(respose.getStatusCode() == StatusCodes.CREATED ||  respose.getStatusCode() == StatusCodes.OK){
            logger.info("[USER CREATED] -- " + user);
        }else{
            logger.error("[USER NOT CREATED] -- " + user);
        }
    }


    public void updateUser(User user) {
        CosmosItemResponse<User> respose =container.upsertItem(user);
        if(respose.getStatusCode() == StatusCodes.CREATED ||  respose.getStatusCode() == StatusCodes.OK){
            logger.info("[USER UPDATED] -- " + user);
        }else{
            logger.error("[USER NOT UPDATED] -- " + user);
        }
    }

    public void delete(String id ) {
        try{
            CosmosItemResponse<Object> respose = container.deleteItem(id, new PartitionKey(id), new CosmosItemRequestOptions());

            if(respose.getStatusCode() == StatusCodes.NO_CONTENT){
                logger.info("[USER DELETED] id : -- " + id);
            }else{
                logger.error("[USER NOT DELETED]  id -- " + id);
            }
        }catch(Exception e){
            logger.error("[USER NOT DELETED]  id -- " + id + " Error : " + e.getMessage());
        }
        
    }


}
