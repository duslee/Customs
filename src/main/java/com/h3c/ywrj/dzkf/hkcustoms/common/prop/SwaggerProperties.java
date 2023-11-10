package com.h3c.ywrj.dzkf.hkcustoms.common.prop;

import com.h3c.ywrj.dzkf.hkcustoms.common.util.CompositePropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by @author wfw2525 on 2019/12/24 9:03
 */
@Component
@PropertySource(value = {"classpath:/custom/swagger.yml"}, factory = CompositePropertySourceFactory.class)
@ConfigurationProperties(prefix = "swagger")
@Data
public class SwaggerProperties implements ReportProperties {
    private String title;
    private String description;
    private String termsOfServiceUrl;
    private String contactName;
    private String contactUrl;
    private String contactEmail;
    private String version;

    @Override
    public String toString() {
        return "SwaggerProperties{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", termsOfServiceUrl='" + termsOfServiceUrl + '\'' +
                ", contactName='" + contactName + '\'' +
                ", contactUrl='" + contactUrl + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
