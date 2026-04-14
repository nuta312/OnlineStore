package kg.benext.db.config;

import kg.benext.common.utils.file.AppConfig;
import kg.benext.common.utils.file.ConfigurationManager;
import kg.benext.db.entity.MtDocBrand;
import kg.benext.db.entity.MtDocCategory;
import kg.benext.db.entity.MtDocFavorite;
import kg.benext.db.entity.MtDocProduct;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateConfig {

    private static SessionFactory catalogSessionFactory;
    private static SessionFactory basketSessionFactory;

    public static SessionFactory getCatalogSessionFactory() {
        if (catalogSessionFactory == null) {
            synchronized (HibernateConfig.class) {
                if (catalogSessionFactory == null) {
                    catalogSessionFactory = buildCatalogSessionFactory();
                }
            }
        }
        return catalogSessionFactory;
    }

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

    private static SessionFactory buildCatalogSessionFactory() {
        AppConfig config = ConfigurationManager.getBaseConfig();

        String url = String.format("jdbc:postgresql://5.129.193.163:%s/%s",
                config.catalogDbPort(), config.catalogDbName());

        return buildSessionFactory(url, config);
    }

    private static SessionFactory buildBasketSessionFactory() {
        AppConfig config = ConfigurationManager.getBaseConfig();

        String url = String.format("jdbc:postgresql://5.129.193.163:%s/%s",
                config.basketDbPort(), config.basketDbName());

        return buildSessionFactory(url, config);
    }

    private static SessionFactory buildSessionFactory(String url, AppConfig config) {
        return new Configuration()
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.url", url)
                .setProperty("hibernate.connection.username", config.dbUserName())
                .setProperty("hibernate.connection.password", config.dbPassword())
                .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .setProperty("hibernate.hbm2ddl.auto", "validate")
                .setProperty("hibernate.show_sql", "true")
                .setProperty("hibernate.format_sql", "true")
                .addAnnotatedClass(MtDocProduct.class)
                .addAnnotatedClass(MtDocBrand.class)
                .addAnnotatedClass(MtDocCategory.class)
                .addAnnotatedClass(MtDocFavorite.class)
                .buildSessionFactory();
    }

    public static void shutdown() {
        if (catalogSessionFactory != null) catalogSessionFactory.close();
        if (basketSessionFactory != null) basketSessionFactory.close();
    }
}
