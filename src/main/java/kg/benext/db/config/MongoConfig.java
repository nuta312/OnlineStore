package kg.benext.db.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoConfig {

    private static MongoClient orderingMongoClient;

    /**
     * Возвращает единственный экземпляр MongoClient для ordering_db (Singleton)
     */
    public static MongoClient getOrderingMongoClient() {
        if (orderingMongoClient == null) {
            synchronized (MongoConfig.class) {
                if (orderingMongoClient == null) {
                    orderingMongoClient = MongoClients.create("mongodb://5.129.193.163:27017");
                }
            }
        }
        return orderingMongoClient;
    }

    /**
     * Возвращает базу данных ordering_db
     */
    public static MongoDatabase getOrderingDb() {
        return getOrderingMongoClient().getDatabase("ordering_db");
    }

    /**
     * Закрывает соединение с MongoDB
     */
    public static void shutdown() {
        if (orderingMongoClient != null) {
            orderingMongoClient.close();
        }
    }
}