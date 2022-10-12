package com.okami.listener;

import com.okami.FxApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class StageInitializer implements ApplicationListener<FxApplication.StageReadyEvent> {

    @Value("classpath:/static/view.fxml")
    private Resource fxResource;

    @Autowired
    private ApplicationContext applicationContext;

    @SneakyThrows
    @Override
    public void onApplicationEvent(FxApplication.StageReadyEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(fxResource.getURL());
        fxmlLoader.setControllerFactory(aClass -> applicationContext.getBean(aClass));
        Parent parent = fxmlLoader.load();

        Stage stage = event.getStage();
        Scene scene = new Scene(parent, 430, 604);
        stage.setTitle("Client Application");
        stage.setScene(scene);
        stage.show();
    }
}
