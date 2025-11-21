package Engine.Math;

import Engine.Camera;

import java.util.ArrayList;

public class Transformer {



    //perform back-face culling and cuts negativ triangles
    public int[] cutFaces(float[][][] triangles){
        ArrayList<Integer> cutFaces = new ArrayList<>();
        for (int i = 0; i < triangles.length; i++) {
            if(cutNegativTriangles(triangles[i]) || backFaceCulling(triangles[i]))
                cutFaces.add(i);
        }

        //copy ArrayList into array
        int[] cutFacesArray = new int[cutFaces.size()];
        for (int i = 0; i < cutFacesArray.length; i++) {
            cutFacesArray[i] = cutFaces.get(i);
        }
        return cutFacesArray;
    }

    //no need to draw triangles behind the camera
    //returns true if triangle can be cut
    public boolean cutNegativTriangles(float[][] triangle){
        //check z values if one of them is behind the camera
        //if yes cut the face
        for (int i = 0; i < 3; i++) {
            if(triangle[2][i] < 0)
                return true;
        }
        return false;
    }

    //we do not need to render triangles, which are on the back of solid objects
    //returns true if triangle can be cut
    public boolean backFaceCulling(float[][] triangle){
        //getNormals
        float[] triangleNormal = getNormalVectorOfTriangle(triangle);

        //vertex A is the vector from camera to triangle, because camera is always at 0,0,0
        float[] vertexA = new float[]{triangle[0][0], triangle[1][0], triangle[2][0]};

        //checks the angle between camera to triangle and the triangles normal vector
        //if the triangle isn't facing the camera it is cut
        return ComplexCalculator.dotProduct(triangleNormal, vertexA) > 0;
    }

    public float[] getNormalVectorOfTriangle(float[][] triangle){
        //Points od the triangle
        float[] pointA = new float[]{triangle[0][0], triangle[1][0], triangle[2][0]};
        float[] pointB = new float[]{triangle[0][1], triangle[1][1], triangle[2][1]};
        float[] pointC = new float[]{triangle[0][2], triangle[1][2], triangle[2][2]};

        //Vectors from A to B and from A to C
        float[] ABVector = ComplexCalculator.vectorSubtraction(pointB, pointA);
        float[] ACVector = ComplexCalculator.vectorSubtraction(pointC, pointA);

        //calculate normal
        float[] normalVector = ComplexCalculator.crossProduct(ACVector, ABVector);
        return normalVector;
    }


    //SRT
    //SCALE
    //ROTATE
    //TRANSLATE
    public float[][] scaleVertices(float[][] vertices, float scalar){
        float[][] newVertices = new float[vertices.length][];
        for (int i = 0; i < newVertices.length; i++) {
            newVertices[i] = ComplexCalculator.scalarMultiplication(vertices[i], scalar);
        }
        return newVertices;
    }

    public float[][] rotateVerticesX(float[][] vertices, float angle){
        float[][] rotationMatrix = ComplexCalculator.getXRotationMatrix(angle);
        float[][] newVertices = new float[vertices.length][];
        for (int i = 0; i < newVertices.length; i++) {
            newVertices[i] = ComplexCalculator.matrixVectorMultiplication(rotationMatrix, vertices[i]);
        }
        return newVertices;
    }

    public float[][] rotateVerticesY(float[][] vertices, float angle){
        float[][] rotationMatrix = ComplexCalculator.getYRotationMatrix(angle);
        float[][] newVertices = new float[vertices.length][];
        for (int i = 0; i < newVertices.length; i++) {
            newVertices[i] = ComplexCalculator.matrixVectorMultiplication(rotationMatrix, vertices[i]);
        }
        return newVertices;
    }

    public float[][] rotateVerticesZ(float[][] vertices, float angle){
        float[][] rotationMatrix = ComplexCalculator.getZRotationMatrix(angle);
        float[][] newVertices = new float[vertices.length][];
        for (int i = 0; i < newVertices.length; i++) {
            newVertices[i] = ComplexCalculator.matrixVectorMultiplication(rotationMatrix, vertices[i]);
        }
        return newVertices;
    }

    public float[][] translateVertices(float[][] vertices, float[] translationVector){
        float[][] newVertices = new float[vertices.length][];
        for (int i = 0; i < newVertices.length; i++) {
            newVertices[i] = ComplexCalculator.vectorAddition(vertices[i], translationVector);
        }
        return newVertices;
    }

    public float[][] translateVerticesToCamera(float[][] vertices, Camera camera){
        float[][] newVertices = new float[vertices.length][];
        for (int i = 0; i < newVertices.length; i++) {
            newVertices[i] = ComplexCalculator.vectorSubtraction(vertices[i], camera.getPosition());
        }
        return newVertices;
    }

    public float[][] rotateVerticesToCamera(float[][] vertices, Camera camera){
        float[][] newVertices = new float[vertices.length][];
        for (int i = 0; i < newVertices.length; i++) {
            //rotate the objects in the opposite way to simulate the camera rotating in the other direction
            newVertices = rotateVerticesZ(vertices, -camera.getRotation()[2]);
            newVertices = rotateVerticesY(newVertices, -camera.getRotation()[1]);
            newVertices = rotateVerticesX(newVertices, -camera.getRotation()[0]);
        }
        return newVertices;
    }

    //1. translate vertices relativ to the cameras position
    //2. rotate everything opposite the cameras rotation
    //3. width the vertices relativ to the camera, convert the 3d pixels to 2d via WPP
    //return vertices[index][x,y]
    public float[][] transformToScreenCoordinates(float[][] vertices, Camera camera){
        float[][] transformedVertices = new float[vertices.length][];
        for (int i = 0; i < transformedVertices.length; i++) {
            transformedVertices[i] = weakPerspectiveProjection(vertices[i], camera);
        }
        return transformedVertices;
    }

    public float[] weakPerspectiveProjection(float[] vertex, Camera camera){
        float[] newPos = new float[2];
        //x' = (x * f)/z
        //y' = (y * f)/z
        newPos[0] = (vertex[0]) /vertex[2] * camera.getFocalLength();
        newPos[1] = (vertex[1]) /vertex[2] * camera.getFocalLength();
        return newPos;
    }


}
