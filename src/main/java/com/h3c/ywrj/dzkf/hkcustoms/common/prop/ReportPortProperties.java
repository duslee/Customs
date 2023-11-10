package com.h3c.ywrj.dzkf.hkcustoms.common.prop;

import com.h3c.ywrj.dzkf.hkcustoms.common.util.CompositePropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by @author wfw2525 on 2019/12/25 10:55
 */
@Component
@PropertySource(value = {"classpath:/custom/report.yml"}, factory = CompositePropertySourceFactory.class)
@ConfigurationProperties(prefix = "report.port")
@Data
public class ReportPortProperties implements ReportProperties {
    private String tip;
    private String dateFormat;
    private List<String> columnTitleList;
    private List<String> columnFieldList;
    private List<String> notNullColumns;

    @Override
    public String toString() {
        return "ReportPortProperties{" +
                "tip='" + tip + '\'' +
                ", dateFormat='" + dateFormat + '\'' +
                ", columnTitleList=" + columnTitleList +
                ", columnFieldList=" + columnFieldList +
                ", notNullColumns=" + notNullColumns +
                '}';
    }
}
