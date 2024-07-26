package org.foxesworld.engine.player;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.foxesworld.FrozenLands;
import org.foxesworld.engine.Updateable;

public class Player implements ActionListener, Updateable {

    private InputManager inputManager;
    private CharacterControl player;
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false;
    private boolean onGround = false;
    private boolean firstPerson = true;  // To track the camera view mode

    final private Vector3f camDir = new Vector3f();
    final private Vector3f camLeft = new Vector3f();
    private Node rootNode;
    private Node playerNode;
    private Camera cam;

    private float rotationSpeed = 2f;
    private float distanceFromPlayer = 5f;  // Distance for third-person view

    // Для покачивания камеры
    private float swayOffset = 0f; // Текущий сдвиг покачивания
    private boolean isMoving = false; // Двигается ли игрок
    private static final float SWAY_AMOUNT = 0.1f; // Сила покачивания
    private static final float SWAY_SPEED = 5f; // Скорость покачивания
    private FpsCam fpsCam;

    public Player(FrozenLands frozenLands) {
        this.inputManager = frozenLands.getInputManager();
        this.rootNode = frozenLands.getRootNode();
        this.cam = frozenLands.getCamera();
        setUpKeys();
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 1f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setJumpSpeed(10);
        player.setFallSpeed(30);
        player.setGravity(30);
        player.setPhysicsLocation(new Vector3f(0, 10, 0));

        playerNode = new Node("PlayerNode");
        playerNode.addControl(player);
        rootNode.attachChild(playerNode);
        frozenLands.getBulletAppState().getPhysicsSpace().add(player);
    }

    private void setUpKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("SwitchView", new KeyTrigger(KeyInput.KEY_V));
        fpsCam = new FpsCam(inputManager, cam);
        fpsCam.registerInput();

        inputManager.addListener(this, "Left", "Right", "Up", "Down", "Jump", "SwitchView");
    }

    @Override
    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Left")) {
            left = value;
        } else if (binding.equals("Right")) {
            right = value;
        } else if (binding.equals("Up")) {
            up = value;
        } else if (binding.equals("Down")) {
            down = value;
        } else if (binding.equals("Jump") && value && onGround) {
            player.jump();
        } else if (binding.equals("SwitchView") && value) {
            firstPerson = !firstPerson;
        }
    }

    @Override
    public void update(float tpf) {
        camDir.set(cam.getDirection()).multLocal(0.3f);
        camLeft.set(cam.getLeft()).multLocal(0.3f);
        onGround = player.onGround();
        walkDirection.set(0, 0, 0);

        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir);
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
        }

        player.setWalkDirection(walkDirection);
        isMoving = walkDirection.lengthSquared() > 0;

        if(isMoving) {
            //fpsCam.updateSway(tpf);
        } else {
            cam.setLocation(new Vector3f(0,0,0));
        }

        if (firstPerson) {
            cam.setLocation(player.getPhysicsLocation());
        } else {
            Vector3f thirdPersonCamLocation = player.getPhysicsLocation().subtract(camDir.mult(distanceFromPlayer));
            cam.setLocation(thirdPersonCamLocation);
        }
    }

    public void setModel(Spatial model) {
        if (model != null) {
            playerNode.attachChild(model);
        }
    }

    public CharacterControl getPlayer() {
        return player;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
}