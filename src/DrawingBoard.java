import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class DrawingBoard extends JFrame {

    private int imageWidth;
    private int imageHeight;

    public DrawingBoard(int imageWidth, int imageHeight) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.setSize(imageWidth, imageHeight);
        this.add(new PaintSurface());
        this.setUndecorated(true);
        this.setBackground(new Color(0,0,0,0.1f)); // Pas beau, mais procure un fond transparent
        this.setLocation(235, 95); // Pas beau, nécessite la fenêtre de base en plein écran pour un superpositionnement parfait
        this.setAlwaysOnTop(true); // Pas beau, reste devant même si la fenêtre de base se ferme
        this.setVisible(true);
    }

    private class PaintSurface extends JComponent {
        ArrayList<Shape> shapes = new ArrayList<>();
        Point startDrag, endDrag;
        Line2D horizontalLine, verticalLine;

        public PaintSurface() {
            this.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    startDrag = new Point(e.getX(), e.getY());
                    endDrag = startDrag;
                    repaint();
                }

                public void mouseReleased(MouseEvent e) {
                    Shape r = makeRectangle(startDrag.x, startDrag.y, e.getX(), e.getY());
                    shapes.add(r);
                    startDrag = null;
                    endDrag = null;
                    repaint();
                }
            });

            this.addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    endDrag = new Point(e.getX(), e.getY());
                    repaint();
                }

                public void mouseMoved(MouseEvent e) {
                    verticalLine = makeVerticalLine(e.getX());
                    horizontalLine = makeHorizontalLine(e.getY());
                    repaint();
                }
            });
        }

        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color[] colors = { Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.RED, Color.BLUE, Color.PINK}; // Possibilité d'ajouter d'autres couleurs
            int colorIndex = 0;

            g2.setStroke(new BasicStroke(3));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f));

            // Affiche les rectangles terminés
            for (Shape s : shapes) {
                g2.setPaint(colors[(colorIndex++) % 6]);
                g2.draw(s);
            }

            // Affiche les lignes verticales et horizontales
            if (verticalLine != null && horizontalLine != null) {
                g2.setPaint(Color.ORANGE);
                g2.draw(verticalLine);
                g2.draw(horizontalLine);
            }

            // Affiche le rectangle en cours de dessin
            if (startDrag != null && endDrag != null) {
                g2.setPaint(Color.ORANGE);
                Shape r = makeRectangle(startDrag.x, startDrag.y, endDrag.x, endDrag.y);
                g2.draw(r);
            }
        }

        private Rectangle2D.Float makeRectangle(int x1, int y1, int x2, int y2) {
            return new Rectangle2D.Float(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
            // TODO : associer le rectangle à un label (via une liste par exemple)
            // pour pouvoir récupérer ses 4 coordonnées et mettre tout ça dans le fichier d'output
        }

        private Line2D.Float makeHorizontalLine(int y) {
            return new Line2D.Float(0, y, imageWidth, y);
        }

        private Line2D.Float makeVerticalLine(int x) {
            return new Line2D.Float(x, 0, x, imageHeight);
        }
    }
}