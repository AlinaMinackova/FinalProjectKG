package com.cgvsu.model;
import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.triangulation.Triangulation;

import java.util.*;

public class Model {

    public ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
    public ArrayList<Vector2f> textureVertices = new ArrayList<Vector2f>();
    public ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
    public ArrayList<Polygon> polygons = new ArrayList<Polygon>();
    public ArrayList<Polygon> polygonsWithoutTriangulation = new ArrayList<Polygon>();

    public void triangulate(){
        polygonsWithoutTriangulation = polygons;
        polygons = (ArrayList<Polygon>) Triangulation.triangulate(polygons);
    }
    public void calculateNormals(){
        ArrayList<Vector3f> normalsNew = new ArrayList<>();
        for (Vector3f vertex : vertices) {
            Vector3f a = new Vector3f(0, 0, 0);
            int countPolygons = 0;
            for (Polygon polygon : polygons) {
                if (vertices.get(polygon.getVertexIndices().get(0)).equals(vertex)) {
                    a.add(getVector(polygon.getVertexIndices().get(0), polygon.getVertexIndices().get(1), polygon.getVertexIndices().get(2)));
                    countPolygons++;
                }
                else if (vertices.get(polygon.getVertexIndices().get(1)).equals(vertex)) {
                    a.add(getVector(polygon.getVertexIndices().get(1), polygon.getVertexIndices().get(0), polygon.getVertexIndices().get(2)));
                    countPolygons++;
                }
                else if (vertices.get(polygon.getVertexIndices().get(2)).equals(vertex)) {
                    a.add(getVector(polygon.getVertexIndices().get(2), polygon.getVertexIndices().get(1), polygon.getVertexIndices().get(0)));
                    countPolygons++;
                }
            }
            a.del(countPolygons);
            a.normalize();
            normalsNew.add(a);
        }
        normals = normalsNew;
    }
    public Vector3f getVector(int v1, int v2, int v3){
        Vector3f a = new Vector3f();
        a.sub(vertices.get(v2), vertices.get(v1));
        Vector3f b = new Vector3f();
        b.sub(vertices.get(v3), vertices.get(v1));
        Vector3f n = new Vector3f();
        n.mult(a, b);
        return n;
    }
}
