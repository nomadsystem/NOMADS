import java.awt.*;
import javax.swing.*;
public class DiscussDisplayImagePanel extends JPanel
{
        Image img;
        public DiscussDisplayImagePanel( Image ic )
        {
			img=ic;
//			return img;
		}
        public void paintComponent( Graphics g )
        {
                super.paintComponent(g);
                if ( img != null )  
	{       
			//		System.out.println("Display image drawing");
                    int imgWidth = img.getWidth(this);  	// find the width of the image
                    int panelWidth = this.getWidth( );	// find the width of the panel
                    int x = (panelWidth - imgWidth ) / 2;	// calculate x to center the img
   
                    int imgHeight = img.getHeight( this );	// find height of image
                    int panelHeight = this.getHeight( );	// find height of panel
                    int y = (panelHeight - imgHeight ) / 2;	// calculate y to center the img
                    g.drawImage(img,x,y,img.getWidth(this),img.getHeight(this),this);	// paint the image
            }
            else {
            	System.out.println("ERROR loading image");
			}
    }
}
