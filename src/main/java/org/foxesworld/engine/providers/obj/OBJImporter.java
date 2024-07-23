package org.foxesworld.engine.providers.obj;

import com.jme3.asset.*;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.*;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import org.apache.logging.log4j.Logger;
import org.foxesworld.FrozenLands;
import org.foxesworld.engine.providers.obj.utils.MeshUtils;

import java.io.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

public class OBJImporter implements AssetLoader {

    private static final Logger logger = FrozenLands.logger;

    @Override
    public Object load(AssetInfo assetInfo) {
        if (!(assetInfo.getKey() instanceof ModelKey)) {
            throw new IllegalArgumentException("Asset key must be a ModelKey.");
        }

        ModelKey modelKey = (ModelKey) assetInfo.getKey();
        String objFilePath = modelKey.getName();
        String objDir = objFilePath.substring(0, objFilePath.lastIndexOf('/'));

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(assetInfo.openStream()))) {
            Map<Integer, Vector3f> vertices = new HashMap<>();
            Map<Integer, Vector3f> normals = new HashMap<>();
            Map<Integer, Vector2f> texCoords = new HashMap<>();
            List<Face> faces = new ArrayList<>();

            Map<String, MaterialData> materials = new HashMap<>();
            String currentMaterial = null;

            String line;
            int vertexIndex = 1;
            int normalIndex = 1;
            int texCoordIndex = 1;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length == 0 || line.startsWith("#")) {
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
                                int vIndex = Integer.parseInt(indices[0]);
                                int tIndex = indices.length > 1 && !indices[1].isEmpty() ? Integer.parseInt(indices[1]) : 0;
                                int nIndex = indices.length > 2 && !indices[2].isEmpty() ? Integer.parseInt(indices[2]) : 0;
                                face.add(vIndex, tIndex, nIndex);
                            } catch (NumberFormatException ex) {
                                logger.warn("Invalid index: " + indices[0]);
                            }
                        }
                        faces.add(face);
                    }
                    case "mtllib" -> {
                        String mtlFilePath = parts[1];
                        loadMTL(assetInfo.getManager(), objDir + '/' + mtlFilePath, materials);
                    }
                    case "usemtl" -> currentMaterial = parts[1];
                }
            }

            Mesh mesh = new Mesh();

            FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.size() * 3);
            for (Vector3f vertex : vertices.values()) {
                vertexBuffer.put(vertex.getX()).put(vertex.getY()).put(vertex.getZ());
            }
            vertexBuffer.flip();
            mesh.setBuffer(VertexBuffer.Type.Position, 3, vertexBuffer);

            if (!normals.isEmpty()) {
                FloatBuffer normalBuffer = BufferUtils.createFloatBuffer(normals.size() * 3);
                for (Vector3f normal : normals.values()) {
                    normalBuffer.put(normal.getX()).put(normal.getY()).put(normal.getZ());
                }
                normalBuffer.flip();
                mesh.setBuffer(VertexBuffer.Type.Normal, 3, normalBuffer);
            }

            if (!texCoords.isEmpty()) {
                FloatBuffer texCoordBuffer = BufferUtils.createFloatBuffer(texCoords.size() * 2);
                for (Vector2f texCoord : texCoords.values()) {
                    texCoordBuffer.put(texCoord.getX()).put(texCoord.getY());
                }
                texCoordBuffer.flip();
                mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, texCoordBuffer);
            }

            IntBuffer indexBuffer = BufferUtils.createIntBuffer(faces.size() * 3);
            for (Face face : faces) {
                for (Vertex v : face.getVertices()) {
                    indexBuffer.put(v.getVertexIndex() - 1);
                }
            }
            indexBuffer.flip();
            mesh.setBuffer(VertexBuffer.Type.Index, 3, indexBuffer);

            if (!normals.isEmpty() && !texCoords.isEmpty()) {
                MeshUtils.computeTangentBinormal(mesh);
            }

            Geometry geometry = new Geometry("OBJGeometry", mesh);
            Material material = createMaterial(assetInfo.getManager(), objDir, materials.get(currentMaterial));
            if (material == null) {
                material = new Material(assetInfo.getManager(), "Common/MatDefs/Light/Lighting.j3md");
                material.setColor("Diffuse", ColorRGBA.White);
            }
            geometry.setMaterial(material);

            Node rootNode = new Node("OBJRootNode");
            rootNode.attachChild(geometry);

            return rootNode;

        } catch (IOException ex) {
            logger.error("Error loading OBJ file: " + objFilePath, ex);
            return new Node();
        }
    }

    private void loadMTL(AssetManager assetManager, String mtlFilePath, Map<String, MaterialData> materials) {
        try {
            InputStream mtlStream = assetManager.locateAsset(new AssetKey<>(mtlFilePath)).openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(mtlStream));
            String line;
            String currentMaterial = null;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length == 0 || line.startsWith("#")) {
                    continue;
                }

                switch (parts[0]) {
                    case "newmtl":
                        currentMaterial = parts[1];
                        materials.put(currentMaterial, new MaterialData());
                        break;
                    case "Ka":
                        if (currentMaterial != null) {
                            materials.get(currentMaterial).setAmbientColor(new ColorRGBA(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]), 1f));
                        }
                        break;
                    case "Kd":
                        if (currentMaterial != null) {
                            materials.get(currentMaterial).setDiffuseColor(new ColorRGBA(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]), 1f));
                        }
                        break;
                    case "Ks":
                        if (currentMaterial != null) {
                            materials.get(currentMaterial).setSpecularColor(new ColorRGBA(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]), 1f));
                        }
                        break;
                    case "Ns":
                        if (currentMaterial != null) {
                            materials.get(currentMaterial).setShininess(Float.parseFloat(parts[1]));
                        }
                        break;
                    case "map_Kd":
                        if (currentMaterial != null) {
                            materials.get(currentMaterial).setDiffuseMap(parts[1]);
                        }
                        break;
                }
            }
            reader.close();
        } catch (IOException ex) {
            logger.error("Error loading MTL file: " + mtlFilePath, ex);
        }
    }

    private Material createMaterial(AssetManager assetManager, String objDir, MaterialData materialData) {
        if (materialData == null) {
            return null;
        }
        Material material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        material.setColor("Ambient", materialData.getAmbientColor());
        material.setColor("Diffuse", materialData.getDiffuseColor());
        material.setColor("Specular", materialData.getSpecularColor());
        material.setFloat("Shininess", materialData.getShininess());

        if (materialData.getDiffuseMap() != null) {
            Texture texture = assetManager.loadTexture(materialData.getDiffuseMap());
            material.setTexture("DiffuseMap", texture);
        }

        return material;
    }
}