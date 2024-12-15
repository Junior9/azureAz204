package com.azure204.cloud.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.azure.cosmos.util.CosmosPagedIterable;
import com.azure204.cloud.common.KeyVault;
import com.azure204.cloud.model.User;
import com.azure204.cloud.repositories.UserRepository;

import lombok.AllArgsConstructor;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.UnifiedJedis;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private EventHub eventHub;
    protected static Logger logger = LoggerFactory.getLogger(UserService.class);


    public CosmosPagedIterable<Object>  getUsers(){

        /* 
         * REDIS 
         *  FIX DE REDIS CONNECTIOM
        */
          
        KeyVault keyVault = new KeyVault();

        
        try {
            boolean useSsl = true;
            String cacheHostname = keyVault.getSecret("redishostname");
            String cachekey = keyVault.getSecret("rediskey");
            String urlRedisConnection = keyVault.getSecret("redisconnetionstring");
            
            // Connect to the Azure Cache for Redis over the TLS/SSL port using the key.
            Jedis jedis = new Jedis(urlRedisConnection);

            // Perform cache operations using the cache connection object...

            // Simple PING command
            System.out.println( "\nCache Command  : Ping" );
            System.out.println( "Cache Response : " + jedis.ping());

            // Simple get and put of integral data types into the cache
            System.out.println( "\nCache Command  : GET Message" );
            System.out.println( "Cache Response : " + jedis.get("Message"));

            System.out.println( "\nCache Command  : SET Message" );
            System.out.println( "Cache Response : " + jedis.set("Message", "Hello! The cache is working from Java!"));

            // Demonstrate "SET Message" executed as expected...
            System.out.println( "\nCache Command  : GET Message" );
            System.out.println( "Cache Response : " + jedis.get("Message"));

            // Get the client list, useful to see if connection list is growing...
            System.out.println( "\nCache Command  : CLIENT LIST" );
            System.out.println( "Cache Response : " + jedis.clientList());

            jedis.close();

        }catch(Exception e){
            logger.error("[ERROR Redis]: "  +  e.getMessage());
        }
        */
        return this.userRepository.getUsers();
    }

    public CosmosPagedIterable<Object>  getUserById(String id){
        return this.userRepository.getUserById(id);
    }

    public void addUser(User user){
        boolean userAdded = this.userRepository.add(user);
        if(userAdded){
            this.eventHub.newUserNotification(user);
        }
    }

    public void update(User user) {
        this.userRepository.updateUser(user);
    }

    public void delete(String id) {
        this.userRepository.delete(id);
    }



}
