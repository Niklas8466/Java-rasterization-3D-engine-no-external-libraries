package Engine.Object3D;

import Engine.Camera;
import Engine.Math.ComplexCalculator;

import java.awt.*;
import java.util.ArrayList;

public abstract class Object3D {
    /*updating order
    * 1. vertices relativ to object
    * 1.1 set edges
    * 2. vertices relativ to origin
    * 3. vertices relativ to camera
    * 4. triangles relativ to camera
    * 5. cut edges with bfc
    * 6. vertices relativ to screen position
    * 7. triangles relativ to screen position
    */


    protected float[] position;
    protected float[] rotation = new float[3];
    protected float size;

    //vertices[index][x,y,z]
    protected float[][] verticesRelativeToObject;
    protected float[][] verticesRelativeToCamera;

    //edges[index][vertex1, vertex2, vertex3]
    protected int[][] edges;

    //triangles[index][vertexNumber][x,y,z]

    protected Color color = Color.white;
    protected Camera camera;

    public Object3D(){

    }

    //returns the projected vertices, but with their original z values
    public float[][] getVerticesRelativeToCamera(){
        float[][] transformationMatrix = createTransformationMatrix();
        float[][] newVertices = new float[verticesRelativeToObject.length][];
        for (int i = 0; i < newVertices.length; i++) {
            newVertices[i] = ComplexCalculator.matrixVectorMultiplication(transformationMatrix, verticesRelativeToObject[i]);
            newVertices[i] = ComplexCalculator.matrixVectorMultiplication(ComplexCalculator.getProjectionMatrix(camera.getFocalLength()), newVertices[i]);
            verticesRelativeToCamera = newVertices;

            //normalizing the vector removes the z value, so save it first and add it again later
            float z = newVertices[i][2];
            newVertices[i] = ComplexCalculator.normalizeHomogeneousVector(newVertices[i]);
            newVertices[i][2] = z;
        }
        return newVertices;
    }

    //returns the triangles with the projected vertices
    public float[][][] getTrianglesRelativeToCamera() {
        ArrayList<float[][]> trianglesList = new ArrayList<>();
        float[][] vertices = getVerticesRelativeToCamera();
        //loop through all edge pairs to create triangle
        for (int i = 0; i < edges.length; i++) {
            float[][] triangle = new float[][]{
                    vertices[edges[i][0]],
                    vertices[edges[i][1]],
                    vertices[edges[i][2]],
            };
            if(backFaceCulling(edges[i]) || !triangleIsPositive(triangle))
                continue;
            trianglesList.add(triangle);
        }

        return trianglesList.toArray(new float[trianglesList.size()][][]);
    }

    //if the triangle is completely behind the camera,
    //all the vertices z-values will be <= 0 and aren't visible
    public boolean triangleIsPositive(float[][] triangle){
        return triangle[0][2] > 0 && triangle[1][2] > 0 && triangle[2][2] > 0;
    }

    //cut faces which aren't facing the camera and aren't visible
    public boolean backFaceCulling(int[] edges){
        float[][] triangle = new float[][]{
                verticesRelativeToCamera[edges[0]],
                verticesRelativeToCamera[edges[1]],
                verticesRelativeToCamera[edges[2]]
        };

        //getNormals
        float[] triangleNormal = ComplexCalculator.getNormalVectorOfTriangle(triangle);

        //vertex A is the vector from camera to triangle, because camera is always at 0,0,0
        float[] vertexA = new float[]{triangle[0][0], triangle[1][0], triangle[2][0]};
        float[] cameraNormal = new float[]{0, 0, 1};

        //checks the angle between camera to triangle and the triangles normal vector
        //if the triangle isn't facing the camera it is cut
        return ComplexCalculator.dotProduct(triangleNormal, vertexA) > 0;
    }

    //we can combine multiple transformation steps into one matrix
    //this is a BIG optimisation
    protected float[][] createTransformationMatrix() {
        //relative to object
        //1. scale, 2. rotate
        float[][] objectMatrix = ComplexCalculator.makeHomogeneousMatrix(ComplexCalculator.matrixMultiplication(ComplexCalculator.getXYZRotationMatrix(rotation[0], rotation[1], rotation[2]), ComplexCalculator.getScaleMatrix3D(size)));

        //relative to origin and camera
        //translation
        float[] translationRelativeToCamera = ComplexCalculator.vectorSubtraction(position, camera.getPosition());
        float[][] translationMatrix = ComplexCalculator.getTranslationMatrix3D(translationRelativeToCamera);
        //rotation
        float[] cameraRotation = camera.getRotation();
        float[][] rotationRelativeToCamera = ComplexCalculator.makeHomogeneousMatrix(ComplexCalculator.getZYXRotationMatrix(-cameraRotation[2], -cameraRotation[1], -cameraRotation[0]));
        float[][] relativeToCameraMatrix = ComplexCalculator.matrixMultiplication(rotationRelativeToCamera, ComplexCalculator.matrixMultiplication(translationMatrix, objectMatrix));
        return relativeToCameraMatrix;
    }

    public void setPosition(float[] position){
        this.position = position;
    }

    public void setSize(float size){
        this.size = size;
    }

    public void move(float[] delta){
        position = ComplexCalculator.vectorAddition(position, delta);
    }

    public void rotateX(float deltaDegrees){
        rotation[0] += deltaDegrees;
    }

    public void rotateY(float deltaDegrees){
        rotation[1] += deltaDegrees;
    }

    public void rotateZ(float deltaDegrees){
        rotation[2] += deltaDegrees;
    }

    public float[][] getVerticesRelativeToObject() {
        return verticesRelativeToObject;
    }

    public float[][] getVerticesRelativeToOrigin() {
        return null; //todo
    }

    public void setColor(Color color){
        this.color = color;
    }

    public Color getColor(){
        return color;
    }
}
