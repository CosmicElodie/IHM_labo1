import java.awt.*;

public class MyShape {
    private Shape shape;
    private int x1, y1, x2, y2;
    private String label;

    MyShape(Shape shape, int x1, int y1, int x2, int y2, String label) {
        this.shape = shape;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.label = label;
    }

    public Shape getShape() {
        return shape;
    }

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }

    public String getLabel() {
        return label;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public String toString() {
        return "[(" + x1 + ", " + y1 + "), (" + x2 + ", " + y2 + ")]";
    }
}
