package Engine.Object3D;

import Engine.Camera;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Model3D extends Object3D{

    public Model3D(String path, String modelName, float[] pos, float size, Camera camera){
        this.position = pos;
        this.camera = camera;
        this.size = size;

        //read in the file and create object relativ to itself
        readFile(path, modelName);
    }

    private void readFile(String path, String name){
        File file = new File(path);
        try{
            Scanner scanner = new Scanner(file);
            ArrayList<float[]> vertices = new ArrayList<>();
            ArrayList<int[]> faces = new ArrayList<>();
            ArrayList<Integer> separateVerticesIndexes = new ArrayList<>();
            ArrayList<Integer> separateFacesIndexes = new ArrayList<>();
            ArrayList<String> modelNames = new ArrayList<>();

            while(scanner.hasNextLine()){
                String line = scanner.nextLine().trim();

                //skip not needed lines
                if(line.isEmpty() || line.startsWith("#") || line.startsWith("vn") || line.startsWith("vt") || line.startsWith("g") || line.startsWith("usemtl") || line.startsWith("mtllib") || line.startsWith("s"))
                    continue;

                //splits at spaces
                String[] splitLine = line.split("\\s+");



                //check first char
                switch (splitLine[0]){
                    //new object
                    case "o" -> {
                        modelNames.add(splitLine[1]);
                        separateVerticesIndexes.add(vertices.size());
                        separateFacesIndexes.add(faces.size());
                    }
                    //vertex
                    case "v" -> {
                        vertices.add(new float[]{Float.parseFloat(splitLine[1]), Float.parseFloat(splitLine[2]), Float.parseFloat(splitLine[3]), 1});
                    }
                    case "f" -> {

                        //i is the amount of faces that are made from this line
                        for (int i = 0; i < splitLine.length-3; i++) {
                            //each triplet consist of the first vertex and two vertices that are changing
                            //note: the object file format starts counting at 1 not at 0. obj-file index 486 is my 485, so we subtract 1 from every index
                            int firstVertexIndex = Integer.parseInt(splitLine[1].split("/")[0]) - 1;
                            int secondVertexIndex = Integer.parseInt(splitLine[i + 2].split("/")[0]) - 1;
                            int thirdVertexIndex = Integer.parseInt(splitLine[i + 3].split("/")[0]) - 1;
                            faces.add(new int[]{firstVertexIndex, secondVertexIndex, thirdVertexIndex});
                        }
                    }
                }
            }
            separateVerticesIndexes.add(vertices.size());
            separateFacesIndexes.add(faces.size());

            //create vertices array
            //vertices[modelIndex][vertexIndex][x,y,z]
            int index = 0;
            float[][][] verticesSeparatedByModel = new float[Math.max(separateVerticesIndexes.size()-1, 1)][][];
            for (int i = 0; i < verticesSeparatedByModel.length; i++) {
                verticesSeparatedByModel[i] = new float[separateVerticesIndexes.get(i+1) - separateVerticesIndexes.get(i)][3];
                for (int j = 0; j < verticesSeparatedByModel[i].length; j++) {
                    verticesSeparatedByModel[i][j] = vertices.get(index);
                    index++;
                }
            }

            index = 0;
            //faces[modelIndex][triangleIndex][pointIndex]
            int[][][] facesSeparatedByModel = new int[separateFacesIndexes.size()-1][][];
            for (int i = 0; i < facesSeparatedByModel.length; i++) {
                facesSeparatedByModel[i] = new int[separateFacesIndexes.get(i+1) - separateFacesIndexes.get(i)][3];
                for (int j = 0; j < facesSeparatedByModel[i].length; j++) {
                    facesSeparatedByModel[i][j] = faces.get(index);
                    index++;
                }
            }

            //remove vertex biases from previous objects, so everything starts at vertex 0
            for (int i = 1; i < facesSeparatedByModel.length; i++) {
                for (int j = 0; j < facesSeparatedByModel[i].length; j++) {
                    for (int k = 0; k < facesSeparatedByModel[i][j].length; k++) {
                        facesSeparatedByModel[i][j][k] -= separateVerticesIndexes.get(i);
                    }
                }
            }


            //there might be multiple objects in one file
            //each object in the file has a name
            //if the name is found, return the model, else return the first object
            if(!modelNames.isEmpty() && modelNames.contains(name)){
                edges = facesSeparatedByModel[modelNames.indexOf(name)];
                verticesRelativeToObject = verticesSeparatedByModel[modelNames.indexOf(name)];
            }
            else{
                edges = facesSeparatedByModel[0];
                verticesRelativeToObject = verticesSeparatedByModel[0];
            }

        }catch(java.io.FileNotFoundException e){
            System.out.println("File not found: " + path);
        }
    }
}
