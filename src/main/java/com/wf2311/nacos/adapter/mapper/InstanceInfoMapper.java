/**
 * The MIT License
 * Copyright © 2018 Twinformatics GmbH
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.wf2311.nacos.adapter.mapper;

import com.wf2311.nacos.adapter.model.Service;
import com.wf2311.nacos.adapter.model.ServiceHealth;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class InstanceInfoMapper {

    private static final List<String> NO_SERVICE_TAGS = new ArrayList<>();

    public Service map(ServiceInstance instance) {
        String address = getAddress(instance);
        return Service.builder()
                .address(address)
                .serviceAddress(address)
                .serviceName(instance.getServiceId())
                .serviceID(getServiceId(instance))
                .servicePort(getPort(instance))
                .node(instance.getServiceId())
                .nodeMeta(instance.getMetadata())
                .serviceMeta(serviceMeta(instance))
                .serviceTags(NO_SERVICE_TAGS)
                .build();
    }

    public ServiceHealth mapToHealth(ServiceInstance instance) {
        String address = getAddress(instance);
        ServiceHealth.Node node = ServiceHealth.Node.builder()
                .name(instance.getServiceId())
                .address(address)
                .meta(instance.getMetadata())
                .build();
        ServiceHealth.Service service = ServiceHealth.Service.builder()
                .id(getServiceId(instance))
                .name(instance.getServiceId())
                .tags(NO_SERVICE_TAGS)
                .address(address)
                .meta(serviceMeta(instance))
                .port(getPort(instance))
                .build();
        ServiceHealth.Check check = ServiceHealth.Check.builder()
                .node(instance.getServiceId())
                .checkID("service:" + instance.getServiceId())
                .name("Service '" + getServiceId(instance) + "' check")
                .status("up")
                .build();
        return ServiceHealth.builder()
                .node(node)
                .service(service)
                .checks(Collections.singletonList(check))
                .build();
    }

    private Map<String, String> serviceMeta(ServiceInstance instance) {
        Map<String, String> map = new HashMap<>();
        map.put("management.port", "" + instance.getPort());
        return map;
    }

    private String getAddress(ServiceInstance instance) {
        return instance.getHost();
    }
    private String getServiceId(ServiceInstance instance) {
        return instance.getHost() + ":" + instance.getPort();
    }

    private int getPort(ServiceInstance instance) {
        return instance.getPort();
    }
}
