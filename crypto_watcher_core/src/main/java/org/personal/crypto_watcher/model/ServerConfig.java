package org.personal.crypto_watcher.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ServerConfig extends Configuration {

    @NotEmpty
    @JsonProperty
    private String template;


    @NotEmpty
    @JsonProperty
    private String defaultName = "Stranger";



    public String getTemplate() {
        return template;
    }

    public String getDefaultName() {
        return defaultName;
    }

}
