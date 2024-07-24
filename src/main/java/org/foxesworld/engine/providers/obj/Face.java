package org.foxesworld.engine.providers.obj;

import java.util.ArrayList;
import java.util.List;

public class Face {
    private final List<Vertex> vertices = new ArrayList<>();

    public void add(int vertexIndex, int texCoordIndex, int normalIndex) {
        vertices.add(new Vertex(vertexIndex, texCoordIndex, normalIndex));
    }

    public List<Vertex> getVertices() {
        return vertices;
    }
}