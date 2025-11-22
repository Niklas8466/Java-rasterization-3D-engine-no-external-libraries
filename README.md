# Java-rasterization-3D-engine-no-external-libraries
My fourth attempt at a 3D engine

**No AI was used to write this code**
<br>
**3D Models aren't made by me** 
<br>

Some of the final pictures made in this engine:
<img width="992" height="1000" alt="grafik" src="https://github.com/user-attachments/assets/5e1b5fb6-ed0b-4ebd-a1a0-0652f05e7ba1" />
<img width="1002" height="992" alt="grafik" src="https://github.com/user-attachments/assets/314764c6-db60-458f-862f-0c64a05715c0" />
<img width="982" height="987" alt="grafik" src="https://github.com/user-attachments/assets/91440f41-c12c-47a2-ab73-011a30f64ac7" />
<img width="992" height="998" alt="grafik" src="https://github.com/user-attachments/assets/99e14f50-411a-4bb6-b7af-bf329bfa8c27" />


## Features:
- Cubes, Pyramides, Spheres
- Camera movement
- Camera rotation
- Backface culling
- Culling of negativ z-values
- Rasterization
- Different resolutions
- Shape outlines
- 3D Models

## How does it work?
### triangle transformation
1. calculate vertices relativ to **object**
2. calculate vertices relativ to the **origin**
3. calculate vertices relativ to **camera**
4. project vertices on the screen
5. connect the vertices to form triangles
6. cut triangles, which aren't visible (backface culling and culling negativ z-values)<br>

### projection
use a raster to calculate the correct color of the traingle for each pixel   
1. loop over ever pixel
2. calculate the z-value of the point in the triangle using interpolation
3. check if the triangle is the closest triangle for this pixel
4. save the correct color
5. draw every pixel on the screen

## Optimizations after my 3rd try
1. combining many transformations into one matrix via matrix multiplication
2. bounding-boxes of triangles (only check the pixel, if the triangle is actually there)
3. homogeneous coordinates

## known bugs
- frostum clipping:
<img width="1057" height="518" alt="one_out" src="https://github.com/user-attachments/assets/dfa08782-24ee-4de0-bbff-2bf845eb8261" />
When part of the triangle is behind the camera or out of view, the entire triangle is culled, however the triangle should be cut into pieces in order to show the remaining part of the triangle 
This causes bigger surfaces to randomly disapear, when part of the triangle is out of view<br><br>

- speed (It is REALLY slow)

## conclusion
rasterization is way better than the painter's algorithm, however it doesn't look realistic. It looks pixelated and light is missing... so **raytracing** is probably next...
