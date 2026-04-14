package kg.benext.common.utils.file;

import org.aeonbits.owner.Config;

@Config.Sources({"classpath:app.properties"})
public interface AppConfig extends Config {

    @Key("base.url")
    String baseUrl();

    @Key("catalog.db.port")
    String catalogDbPort();

    @Key("catalog.db.name")
    String catalogDbName();

    @Key("db.userName")
    String dbUserName();

    @Key("db.password")
    String dbPassword();

    @Key("basket.db.port")
    String basketDbPort();

    @Key("basket.db.name")
    String basketDbName();


}