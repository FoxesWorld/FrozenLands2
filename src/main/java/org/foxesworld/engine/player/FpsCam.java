package org.foxesworld.engine.player;

import com.jme3.input.CameraInput;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

public class FpsCam implements AnalogListener, ActionListener {

    private static final float DEFAULT_ROTATION_SPEED = 1f;
    private static final float MOVE_MULTIPLIER = 1f;
    private static final float SWAY_AMOUNT = 0.1f; // Amount of sway
    private static final float SWAY_SPEED = 5f; // Speed of sway effect

    private Camera cam;
    private InputManager inputManager;
    private boolean invertY = false;
    private Vector3f initialUpVec;
    private float rotationSpeed;

    private float swayOffset = 0f; // Current sway offset
    private boolean isMoving = false; // Is the camera currently moving

    private static final String[] MAPPINGS = {
            CameraInput.FLYCAM_LEFT,
            CameraInput.FLYCAM_RIGHT,
            CameraInput.FLYCAM_UP,
            CameraInput.FLYCAM_DOWN,
            CameraInput.FLYCAM_ROTATEDRAG
    };

    public FpsCam(InputManager inputManager, Camera cam) {
        this.inputManager = inputManager;
        this.cam = cam;
        this.initialUpVec = cam.getUp().clone();
        this.rotationSpeed = DEFAULT_ROTATION_SPEED;
    }

    public void registerInput() {
        inputManager.addMapping(CameraInput.FLYCAM_LEFT,
                new MouseAxisTrigger(MouseInput.AXIS_X, true),
                new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping(CameraInput.FLYCAM_RIGHT,
                new MouseAxisTrigger(MouseInput.AXIS_X, false),
                new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping(CameraInput.FLYCAM_UP,
                new MouseAxisTrigger(MouseInput.AXIS_Y, false),
                new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping(CameraInput.FLYCAM_DOWN,
                new MouseAxisTrigger(MouseInput.AXIS_Y, true),
                new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping(CameraInput.FLYCAM_ROTATEDRAG, new MouseAxisTrigger(MouseInput.AXIS_X, true));

        inputManager.addListener(this, MAPPINGS);
        inputManager.setCursorVisible(false);
    }

    protected void rotateCamera(float value, Vector3f axis) {
        if (value != 0 && axis != null) {
            Matrix3f rotationMatrix = new Matrix3f();
            rotationMatrix.fromAngleNormalAxis(rotationSpeed * value, axis);

            Vector3f up = new Vector3f();
            Vector3f left = new Vector3f();
            Vector3f dir = new Vector3f();

            rotationMatrix.mult(cam.getUp(), up);
            rotationMatrix.mult(cam.getLeft(), left);
            rotationMatrix.mult(cam.getDirection(), dir);

            Quaternion q = new Quaternion();
            q.fromAxes(left, up, dir);
            cam.setAxes(q.normalizeLocal());
        }
    }
    protected void moveCamera(float value, boolean sideways) {
        Vector3f direction = sideways ? cam.getLeft(new Vector3f()) : cam.getDirection(new Vector3f());
        direction.multLocal(value * MOVE_MULTIPLIER);
        cam.setLocation(cam.getLocation().add(direction));

        // Установить состояние движения
        isMoving = value != 0;
    }

    public void setUpVector(Vector3f upVec) {
        this.initialUpVec.set(upVec);
    }

    @Override
    public void onAction(String name, boolean value, float tpf) {
        if (name.equals(CameraInput.FLYCAM_ROTATEDRAG)) {
            if (value) {
                inputManager.setCursorVisible(false);
            } else {
                //inputManager.setCursorVisible(true);
            }
        } else if (name.equals(CameraInput.FLYCAM_INVERTY)) {
            if (!value) {
                invertY = !invertY;
            }
        }
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        switch (name) {
            case CameraInput.FLYCAM_LEFT:
                rotateCamera(value, initialUpVec);
                break;
            case CameraInput.FLYCAM_RIGHT:
                rotateCamera(-value, initialUpVec);
                break;
            case CameraInput.FLYCAM_UP:
                rotateCamera(-value * (invertY ? -1 : 1), cam.getLeft());
                break;
            case CameraInput.FLYCAM_DOWN:
                rotateCamera(value * (invertY ? -1 : 1), cam.getLeft());
                break;
        }
    }

    public void updateSway(float tpf) {
        swayOffset += tpf * SWAY_SPEED;
        float sway = (float) Math.sin(swayOffset) * SWAY_AMOUNT;
        Vector3f camLocation = cam.getLocation();
        rotateCamera(sway, camLocation.add(0, sway, 0)); // Покачивание вверх и вниз
    }
}