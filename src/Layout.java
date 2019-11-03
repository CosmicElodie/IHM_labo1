import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.PrintWriter;

public class Layout extends Application {

    File file;
    boolean fichierExporte; //sert pour s'assurer que l'user ait bien sauvegarder avant de quitter.

    Image image;
    ImageView imageView;

    Button importButton;
    Button exportButton;
    Button quitButton;

    ListView panneauLabel;

    Label messageImporterExporter;


    int x1, y1, x2, y2 = 0;

    @Override
    public void start(Stage stage) throws Exception {
        //------------------------------------------------------------------
        // DEFINITION DE LA FENÊTRE
        //------------------------------------------------------------------
        //Racine de scene
        BorderPane border = new BorderPane();

        //fond blanc de base
        border.getStyleClass().add("general-borderPanel");

        //------------------------------------------------------------------
        // BARRE DE NAVIGATION
        //------------------------------------------------------------------

        //Import and export buttons
        importButton = new Button("Select image");
        importButton.getStyleClass().add("header-button");

        exportButton = new Button("Save");
        exportButton.getStyleClass().add("header-button");

        quitButton = new Button();
        quitButton.getStyleClass().add("header-quit-button");

        //On crée une barre de navigation dans le BorderPane
        border.setTop(navBar());

        //------------------------------------------------------------------
        // CORPS LOGICIEL OÙ SE TROUVE L'IMAGE
        //------------------------------------------------------------------

        //On met en place le corps du texte
        border.setCenter(corpsLogiciel());

        //------------------------------------------------------------------
        // COLONNE GAUCHE POUR LABELS
        //------------------------------------------------------------------

        //On crée une colonne contenant les labels
        border.setLeft(menuLabels());

        //------------------------------------------------------------------
        // COLONNE DROITE POUR ???
        //------------------------------------------------------------------

        //On crée une colonne contenant les labels
        border.setRight(menuDroite());

        //------------------------------------------------------------------
        // FOOTER
        //------------------------------------------------------------------

        //On crée un footer dans le BorderPane
        border.setBottom(footerBar());

        //------------------------------------------------------------------
        // PARAMÈTRES DE LA FENÊTRE DU LOGICIEL
        //------------------------------------------------------------------

        Scene scene = new Scene(border, 950, 600);
        scene.getStylesheets().add("/design/stylesheet.css");
        stage.setScene(scene);
        stage.setTitle("BeSt ApP Ev4");
        stage.show();
    }

    // Dessine un rectangle
    public void paint(Graphics g2d, int x1, int y1, int x2, int y2) {
        g2d.setColor(java.awt.Color.RED);
        int px = Math.min(x1, x2);
        int py = Math.min(y1, y2);
        int pw = Math.abs(x1 - x2);
        int ph = Math.abs(y1 - y2);
        g2d.drawRect(px, py, pw, ph);
    }

    /**
     * NAVIGATION
     * -> upload button
     * -> help button
     *
     * @return
     */
    private HBox navBar() {
        HBox hbox = new HBox(10);
        hbox.setPadding(new Insets(15, 15, 15, 15));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.getStyleClass().add("header-hbox");

        messageImporterExporter = new Label("");

        hbox.getChildren().addAll(importButton, exportButton, messageImporterExporter);

        exportButton.setOnAction(
                event -> {

                    /*
                    try {
                        BufferedImage bufferedImage = ImageIO.read(file);
                        String format = "";
                        int i = file.getName().lastIndexOf('.');
                        if (i > 0) {
                            format = file.getName().substring(i+1);
                        }
                        File output = new File(file.getName());
                        ImageIO.write(bufferedImage, format, output);
                    } catch (Exception e) {
                        messageImporterExporter.setText("L'image n'a pas pu être importée.");
                    }
                    */


                    // Récupère le nom de l'image sans son extension l'extension
                    String format = "";
                    int i = file.getName().lastIndexOf('.');
                    if (i > 0) {
                        format = file.getName().substring(i);
                    }
                    String fileName = file.getName().replace(format, "");

                    // Sauvegarde l'output à la racine du projet
                    try (PrintWriter pw = new PrintWriter(new File(fileName + "Output.csv"))) {
                        fichierExporte = true;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Image");
                        sb.append(',');
                        sb.append("Objects");
                        sb.append(',');
                        sb.append("Coordinates");
                        sb.append('\n');

                        sb.append(file.getPath());
                        sb.append(',');
                        sb.append(panneauLabel.getItems());
                        sb.append(',');
                        sb.append("Coordonnées à implémenter");
                        sb.append('\n');

                        pw.write(sb.toString());

                        //on supprime les labels de la fenêtre ensuite.
                        //panneauLabel.getItems().clear();
                        messageImporterExporter.setText("L'output a été correctement généré.");
                    } catch (Exception e) {
                        fichierExporte = false;
                        messageImporterExporter.setText("L'output n'a pas correctement été généré");
                    }
                }
        );



        // Ajouter un bouton "aide" dans le header
        quitButton(hbox);

        return hbox;
    }

    /**
     * Footer
     */
    private HBox footerBar() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 15, 15, 15));
        hbox.getStyleClass().add("footer-header-hbox");

        Label copyright = new Label();
        copyright.setText("Copyright Crüll Loris, Lagier Elodie");
        hbox.getChildren().add(copyright);

        return hbox;
    }

    /**
     * Corps logiciel
     * -> là où se charge l'image
     *
     * @return
     */
    private GridPane corpsLogiciel() {
        //On crée le corps du logiciel (là où sera l'image)
        GridPane grid = new GridPane();
        grid.getStyleClass().add("corps-gridPane");

        //On upload l'image à partir de la sélection faite dans le gestionnaire de fichier
        importButton.setOnAction(
                event -> {
                    FileChooser fileChooser = new FileChooser();

                    if (file != null) {
                        File existDirectory = file.getParentFile();
                        fileChooser.setInitialDirectory(existDirectory);
                    }

                    //permet d'afficher les extensions qu'on accepte de sélectionner.

                    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.webp", "*.bmp");
                    fileChooser.getExtensionFilters().add(extFilter);

                    //Ouvre la fenêtre du gestionnaire de fichiers.
                    file = fileChooser.showOpenDialog(null);

                    if (file != null) {
                        //Permet d'afficher l'image dans le corps de l'application
                        grid.getChildren().remove(imageView);
                        image = new Image(file.toURI().toString(), 500, 450, true, false);
                        imageView = new ImageView();
                        imageView.setImage(image);
                        grid.getChildren().add(imageView);


                        //CODE POUR DESSINER RECTANGLE
                        // Permet de dessiner des rectangles
                        imageView.setOnMouseEntered(e -> {
                            //
                        });

                        imageView.setOnMouseExited(e -> {
                            //
                        });

                        imageView.setOnMousePressed(e -> {
                            this.x1 = (int) e.getX();
                            this.y1 = (int) e.getY();
                        });

                        imageView.setOnMouseDragged(e -> {
                            this.x2 = (int) e.getX();
                            this.y2 = (int) e.getY();
                            //paint(g2d, x1, y1, x2, y2);
                        });

                        imageView.setOnMouseReleased(e -> {
                            this.x2 = (int) e.getX();
                            this.y2 = (int) e.getY();
                            //paint(g2d, x1, y1, x2, y2);
                        });
                    }
                }
        );

        return grid;
    }

    /**
     * Menu de gestions de labels (gauche)
     *
     * @return
     */
    private VBox menuLabels() {

        VBox panneauVerticalGauche = new VBox();
        panneauVerticalGauche.getStyleClass().add("menuLabelsGauche-vbox");


        //LABEL AJOUTER UN LABEL
        Label titreLabel = new Label("Ajouter un label");
        panneauVerticalGauche.getChildren().add(titreLabel); //permet d'afficher l'élément dans le panneau

        //CASE AJOUTER UN LABEL
        TextField ajouterLabel = new TextField();
        panneauVerticalGauche.getChildren().add(ajouterLabel); //permet d'afficher l'élément dans le panneau

        //LABEL CHECK LABEL
        Label checkLabel = new Label("");
        panneauVerticalGauche.getChildren().add(checkLabel); //indique l'état de l'ajout d'un label

        //ADD LABEL BUTTON
        Button addLabelButton = new Button();
        panneauVerticalGauche.getChildren().add(addLabelButton); //permet d'afficher l'élément dans le panneau

        Image checkIcon = new Image(getClass().getResourceAsStream("/images/check.png"));
        ImageView checkIconView = new ImageView(checkIcon);
        checkIconView.setFitHeight(15);
        checkIconView.setFitWidth(15);
        addLabelButton.setGraphic(checkIconView);//setting icon to button
        addLabelButton.getStyleClass().add("left-button");

        //Le bouton devient visible seulement lorsqu'on écrit qqchse dans la case
        //checkButton.visibleProperty().bind(ajouterLabel.textProperty().isEmpty().not());

        //CASE OÙ SONT STOCKéS LES LABELS
        panneauLabel = new ListView();
        panneauVerticalGauche.getChildren().add(panneauLabel); //permet d'afficher l'élément dans le panneau
        panneauLabel.getStyleClass().add("panneauLabel");

        //Event qui ajoute un label dans le panneau
        addLabelButton.setOnAction(e ->
        {
            if (ajouterLabel.getText().matches("[A-Za-z0-9éöèüàäç]+")) {
                if (!panneauLabel.getItems().contains(ajouterLabel.getText())) {
                    panneauLabel.getItems().add(ajouterLabel.getText());
                    checkLabel.setText("\"" + ajouterLabel.getText() + "\" ajouté avec succès !");
                } else {
                    checkLabel.setText("\"" + ajouterLabel.getText() + "\" est déjà présent dans la liste.");
                }
                ajouterLabel.setText(""); //case vide à nouveau
            } else {
                checkLabel.setText("Chiffres et lettres uniquement !");
            }
        });

        //DELETE LABEL BUTTON
        Button deleteLabelButton = new Button();
        panneauVerticalGauche.getChildren().add(deleteLabelButton); //permet d'afficher l'élément dans le panneau

        Image deleteIcon = new Image(getClass().getResourceAsStream("/images/delete.png"));
        ImageView deleteIconView = new ImageView(deleteIcon);
        deleteIconView.setFitHeight(15);
        deleteIconView.setFitWidth(15);
        deleteLabelButton.setGraphic(deleteIconView);//setting icon to button
        deleteLabelButton.getStyleClass().add("left-button");
        panneauLabel.getSelectionModel().select(0);

        Label deleteLabel = new Label("");

        deleteLabelButton.setOnAction(e ->
        {
            try {
                panneauLabel.getItems().remove(panneauLabel.getSelectionModel().getSelectedIndex());
            } catch (Exception ex) {
                deleteLabel.setText("Aucun label sélectionné.");
            }
        });

        panneauVerticalGauche.getChildren().add(deleteLabel); //indique l'état de l'ajout d'un label


        //marges extérieures des deux cases + buttons
        VBox.setMargin(panneauLabel, new Insets(10, 10, 10, 10));
        VBox.setMargin(titreLabel, new Insets(10, 10, 10, 10));
        VBox.setMargin(ajouterLabel, new Insets(10, 10, 10, 10));
        VBox.setMargin(checkLabel, new Insets(1, 10, 10, 10));
        VBox.setMargin(deleteLabel, new Insets(1, 10, 10, 10));
        VBox.setMargin(addLabelButton, new Insets(1, 10, 1, 150));
        VBox.setMargin(deleteLabelButton, new Insets(1, 10, 1, 150));

        //La colonne prend la longueur de la fenêtre
        //VBox.setVgrow(panneauLabel, Priority.ALWAYS);

        return panneauVerticalGauche;
    }

    /**
     * MENU DROITE
     *
     * @return
     */
    private VBox menuDroite() {
        VBox panneauVerticalDroit = new VBox();
        panneauVerticalDroit.getStyleClass().add("menuLabelsDroite-vbox");

        //CASE AJOUTER UN LABEL
        TextField ajouterLabel = new TextField();
        panneauVerticalDroit.getChildren().add(ajouterLabel); //permet d'afficher l'élément dans le panneau

        return panneauVerticalDroit;
    }

    /**
     * QUIT BUTTON
     *
     * @param hb
     */
    private void quitButton(HBox hb) {

        StackPane stack = new StackPane();

        //Permet de quitter l'application
        Image quitIcon = new Image(getClass().getResourceAsStream("/images/quit.png"));
        ImageView quitIconView = new ImageView(quitIcon);
        quitIconView.setFitHeight(15);
        quitIconView.setFitWidth(15);
        quitButton.setGraphic(quitIconView);//setting icon to button
        quitButton.setAlignment(Pos.CENTER_RIGHT);

        quitButton.setOnAction(event -> {
            if(fichierExporte)
            {
                Stage stage = (Stage) quitButton.getScene().getWindow();
                stage.close();
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Quitter l'application");
                alert.setHeaderText("");
                alert.setContentText("Vous êtes sur le point de quitter l'application sans avoir sauvegardé.");

                ButtonType oui = new ButtonType("Ok desu.");
                ButtonType non = new ButtonType("Annuler");

                alert.getButtonTypes().setAll(oui, non);

                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add(
                        getClass().getResource("/design/stylesheet.css").toExternalForm());


                alert.showAndWait();
                if (alert.getResult() == oui) {
                    Stage stage = (Stage) quitButton.getScene().getWindow();
                    stage.close();
                }
            }

        });

        stack.getChildren().addAll(quitButton);
        stack.setAlignment(Pos.CENTER_RIGHT);
        // Add offset to right for question mark to compensate for RIGHT
        // alignment of all nodes

        hb.getChildren().add(stack);
        HBox.setHgrow(stack, Priority.ALWAYS);

    }

    /*
     * BOUTONS SAUVEGARDER ET ANNULER (pas encore utilisés)
     *
     * @param grid Grid to anchor to the top of the anchor pane
     */
    private AnchorPane saveAndCancel(GridPane grid) {

        AnchorPane anchorpane = new AnchorPane();

        Button buttonSave = new Button("Save");
        Button buttonCancel = new Button("Cancel");

        HBox hb = new HBox();
        hb.setPadding(new Insets(0, 10, 10, 10));
        hb.setSpacing(10);
        hb.getChildren().addAll(buttonSave, buttonCancel);

        anchorpane.getChildren().addAll(grid, hb);
        // Anchor buttons to bottom right, anchor grid to top
        AnchorPane.setBottomAnchor(hb, 8.0);
        AnchorPane.setRightAnchor(hb, 5.0);
        AnchorPane.setTopAnchor(grid, 10.0);

        return anchorpane;
    }
}
