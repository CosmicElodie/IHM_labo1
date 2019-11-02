import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.naming.Context;
import javax.print.attribute.AttributeSet;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Sample application that shows examples of the different layout panes
 * provided by the JavaFX layout API.
 * The resulting UI is for demonstration purposes only and is not interactive.
 */
public class Layout extends Application
{

    File file;
    Image image;
    ImageView imageView;

    File file;
    Image image;
    ImageView imageView;
    Button importButton;
    Button exportButton;
    int x1, y1, x2, y2 = 0;

    @Override
    public void start(Stage stage) throws Exception
    {
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
        importButton = new Button("Importer une image");
        importButton.getStyleClass().add("header-button");

        exportButton = new Button("Exporter les labels");
        exportButton.getStyleClass().add("header-button");

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

        //On crée une barre de navigation dans le BorderPane
        border.setBottom(footerBar());

        //------------------------------------------------------------------
        // PARAMÈTRES DE LA FENÊTRE DU LOGICIEL
        //------------------------------------------------------------------
        //Si on veut rajouter une colonne à droite
        //border.setRight(addFlowPane());
        //border.setRight(addTilePane());

        Scene scene = new Scene(border,950,600);
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
     * @return
     */
    private HBox navBar()
    {
        HBox hbox = new HBox(10);
        hbox.setPadding(new Insets(15, 15, 15, 15));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.getStyleClass().add("footer-header-hbox");
        hbox.getChildren().addAll(importButton, exportButton);

        // Ajouter un bouton "aide" dans le header
        helpButton(hbox);

        return hbox;
    }

    /**
     * Footer
     */
    private HBox footerBar()
    {
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
     * @return
     */
    private GridPane corpsLogiciel()
    {
        //On crée le corps du logiciel (là où sera l'image)
        GridPane grid = new GridPane();
        grid.getStyleClass().add("corps-gridPane");

        //On upload l'image à partir de la sélection faite dans le gestionnaire de fichier
        importButton.setOnAction(
                event -> {
                    FileChooser fileChooser = new FileChooser();
                    //Open directory from existing directory
                    if(file != null) {
                        File existDirectory = file.getParentFile();
                        fileChooser.setInitialDirectory(existDirectory);
                    }

                    //Set extension filter
                    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.webp", "*.bmp");

                    fileChooser.getExtensionFilters().add(extFilter);

                    //Show open file dialog
                    file = fileChooser.showOpenDialog(null);

                    if(file != null) {
                        //Permet d'afficher l'image dans le corps de l'application
                        grid.getChildren().remove(imageView);
                        image = new Image(file.toURI().toString(), 500, 450, true, false);
                        imageView = new ImageView();
                        imageView.setImage(image);
                        grid.getChildren().add(imageView);

                        // Permet de dessiner des rectangles
                        imageView.setOnMouseEntered(e-> {
                            //
                        });

                        imageView.setOnMouseExited(e-> {
                            //
                        });

                        imageView.setOnMousePressed(e->{
                            this.x1 = (int)e.getX();
                            this.y1 = (int)e.getY();
                        });

                        imageView.setOnMouseDragged(e-> {
                            this.x2 = (int)e.getX();
                            this.y2 = (int)e.getY();
                            //paint(g2d, x1, y1, x2, y2);
                        });

                        imageView.setOnMouseReleased(e-> {
                            this.x2 = (int)e.getX();
                            this.y2 = (int)e.getY();
                            //paint(g2d, x1, y1, x2, y2);
                        });
                    }
                }
        );

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
                        System.out.println("Exception while saving the image");
                    }
                    */

                    // Récupère le nom de l'image sans son extension l'extension
                    String format = "";
                    int i = file.getName().lastIndexOf('.');
                    if (i > 0) {
                        format = file.getName().substring(i);
                    }
                    String fileName = file.getName().replace(format,"");

                    // Sauvegarde l'output à la racine du projet
                    try (PrintWriter pw = new PrintWriter(new File(fileName + "Output.csv"))) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Image");
                        sb.append(',');
                        sb.append("Objects");
                        sb.append(',');
                        sb.append("Coordinates");
                        sb.append('\n');

                        sb.append(file.getPath());
                        sb.append(',');
                        sb.append("Yes");
                        sb.append(',');
                        sb.append("Yes");
                        sb.append('\n');

                        pw.write(sb.toString());
                    } catch (Exception e) {
                        System.out.println("Exception while saving the output file");
                    }
                }
        );
        return grid;
    }

    /**
     * Menu de gestions de labels (gauche)
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
        ListView panneauLabel = new ListView();
        panneauVerticalGauche.getChildren().add(panneauLabel); //permet d'afficher l'élément dans le panneau
        panneauLabel.getStyleClass().add("panneauLabel");

        //Event qui ajoute un label dans le panneau
        addLabelButton.setOnAction( e ->
        {
            if(ajouterLabel.getText().matches("[A-Za-z0-9éöèüàäç]+")) {
                if(!panneauLabel.getItems().contains(ajouterLabel.getText())) {
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

        deleteLabelButton.setOnAction( e ->
        {
            try {
                panneauLabel.getItems().remove(panneauLabel.getSelectionModel().getSelectedIndex());
            } catch(Exception ex) {
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
     * @return
     */
    private VBox menuDroite()
    {
        VBox panneauVerticalDroit = new VBox();
        panneauVerticalDroit.getStyleClass().add("menuLabelsDroite-vbox");

        //CASE AJOUTER UN LABEL
        TextField ajouterLabel = new TextField();
        panneauVerticalDroit.getChildren().add(ajouterLabel); //permet d'afficher l'élément dans le panneau

        return panneauVerticalDroit;
    }

    /**
     * Help button
     * -> pas encore implémenté les fonctionnalités
     * @param hb
     */
    private void helpButton(HBox hb) {

        StackPane stack = new StackPane();
        Rectangle helpIcon = new Rectangle(30.0, 25.0);
        helpIcon.setFill(new LinearGradient(0,0,0,1, true, CycleMethod.NO_CYCLE,
                new Stop[]{
                        new Stop(0,Color.web("#4977A3")),
                        new Stop(0.5, Color.web("#B0C6DA")),
                        new Stop(1,Color.web("#9CB6CF")),}));
        helpIcon.setStroke(Color.web("#D0E6FA"));
        helpIcon.setArcHeight(3.5);
        helpIcon.setArcWidth(3.5);

        Text helpText = new Text("?");
        helpText.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        helpText.setFill(Color.WHITE);
        helpText.setStroke(Color.web("#7080A0"));

        stack.getChildren().addAll(helpIcon, helpText);
        stack.setAlignment(Pos.CENTER_RIGHT);
        // Add offset to right for question mark to compensate for RIGHT
        // alignment of all nodes
        StackPane.setMargin(helpText, new Insets(0, 10, 0, 0));

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

        anchorpane.getChildren().addAll(grid,hb);
        // Anchor buttons to bottom right, anchor grid to top
        AnchorPane.setBottomAnchor(hb, 8.0);
        AnchorPane.setRightAnchor(hb, 5.0);
        AnchorPane.setTopAnchor(grid, 10.0);

        return anchorpane;
    }
}