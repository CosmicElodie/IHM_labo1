package sample;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ButtonUpload
{
    private Desktop desktop = Desktop.getDesktop();
    private Button button = new Button("Select image");
    private String absolutePathImage = "";

    public void definirBouton(Stage primaryStage) throws Exception
    {
        final   FileChooser fileChooser     = new FileChooser();
        TextArea textArea = new TextArea();
        textArea.setMinHeight(70);

        button.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                textArea.clear();
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null)
                {
                    List<File> files = Arrays.asList(file);
                    printLog(textArea, files);
                }
            }
        });
    }

    private void printLog(TextArea textArea, List<File> files) {
        if (files == null || files.isEmpty()) {
            return;
        }
        for (File file : files) {
            textArea.appendText(file.getAbsolutePath() + "\n");
            absolutePathImage = file.getAbsolutePath();
        }
    }

    public Button getButton() {
        return button;
    }

    public String getAbsolutePathImage() {
        return absolutePathImage;
    }
}