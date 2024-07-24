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

        int triangleCount = mesh.getTriangleCount();
        int vertexCount = mesh.getVertexCount();

        FloatBuffer tangents = BufferUtils.createFloatBuffer(vertexCount * 3);
        FloatBuffer binormals = BufferUtils.createFloatBuffer(vertexCount * 3);

        Vector3f[] tan = new Vector3f[vertexCount];
        Vector3f[] bin = new Vector3f[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            tan[i] = new Vector3f();
            bin[i] = new Vector3f();
        }

        for (int i = 0; i < triangleCount; i++) {
            int index1 = mesh.getIndexBuffer().get(i * 3);
            int index2 = mesh.getIndexBuffer().get(i * 3 + 1);
            int index3 = mesh.getIndexBuffer().get(i * 3 + 2);

            Vector3f v1 = getVector3f(pos, index1);
            Vector3f v2 = getVector3f(pos, index2);
            Vector3f v3 = getVector3f(pos, index3);

            Vector2f uv1 = getVector2f(uv, index1);
            Vector2f uv2 = getVector2f(uv, index2);
            Vector2f uv3 = getVector2f(uv, index3);

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

            tan[index1].addLocal(tangent);
            tan[index2].addLocal(tangent);
            tan[index3].addLocal(tangent);
            bin[index1].addLocal(binormal);
            bin[index2].addLocal(binormal);
            bin[index3].addLocal(binormal);
        }

        for (int i = 0; i < vertexCount; i++) {
            Vector3f t = tan[i];
            t.normalizeLocal();
            tangents.put(i * 3, t.getX());
            tangents.put(i * 3 + 1, t.getY());
            tangents.put(i * 3 + 2, t.getZ());

            Vector3f b = bin[i];
            b.normalizeLocal();
            binormals.put(i * 3, b.getX());
            binormals.put(i * 3 + 1, b.getY());
            binormals.put(i * 3 + 2, b.getZ());
        }

        tangents.flip();
        binormals.flip();
        mesh.setBuffer(VertexBuffer.Type.Tangent, 3, tangents);
        mesh.setBuffer(VertexBuffer.Type.Binormal, 3, binormals);
    }

    private static Vector3f getVector3f(FloatBuffer buffer, int index) {
        if (index * 3 + 2 >= buffer.limit()) {
            throw new IndexOutOfBoundsException(index);
        }
        return new Vector3f(buffer.get(index * 3), buffer.get(index * 3 + 1), buffer.get(index * 3 + 2));
    }

    private static Vector2f getVector2f(FloatBuffer buffer, int index) {
        if (index * 2 + 1 >= buffer.limit()) {
            throw new IndexOutOfBoundsException(index);
        }
        return new Vector2f(buffer.get(index * 2), buffer.get(index * 2 + 1));
    }
}