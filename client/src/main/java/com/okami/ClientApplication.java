package com.okami;

import com.okami.config.UserProperties;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(UserProperties.class)
public class ClientApplication {

    public static void main(String[] args) {
        Application.launch(FxApplication.class, args);
    }
}
