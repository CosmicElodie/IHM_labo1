import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jdk.nashorn.internal.runtime.arrays.ArrayIndex;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

public class Layout extends Application {

    private File file, tempFile;
    private boolean fichierExporte; //sert à s'assurer que l'utilisateur ait bien sauvegardé avant de quitter
    private boolean labelExporte; //pareil, mais pour s'assurer que les labels aient bien été exportés

    private Image image;

    private Button importButton;
    private Button exportButton;
    private Button quitButton;

    private ListView<String> panneauLabel;
    private Label messageImporterExporter;
    private String fileName;

    //Corps du logiciel
    private StackPane sp;

    @Override
    public void start(Stage stage) {
        //------------------------------------------------------------------
        // DEFINITION DE LA FENÊTRE
        //------------------------------------------------------------------
        //Racine de scene
        BorderPane border = new BorderPane();

        //fond de base
        border.getStyleClass().add("general-borderPanel");

        //------------------------------------------------------------------
        // BARRE DE NAVIGATION
        //------------------------------------------------------------------

        //Import and export buttons
        importButton = new Button("Nouveau projet");
        importButton.getStyleClass().add("header-button");

        exportButton = new Button("Exporter projet");
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
        //border.setRight(menuDroite());

        //------------------------------------------------------------------
        // FOOTER
        //------------------------------------------------------------------

        //On crée un footer dans le BorderPane
        border.setBottom(footerBar());

        //------------------------------------------------------------------
        // PARAMÈTRES DE LA FENÊTRE DU LOGICIEL
        //------------------------------------------------------------------

        Scene scene = new Scene(border, 1200, 600);

        scene.getStylesheets().add("/design/stylesheet.css");
        stage.setScene(scene);
        stage.setTitle("IHM - Labo1");
        //window.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    /**
     * NAVIGATION
     *
     * @return hbox
     */
    private HBox navBar() {
        //On créé une boxe horizontale qui définira l'espace "navigation".
        HBox hbox = new HBox(10);

        //on règle l'écart du contenu intérieur avec les bords de la boxe
        hbox.setPadding(new Insets(15, 15, 15, 15));

        //Espace entre les éléments
        hbox.setSpacing(10);   // Gap between nodes

        //On lui applique d'autres styles présents dans la feuille CSS
        hbox.getStyleClass().add("header-hbox");

        //On déclare le label qui va nous signifier si l'image a correctement été importée/exportée
        messageImporterExporter = new Label("");

        //CASE NOMMER FICHIER
        TextField nomOutputFile = new TextField();
        nomOutputFile.getStyleClass().add("header-interieur-case");

        //On ajoute tous ses éléments à la boxe
        hbox.getChildren().addAll(importButton, exportButton, nomOutputFile, messageImporterExporter);
        nomOutputFile.setText("NomDeVotreProjet");
        //On définit un bouton "exporter"
        exportButton.setOnAction(
                event ->
                {
                    fileName = "";
                    messageImporterExporter.setText("");
                    boolean isCorrectName = false;
                    boolean isEmpty = false;
                    boolean isSameName = false;

                    //seulement les chiffres + _ + lettres sans accent sont acceptées.
                    if (nomOutputFile.getText().matches("[A-Za-z0-9_]+")) {
                        isCorrectName = true;

                        if (file != null) {
                            //Le nom de l'output généré sera celui rentré dans la case.
                            fileName = nomOutputFile.getText();

                            //on définit l'extension
                            fileName += ".csv";

                            //on remet la case de l'output file à vide.
                            nomOutputFile.setText("");
                            messageImporterExporter.setText("");
                        }
                    } else if (nomOutputFile.getText().matches("")) {
                        isEmpty = true;
                    }


                    // Sauvegarde l'output à la racine du projet
                    try {
                        //Test si un fichier porte le même nom.
                        File directory = new File("src/output");
                        File[] fList = directory.listFiles();
                        for (File file : fList) {
                            if (file.getName().equals(fileName)) {
                                isSameName = true;
                                throw new Exception();
                            }
                        }

                        PrintWriter pw = new PrintWriter(new File("src\\output\\" + fileName));

                        fichierExporte = true;
                        labelExporte = true;

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
                        pw.close();

                        messageImporterExporter.setText(fileName + " a été correctement généré.");
                    } catch (Exception e) {
                        if (isSameName) {
                            messageImporterExporter.setText("Ce nom existe déjà, veuiillez en choisir un autre.");
                        } else if (!isCorrectName && !isEmpty)
                            messageImporterExporter.setText("Chiffre et lettre uniquement !");
                        else
                            messageImporterExporter.setText("L'output n'a pas correctement été généré");
                    }
                }
        );

        // Ajouter un bouton "quitter" dans le header
        quitButton(hbox);
        return hbox;
    }

    /**
     * Footer
     */
    private HBox footerBar() {

        //On définit une boxe horizontale qui définira l'espace "footer" -> copyright.
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
     * @return sp
     */
    private StackPane corpsLogiciel() {
        //On crée le corps du logiciel (là où sera l'image)
        sp = new StackPane();
        sp.getStyleClass().add("corps-gridPane");
        SwingNode swingNode = new SwingNode();
        sp.getChildren().add(swingNode);
        StackPane.setAlignment(swingNode, Pos.TOP_LEFT);

        //On upload l'image à partir de la sélection faite dans le gestionnaire de fichier
        importButton.setOnAction(
                event -> {
                    FileChooser fileChooser = new FileChooser();
                    fichierExporte = false;
                    messageImporterExporter.setText("");

                    if (file != null) {
                        File existDirectory = file.getParentFile();
                        fileChooser.setInitialDirectory(existDirectory);
                    }

                    //permet d'afficher les extensions qu'on accepte de sélectionner.
                    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.webp", "*.bmp");
                    fileChooser.getExtensionFilters().add(extFilter);

                    //Ouvre la fenêtre du gestionnaire de fichiers.
                    tempFile = fileChooser.showOpenDialog(null);
                    if (tempFile != null) {
                        file = tempFile;
                        tempFile = null;
                        //On supprime les labels de la fenêtre ensuite
                        panneauLabel.getItems().clear();

                        //Permet d'afficher l'image dans le corps de l'application
                        image = new Image(file.toURI().toString(), (sp.getWidth()), (sp.getHeight()), true, false);

                        // Affiche la fenêtre de dessin
                        SwingUtilities.invokeLater(() -> {
                            PaintSurface ps = new PaintSurface(image, file, panneauLabel, sp);
                            PaintSurface.compteurRectangle = 1;
                            swingNode.setContent(ps);
                        });
                    }
                }
        );

        return sp;
    }

    /**
     * Menu de gestions de labels (gauche)
     *
     * @return panneauVerticalGauche
     */
    private VBox menuLabels() {

        VBox panneauVerticalGauche = new VBox();
        panneauVerticalGauche.getStyleClass().add("menuLabelsGauche-vbox");

        Label titrePanneauLabeal = new Label("Labels");
        titrePanneauLabeal.getStyleClass().add("titre-label");
        panneauVerticalGauche.getChildren().add(titrePanneauLabeal);
        //CASE OÙ SONT STOCKéS LES LABELS
        panneauLabel = new ListView();
        panneauLabel.setCellFactory(TextFieldListCell.forListView());
        panneauLabel.setEditable(true);

        panneauVerticalGauche.getChildren().add(panneauLabel); //permet d'afficher l'élément dans le panneau
        panneauLabel.getStyleClass().add("panneauLabel");

        //permet de sélectionner plusieurs labels
        panneauLabel.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //DELETE LABEL BUTTON
        Label deleteLabel = new Label("");
        Button deleteLabelButton = new Button();
        panneauVerticalGauche.getChildren().add(deleteLabelButton); //permet d'afficher l'élément dans le panneau
        panneauVerticalGauche.getChildren().add(deleteLabel); //indique l'état de l'ajout d'un label

        Image deleteIcon = new Image(getClass().getResourceAsStream("/images/delete.png"));
        ImageView deleteIconView = new ImageView(deleteIcon);
        deleteIconView.setFitHeight(15);
        deleteIconView.setFitWidth(15);
        deleteLabelButton.setGraphic(deleteIconView);//setting icon to button
        deleteLabelButton.getStyleClass().add("left-button");
        panneauLabel.getSelectionModel().select(0);

        deleteLabelButton.setOnAction(e ->
        {
            try {

                //Permet de supprimer plusieurs éléments en même temps
                if(panneauLabel.getSelectionModel().getSelectedItems().size() > 1)
                {
                    int taille = panneauLabel.getSelectionModel().getSelectedItems().size();
                    while (taille > 0)
                    {
                        System.out.println("Selected items : " + panneauLabel.getSelectionModel().getSelectedItems().size());
                        //on supprime le rectangle de l'array (il faut bouger la souris pour qu'il disparaisse)
                        PaintSurface.getShapes().remove(panneauLabel.getSelectionModel().getSelectedIndex());
                        panneauLabel.getItems().remove(panneauLabel.getSelectionModel().getSelectedItem());
                        --taille;
                    }
                    deleteLabel.setText("Les labels ont correctement\n été supprimés.");
                }
                //condition pour un unique élément sélectionné
                else
                {
                    PaintSurface.getShapes().remove(panneauLabel.getSelectionModel().getSelectedIndex());
                    panneauLabel.getItems().remove(panneauLabel.getSelectionModel().getSelectedItem());
                    deleteLabel.setText("Le label a correctement été supprimé.");
                }

            } catch (Exception ex) {
                deleteLabel.setText("Aucun label sélectionné.");
            }
        });

        //marges extérieures des deux cases + buttons
        VBox.setMargin(panneauLabel, new Insets(10, 10, 10, 10));
        VBox.setMargin(deleteLabel, new Insets(1, 10, 10, 10));
        VBox.setMargin(deleteLabelButton, new Insets(1, 10, 1, 150));

        return panneauVerticalGauche;
    }

    /**
     * MENU DROITE
     *
     * @return panneauVerticalDroit
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
     * @param hb : l'espace de la fenêtre sur lequel on travaille
     */
    private void quitButton(HBox hb) {

        StackPane stack = new StackPane();

        //Permet de quitter l'application
        Image quitIcon = new Image(getClass().getResourceAsStream("/images/quit.png"));
        ImageView quitIconView = new ImageView(quitIcon);
        quitIconView.setFitHeight(21);
        quitIconView.setFitWidth(21);
        quitButton.setGraphic(quitIconView);//setting icon to button
        quitButton.setAlignment(Pos.CENTER_RIGHT);

        quitButton.setOnAction(event -> {
            if (fichierExporte && labelExporte) {
                Stage stage = (Stage) quitButton.getScene().getWindow();
                stage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Quitter l'application");
                alert.setHeaderText("");
                alert.setContentText("Votre travail n'a pas été exporté et va donc être perdu. Souhaitez-vous vraiment quitter ?");

                //enlève les boutons de base de la fenêtre.
                alert.initStyle(StageStyle.UNDECORATED);
                ButtonType oui = new ButtonType("Quitter");
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

}
