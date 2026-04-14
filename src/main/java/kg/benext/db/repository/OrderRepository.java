package kg.benext.db.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import kg.benext.db.config.MongoConfig;
import org.bson.Document;
import org.bson.types.Binary;
import org.awaitility.Awaitility;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class OrderRepository {

    private MongoCollection<Document> getCollection() {
        return MongoConfig.getOrderingDb().getCollection("orders");
    }

    /**
     * Получить все заказы
     */
    public List<Document> getAllOrders() {
        return getCollection().find().into(new ArrayList<>());
    }

    /**
     * Найти заказ по ID
     */
    public Document getOrderById(UUID id) {
        return getCollection().find(
            Filters.eq("_id.value", new Binary((byte) 3, uuidToBytes(id)))
        ).first();
    }

    /**
     * Ожидать появления заказа по customerId
     */
    public Document waitForOrderByCustomerId(String customerId) {
        Awaitility.await()
                .atMost(1, TimeUnit.MINUTES)
                .pollInterval(5, TimeUnit.SECONDS)
                .until(() -> getCollection().find(
                        Filters.eq("customerId.value", customerId)
                ).first() != null);

        return getCollection().find(
                Filters.eq("customerId.value", customerId)
        ).first();
    }

    private byte[] uuidToBytes(UUID uuid) {
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] buffer = new byte[16];
        for (int i = 0; i < 8; i++) {
            buffer[i] = (byte) (msb >>> 8 * (7 - i));
            buffer[8 + i] = (byte) (lsb >>> 8 * (7 - i));
        }
        return buffer;
    }
}