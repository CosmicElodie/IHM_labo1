import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
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

import java.io.File;

/**
 * Sample application that shows examples of the different layout panes
 * provided by the JavaFX layout API.
 * The resulting UI is for demonstration purposes only and is not interactive.
 */
public class Layout extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        //------------------------------------------------------------------
        // DEFINITION DE LA FENÊTRE
        //------------------------------------------------------------------
        //Racine de scene
        BorderPane border = new BorderPane();

        //fond rose de base
        border.setStyle("-fx-background-color: FFDEF7;");

        //------------------------------------------------------------------
        // BARRE DE NAVIGATION
        //------------------------------------------------------------------

        Button button1 = new Button("Upload picture");

        HBox hbox = new HBox(10, button1);
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.setStyle("-fx-background-color: #CDAEF3;");

        //hbox.getChildren().addAll(button1, chosen);

        // Ajouter un bouton "aide" dans le header
        helpButton(hbox);

        //On crée une barre de navigation dans le BorderPane
        border.setTop(hbox);

        //------------------------------------------------------------------
        // CORPS LOGICIEL OÙ SE TROUVE L'IMAGE
        //------------------------------------------------------------------

        //On crée le corps du logiciel (là où sera l'image)
        GridPane grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(50, 50, 50, 50));
        grid.setStyle("-fx-background-color: #DEEAFF;");

        //On met en place le corps du texte
        border.setCenter(grid);

        //On upload l'image à partir de la sélection faite dans le gestionnaire de fichier
        button1.setOnAction(
                event -> {
                    Image image = null;
                    ImageView imageView = new ImageView();

                    FileChooser chooser = new FileChooser();
                    File file2 = chooser.showOpenDialog(stage);
                    if(file2 != null)
                    {
                        //Permet d'afficher l'image dans le corps de l'application
                        image = new Image(file2.toURI().toString(), 800, 300 ,true,false);
                        imageView.setImage(image);
                        grid.getChildren().add(imageView);
                    }


                }
        );


        //------------------------------------------------------------------
        // COLONNE GAUCHE POUR LABELS
        //------------------------------------------------------------------

        //On crée une colonne contenant les labels
        border.setLeft(addVBox());

        //------------------------------------------------------------------
        // PARAMÈTRES DE LA FENÊTRE DU LOGICIEL
        //------------------------------------------------------------------
        //Si on veut rajouter une colonne à droite
        //border.setRight(addFlowPane());
        //border.setRight(addTilePane());

        Scene scene = new Scene(border,950,600);
        stage.setScene(scene);
        stage.setTitle("BeSt ApP Ev4");
        stage.show();
    }

    /*
     * MENU GAUCHE
     */
    private VBox addVBox() {

        VBox panneauVertical = new VBox();
        panneauVertical.setPadding(new Insets(15, 12, 15, 12));

        //Titre
        Label labelTextField = new Label("Ajouter un label");
        panneauVertical.getChildren().add(labelTextField); //permet d'afficher l'élément dans le panneau

        //Case pour ajouter un label
        TextField ajouterLabel = new TextField();
        panneauVertical.getChildren().add(ajouterLabel); //permet d'afficher l'élément dans le panneau

        Button checkButton = new Button();
        panneauVertical.getChildren().add(checkButton); //permet d'afficher l'élément dans le panneau

        //the check icon
        Image checkIcon = new Image(getClass().getResourceAsStream("/images/check.png"));
        ImageView checkIconView = new ImageView(checkIcon);
        checkIconView.setFitHeight(10);
        checkIconView.setFitWidth(10);
        checkButton.setGraphic(checkIconView);//setting icon to button

        //Le bouton devient visible seulement lorsqu'on écrit qqchse dans la case
        //checkButton.visibleProperty().bind(ajouterLabel.textProperty().isEmpty().not());

        TextArea panneauLabel = new TextArea();
        panneauVertical.getChildren().add(panneauLabel); //permet d'afficher l'élément dans le panneau
        panneauLabel.setPrefWidth(150);
        panneauLabel.setPrefHeight(300);

        Button deleteButton = new Button();
        panneauVertical.getChildren().add(deleteButton); //permet d'afficher l'élément dans le panneau

        //the delete icon
        Image deleteIcon = new Image(getClass().getResourceAsStream("/images/delete.png"));
        ImageView deleteIconView = new ImageView(deleteIcon);
        deleteIconView.setFitHeight(10);
        deleteIconView.setFitWidth(10);
        deleteButton.setGraphic(deleteIconView);//setting icon to button

        //marges extérieures des deux cases + buttons
        VBox.setMargin(panneauLabel, new Insets(10, 10, 10, 10));
        VBox.setMargin(ajouterLabel, new Insets(10, 10, 10, 10));
        VBox.setMargin(checkButton, new Insets(1, 10, 1, 150));
        VBox.setMargin(deleteButton, new Insets(1, 10, 1, 150));

        //La colonne prend la longueur de la fenêtre
        //VBox.setVgrow(panneauLabel, Priority.ALWAYS);

        return panneauVertical;
    }

    /*
     * HELP BUTTON EN HAUT à DROITE
     *
     * @param hb HBox to add the stack to
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


    /* PAS ENCORE UTILISÉ
     * Creates a horizontal flow pane with eight icons in four rows
     */
    private FlowPane addFlowPane() {

        FlowPane flow = new FlowPane();

        flow.setPadding(new Insets(5, 0, 5, 0));
        flow.setVgap(4);
        flow.setHgap(4);
        flow.setPrefWrapLength(170); // preferred width allows for two columns
        flow.setStyle("-fx-background-color: DAE6F3;");

        ImageView pages[] = new ImageView[8];
        for (int i=0; i<8; i++) {
            pages[i] = new ImageView(
                    new Image(Layout.class.getResourceAsStream(
                            "graphics/chart_"+(i+1)+".png")));
            flow.getChildren().add(pages[i]);
        }

        return flow;
    }

    /* PAS ENCORE UTILISÉ
     * Creates a horizontal (default) tile pane with eight icons in four rows
     */
    private TilePane addTilePane() {

        TilePane tile = new TilePane();
        tile.setPadding(new Insets(5, 0, 5, 0));
        tile.setVgap(4);
        tile.setHgap(4);
        tile.setPrefColumns(2);
        tile.setStyle("-fx-background-color: DAE6F3;");

        ImageView pages[] = new ImageView[8];
        for (int i=0; i<8; i++) {
            pages[i] = new ImageView(
                    new Image(Layout.class.getResourceAsStream(
                            "graphics/chart_"+(i+1)+".png")));
            tile.getChildren().add(pages[i]);
        }

        return tile;
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
