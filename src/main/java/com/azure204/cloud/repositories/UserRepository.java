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
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure204.cloud.common.KeyVault;
import com.azure204.cloud.model.User;

import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
@AllArgsConstructor
public class UserRepository {


    private final String databaseName = "cosmodbaz204exam";
    private final String containerName = "users";
    private KeyVault keyVaultSecrets;
    protected static Logger logger = LoggerFactory.getLogger(UserRepository.class);


    public CosmosContainer getContainer() {
        CosmosDatabase database;
        CosmosClient client;
        keyVaultSecrets = new KeyVault();

        ArrayList<String> preferredRegions = new ArrayList<String>();
        preferredRegions.add("West US");

        String cosmoDbkey  = keyVaultSecrets.getSecret("cosmodb204key");
        String cosmoDbHost  = keyVaultSecrets.getSecret("cosmodbhost");

        new DefaultAzureCredentialBuilder().build();

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



    public CosmosPagedIterable<Object> getUsers() {

        CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();
        queryOptions.setQueryMetricsEnabled(true);
        
        CosmosContainer container = this.getContainer();

        CosmosPagedIterable<Object> result = container.queryItems(
            "SELECT * FROM c", queryOptions, Object.class);

        return result;
    }

    public CosmosPagedIterable<Object> getUserById(String id) {

        CosmosContainer container = this.getContainer();

        CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();
        queryOptions.setQueryMetricsEnabled(true);

        CosmosPagedIterable<Object> result = container.queryItems(
            "SELECT * FROM c WHERE c.id = '" + id + "'", queryOptions, Object.class);

        return result;
    }

    public boolean add(User user) {

        try{
            CosmosContainer container = this.getContainer();
            CosmosItemResponse<User> respose = container.createItem(user);
            if(respose.getStatusCode() == StatusCodes.CREATED ||  respose.getStatusCode() == StatusCodes.OK){
                logger.info("[USER CREATED] -- " + user);
                return true;
            }else{
                logger.error("[USER NOT CREATED] -- " + user);
                return false;
            }

        }catch (Exception e){
            e.hashCode();
            logger.error("Error to add a user: " +  e.getMessage());
            return false;
        }
      
    }


    public void updateUser(User user) {

        CosmosContainer container = this.getContainer();
        CosmosItemResponse<User> respose =container.upsertItem(user);
        if(respose.getStatusCode() == StatusCodes.CREATED ||  respose.getStatusCode() == StatusCodes.OK){
            logger.info("[USER UPDATED] -- " + user);
        }else{
            logger.error("[USER NOT UPDATED] -- " + user);
        }
    }

    public void delete(String id ) {
        try{
            CosmosContainer container = this.getContainer();
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
