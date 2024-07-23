package org.foxesworld.engine.providers.obj;

public class Vertex {
    private final int vertexIndex;
    private final int texCoordIndex;
    private final int normalIndex;

    public Vertex(int vertexIndex, int texCoordIndex, int normalIndex) {
        this.vertexIndex = vertexIndex;
        this.texCoordIndex = texCoordIndex;
        this.normalIndex = normalIndex;
    }

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
