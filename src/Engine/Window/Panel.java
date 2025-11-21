package Engine.Window;

import javax.swing.*;
import java.awt.*;

public class Panel extends JPanel {
    private final int width;
    private final int height;
    private Color[][] raster;

    public Panel(int width, int height){
        this.width = width;
        this.height = height;
        this.setBackground(Color.red);
        this.setPreferredSize(new Dimension(width, height));
        this.setVisible(true);
        this.setLayout(null);
    }

    public void setRaster(Color[][] raster){
        this.raster = raster;
    }

    @Override
    public void paint(Graphics g) {
        //clone the raster to avoid clearing it while drawing
        Color[][] raster = this.raster.clone();

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        //clear the screen
        g2d.setColor(this.getBackground());
        g2d.fillRect(0, 0, width, height);

        //how big is one raster in pixels
        int scale = width/raster.length;

        //draw the raster
        for (int i = 0; i < raster.length; i++) {
            for (int j = 0; j < raster[0].length; j++) {

                g2d.setColor(raster[i][j]);
                if(raster[i][j] == null)
                    g2d.setColor(getBackground());
                g2d.fillRect(i * scale,height - j * scale -scale, scale, scale);
            }
        }
    }
}
