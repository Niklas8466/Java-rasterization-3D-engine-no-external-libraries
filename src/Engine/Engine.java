package Engine;

import Engine.Inputs.Inputs;
import Engine.Math.ComplexCalculator;
import Engine.Math.Transformer;
import Engine.Object3D.Cube;
import Engine.Object3D.Model3D;
import Engine.Object3D.Pyramide;
import Engine.Object3D.Sphere;
import Engine.Window.Window;

import java.awt.*;

public class Engine {
    private final Camera camera;
    private final Window window;
    private final Inputs inputs;

    private int refreshRatePerSecond = 10; //1000 is max
    private int currentFPS = 0;
    private int screenHeight = 800;
    private int screenWidth = 800;

    //3D objects
    //Test scene
    private Cube blueCube;
    private Cube redCube;
    private Cube greenCube;
    private Cube whiteCube1;
    private Cube whiteCube2;
    private Cube whiteCube3;

    //other tests
    private Pyramide pyramide;
    private Sphere sphere;
    private Model3D queen;

    //the floor is made of 2D planes
    private Model3D[][] floor;


    public Engine(){
        this.camera = new Camera(new float[]{0f, 0, 0}, 45, screenHeight);
        this.window = new Window(screenWidth, screenHeight,1, "rasterization");
        this.inputs = new Inputs();
        window.setInputListener(inputs);
    }

    //this is the loop, which
    public void run(){
        innit(); //execute innit function once

        //run next frame if enough time has passed (fps cap)
        long lastTime = System.currentTimeMillis();
        while (true){
            long newTime = System.currentTimeMillis();
            if(newTime-lastTime >= 1000/refreshRatePerSecond){
                currentFPS = (int) (1000/(newTime-lastTime));
                lastTime = System.currentTimeMillis();

                //update is disabled in order to create one singular image with high quality
                //update();
            }

        }
    }

    public void innit(){
        blueCube = new Cube(new float[]{-100, 0, 150}, 100, camera);
        blueCube.setColor(new Color(40, 79, 167));

        redCube = new Cube(new float[]{0, -30, 150},40, camera);
        redCube.setColor(Color.pink);
        redCube.rotateY(45);

        greenCube = new Cube(new float[]{100, 0, 150}, 100, camera);
        greenCube.setColor(new Color(38, 204, 3));

        whiteCube1 = new Cube(new float[]{0, 100, 150}, 100, camera);
        whiteCube1.setColor(Color.lightGray);

        whiteCube2 = new Cube(new float[]{0, -100, 150}, 100, camera);
        whiteCube2.setColor(Color.darkGray);

        whiteCube3 = new Cube(new float[]{0, 0, 250}, 100, camera);
        whiteCube3.setColor(Color.gray);

        pyramide = new Pyramide(new float[]{0, 100, 200}, 100, camera);
        pyramide.setColor(Color.BLUE);

        sphere = new Sphere(new float[]{0, 0, 100}, 10, 10, 100, camera);
        sphere.setColor(Color.red);

        queen = new Model3D("3DModels/chess pieces finished.obj", "queen", new float[]{63, -40f, 130f}, 50, camera);
        queen.setColor(Color.pink);

        //floor
        floor = new Model3D[10][10];
        float size = 200;
        for (int i = 0; i < floor.length; i++) {
            for (int j = 0; j < floor[0].length; j++) {
                floor[i][j] = new Model3D("3DModels/2DSquare.obj", "2DSquare", new float[]{(i- (float) floor.length /2)*size, -50f, (j- (float) floor[0].length /2) * size}, 200, camera);
                floor[i][j].rotateX(90);
                floor[i][j].setColor(Color.lightGray);
            }
        }

        window.setVisible(true);
        window.setBackgroundColor(new Color(30, 229, 229));

        window.clear();

        //draw objects on the screen
        window.drawObject3D(blueCube);
        window.drawObject3D(queen);
        window.drawObject3D(greenCube);
        window.drawObject3D(whiteCube1);
        window.drawObject3D(whiteCube2);
        window.drawObject3D(whiteCube3);

        //floor is disabled for test scene
//        for (int i = 0; i < floor.length; i++) {
//            for (int j = 0; j < floor[i].length; j++) {
//                window.drawObject3D(floor[i][j]);
//            }
//        }

        window.repaint();
    }

    public void update(){
        System.out.println("FPS: " + getCurrentFPS());

        //Scene
        window.clear();
        testInputs();
        //window.drawObject3D(pyramide);
        window.drawObject3D(queen);
        //window.drawObject3D(redCube);
        window.drawObject3D(greenCube);

        //window.drawObject3D(sphere);
//        for (int i = 0; i < floor.length; i++) {
//            for (int j = 0; j < floor[i].length; j++) {
//                window.drawObject3D(floor[i][j]);
//            }
//        }

        window.repaint();
    }

    private void testInputs(){
        float speed = 1.5f;
        float rotationSpeed = 1.2f;
        if(inputs.isPressed('w'))
            camera.moveRelativToCamera(0, speed);
        if(inputs.isPressed('s'))
            camera.moveRelativToCamera(180, speed);
        if(inputs.isPressed('d'))
            camera.moveRelativToCamera(90, speed);
        if(inputs.isPressed('a'))
            camera.moveRelativToCamera(-90, speed);
        if(inputs.isPressed('q'))
            camera.rotateY(-rotationSpeed);
        if(inputs.isPressed('e'))
            camera.rotateY(rotationSpeed);
        if(inputs.isPressed('v'))
            camera.rotateX(-rotationSpeed);
        if(inputs.isPressed('b'))
            camera.rotateX(rotationSpeed);
        if(inputs.isPressed(' '))
            camera.move(new float[]{0f, speed, 0f});
        if(inputs.isPressed('c'))
            camera.move(new float[]{0f, -speed, 0f});
    }


    public int getCurrentFPS(){
        return currentFPS;
    }

    public void setRefreshRatePerSecond(int refreshRatePerSecond){
        this.refreshRatePerSecond = refreshRatePerSecond;
    }
}
