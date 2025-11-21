package Engine.Window;

import Engine.Inputs.Inputs;
import Engine.Math.ComplexCalculator;
import Engine.Object3D.Object3D;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Random;

public class Window extends JFrame {
    private final int width, height;
    private final Panel panel;

    //raster[x][y], raster[0][0] = bottom left corner
    private final Color[][] raster;
    private final float[][] rasterZValues;
    private final int rasterWidth;
    private final int rasterHeight;
    private final int scale;

    private Color backgroundColor = new Color(0, 0, 0);
    private float[][][] triangles = new float[0][][];
    private Color[] colors = new Color[0];


    public Window(int width, int height,int scale, String title){
        this.width = width;
        this.height = height;
        this.scale = scale;

        rasterWidth = width /scale;
        rasterHeight = height /scale;
        this.raster = new Color[(int) rasterWidth][(int) rasterHeight];
        this.rasterZValues = new float[(int) rasterWidth][(int) rasterHeight];


        panel = new Panel(width, height);
        panel.setRaster(raster);
        this.add(panel);
        this.pack();
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setTitle(title);

        //setTestTriangles();
        //setRaster();
    }

    public void drawObject3D(Object3D object3D){
        triangles = (float[][][]) ComplexCalculator.appendArray(triangles, object3D.getTrianglesRelativeToCamera());
        Color[] newColors = new Color[object3D.getTrianglesRelativeToCamera().length];

        if(object3D.getColor() == Color.PINK){
            Random random = new Random();
            for (int i = 0; i < newColors.length; i++) {
                Color color = new Color(random.nextInt(0,256), random.nextInt(0,256), random.nextInt(0,256));
                newColors[i] = color;
            }
        }
        else
            Arrays.fill(newColors, object3D.getColor());
        colors = (Color[]) ComplexCalculator.appendArray(colors, newColors);
    }

    public void clear(){
        //clear raster
        for (int i = 0; i < raster.length; i++) {
            Arrays.fill(raster[i], backgroundColor);
            Arrays.fill(rasterZValues[i], Float.MAX_VALUE);
        }
        triangles = new float[0][][];
        colors = new Color[0];
    }


    //triangle[index][x,y]
    private boolean isPointInTriangle(float[][] triangle, float[] point){
        float[] interpolation = interpolation(triangle, point);

        float alpha = interpolation[0];
        float beta = interpolation[1];
        float gamma = interpolation[2];

        return alpha >= 0 && beta >= 0 && gamma >= 0 && Math.abs(alpha+beta+gamma - 1) < 0.000001;
    }

    //raster[x][y] => raster[4][6] => (-140,50) on screen
    //the coordinate is the center of the square in the raster
    private float[] rasterIndexToScreenCoordinate(int[] index){
        float centerX = (float) rasterWidth/2;
        float centerY = (float) rasterHeight/2;

        //screen coordinate = (index - center) * scale
        return new float[]{
                (index[0] - centerX) * scale,
                (index[1] - centerY) * scale,
                0,0
        };
    }

    private void setRaster(){
        //Test image
        for (int i = 0; i < rasterWidth; i++) {
            for (int j = 0; j < rasterHeight; j++) {
                for (int k = 0; k < triangles.length; k++) {
                    float[] coordinate = rasterIndexToScreenCoordinate(new int[]{i, j});
                    float minX = ComplexCalculator.getMin(new float[]{
                            triangles[k][0][0],
                            triangles[k][1][0],
                            triangles[k][2][0]
                    });
                    float maxX = ComplexCalculator.getMax(new float[]{
                            triangles[k][0][0],
                            triangles[k][1][0],
                            triangles[k][2][0]
                    });
                    float minY = ComplexCalculator.getMin(new float[]{
                            triangles[k][0][1],
                            triangles[k][1][1],
                            triangles[k][2][1]
                    });
                    float maxY = ComplexCalculator.getMax(new float[]{
                            triangles[k][0][1],
                            triangles[k][1][1],
                            triangles[k][2][1]
                    });
                    if(coordinate[0] > maxX || coordinate[0] < minX || coordinate[1] > maxY || coordinate[1] < minY)
                        continue;

                    float zValue = calculateZValue(triangles[k], coordinate);
                    if(!isPointInTriangle(triangles[k], coordinate) || zValue < 0 || zValue > rasterZValues[i][j]){
                        continue;
                    }

                    raster[i][j] = colors[k];
                    rasterZValues[i][j] = zValue;

                }
            }
        }
    }

    //(-140, 50) on screen => raster[4][6]
    private int[] screenCoordinateToRasterIndex(float[] coordinate){
        float centerX = (float) rasterWidth/2;
        float centerY = (float) rasterHeight/2;

        //index = coordinate/scale + center
        return new int[]{
                (int) (coordinate[0]/scale + centerX),
                (int) (coordinate[1]/scale + centerY),
        };
    }

    public void setInputListener(Inputs inputs){
        this.addKeyListener(inputs);
        this.addMouseListener(inputs);
        this.addMouseMotionListener(inputs);
        this.addMouseWheelListener(inputs);
    }

    public void setVisible(boolean bool){
        super.setVisible(bool);
    }


    public void repaint(){
        setRaster();
        panel.repaint();
    }

    public void setBackgroundColor(Color color){
        backgroundColor = color;
        panel.setBackground(color);
    }

    //interpolation using barycentric coordinates
    //returns alpha, beta and gamma value
    private float[] interpolation(float[][] triangle, float[] point){
        //barycentric coordinate
        float areaTriangle = ComplexCalculator.calculateTriangle2DArea(triangle);
        float areaPBC = ComplexCalculator.calculateTriangle2DArea(new float[][]{
                point,
                triangle[1],
                triangle[2]
        });

        float areaPCA = ComplexCalculator.calculateTriangle2DArea(new float[][]{
                point,
                triangle[2],
                triangle[0]
        });

        float areaPAB = ComplexCalculator.calculateTriangle2DArea(new float[][]{
                point,
                triangle[0],
                triangle[1]
        });

        float alpha = areaPBC/areaTriangle;
        float beta = areaPCA/areaTriangle;
        float gamma = areaPAB/areaTriangle;
        return new float[]{alpha, beta, gamma};
    }

    //calculate the z value of a point in the triangle
    private float calculateZValue(float[][] triangle, float[] point){
        float[] interpolation = interpolation(triangle, point);
        //z = alpha*z1 + beta*z2 + gamma*z3
        return interpolation[0] * triangle[0][2] + interpolation[1] * triangle[1][2] + interpolation[2] * triangle[2][2];
    }

}
