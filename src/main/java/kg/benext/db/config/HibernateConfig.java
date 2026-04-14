package kg.benext.db.config;

import kg.benext.common.utils.file.AppConfig;
import kg.benext.common.utils.file.ConfigurationManager;
import kg.benext.db.entity.MtDocBrand;
import kg.benext.db.entity.MtDocCategory;
import kg.benext.db.entity.MtDocFavorite;
import kg.benext.db.entity.MtDocProduct;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * HibernateConfig — класс конфигурации Hibernate.
 *
 * Hibernate — это ORM (Object-Relational Mapping) фреймворк,
 * который позволяет работать с базой данных через Java объекты,
 * вместо того чтобы писать SQL запросы вручную.
 *
 * Этот класс создаёт и хранит SessionFactory — фабрику сессий.
 * SessionFactory — это главный объект Hibernate, через который
 * открываются сессии (соединения) с базой данных.
 *
 * В проекте две базы данных:
 * - catalogdb (PostgreSQL, порт 5432) — каталог товаров
 * - basketdb  (PostgreSQL, порт 5433) — корзина покупок
 */
public class HibernateConfig {

    // Статическое поле — хранит единственный экземпляр SessionFactory для catalogdb.
    // static означает что поле одно на весь класс, не создаётся заново при каждом вызове.
    // volatile гарантирует видимость изменений между потоками (thread-safety).
    private static SessionFactory catalogSessionFactory;

    // Статическое поле — хранит единственный экземпляр SessionFactory для basketdb.
    private static SessionFactory basketSessionFactory;

    /**
     * Возвращает SessionFactory для базы данных каталога (catalogdb).
     *
     * Использует паттерн Singleton с двойной проверкой (Double-Checked Locking).
     * Это значит: SessionFactory создаётся только один раз при первом вызове,
     * все последующие вызовы возвращают уже созданный экземпляр.
     *
     * @return SessionFactory для catalogdb
     */
    public static SessionFactory getCatalogSessionFactory() {
        // Первая проверка — если фабрика уже создана, сразу возвращаем её
        // без входа в synchronized блок (это быстрее)
        if (catalogSessionFactory == null) {

            // synchronized — блокирует класс для других потоков,
            // чтобы два потока одновременно не создали две фабрики
            synchronized (HibernateConfig.class) {

                // Вторая проверка — после получения блокировки проверяем снова,
                // вдруг другой поток уже успел создать фабрику пока мы ждали
                if (catalogSessionFactory == null) {
                    catalogSessionFactory = buildCatalogSessionFactory();
                }
            }
        }
        // Возвращаем готовую фабрику
        return catalogSessionFactory;
    }

    /**
     * Возвращает SessionFactory для базы данных корзины (basketdb).
     *
     * Логика аналогична getCatalogSessionFactory() — паттерн Singleton
     * с двойной проверкой для thread-safety.
     *
     * @return SessionFactory для basketdb
     */
    public static SessionFactory getBasketSessionFactory() {
        if (basketSessionFactory == null) {
            synchronized (HibernateConfig.class) {
                if (basketSessionFactory == null) {
                    basketSessionFactory = buildBasketSessionFactory();
                }
            }
        }
        return basketSessionFactory;
    }

    /**
     * Строит SessionFactory для catalogdb.
     *
     * Читает настройки из конфига (порт и имя БД),
     * формирует JDBC URL и передаёт в общий метод buildSessionFactory().
     *
     * @return SessionFactory настроенный для catalogdb
     */
    private static SessionFactory buildCatalogSessionFactory() {
        // Получаем объект конфига — там хранятся порты, имена БД, логин, пароль
        AppConfig config = ConfigurationManager.getBaseConfig();

        // Формируем JDBC URL для подключения к PostgreSQL
        // Например: jdbc:postgresql://5.129.193.163:5432/catalogdb
        String url = String.format("jdbc:postgresql://5.129.193.163:%s/%s",
                config.catalogDbPort(),  // порт из конфига (5432)
                config.catalogDbName()); // имя БД из конфига (catalogdb)

        // Передаём URL и конфиг в общий метод построения фабрики
        return buildSessionFactory(url, config);
    }

    /**
     * Строит SessionFactory для basketdb.
     *
     * Аналогично buildCatalogSessionFactory(), но использует
     * порт и имя корзины из конфига.
     *
     * @return SessionFactory настроенный для basketdb
     */
    private static SessionFactory buildBasketSessionFactory() {
        AppConfig config = ConfigurationManager.getBaseConfig();

        // Например: jdbc:postgresql://5.129.193.163:5433/basketdb
        String url = String.format("jdbc:postgresql://5.129.193.163:%s/%s",
                config.basketDbPort(),   // порт из конфига (5433)
                config.basketDbName());  // имя БД из конфига (basketdb)

        return buildSessionFactory(url, config);
    }

    /**
     * Общий метод построения SessionFactory.
     *
     * Принимает готовый URL и конфиг, настраивает Hibernate
     * и регистрирует все Entity классы (таблицы БД).
     *
     * @param url    JDBC URL подключения к базе данных
     * @param config объект с настройками (логин, пароль и др.)
     * @return готовый SessionFactory
     */
    private static SessionFactory buildSessionFactory(String url, AppConfig config) {
        return new Configuration()
                // Драйвер для подключения к PostgreSQL
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")

                // URL подключения к БД (передаётся снаружи)
                .setProperty("hibernate.connection.url", url)

                // Логин для подключения к БД
                .setProperty("hibernate.connection.username", config.dbUserName())

                // Пароль для подключения к БД
                .setProperty("hibernate.connection.password", config.dbPassword())

                // Диалект — говорим Hibernate что используем PostgreSQL,
                // чтобы он генерировал правильный SQL синтаксис
                .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")

                // validate — Hibernate проверяет что структура таблиц в БД
                // совпадает с Entity классами, но ничего не меняет в БД
                .setProperty("hibernate.hbm2ddl.auto", "validate")

                // Выводить SQL запросы в консоль (удобно для отладки)
                .setProperty("hibernate.show_sql", "true")

                // Форматировать SQL запросы в консоли для читаемости
                .setProperty("hibernate.format_sql", "true")

                // Регистрируем Entity классы — каждый класс соответствует таблице в БД:
                .addAnnotatedClass(MtDocProduct.class)   // таблица mt_doc_product
                .addAnnotatedClass(MtDocBrand.class)     // таблица mt_doc_brand
                .addAnnotatedClass(MtDocCategory.class)  // таблица mt_doc_category
                .addAnnotatedClass(MtDocFavorite.class)  // таблица mt_doc_favorite

                // Создаём и возвращаем готовую фабрику сессий
                .buildSessionFactory();
    }

    /**
     * Закрывает все открытые SessionFactory и освобождает ресурсы.
     *
     * Должен вызываться при завершении работы приложения или тестов,
     * чтобы корректно закрыть соединения с базой данных.
     */
    public static void shutdown() {
        // Закрываем фабрику каталога если она была создана
        if (catalogSessionFactory != null) catalogSessionFactory.close();

        // Закрываем фабрику корзины если она была создана
        if (basketSessionFactory != null) basketSessionFactory.close();
    }
}