package org.foxesworld.engine.providers.obj;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.ModelKey;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.*;
import com.jme3.util.BufferUtils;
import com.jme3.util.TangentBinormalGenerator;
import org.apache.logging.log4j.Logger;
import org.foxesworld.FrozenLands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OBJ-importer JME 3.1.6
 */
public class OBJImporter implements AssetLoader {

    private static final Logger logger = FrozenLands.logger;

    @Override
    public Object load(AssetInfo assetInfo) {
        if (!(assetInfo.getKey() instanceof ModelKey)) {
            throw new IllegalArgumentException("Asset key must be a ModelKey.");
        }

        ModelKey modelKey = (ModelKey) assetInfo.getKey();
        String objFilePath = modelKey.getName();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(assetInfo.openStream()))) {
            // Парсим OBJ-файл
            Map<Integer, Vector3f> vertices = new HashMap<>();
            Map<Integer, Vector3f> normals = new HashMap<>();
            Map<Integer, Vector2f> texCoords = new HashMap<>();
            List<Face> faces = new ArrayList<>();

            String line;
            int vertexIndex = 0;
            int normalIndex = 0;
            int texCoordIndex = 0;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length == 0) {
                    continue;
                }

                switch (parts[0]) {
                    case "v" -> vertices.put(vertexIndex++, new Vector3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3])));
                    case "vn" -> normals.put(normalIndex++, new Vector3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3])));
                    case "vt" -> texCoords.put(texCoordIndex++, new Vector2f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2])));
                    case "f" -> {
                        Face face = new Face();
                        for (int i = 1; i < parts.length; i++) {
                            String[] indices = parts[i].split("/");
                            try {
                                vertexIndex = Integer.parseInt(indices[0]);
                                texCoordIndex = indices.length > 1 && !indices[1].isEmpty() ? Integer.parseInt(indices[1]) : 0;
                                normalIndex = indices.length > 2 && !indices[2].isEmpty() ? Integer.parseInt(indices[2]) : 0;
                                face.add(vertexIndex, texCoordIndex, normalIndex);
                            } catch (NumberFormatException ex) {
                                // Проверяем, есть ли десятичная точка
                                if (indices[0].contains(".")) {
                                    // Преобразуем в float, но игнорируем дробную часть
                                    vertexIndex = (int) Math.floor(Float.parseFloat(indices[0]));
                                    texCoordIndex = indices.length > 1 && !indices[1].isEmpty() ? Integer.parseInt(indices[1]) : 0;
                                    normalIndex = indices.length > 2 && !indices[2].isEmpty() ? Integer.parseInt(indices[2]) : 0;
                                    face.add(vertexIndex, texCoordIndex, normalIndex);
                                } else {
                                    logger.warn("Invalid index: " + indices[0]);
                                }
                            }
                        }
                        faces.add(face);
                    }
                }
            }

            Mesh mesh = new Mesh();

            FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.size() * 3);
            for (Vector3f vertex : vertices.values()) {
                vertexBuffer.put(vertex.getX()).put(vertex.getY()).put(vertex.getZ());
            }
            vertexBuffer.flip();
            mesh.setBuffer(VertexBuffer.Type.Position, 3, vertexBuffer);

            FloatBuffer normalBuffer = BufferUtils.createFloatBuffer(normals.size() * 3);
            for (Vector3f normal : normals.values()) {
                normalBuffer.put(normal.getX()).put(normal.getY()).put(normal.getZ());
            }
            normalBuffer.flip();
            mesh.setBuffer(VertexBuffer.Type.Normal, 3, normalBuffer);

            FloatBuffer texCoordBuffer = BufferUtils.createFloatBuffer(texCoords.size() * 2);
            for (Vector2f texCoord : texCoords.values()) {
                texCoordBuffer.put(texCoord.getX()).put(texCoord.getY());
            }
            texCoordBuffer.flip();
            mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, texCoordBuffer);

            IntBuffer indexBuffer = BufferUtils.createIntBuffer(faces.size() * 3);
            for (Face face : faces) {
                for (Vertex v : face.getVertices()) {
                    indexBuffer.put(v.getVertexIndex() - 1);
                }
            }
            indexBuffer.flip();
            mesh.setBuffer(VertexBuffer.Type.Index, 3, indexBuffer);

            TangentBinormalGenerator.generate(mesh);

            Geometry geometry = new Geometry("OBJGeometry", mesh);
            Material material = new Material(assetInfo.getManager(), "Common/MatDefs/Light/Lighting.j3md");
            material.setColor("Diffuse", ColorRGBA.White);
            geometry.setMaterial(material);

            Node rootNode = new Node("OBJRootNode");
            rootNode.attachChild(geometry);

            return rootNode;

        } catch (IOException ex) {
            logger.error("Error loading OBJ file: " + objFilePath, ex);
            return new Node();
        }
    }

    private static class Face {
        private List<Vertex> vertices = new ArrayList<>();

        public void add(int vertexIndex, int texCoordIndex, int normalIndex) {
            vertices.add(new Vertex(vertexIndex, texCoordIndex, normalIndex));
        }

        public List<Vertex> getVertices() {
            return vertices;
        }
    }

    private static class Vertex {
        private int vertexIndex;
        private int texCoordIndex;
        private int normalIndex;

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
}