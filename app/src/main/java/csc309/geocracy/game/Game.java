package csc309.geocracy.game;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;

import csc309.geocracy.EventBus;
import csc309.geocracy.GameStates.GameData;
import csc309.geocracy.GameStates.GameState;
import csc309.geocracy.Util;
import csc309.geocracy.space.SpaceRenderer;
import csc309.geocracy.world.Territory;
import csc309.geocracy.world.World;
import glm_.vec2.Vec2;
import glm_.vec2.Vec2i;
import glm_.vec3.Vec3;

public class Game {

    private GameActivity game_act;

    private long startT; // time the game was started
    private long lastT; // time last frame happened
    private World world;
    private SpaceRenderer spaceRenderer;
    private CameraController cameraController;
    private int idFBHandle;
    private int idValueTexHandle;
    private int idDepthRBHandle;
    private Vec2i screenSize;
    private Vec2i swipeDelta;
    private Vec2i tappedPoint;
    private float zoomFactor;
    private ByteBuffer readbackBuffer;
    static public GameState gameStates;
    static public GameData gameData;

    public CurrentState state;

    public Game(GameActivity activity) {

        state = new CurrentState(activity, new csc309.geocracy.game.GameData());

        world = new World(0); // TODO: seed should not be predefined

        spaceRenderer = new SpaceRenderer();

        cameraController = new CameraController();

        EventBus.subscribe("CAMERA_ZOOM_EVENT", this, e -> wasZoom((float)e));

        readbackBuffer = ByteBuffer.allocateDirect(1);

        startT = System.nanoTime();
        lastT = 0;
    }

    // May be called more than once during app execution (waking from sleep, for instance)
    // In this method we need to create/recreate any GPU resources
    public boolean loadOpenGL() {
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // background color
        GLES30.glEnable(GLES30.GL_DEPTH_TEST); // enable depth testing (close things rendered on top of far things)
        GLES30.glDepthFunc(GLES30.GL_LEQUAL); // set to less than or equal to rather than less than as a cubemap optimization
        GLES30.glEnable(GLES30.GL_CULL_FACE); // enable face culling (back faces of triangles aren't rendered)
        GLES30.glEnable(GLES30.GL_BLEND); // enable alpha blending (allows for transparency/translucency)
        GLES30.glBlendFuncSeparate(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA, GLES30.GL_ONE_MINUS_DST_ALPHA, GLES30.GL_ONE);
        GLES30.glBlendEquationSeparate(GLES30.GL_FUNC_ADD, GLES30.GL_FUNC_ADD);

        if (Util.isGLError()) {
            Log.e("Game", "Failed to setup OpenGL state");
            return false;
        }

        if (!world.load()) {
            Log.e("Game", "Failed to load world");
            return false;
        }

        if (!spaceRenderer.load()) {
            Log.e("Game", "Failed to load space renderer");
            return false;
        }

        // Check for OpenGL errors
        if (Util.isGLError()) {
            return false;
        }

        return true;
    }

    // One iteration of the game loop
    public void step() {
        long t = System.nanoTime() - startT;
        float dt = (t - lastT) * 1e-9f;
        //System.out.println("FPS: " + (1.0f / dt));

//        gameData.handleInput(gameStates, game_act);

        update(t, dt);
        render(t, dt);



        lastT = t;
    }

    public void screenResized(Vec2i size) {
        screenSize = size;

        if (!reloadIdFrameBuffer()) {
            Log.e("Game", "Failed to reload frame buffer");
        }

        GLES30.glViewport(0, 0, screenSize.x, screenSize.y);
        cameraController.getCamera().setAspectRatio((float)screenSize.x / (float)screenSize.y);
    }

    public void wasTap(Vec2i p) {
        synchronized (this) {
            tappedPoint = p;
        }
    }

    public void wasSwipe(Vec2i d) {
        synchronized (this) {
            swipeDelta = d;
        }
    }

    public void wasZoom(float factor) {
        synchronized (this) {
            zoomFactor = factor;
        }
    }

    // The core game logic
    private void update(long t, float dt) {
        handleInput();

        cameraController.update(dt);
    }

    private void handleInput() {
        synchronized (this) {
            if (swipeDelta != null && (swipeDelta.x != 0 || swipeDelta.y != 0)) {
                Vec2 delta = new Vec2(swipeDelta);
                delta.timesAssign(-1.0f);
                cameraController.move(delta);
                swipeDelta = null;
            }
            if (tappedPoint != null) {
                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, idFBHandle);
                GLES30.glReadPixels(tappedPoint.x, screenSize.y - tappedPoint.y - 1, 1, 1, GLES30.GL_RED_INTEGER, GLES30.GL_UNSIGNED_BYTE, readbackBuffer);
                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
                byte terrId = readbackBuffer.get(0);
                if (terrId > 0) {
                    Territory terr = world.getTerritory(terrId);
                    world.selectTerritory(terr);
                    world.unhighlightTerritories();
                    world.highlightTerritories(terr.getAdjacentTerritories());
                    cameraController.targetTerritory(terr);
                }
                else {
                    world.unselectTerritory();
                    world.unhighlightTerritories();
                }
                tappedPoint = null;
            }
            if (zoomFactor != 0.0f) {
                cameraController.zoom(zoomFactor);
                zoomFactor = 0.0f;
            }
        }

    }

    // Render the game
    private void render(long t, float dt) {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, idFBHandle);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        world.renderId(cameraController.getCamera());

        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        Vec3 lightDir = camera.getOrientMatrix().times((new Vec3(-1.0f, -1.0f, -1.0f)).normalizeAssign());
        world.render(t, camera, lightDir);
        //noiseTest.render();
    }

}
