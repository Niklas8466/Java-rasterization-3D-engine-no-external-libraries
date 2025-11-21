package Engine;

import Engine.Math.ComplexCalculator;

public class Camera {
    private float[] position;
    private float[] rotation;
    private float FOV;
    private float focalLength;

    public Camera(float[] pos, float FOV, int screenHeight){
        this.position = pos;
        this.FOV = FOV;
        this.focalLength = (float) (screenHeight /(2f * Math.tan(Math.toRadians(FOV)/2)));
        this.rotation = new float[3];
    }

    //when you want to move forward, you can't just go along the z axis
    //you need to move along the cameras view direction
    public void moveRelativToCamera(float angleXZPlane, float speed){
        //y stays 1 to make it independent of cameras position and x and y never lead to going up
        float[] unitVectorForwards = getForwardVector();
        float[] scaledVectorForwards = ComplexCalculator.scalarMultiplication(unitVectorForwards, speed);
        float[] deltaRelativToOrigin = ComplexCalculator.rotatePointY(scaledVectorForwards, angleXZPlane);
        move(deltaRelativToOrigin);
    }

    //move along the global axis
    public void move(float[] delta){
        position = ComplexCalculator.vectorAddition(position, delta);
    }

    public void rotateX(float angleDegree){
        rotation[0] += angleDegree;
        rotation[0] = ComplexCalculator.clampToRange(rotation[0], -90, 90);
    }

    public void rotateY(float angleDegree){
        rotation[1] += angleDegree;

    }

    public float[] getPosition(){
        return position;
    }

    public void setPosition(float[] position) {
        this.position = position;
    }

    public float[] getRotation() {
        return rotation;
    }

    public void setRotation(float[] rotation) {
        this.rotation = rotation;
    }

    public float getFocalLength(){
        return focalLength;
    }

    private float[] getForwardVector(){
        float[] rotationInRadians = new float[]{(float) Math.toRadians(rotation[0]), (float) Math.toRadians(rotation[1]), (float) Math.toRadians(rotation[2])};

        //y stays 0 so the vector doesn't go up
        float[] unitVectorForwards = new float[]{(float) Math.sin(rotationInRadians[1]), 0f, (float) Math.cos(rotationInRadians[1])};
        return unitVectorForwards;
    }

}