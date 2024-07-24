package org.foxesworld.engine.providers.obj;

public record Vertex(int vertexIndex, int texCoordIndex, int normalIndex) {

    public int getVertexIndex() {
        return vertexIndex;
    }

    public int getTexCoordIndex() {
        return texCoordIndex;
    }

    public int getNormalIndex() {
        return normalIndex;
    }
}
