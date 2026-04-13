package kg.benext.common.utils.file;

import org.aeonbits.owner.ConfigCache;

public class ConfigurationManager {

    public static AppConfig getBaseConfig(){
        return ConfigCache.getOrCreate(AppConfig.class);
    }
}