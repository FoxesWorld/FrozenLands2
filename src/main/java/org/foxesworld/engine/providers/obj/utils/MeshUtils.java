package org.foxesworld.engine.providers.obj.utils;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

import java.nio.FloatBuffer;

public class MeshUtils {

    public static void computeTangentBinormal(Mesh mesh) {
        FloatBuffer pos = mesh.getFloatBuffer(VertexBuffer.Type.Position);
        FloatBuffer uv = mesh.getFloatBuffer(VertexBuffer.Type.TexCoord);

        int vertexCount = mesh.getVertexCount();

        FloatBuffer tangents = BufferUtils.createFloatBuffer(vertexCount * 3);
        FloatBuffer binormals = BufferUtils.createFloatBuffer(vertexCount * 3);

        Vector3f[] tan = new Vector3f[vertexCount];
        Vector3f[] bin = new Vector3f[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            tan[i] = new Vector3f();
            bin[i] = new Vector3f();
        }

        calculateTangentsAndBinormals(pos, uv, vertexCount, tan, bin);

        normalizeAndSetBuffers(mesh, tangents, binormals, tan, bin);
    }

    private static void calculateTangentsAndBinormals(FloatBuffer pos, FloatBuffer uv, int vertexCount, Vector3f[] tan, Vector3f[] bin) {
        for (int i = 0; i < vertexCount; i += 3) {
            Vector3f v1 = getVector3f(pos, i);
            Vector3f v2 = getVector3f(pos, i + 1);
            Vector3f v3 = getVector3f(pos, i + 2);

            Vector2f uv1 = getVector2f(uv, i);
            Vector2f uv2 = getVector2f(uv, i + 1);
            Vector2f uv3 = getVector2f(uv, i + 2);

            Vector3f edge1 = v2.subtract(v1);
            Vector3f edge2 = v3.subtract(v1);

            Vector2f deltaUV1 = uv2.subtract(uv1);
            Vector2f deltaUV2 = uv3.subtract(uv1);

            float f = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV2.x * deltaUV1.y);
            Vector3f tangent = new Vector3f(
                    f * (deltaUV2.y * edge1.x - deltaUV1.y * edge2.x),
                    f * (deltaUV2.y * edge1.y - deltaUV1.y * edge2.y),
                    f * (deltaUV2.y * edge1.z - deltaUV1.y * edge2.z)
            );
            Vector3f binormal = new Vector3f(
                    f * (-deltaUV2.x * edge1.x + deltaUV1.x * edge2.x),
                    f * (-deltaUV2.x * edge1.y + deltaUV1.x * edge2.y),
                    f * (-deltaUV2.x * edge1.z + deltaUV1.x * edge2.z)
            );

            addToVectors(tan, bin, i, tangent, binormal);
        }
    }

    private static Vector3f getVector3f(FloatBuffer buffer, int index) {
        return new Vector3f(buffer.get(index * 3), buffer.get(index * 3 + 1), buffer.get(index * 3 + 2));
    }

    private static Vector2f getVector2f(FloatBuffer buffer, int index) {
        return new Vector2f(buffer.get(index * 2), buffer.get(index * 2 + 1));
    }

    private static void addToVectors(Vector3f[] tan, Vector3f[] bin, int i, Vector3f tangent, Vector3f binormal) {
        tan[i].addLocal(tangent);
        tan[i + 1].addLocal(tangent);
        tan[i + 2].addLocal(tangent);
        bin[i].addLocal(binormal);
        bin[i + 1].addLocal(binormal);
        bin[i + 2].addLocal(binormal);
    }

    private static void normalizeAndSetBuffers(Mesh mesh, FloatBuffer tangents, FloatBuffer binormals, Vector3f[] tan, Vector3f[] bin) {
        for (int i = 0; i < tan.length; i++) {
            tan[i].normalizeLocal();
            bin[i].normalizeLocal();
            setBufferValues(tangents, binormals, i, tan[i], bin[i]);
        }

        tangents.flip();
        binormals.flip();
        mesh.setBuffer(VertexBuffer.Type.Tangent, 3, tangents);
        mesh.setBuffer(VertexBuffer.Type.Binormal, 3, binormals);
    }

    private static void setBufferValues(FloatBuffer tangents, FloatBuffer binormals, int i, Vector3f t, Vector3f b) {
        tangents.put(i * 3, t.getX());
        tangents.put(i * 3 + 1, t.getY());
        tangents.put(i * 3 + 2, t.getZ());

        binormals.put(i * 3, b.getX());
        binormals.put(i * 3 + 1, b.getY());
        binormals.put(i * 3 + 2, b.getZ());
    }
}