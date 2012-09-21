import java.awt.*;
import javax.swing.*;


class colorBox extends JPanel {

    //======================================================== fields
    private Color _ovalColor;      // Color of oval.
    private int red;
    private int green;
    private int blue;
    private int alpha = 255;

    //=================================================== constructor
    public colorBox() {
        _ovalColor = Color.GREEN;  // Initial color.
        setPreferredSize(new Dimension(100,100));
    }

    //================================================ paintComponent
    @Override public void paintComponent(Graphics g) {
        super.paintComponent(g);  // Ask parent to paint background.

        g.setColor(_ovalColor);
        int w = getWidth();       // Size might have changed if
        int h = getHeight();      // user resized window.
	int x = (int)w/5;
        g.fillOval(x, 0, 300, 300);
    }

    public void setAlpha(int a) {
	alpha = a;
	_ovalColor = new Color(red,green,blue,alpha);
    }

    //==================================================== setMyColor
    public void setMyColor(int r, int gr, int b) {
	red = r;
	green = gr;
	blue = b;
	_ovalColor = new Color(red,green,blue,alpha);
	// Remember color.
        repaint();         // Color changed, must repaint
    }
}
