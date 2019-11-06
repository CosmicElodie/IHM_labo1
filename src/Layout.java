import javafx.application.Application;
import javafx.embed.swing.SwingNode;
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
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.View;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Layout extends Application {

    private Stage window;
    private File file;
    private boolean fichierExporte; //sert à s'assurer que l'utilisateur ait bien sauvegardé avant de quitter

    private Image image;

    private Button importButton;
    private Button exportButton;
    private Button quitButton;

    private ListView panneauLabel;
    private Label checkLabel;
    private Label messageImporterExporter;

    @Override
    public void start(Stage stage) {
        window = stage;
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
        border.setRight(menuDroite());

        //------------------------------------------------------------------
        // FOOTER
        //------------------------------------------------------------------

        //On crée un footer dans le BorderPane
        border.setBottom(footerBar());

        //------------------------------------------------------------------
        // PARAMÈTRES DE LA FENÊTRE DU LOGICIEL
        //------------------------------------------------------------------

        // Pas beau : définit la taille de la fenêtre au maximum pour la superposition correcte de la fenêtre de dessin
        //double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        //double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 45;
        Scene scene = new Scene(border, 1200, 600);

        scene.getStylesheets().add("/design/stylesheet.css");
        window.setScene(scene);

        window.setTitle("IHM - Labo1");
        window.initStyle(StageStyle.UNDECORATED);
        window.show();
    }

    /**
     * NAVIGATION
     * -> upload button
     * -> help button
     *
     * @return
     */
    private HBox navBar() {
        //On créé une boxe horizontale qui définira l'espace "navigation".
        HBox hbox = new HBox(10);

        //on règle l'écart du contenu intérieur avec les bords de la boxe
        hbox.setPadding(new Insets(15, 15, 15, 15));

        //Espace entre les éléments
        hbox.setSpacing(10);

        //On lui applique d'autres styles présents dans la feuille CSS
        hbox.getStyleClass().add("header-hbox");

        //On déclare le label qui va nous signifier si l'image a correctement été importée/exportée
        messageImporterExporter = new Label("");

        //On ajoute tous ses éléments à la boxe
        hbox.getChildren().addAll(importButton, exportButton, messageImporterExporter);

        //On définit un bouton "exporter"
        exportButton.setOnAction(
            event -> {
                // Récupère le nom de l'image sans son extension
                if (file != null) {
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

                        messageImporterExporter.setText("L'output a été correctement généré.");
                    } catch (Exception e) {
                        fichierExporte = false;
                        messageImporterExporter.setText("L'output n'a pas correctement été généré");
                    }
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
     * Surface de dessin
     */
    private class PaintSurface extends JComponent
    {
        //Contient tous les différents rectangles qui ont pu être dessinés.
        private ArrayList<Shape> shapes = new ArrayList<>();

        //Points de départ et d'arrivée du rectangle
        private Point startDrag, endDrag;

        //Axes de symétrie
        private Line2D horizontalLine, verticalLine;

        private PaintSurface()
        {
            this.addMouseListener(new MouseAdapter()
            {
                public void mousePressed(MouseEvent e)
                {
                    //Si on clique et qu'on ne bouge pas, les coordonnées sont stockées dans le point de départ
                    startDrag = new Point(e.getX(), e.getY());
                    endDrag = startDrag;
                    repaint();
                }


                public void mouseReleased(MouseEvent e)
                {
                    //On stocke les variables du rectangle qu'on a dessiné.
                    int x = e.getX();
                    int y = e.getY();

                    // En cas de relachement en dehors de l'image, le carré se dessine en bordure
                    if(endDrag.x < 0)
                        x = 0;
                    if(endDrag.x > image.getWidth())
                        x = (int)image.getWidth();
                    if(endDrag.y < 0)
                        y = 0;
                    if(endDrag.y > image.getHeight())
                        y = (int)image.getHeight();

                    Shape r = makeRectangle(startDrag.x, startDrag.y, x, y);
                    shapes.add(r);
                    // TODO : associer le rectangle à un label (via une liste par exemple)
                    // pour pouvoir récupérer ses 4 coordonnées et mettre tout ça dans le fichier d'output
                    startDrag = null;
                    endDrag = null;
                    repaint();
                }

                public void mouseExited(MouseEvent e)
                {
                    verticalLine = null;
                    horizontalLine = null;
                    repaint();
                }
            });

            this.addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    endDrag = new Point(e.getX(), e.getY());
                    repaint();
                }

                public void mouseMoved(MouseEvent e) {
                    verticalLine = makeVerticalLine(e.getX(), (int)image.getHeight());
                    horizontalLine = makeHorizontalLine(e.getY(), (int)image.getWidth());
                    repaint();
                }
            });
        }

        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Définit les couleurs utilisées
            Color[] colors = { Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.RED, Color.BLUE, Color.PINK, Color.ORANGE, Color.GREEN};
            int colorIndex = 0;

            // Dessine l'image en arrière-plan
            BufferedImage bi = null;
            try {
                bi = ImageIO.read(file);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            g2.drawImage(bi, 0, 0, (int)image.getWidth(), (int)image.getHeight(), null);

            // Définit la taille et l'opacité des traits
            g2.setStroke(new BasicStroke(3));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

            // Affiche les rectangles terminés selon les couleurs présentes dans le tableau "colors"
            for (Shape s : shapes) {
                g2.setPaint(colors[(colorIndex++) % colors.length]);
                g2.draw(s);
            }

            // Affiche les lignes verticales et horizontales
            if (verticalLine != null && horizontalLine != null) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                g2.setPaint(Color.ORANGE);
                g2.draw(verticalLine);
                g2.draw(horizontalLine);
            }

            // Affiche le rectangle en cours de dessin
            if (startDrag != null && endDrag != null) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                g2.setPaint(Color.ORANGE);
                Shape r = makeRectangle(startDrag.x, startDrag.y, endDrag.x, endDrag.y);
                g2.draw(r);
            }
        }

        private Rectangle2D.Float makeRectangle(int x1, int y1, int x2, int y2) {
            return new Rectangle2D.Float(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
        }

        private Line2D.Float makeVerticalLine(int x, int imageHeight) {
            return new Line2D.Float(x, 0, x, imageHeight);
        }

        private Line2D.Float makeHorizontalLine(int y, int imageWidth) {
            return new Line2D.Float(0, y, imageWidth, y);
        }
    }

    /**
     * Corps logiciel
     * -> là où se charge l'image
     *
     * @return
     */
    private StackPane corpsLogiciel() {
        //On crée le corps du logiciel (là où sera l'image)
        StackPane sp = new StackPane();
        sp.getStyleClass().add("corps-gridPane");
        SwingNode swingNode = new SwingNode();
        sp.getChildren().add(swingNode);
        sp.setAlignment(swingNode, Pos.TOP_LEFT);

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
                file = fileChooser.showOpenDialog(null);

                if (file != null) {
                    //On supprime les labels de la fenêtre ensuite
                    panneauLabel.getItems().clear();
                    checkLabel.setText("");

                    //Permet d'afficher l'image dans le corps de l'application
                    image = new Image(file.toURI().toString(), sp.getWidth(), sp.getHeight(), true, false);

                    // Affiche la fenêtre de dessin
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            swingNode.setContent(new PaintSurface());
                        }
                    });
                }
            }
        );

        return sp;
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
        checkLabel = new Label("");
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
                    checkLabel.setText("");
                    ajouterLabel.setText(""); //case vide à nouveau
                } else {
                    checkLabel.setText("\"" + ajouterLabel.getText() + "\" est déjà présent dans la liste.");
                }

            } else {
                checkLabel.setText("Chiffres et lettres uniquement !");
            }
        });


        Label deleteLabel = new Label("");
        panneauVerticalGauche.getChildren().add(deleteLabel); //indique l'état de l'ajout d'un label

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

        deleteLabelButton.setOnAction(e ->
        {
            try {
                panneauLabel.getItems().remove(panneauLabel.getSelectionModel().getSelectedIndex());
                deleteLabel.setText("");
            } catch (Exception ex) {
                deleteLabel.setText("Aucun label sélectionné.");
            }
        });

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
        quitIconView.setFitHeight(21);
        quitIconView.setFitWidth(21);
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
