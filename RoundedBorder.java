import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.Border;

class RoundedBorder implements Border{
   private int radius;

   RoundedBorder(int radius){
       this.radius = radius;
   }

   @Override
   public Insets getBorderInsets(Component c ){
       return new Insets(this.radius+1, this.radius+1, this.radius+1, this.radius);
   }

   @Override
   public boolean isBorderOpaque(){
       return true;
   }

   @Override
   public void paintBorder(Component c , Graphics g, int x, int y, int width, int height){
       g.setColor(c.getForeground());
       g.drawRoundRect(x, y, width-1, height-1, radius, radius);
   }
}
