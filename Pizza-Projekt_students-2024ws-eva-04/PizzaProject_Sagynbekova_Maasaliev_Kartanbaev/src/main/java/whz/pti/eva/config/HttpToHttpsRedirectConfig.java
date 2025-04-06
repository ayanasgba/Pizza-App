package whz.pti.eva.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpToHttpsRedirectConfig {

    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addAdditionalTomcatConnectors(createHttpConnector());
        return factory;
    }

    private Connector createHttpConnector() {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(8081); // HTTP порт
        connector.setSecure(false);
        connector.setRedirectPort(8080); // HTTPS порт
        return connector;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
