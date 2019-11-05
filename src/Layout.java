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
    private boolean fichierExporte; //sert à s'assurer que l'utilisateur a bien sauvegardé avant de quitter

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

        //fond blanc de base
        border.getStyleClass().add("general-borderPanel");

        //------------------------------------------------------------------
        // BARRE DE NAVIGATION
        //------------------------------------------------------------------

        //Import and export buttons
        importButton = new Button("Sélectionner une image");
        importButton.getStyleClass().add("header-button");

        exportButton = new Button("Exporter les labels");
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
        Scene scene = new Scene(border, 1200, 700);

        scene.getStylesheets().add("/design/stylesheet.css");
        window.setScene(scene);

        window.setTitle("IHM - Labo1");
        window.initStyle(StageStyle.UNDECORATED);
        window.show();

        /*
        // Réduit ou agrandit toutes les fenêtres d'un coup
        this.addWindowStateListener(e -> {
            // Minimized
            if ((e.getNewState() & Frame.ICONIFIED) == Frame.ICONIFIED && drawingBoard != null) {
                drawingBoard.setAlwaysOnTop(false);
                drawingBoard.setState(Frame.ICONIFIED);

            }
            // Maximized
            else if ((e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH && drawingBoard != null) {
                drawingBoard.setAlwaysOnTop(true);
                drawingBoard.setState(Frame.NORMAL);
            }
        });
        */
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
     * Surface de dessin
     */
    private class PaintSurface extends JComponent {
        ArrayList<Shape> shapes = new ArrayList<>();
        Point startDrag, endDrag;
        Line2D horizontalLine, verticalLine;

        private PaintSurface() {
            this.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    startDrag = new Point(e.getX(), e.getY());
                    endDrag = startDrag;
                    repaint();
                }

                public void mouseReleased(MouseEvent e) {
                    int x = e.getX();
                    int y = e.getY();

                    // En cas de relachement en dehors de l'image, le carré se dessine en bordure
                    if(endDrag.x < 0) x = 0;
                    if(endDrag.x > image.getWidth()) x = (int)image.getWidth();
                    if(endDrag.y < 0) y = 0;
                    if(endDrag.y > image.getHeight()) y = (int)image.getHeight();

                    Shape r = makeRectangle(startDrag.x, startDrag.y, x, y);
                    shapes.add(r);
                    // TODO : associer le rectangle à un label (via une liste par exemple)
                    // pour pouvoir récupérer ses 4 coordonnées et mettre tout ça dans le fichier d'output
                    startDrag = null;
                    endDrag = null;
                    repaint();
                }

                public void mouseExited(MouseEvent e) {
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
            Color[] colors = { Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.RED, Color.BLUE, Color.PINK};
            int colorIndex = 0;

            // Dessine l'image en arrière-plan
            BufferedImage bi = null;
            try {
                bi = ImageIO.read(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            g2.drawImage(bi, 0, 0, null);

            // Définit la taille et l'opacité des traits
            g2.setStroke(new BasicStroke(3));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            // Affiche les rectangles terminés
            for (Shape s : shapes) {
                g2.setPaint(colors[(colorIndex++) % 6]);
                g2.draw(s);
            }

            // Affiche les lignes verticales et horizontales
            if (verticalLine != null && horizontalLine != null) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f));
                g2.setPaint(Color.ORANGE);
                g2.draw(verticalLine);
                g2.draw(horizontalLine);
            }

            // Affiche le rectangle en cours de dessin
            if (startDrag != null && endDrag != null) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f));
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

                    /*
                    // Affiche la fenêtre de dessin, la détruit si elle existait avant
                    if(drawingBoard != null) {
                        drawingBoard.dispose();
                    }
                    drawingBoard = new DrawingBoard((int)image.getWidth(), (int)image.getHeight());
                     */
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
                alert.setContentText("Tous les labels n'ont pas été exportés. Souhaitez-vous vraiment quitter ?");
                
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
