package sample;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ButtonUpload
{
    private Desktop desktop = Desktop.getDesktop();
    private Button button = new Button("Select a picture");

    public void definirBouton(Stage primaryStage) throws Exception
    {
        final   FileChooser fileChooser     = new FileChooser();

        button.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null)
                {
                    openFile(file);
                    List<File> files = Arrays.asList(file);
                }
            }
        });
        //setPosition(-510,-320);
    }
/*
    private void printLog(TextArea textArea, List<File> files) {
        if (files == null || files.isEmpty()) {
            return;
        }
        for (File file : files) {
            textArea.appendText(file.getAbsolutePath() + "\n");
        }
    }
    */

    private void openFile(File file) {
        try {
            this.desktop.open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPosition(int x, int y)
    {
        button.setTranslateX(x);
        button.setTranslateY(y);
    }

    public Button getButton() {
        return button;
    }
}