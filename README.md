## Nacos Consul Adapter (for Prometheus)
当使用Nacos作为注册中心时通过`nacos-consul-adapter`能够使prometheus自动发现Nacos中的服务

项目地址：[wf2311/nacos-consul-adapter](https://github.com/wf2311/nacos-consul-adapter) Fork自[xuande/nacos-consul-adapter](https://github.com/xuande/nacos-consul-adapter)

## Restrictions
这个适配器只实现了prometheus使用consul_sd_config配置时需要的http接口，具体实现的接口如下：
- /v1/agent/self 返回默认的datacenter
- /v1/catalog/services 返回nacos中的服务列表
- /v1/catalog/service/{service} 返回服务实例
- /v1/health/service/{appName}  适配 https://www.consul.io/api/health#list-nodes-for-service

## Requirements
- Java 1.8+
- Spring Boot 2.1.x
- Spring Cloud Greenwich


## 服务过滤功能
在nacos的配置管理中新建一条 dataId=nacos-consul-adapter，groupId=DEFAULT_GROUP的配置文件，内容如下：

```yml
app:
  filter:
    enabled: true   #默认为false，表示将会自动发现所有的服务；为true时，会先读取nacos中所有的服务再根据app.filter.services中的服务列表进行过滤
    services:
      - service-a
      - service-b
      - service-c
```
## 启动方式
### 本机调试
修改`application.yml`中的
- spring.cloud.nacos.config.server-addr
- spring.cloud.nacos.config.namespace
  启动项目

### docker启动

```shell
docker run -d -t  -p 3040:3040 \
-v ~/logs/nacos-consul-adapter/:/application/logs \
-e JAVA_OPTS='-Xmx256m -Xms256m -Xss256k' \
-e SERVER_PORT='5499' \
-e NACOS_ADDR='<nacos服务地址>' \
-e NACOS_NAMESPACE='<nacos命名空间>' \
--name nacos-consul-adapter wf2311/nacos-consul-adapter:latest
```

### docker-compose启动
```yml
version: "3"
services:
  nacos-consul-adapter:
    image: wf2311/nacos-consul-adapter:latest
    container_name: nacos-consul-adapter
    environment:
      - JAVA_OPTS=-Xmx256m -Xms256m -Xss256k
      - SERVER_PORT=5499
      - NACOS_ADDR=<nacos服务地址>
      - NACOS_NAMESPACE='<nacos命名空间>'
    volumes:
      - ~/Share/logs/nacos-consul-adapter/:/application/logs
    ports:
      - "5499:5499"
```

### k8s部署脚本
```yml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nacos-consul-adapter
spec:
  replicas: 1
  selector:
    matchLabels:
      app: nacos-consul-adapter
  template:
    metadata:
      labels:
        app: nacos-consul-adapter
    spec:
      containers:
        - env:
            - name: SERVER_PORT
              value: '5499'
            - name: JVM_OPTS
              value: '-Xmx256m -Xms256m -Xss256k'
            - name: NACOS_ADDR
              value: '<nacos服务地址>'
            - name: NACOS_NAMESPACE
              value: '<nacos命名空间>'
          name: nacos-consul-adapter
          image: wf2311/nacos-consul-adapter:latest
          imagePullPolicy: Always
          ports:
            - containerPort: 5499
---
apiVersion: v1
kind: Service
metadata:
  name: nacos-consul-adapter
  namespace: nacos-consul-adapter-group
spec:
  ports:
    - port: 5499
      targetPort: 5499
      name: nacos-consul-adapter
  selector:
    app: nacos-consul-adapter
  type: LoadBalancer
```

## Prometheus
在prometheus配置文件中使用`consul_sd_configs`配置adapter地址

```
- job_name: 'nacos-prometheus'
  metrics_path: '/actuator/prometheus'
  consul_sd_configs:
  - server: 'nacos-consul-adapter.nacos-consul-adapter-group.svc.cluster.local:5499'
    services: []
```

## 参考项目
[eureka-consule-adapter](https://github.com/twinformatics/eureka-consul-adapter)