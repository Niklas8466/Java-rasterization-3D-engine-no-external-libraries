package Engine.Object3D;

import Engine.Camera;

public class Cube extends Object3D{
    public Cube(float[] pos, float size, Camera camera){
        this.position = pos;
        this.camera = camera;

        //Create object relative to itself
        setVerticesRelativeToObject();
        setEdges();
        this.size = size;

    }

    //SRT
    //SCALE => ROTATE => TRANSPOSE
    private void setVerticesRelativeToObject(){
        verticesRelativeToObject = new float[8][4];
        verticesRelativeToObject[0] = new float[]{-0.5f, 0.5f, -0.5f, 1f};
        verticesRelativeToObject[1] = new float[]{-0.5f, -0.5f, -0.5f, 1f};
        verticesRelativeToObject[2] = new float[]{-0.5f, 0.5f, 0.5f, 1f};
        verticesRelativeToObject[3] = new float[]{-0.5f, -0.5f, 0.5f, 1f};
        verticesRelativeToObject[4] = new float[]{0.5f, 0.5f, -0.5f, 1f};
        verticesRelativeToObject[5] = new float[]{0.5f, -0.5f, -0.5f, 1f};
        verticesRelativeToObject[6] = new float[]{0.5f, 0.5f, 0.5f, 1f};
        verticesRelativeToObject[7] = new float[]{0.5f, -0.5f, 0.5f, 1f};
    }

    private void setEdges() {
        edges = new int[12][];
        edges[0] = new int[]{0, 2, 1};
        edges[1] = new int[]{1, 2, 3};
        edges[2] = new int[]{5, 7, 4};
        edges[3] = new int[]{4, 7, 6};
        edges[4] = new int[]{4, 6, 0};
        edges[5] = new int[]{0, 6, 2};
        edges[6] = new int[]{1, 3, 5};
        edges[7] = new int[]{5, 3, 7};
        edges[8] = new int[]{4, 0, 5};
        edges[9] = new int[]{5, 0, 1};
        edges[10] = new int[]{2, 6, 3};
        edges[11] = new int[]{3, 6, 7};
    }


}
