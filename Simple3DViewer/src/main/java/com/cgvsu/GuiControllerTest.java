package com.cgvsu;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.objwriter.ObjWriter;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.RenderEngine;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.vecmath.Vector3f;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.IllegalFormatCodePointException;
import java.util.Objects;

public class GuiControllerTest {

    final private float TRANSLATION = 0.9F; //шаг перемещения камеры

    @FXML
    public AnchorPane modelPane;

    @FXML
    public AnchorPane gadgetPane;

    @FXML
    private Button open;

    @FXML
    private Button save;

    @FXML
    private TextField text;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    private Model mesh = null;

    private Camera camera = new Camera(
            new Vector3f(0, 0, 100),
            new Vector3f(0, 0, 0),
            1.0F, 1, 0.01F, 100);

    private Timeline timeline;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(50), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            camera.setAspectRatio((float) (height / width)); // задаем AspectRatio

            if (mesh != null) {
                RenderEngine.render(canvas.getGraphicsContext2D(), camera, mesh, (int) width, (int) height); //создаем отрисовку модели
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    public void handleCameraForward() {
        camera.movePosition(new Vector3f(0, 0, -TRANSLATION));
    }

    @FXML
    public void handleCameraBackward() {
        camera.movePosition(new Vector3f(0, 0, TRANSLATION));
    }

    @FXML
    public void handleCameraLeft() {
        camera.movePosition(new Vector3f(TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraRight() {
        camera.movePosition(new Vector3f(-TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraUp() {
        camera.movePosition(new Vector3f(0, TRANSLATION, 0));
    }

    @FXML
    public void handleCameraDown() {
        camera.movePosition(new Vector3f(0, -TRANSLATION, 0));
    }

    public void moveModel(KeyEvent keyEvent) {
        if (Objects.equals(keyEvent.getText(), "w")) {
            handleCameraForward();
        }
        if (Objects.equals(keyEvent.getText(), "s")) {
            handleCameraBackward();
        }
        if (Objects.equals(keyEvent.getText(), "a")) {
            handleCameraLeft();
        }
        if (Objects.equals(keyEvent.getText(), "d")) {
            handleCameraRight();
        }
        if (Objects.equals(keyEvent.getText(), "r")) {
            handleCameraUp();
        }
        if (Objects.equals(keyEvent.getText(), "f")) {
            handleCameraDown();
        }
    }

    @FXML
    void open(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            mesh = ObjReader.read(fileContent);
            // todo: обработка ошибок
        } catch (IOException exception) {

        }
    }

    @FXML
    void save(MouseEvent event) {
        if (mesh != null) {
            if (!Objects.equals(text.getText(), "") && !Objects.equals(text.getText(), "Введите имя сохраняемого файла")) {
                ObjWriter.write(mesh, text.getText()+".obj");
                text.setText("Успешно!");
            }
            else {
                text.setText("Введите имя сохраняемого файла");
            }
        }
    }
}