package org.foxesworld.engine.player;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import org.foxesworld.FrozenLands;
import org.foxesworld.engine.Updateable;

public class Player implements ActionListener, Updateable {

    private InputManager inputManager;
    private CharacterControl player;
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false;
    private boolean onGround = false;

    final private Vector3f camDir = new Vector3f();
    final private Vector3f camLeft = new Vector3f();
    private Node rootNode;
    private Node playerNode;
    private Camera cam;

    private float rotationSpeed = 2f;

    public Player(FrozenLands frozenLands){
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
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "Jump");
    }


    @Override
    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Left")) {
            if (value) { left = true; } else { left = false; }
        } else if (binding.equals("Right")) {
            if (value) { right = true; } else { right = false; }
        } else if (binding.equals("Up")) {
            if (value) { up = true; } else { up = false; }
        } else if (binding.equals("Down")) {
            if (value) { down = true; } else { down = false; }
        } else if (binding.equals("Jump")) {
            if(onGround) {
                player.jump();
            }
        }
    }

    public Vector3f getCamDir() {
        return camDir;
    }

    public Vector3f getCamLeft() {
        return camLeft;
    }

    public CharacterControl getPlayer() {
        return player;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    @Override
    public void update(float tpf) {
        getCamDir().set(cam.getDirection()).multLocal(0.3f);
        getCamLeft().set(cam.getLeft()).multLocal(0.3f);
        setOnGround(player.onGround());
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
        cam.setLocation(player.getPhysicsLocation());
    }
}