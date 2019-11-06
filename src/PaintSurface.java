
import javafx.scene.image.Image;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class PaintSurface extends JComponent
{
    private Shape r;
    private double RATIO_IMAGE = 1.4;
    private Image image;
    private File file;

    //Contient tous les différents rectangles qui ont pu être dessinés.
    private ArrayList<Shape> shapes = new ArrayList<>();

    //Points de départ et d'arrivée du rectangle
    private Point startDrag, endDrag;

    //Axes de symétrie
    private Line2D horizontalLine, verticalLine;

    PaintSurface(Image image, File file)
    {
        this.image = image;
        this.file = file;
        this.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                startDrag = new Point(e.getX(), e.getY());
                endDrag = startDrag;
                repaint();
            }

            public void mouseReleased(MouseEvent e)
            {
                //On stocke les variables du rectangle qu'on a dessiné.
                int x = e.getX();
                int y = e.getY();

                // En cas de relachement en dehors de l'image, le rectangle se dessine en bordure
                if(endDrag.x < 0)
                    x = 0;
                if(endDrag.x > (RATIO_IMAGE * image.getWidth()))
                    x = (int)(RATIO_IMAGE * image.getWidth());
                if(endDrag.y < 0)
                    y = 0;
                if(endDrag.y > (RATIO_IMAGE * image.getHeight()))
                    y = (int)(RATIO_IMAGE * image.getHeight());

                // Le rectangle ne se dessine que s'il était dans l'image au début
                if(startDrag.x <= (RATIO_IMAGE * image.getWidth()) && startDrag.y <= (RATIO_IMAGE * image.getHeight())) {
                    r = makeRectangle(startDrag.x, startDrag.y, x, y);
                    shapes.add(r);
                    // TODO : associer le rectangle à un label (via une liste par exemple)
                    // pour pouvoir récupérer ses 4 coordonnées et mettre tout ça dans le fichier d'output
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
            public void mouseDragged(MouseEvent e) {
                endDrag = new Point(e.getX(), e.getY());
                repaint();
            }

            public void mouseMoved(MouseEvent e) {
                verticalLine = null;
                horizontalLine = null;
                if(e.getX() <= (RATIO_IMAGE * image.getWidth())) {
                    verticalLine = makeVerticalLine(e.getX(), (int)(RATIO_IMAGE * image.getHeight()));
                }
                if(e.getY() <= (RATIO_IMAGE * image.getHeight())) {
                    horizontalLine = makeHorizontalLine(e.getY(), (int)(RATIO_IMAGE * image.getWidth()));
                }
                repaint();
            }
        });
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Définit les couleurs utilisées
        Color[] colors = {Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.RED, Color.BLUE, Color.PINK, Color.ORANGE, Color.GREEN};
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
            r = makeRectangle(startDrag.x, startDrag.y, endDrag.x, endDrag.y);

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