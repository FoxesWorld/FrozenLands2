package org.foxesworld.engine.player;

import com.jme3.anim.AnimComposer;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import org.foxesworld.FrozenLands;
import org.foxesworld.engine.Updateable;

public class Player implements ActionListener, Updateable {

    private  FrozenLands frozenLands;
    private InputManager inputManager;
    private CharacterControl playerControl;
    private AnimComposer anim;
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
    private float swayOffset = 0f;
    private boolean isMoving = false;
    private FpsCam fpsCam;

    public Player(FrozenLands frozenLands) {
        this.frozenLands = frozenLands;
        this.inputManager = frozenLands.getInputManager();
        this.rootNode = frozenLands.getRootNode();
        this.cam = frozenLands.getCamera();
        setUpKeys();
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 1f, 1);
        playerControl = new CharacterControl(capsuleShape, 0.05f);
        playerControl.setJumpSpeed(10);
        playerControl.setFallSpeed(30);
        playerControl.setGravity(30);
        playerControl.setPhysicsLocation(new Vector3f(0, 10, 0));

        playerNode = new Node("PlayerNode");
        playerNode.addControl(playerControl);
        rootNode.attachChild(playerNode);
        frozenLands.getBulletAppState().getPhysicsSpace().add(playerControl);
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
            playerControl.jump();
        } else if (binding.equals("SwitchView") && value) {
            firstPerson = !firstPerson;
        }
    }

    @Override
    public void update(float tpf) {
        camDir.set(cam.getDirection()).multLocal(0.3f);
        camLeft.set(cam.getLeft()).multLocal(0.3f);
        onGround = playerControl.onGround();
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

        playerControl.setWalkDirection(walkDirection);
        isMoving = walkDirection.lengthSquared() > 0;

        if(isMoving) {
            //fpsCam.updateSway(tpf);
        } else {
            cam.setLocation(new Vector3f(0,0,0));
        }

        if (firstPerson) {
            cam.setLocation(playerControl.getPhysicsLocation());
        } else {
            Vector3f thirdPersonCamLocation = playerControl.getPhysicsLocation().subtract(camDir.mult(distanceFromPlayer));
            cam.setLocation(thirdPersonCamLocation);
        }
    }

    public void setModel(Spatial model) {
        if (model != null) {
            playerNode.attachChild(model);
        }
    }

    public CharacterControl getPlayerControl() {
        return playerControl;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
}