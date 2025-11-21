package Engine.Math;

import java.lang.reflect.Array;

//sometimes the Math library isn't enough
//This class has more complex operations
public class ComplexCalculator {
    public static float[] getNormalVectorOfTriangle(float[][] triangle){
        //Points od the triangle
        float[] pointA = new float[]{triangle[0][0], triangle[1][0], triangle[2][0]};
        float[] pointB = new float[]{triangle[0][1], triangle[1][1], triangle[2][1]};
        float[] pointC = new float[]{triangle[0][2], triangle[1][2], triangle[2][2]};

        //Vectors from A to B and from A to C
        float[] ABVector = ComplexCalculator.vectorSubtraction(pointB, pointA);
        float[] ACVector = ComplexCalculator.vectorSubtraction(pointC, pointA);

        //calculate normal
        return ComplexCalculator.crossProduct(ACVector, ABVector);
    }

    public static float[] normalizeHomogeneousVector(float[] vector){
        float scalerW = 1/vector[vector.length-1];
        return scalarMultiplication(vector, scalerW);
    }

    public static float[][] makeHomogeneousMatrix(float[][] matrix){
        float[][] newMatrix = new float[matrix.length+1][matrix[0].length+1];
        for (int i = 0; i < matrix.length; i++) {
            newMatrix[i] = (float[]) appendArray(matrix[i], new float[]{0});
        }
        newMatrix[matrix.length][matrix[0].length] = 1f;
        return newMatrix;
    }

    public static float[][] getScaleMatrix3D(float scaler){
        return new float[][]{
          new float[]{scaler, 0, 0},
          new float[]{0, scaler, 0},
          new float[]{0, 0, scaler}
        };
    }

    public static float[][] getProjectionMatrix(float focalLength){
        return new float[][]{
                new float[]{focalLength, 0, 0, 0},
                new float[]{0, focalLength, 0, 0},
                new float[]{0, 0, 1, 0},
                new float[]{0, 0, 1, 0}
        };
    }

    public static float[][] getTranslationMatrix3D(float[] delta){
        float[][] matrix = new float[][]{
                new float[]{1,0,0, delta[0]},
                new float[]{0,1,0, delta[1]},
                new float[]{0,0,1, delta[2]},
                new float[]{0,0,0, 1},
        };
        return matrix;
    }

    public static float[][] getXYZRotationMatrix(float angleX, float angleY, float angleZ){
        float[][] xMatrix = getXRotationMatrix(angleX);
        float[][] yMatrix = getYRotationMatrix(angleY);
        float[][] zMatrix = getZRotationMatrix(angleZ);

        float[][] yx = matrixMultiplication(yMatrix, xMatrix);
        return matrixMultiplication(zMatrix, yx);
    }

    public static float[][] getZYXRotationMatrix(float angleZ, float angleY, float angleX){
        float[][] xMatrix = getXRotationMatrix(angleX);
        float[][] yMatrix = getYRotationMatrix(angleY);
        float[][] zMatrix = getZRotationMatrix(angleZ);

        float[][] yz = matrixMultiplication(yMatrix, zMatrix);
        return matrixMultiplication(xMatrix, yz);
    }

    //triangle[index][x,y]
    public static float calculateTriangle2DArea(float[][] triangle){
        float[][] newTriangle = new float[3][];
        for (int i = 0; i < 3; i++) {
            newTriangle[i] = new float[]{
                    triangle[i][0], triangle[i][1], 0
            };
        }
        return calculateTriangle3DArea(newTriangle);
    }

    //triangle[index][x,y,z]
    public static float calculateTriangle3DArea(float[][] triangle){
        //area = 0.5 * |AB x AC|
        float[] AB = vectorSubtraction(triangle[1], triangle[0]);
        float[] AC = vectorSubtraction(triangle[2], triangle[0]);

        float[] crossProductVector = crossProduct(AB, AC);
        float magnitude = calculateDistance3D(crossProductVector, new float[]{0,0,0});
        return (float) (magnitude * 0.5);
    }

    public static float sumOfComponents(float[] vector){
        float sum = 0;
        for (int i = 0; i < vector.length; i++) {
            sum += vector[i];
        }
        return sum;
    }

    //triangle[index][x,y,z]
    public static float[] calculateAveragePositionOf3DTriangle(float[][] triangle){
        float averageX = ComplexCalculator.calculateAverage(new float[]{triangle[0][0], triangle[1][0], triangle[2][0]});
        float averageY = ComplexCalculator.calculateAverage(new float[]{triangle[0][1], triangle[1][1], triangle[2][1]});
        float averageZ = ComplexCalculator.calculateAverage(new float[]{triangle[0][2], triangle[1][2], triangle[2][2]});
        return new float[]{averageX, averageY, averageZ};
    }


    public static float calculateDistance3D(float[] pos1, float[] pos2){
        //a² + b² + c² = d²
        return (float) Math.sqrt((pos1[0] - pos2[0]) * (pos1[0] - pos2[0])   +    (pos1[1] - pos2[1]) * (pos1[1] - pos2[1])    +   (pos1[2] - pos2[2]) * (pos1[2] - pos2[2]));
    }

    public static float calculateDistance2D(float[] pos1, float[] pos2){
        //a² + b² = c²
        return (float) Math.sqrt((pos1[0] - pos2[0]) * (pos1[0] - pos2[0])   +    (pos1[1] - pos2[1]) * (pos1[1] - pos2[1]));
    }

    public static float calculateAverage(float[] array){
        float sum = 0f;
        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }
        return sum/array.length;
    }


    public static boolean contains(int[] array, int value){
        for (int element : array)
            if(element == value)
                return true;

        return false;
    }


    public static double roundDouble(double value, int places){
        value *= Math.pow(10, places);
        return Math.round(value) / Math.pow(10, places);
    }

    /**multiply each element of a vector with the other vector
     * v.x = v1.x * v2.x
     * v.y = v1.y * v2.y
     * v.x = v1.x * v2.x*/
    public static float[] hadamardProductVector(float[] v1, float[] v2){
        if(v2.length != v1.length)
            throw new RuntimeException("Vector has the wrong size");

        float[] newVector = new float[v1.length];
        for (int i = 0; i < newVector.length; i++) {
            newVector[i] = v1[i] * v2[i];
        }
        return newVector;
    }

    public static float[][] matrixMultiplication(float[][] m1, float[][] m2){
        if(m1[0].length != m2.length)
            throw new RuntimeException("Matrix has the wrong size");

        float[][] newMatrix = new float[m1.length][m2[0].length];

        //loping through every value of matrix
        for (int i = 0; i < newMatrix.length; i++) {
            for (int j = 0; j < newMatrix[0].length; j++) {

                //calculating the value
                for (int k = 0; k < m1[0].length; k++) {
                    newMatrix[i][j] += m1[i][k] * m2[k][j];
                }
            }
        }
        return newMatrix;
    }

    public static float[] scalarMultiplication(float[] vector, float scalar){
        float[] newVector = new float[vector.length];
        for (int i = 0; i < vector.length; i++) {
            newVector[i] = vector[i] * scalar;
        }
        return newVector;
    }

    public static float clampToRange(float value, float min, float max){
        return Math.max(min, Math.min(value, max));
    }

    public static float calculateVectorNorm(float[] vector, int p){
        //p = infinity => p = max value
        if(p == Integer.MAX_VALUE)
            return getMax(vector);

        float sum = 0f;
        for (int i = 0; i < vector.length; i++) {
            sum += (float) Math.pow(vector[i], p);
        }
        return (float) Math.pow(sum, (double) 1 / p);
    }

    public static float[] crossProduct(float[] v1, float[] v2){
        float[] newVector = {
                v1[1]*v2[2] - v1[2]*v2[1],
                v1[2]*v2[0] - v1[0]*v2[2],
                v1[0]*v2[1] - v1[1]*v2[0]};
        return newVector;
    }

    public static float dotProduct(float[] v1, float[] v2){
        float sum = 0f;
        for (int i = 0; i < v1.length; i++) {
            sum += v1[i] * v2[i];
        }
        return sum;
    }


    public static float[] matrixVectorMultiplication(float[][] matrix, float[] vektor){
        if(matrix[0].length != vektor.length)
            throw new RuntimeException("Matrix has the wrong size");

        float[] newVector = new float[matrix.length];
        for (int i = 0; i < newVector.length; i++) {
            for (int j = 0; j < vektor.length; j++) {
                newVector[i] += matrix[i][j] * vektor[j];
            }
        }
        return  newVector;
    }

    public static float[] vectorAddition(float[] v1, float[] v2){
        if(v2.length != v1.length)
            throw new RuntimeException("Vector has the wrong size");

        float[] newVector = new float[v1.length];
        for (int i = 0; i < v1.length; i++) {
            newVector[i] = v1[i] + v2[i];
        }
        return newVector;
    }

    public static int[] vectorAddition(int[] v1, int[] v2){
        if(v2.length != v1.length)
            throw new RuntimeException("Vector has the wrong size");

        int[] newVector = new int[v1.length];
        for (int i = 0; i < v1.length; i++) {
            newVector[i] = v1[i] + v2[i];
        }
        return newVector;
    }

    public static float[] vectorSubtraction(float[] v1, float[] v2){
        if(v2.length != v1.length)
            throw new RuntimeException("Vector has the wrong size");

        float[] newVector = new float[v1.length];
        for (int i = 0; i < v1.length; i++) {
            newVector[i] = v1[i] - v2[i];
        }
        return newVector;
    }

    public static void printMatrix(float[][] m){
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                System.out.print(m[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void printMatrix(int[][] m){
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                System.out.print(m[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void printVector(float[] v){
        System.out.print("Vektor: ");
        for (int i = 0; i < v.length; i++) {
            System.out.print(v[i] + " ");
        }
        System.out.println();
    }

    public static void printVector(int[] v){
        System.out.print("Vektor: ");
        for (int i = 0; i < v.length; i++) {
            System.out.print(v[i] + " ");
        }
        System.out.println();
    }

    public static float getMax(float[] v){
        float max = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < v.length; i++) {
            if(v[i] > max)
                max = v[i];
        }
        return max;
    }

    public static float getMin(float[] v){
        float max = Float.POSITIVE_INFINITY;
        for (int i = 0; i < v.length; i++) {
            if(v[i] < max)
                max = v[i];
        }
        return max;
    }

    public static int getMaxIndex(float[] v){
        int max = 0;
        for (int i = 1; i < v.length; i++) {
            if(v[i] > v[max])
                max = i;
        }
        return max;
    }

    public static int getMinIndex(float[] v){
        int max = 0;
        for (int i = 1; i < v.length; i++) {
            if(v[i] < v[max])
                max = i;
        }
        return max;
    }

    public static float[] rotatePointX(float[] pos, float angle){
        float[][] matrix = getXRotationMatrix(angle);
        return ComplexCalculator.matrixVectorMultiplication(matrix, pos);
    }

    public static float[] rotatePointY(float[] pos, float angle){
        float[][] matrix = getYRotationMatrix(angle);
        return ComplexCalculator.matrixVectorMultiplication(matrix, pos);
    }

    public static float[] rotatePointZ(float[] pos, float angle){
        float[][] matrix = getZRotationMatrix(angle);
        return ComplexCalculator.matrixVectorMultiplication(matrix, pos);
    }

    public static float[][] getXRotationMatrix(float angle){
        angle = (float) Math.toRadians(angle);
        float[][] matrix = {
                {1f, 0f, 0f},
                {0f, (float) Math.cos(angle), (float) -Math.sin(angle)},
                {0f, (float) Math.sin(angle), (float) Math.cos(angle)}
        };
        return matrix;
    }

    public static float[][] getYRotationMatrix(float angle){
        angle = (float) Math.toRadians(angle);
        float[][] matrix = {
                {(float) Math.cos(angle), 0f, (float) Math.sin(angle)},
                {0f, 1f, 0f},
                {-(float) Math.sin(angle), 0f, (float) Math.cos(angle)}
        };
        return matrix;
    }

    public static float[][] getZRotationMatrix(float angle){
        angle = (float) Math.toRadians(angle);
        float[][] matrix = {
                {(float) Math.cos(angle), (float) -Math.sin(angle), 0f},
                {(float) Math.sin(angle), (float) Math.cos(angle), 0f},
                {0f, 0f, 1f}
        };
        return matrix;
    }

    public static Object appendArray(Object a, Object b){
        Object newArray = Array.newInstance(a.getClass().getComponentType(), Array.getLength(a) + Array.getLength(b));
        System.arraycopy(a,0,newArray,0,Array.getLength(a));
        System.arraycopy(b,0,newArray,Array.getLength(a),Array.getLength(b));
        return newArray;
    }
}
