import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.Console;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PaintSurface extends JComponent
{
    static int compteurRectangle = 1;

    //Image en cours d'utilisation
    private Image image;

    //Fichier en cours d'utilisation
    private File file;

    final double RATIO_IMAGE = 1;

    //Contient tous les différents rectangles qui ont pu être dessinés.
    private static ArrayList<MyShape> shapes = new ArrayList<>();

    //Points de départ et d'arrivée du rectangle
    private Point startDrag, endDrag;

    //Axes de symétrie
    private Line2D horizontalLine, verticalLine;

    private ListView panneauLabel;

    PaintSurface(Image img, File file, ListView panneauLabel, StackPane sp)
    {
        this.image = img;
        this.file = file;
        this.panneauLabel = panneauLabel;
        this.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                startDrag = new Point(e.getX(), e.getY());
                endDrag = startDrag;
                // Le rectangle ne se dessine que s'il était dans l'image au début
                if(startDrag.x <= (RATIO_IMAGE * image.getWidth()) && startDrag.y <= (RATIO_IMAGE * image.getHeight())) {
                    repaint();
                }
            }

            public void mouseReleased(MouseEvent e)
            {
                //On stocke les variables du rectangle qu'on a dessiné.
                int x2 = e.getX();
                int y2 = e.getY();

                // En cas de relachement en dehors de l'image, le rectangle se dessine en bordure
                if(endDrag.x < 0)
                    x2 = 0;
                if(endDrag.x > (RATIO_IMAGE * image.getWidth()))
                    x2 = (int)(RATIO_IMAGE * image.getWidth());
                if(endDrag.y < 0)
                    y2 = 0;
                if(endDrag.y > (RATIO_IMAGE * image.getHeight()))
                    y2 = (int)(RATIO_IMAGE * image.getHeight());

                // Le rectangle ne se dessine que s'il était dans l'image au début
                if(startDrag.x <= (RATIO_IMAGE * image.getWidth()) && startDrag.y <= (RATIO_IMAGE * image.getHeight())) {

                    String label = "<Label" + compteurRectangle + ">";
                    //Fonction lambda qui permet de gérer les erreurs liées à la modification de panneauLabel
                    //tout en ajoutant le label à la listView
                    Platform.runLater(() ->
                    {
                        panneauLabel.getItems().add(compteurRectangle - 1, label + " : double-click to rename");
                        ++compteurRectangle;
                    });

                    // Crée la forme et l'ajoute à la liste
                    shapes.add(new MyShape(makeRectangle(startDrag.x, startDrag.y, x2, y2),
                            Math.min(startDrag.x, x2), Math.min(startDrag.y, y2), Math.abs(startDrag.x - x2), Math.abs(startDrag.y - y2), label));
                }

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
            public void mouseDragged(MouseEvent e)
            {
                endDrag = new Point(e.getX(), e.getY());
                repaint();
            }

            public void mouseMoved(MouseEvent e)
            {
                verticalLine = null;
                horizontalLine = null;
                if(e.getX() <= (RATIO_IMAGE * image.getWidth()))
                {
                    verticalLine = makeVerticalLine(e.getX(), (int)(RATIO_IMAGE * image.getHeight()));
                }
                if(e.getY() <= (RATIO_IMAGE * image.getHeight()))
                {
                    horizontalLine = makeHorizontalLine(e.getY(), (int)(RATIO_IMAGE * image.getWidth()));
                }
                repaint();
            }
        });

        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e)
            {
                if(shapes.isEmpty())
                {
                    image = new Image(file.toURI().toString(), (sp.getWidth()), (sp.getHeight()), true, false);
                    repaint();
                }
            }
        });
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Définit les couleurs utilisées
        Color[] colors = {Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.RED, Color.GRAY, Color.PINK, Color.ORANGE, Color.GREEN};
        int colorIndex = 0;

        // Dessine l'image en arrière-plan
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(file);
        } catch (Exception e) {
            System.out.println("Aucun fichier sélectionné");
        }
        g2.drawImage(bi, 0, 0, (int) (RATIO_IMAGE * image.getWidth()), (int) (RATIO_IMAGE * image.getHeight()), null);

        //Définit la taille et l'opacité des traits
        g2.setStroke(new BasicStroke(3));
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        // Affiche les rectangles terminés selon les couleurs présentes dans le tableau "colors"
        for(int i = 0; i < panneauLabel.getItems().size(); i++) {
            MyShape s = shapes.get(i);
            g2.setPaint(colors[(colorIndex++) % colors.length]);
            g2.draw(s.getShape());
            g2.fillRect(s.getX1(), s.getY1() - 15, s.getX2(), 15);
            g2.setPaint(Color.BLACK);
            g.setFont(new Font("Century Gothic", Font.BOLD, 12));
            s.setLabel(panneauLabel.getItems().get(i).toString());
            g2.drawString(s.getLabel(), s.getX1() + 3, s.getY1() - 3);
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
            Shape s = makeRectangle(startDrag.x, startDrag.y, endDrag.x, endDrag.y);
            g2.draw(s);
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

    public static ArrayList<MyShape> getShapes() {
        return shapes;
    }
}