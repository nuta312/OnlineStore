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

    public Document waitForLatestOrderByCustomerId(String customerId, String productId) {
        Awaitility.await()
                .atMost(1, TimeUnit.MINUTES)
                .pollInterval(5, TimeUnit.SECONDS)
                .until(() -> {
                    List<Document> orders = getCollection()
                            .find(Filters.eq("customerId.value", customerId))
                            .sort(new Document("createdAt", -1))
                            .into(new ArrayList<>());

                    return orders.stream().anyMatch(order -> {
                        List<Document> items = (List<Document>) order.get("orderItems");
                        if (items == null) return false;
                        return items.stream().anyMatch(item -> {
                            Object val = item.get("productId", Document.class).get("value");
                            if (val instanceof org.bson.types.Binary binary) {
                                return uuidFromBinary(binary).toString().equals(productId);
                            }
                            return val.toString().equals(productId);
                        });
                    });
                });

        return getCollection()
                .find(Filters.eq("customerId.value", customerId))
                .sort(new Document("createdAt", -1))
                .first();
    }

    private UUID uuidFromBinary(org.bson.types.Binary binary) {
        byte[] bytes = binary.getData();
        long msb = 0, lsb = 0;
        for (int i = 0; i < 8; i++) msb = (msb << 8) | (bytes[i] & 0xff);
        for (int i = 8; i < 16; i++) lsb = (lsb << 8) | (bytes[i] & 0xff);
        return new UUID(msb, lsb);
    }
}