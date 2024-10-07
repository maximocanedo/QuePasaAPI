package frgp.utn.edu.ar.quepasa.config;

import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry;

public class EndpointAccessConfiguration {

    public void publicAccess(AuthorizeHttpRequestsConfigurer<?> request) {

    }
}
