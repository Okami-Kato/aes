package com.okami.client.template;

import com.okami.config.UserProperties;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class RestTemplateFactory implements FactoryBean<RestTemplate>, InitializingBean {

    private final UserProperties userProperties;

    private final ResponseErrorHandler restTemplateErrorHandler;

    private RestTemplate restTemplate;

    public RestTemplate getObject() {
        return restTemplate;
    }

    public Class<RestTemplate> getObjectType() {
        return RestTemplate.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void afterPropertiesSet() {
        HttpHost host = new HttpHost("localhost", 8080, "http");
        restTemplate = new RestTemplate(
            new HttpComponentsClientHttpRequestFactoryBasicAuth(host));
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(userProperties.getUsername(), userProperties.getPassword()));
        restTemplate.setErrorHandler(restTemplateErrorHandler);
    }
}
