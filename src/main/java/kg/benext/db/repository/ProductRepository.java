package kg.benext.db.repository;

import kg.benext.db.config.HibernateConfig;
import kg.benext.db.entity.MtDocProduct;
import org.awaitility.Awaitility;
import org.hibernate.Session;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ProductRepository {

    public List<MtDocProduct> getAllProducts() {
        try (Session session = HibernateConfig.getCatalogSessionFactory().openSession()) {
            return session.createQuery("FROM MtDocProduct WHERE mtDeleted IS NULL OR mtDeleted = false",
                            MtDocProduct.class).list();
        }
    }

    /**
     * Сохраняет новый продукт в базу данных.
     *
     * @param product объект MtDocProduct который нужно сохранить
     */
    public void createProduct(MtDocProduct product) {
        // Открываем сессию — соединение с БД (try-with-resources автоматически закроет её)
        try (Session session = HibernateConfig.getCatalogSessionFactory().openSession()) {

            // Начинаем транзакцию — все изменения применятся только после commit()
            Transaction transaction = session.beginTransaction();

            try {
                // Генерируем новый уникальный ID для продукта
                product.setId(UUID.randomUUID());

                // Устанавливаем время последнего изменения
                product.setMtLastModified(OffsetDateTime.now());

                // Генерируем версию
                product.setMtVersion(UUID.randomUUID());

                // Сохраняем объект в БД
                session.persist(product);

                // Подтверждаем транзакцию — данные записываются в БД
                transaction.commit();

            } catch (Exception e) {
                // Если что-то пошло не так — откатываем все изменения
                transaction.rollback();
                throw new RuntimeException("Ошибка при создании продукта", e);
            }
        }
    }

    /**
     * Находит продукт по его ID.
     *
     * @param id UUID продукта
     * @return MtDocProduct если найден, null если не найден
     */
    public MtDocProduct getProductById(UUID id) {
        try (Session session = HibernateConfig.getCatalogSessionFactory().openSession()) {
            return session.find(MtDocProduct.class, id);
        }
    }

    /**
     * Ожидает появления продукта в БД по ID.
     * Проверяет каждые 5 секунд, максимум 1 минуту.
     *
     * @param id UUID продукта которого ждём
     * @return MtDocProduct когда появится в БД
     */
    public MtDocProduct waitForProduct(UUID id) {
        Awaitility.await()
                .atMost(1, TimeUnit.MINUTES)
                .pollInterval(5, TimeUnit.SECONDS)
                .until(() -> getProductById(id) != null);

        return getProductById(id);
    }
}