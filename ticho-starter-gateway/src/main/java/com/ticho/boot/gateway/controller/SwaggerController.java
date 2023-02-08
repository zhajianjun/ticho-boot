package com.ticho.boot.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;

import java.util.Optional;

/**
 *
 * @author zhajianjun
 * @date 2020-11-24 23:31
 */
@RestController
public class SwaggerController {

    @Autowired
    private SwaggerResourcesProvider swaggerResources;

    @Autowired(required = false)
    private SecurityConfiguration securityConfiguration;

    @Autowired(required = false)
    private UiConfiguration uiConfiguration;

    // @formatter:off

    @GetMapping("/swagger-resources/configuration/security")
    public Mono<ResponseEntity<SecurityConfiguration>> securityConfiguration() {
        SecurityConfiguration securityConfiguration = Optional.ofNullable(this.securityConfiguration)
            .orElseGet(() -> SecurityConfigurationBuilder.builder().build());
        ResponseEntity<SecurityConfiguration> responseEntity = new ResponseEntity<>(securityConfiguration, HttpStatus.OK);
        return Mono.just(responseEntity);
    }

    @GetMapping("/swagger-resources/configuration/ui")
    public Mono<ResponseEntity<UiConfiguration>> uiConfiguration() {
        UiConfiguration configuration = Optional.ofNullable(this.uiConfiguration)
            .orElseGet(() -> UiConfigurationBuilder.builder().build());
        ResponseEntity<UiConfiguration> data = new ResponseEntity<>(configuration, HttpStatus.OK);
        return Mono.just(data);
    }

    @GetMapping("/swagger-resources")
    public Mono<ResponseEntity<Object>> swaggerResources() {
        return Mono.just((new ResponseEntity<>(swaggerResources.get(), HttpStatus.OK)));
    }

    // @formatter:on

}
