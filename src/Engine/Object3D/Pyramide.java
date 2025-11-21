package Engine.Object3D;

import Engine.Camera;

public class Pyramide extends Object3D {
    public Pyramide(float[] pos, float size, Camera camera){
        this.position = pos;
        this.camera = camera;

        //Create object relativ to itself
        setVerticesRelativeToObject();
        setEdges();
        this.size = size;
    }

    //SRT
    //SCALE => ROTATE => TRANSPOSE
    private void setVerticesRelativeToObject(){
        verticesRelativeToObject = new float[5][3];
        verticesRelativeToObject[0] = new float[] {-0.5f, -0.5f, -0.5f, 1f};
        verticesRelativeToObject[1] = new float[] {0.5f, -0.5f, -0.5f, 1f};
        verticesRelativeToObject[2] = new float[] {-0.5f, -0.5f, 0.5f, 1f};
        verticesRelativeToObject[3] = new float[] {0.5f, -0.5f, 0.5f, 1f};
        verticesRelativeToObject[4] = new float[] {0f, 0.5f, 0f, 1f};
    }

    private void setEdges() {
        edges = new int[6][];
        edges[0] = new int[] {2, 1, 0};
        edges[1] = new int[] {3, 1, 2};
        edges[2] = new int[] {4, 1, 3};
        edges[3] = new int[] {4, 0, 1};
        edges[4] = new int[] {4, 2, 0};
        edges[5] = new int[] {4, 3, 2};
    }
}
