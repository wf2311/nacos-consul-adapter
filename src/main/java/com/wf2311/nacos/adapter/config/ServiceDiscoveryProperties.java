package com.wf2311.nacos.adapter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/1/7 14:16.
 */
@ConfigurationProperties(ServiceDiscoveryProperties.PREFIX)
@Getter
@Setter
public class ServiceDiscoveryProperties {
    public static final String PREFIX = "app.discovery";

    private List<String> services=new ArrayList<>();
    private boolean enabled = false;
}
