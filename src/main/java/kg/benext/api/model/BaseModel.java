package kg.benext.api.model;

import kg.benext.api.utils.JsonUtils;

public abstract class BaseModel {

    public String toJson(){
        return JsonUtils.toJson(this);
    }
}