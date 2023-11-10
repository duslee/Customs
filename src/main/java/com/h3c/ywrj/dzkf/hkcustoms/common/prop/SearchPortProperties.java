package com.h3c.ywrj.dzkf.hkcustoms.common.prop;

import com.h3c.ywrj.dzkf.hkcustoms.common.util.CompositePropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by @author wfw2525 on 2019/12/26 0:20
 */
@Component
@PropertySource(value = {"classpath:/custom/search.yml"}, factory = CompositePropertySourceFactory.class)
@ConfigurationProperties(prefix = "search.port")
@Data
public class SearchPortProperties implements SearchProperties {
    private List<String> ins;
    private List<String> likes;
    private List<String> timeRange;

    @Override
    public String toString() {
        return "SearchPortProperties{" +
                "ins=" + ins +
                ", likes=" + likes +
                ", timeRange=" + timeRange +
                '}';
    }
}
