package org.foxesworld.engine.camera;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class CameraFollowSpatial extends AbstractControl {
    private boolean ready = false;
    private Camera cam;
    private Vector3f offset;
    private Vector3f direction = new Vector3f(0, 0, -1);  // Направление по умолчанию
    private BetterCharacterControl character;
    private Spatial cameraNode;
    private float horizontalRotation = 0;
    private float verticalRotation = 0;
    private final float maxVerticalRotation = FastMath.HALF_PI - 0.1f;
    private float rotationSpeed = 2f; // скорость вращения камеры

    public CameraFollowSpatial(Camera cam) {
        this.cam = cam;
    }

    public void setOffset(Vector3f offset) {
        this.offset = offset;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public void rotate(float value, Vector3f axis) {
        Matrix3f mat = new Matrix3f();
        mat.fromAngleNormalAxis(rotationSpeed * value, axis);

        Vector3f up = cam.getUp();
        Vector3f left = cam.getLeft();
        Vector3f dir = cam.getDirection();

        mat.mult(up, up);
        mat.mult(left, left);
        mat.mult(dir, dir);

        Quaternion q = new Quaternion();
        q.fromAxes(left, up, dir);
        q.normalizeLocal();

        cam.setAxes(q);
    }

    private void initialize() {
        if (ready) return;
        ready = true;

        if (offset == null) {
            // Если смещение (offset) равно null, ищем 'camera' узел в этом spatial
            if (spatial instanceof Node) {
                Node n = (Node) spatial;
                Spatial cameraNode = n.getChild("camera");
                if (cameraNode != null) {
                    this.cameraNode = cameraNode;
                }
            }
        }

        // Находим character control в spatial
        if (character == null) {
            spatial.depthFirstTraversal(sx -> {
                BetterCharacterControl c = sx.getControl(BetterCharacterControl.class);
                if (c != null) character = c;
            });
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        initialize(); // инициализация при необходимости.
        Vector3f loc = cam.getLocation();
        if (offset != null) {
            // Местоположение = местоположение spatial + смещение
            loc.set(spatial.getWorldTranslation());
            loc.addLocal(offset);
        } else if (cameraNode != null) {
            // Местоположение = местоположение camera node
            loc.set(cameraNode.getWorldTranslation());
        } else {
            // Резервный вариант: местоположение spatial
            loc.set(spatial.getWorldTranslation());
        }
        cam.setLocation(loc);

        // если существует character: Направление = направление character
        if (character != null) {
            cam.lookAtDirection(character.getViewDirection(), Vector3f.UNIT_Y);
        } else if (cameraNode != null) {
            // Если существует camera node: Направление = направление camera node
            cam.setRotation(cameraNode.getWorldRotation());
        } else {
            // По умолчанию используется заданное направление
            cam.lookAtDirection(direction, Vector3f.UNIT_Y);
        }
    }

    protected void rotateCamera(float value, Vector3f axis) {
        Matrix3f mat = new Matrix3f();
        mat.fromAngleNormalAxis(rotationSpeed * value, axis);

        Vector3f up = cam.getUp();
        Vector3f left = cam.getLeft();
        Vector3f dir = cam.getDirection();

        mat.mult(up, up);
        mat.mult(left, left);
        mat.mult(dir, dir);

        Quaternion q = new Quaternion();
        q.fromAxes(left, up, dir);
        q.normalizeLocal();

        cam.setAxes(q);
    }


    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // Не требуется код рендеринга
    }
}