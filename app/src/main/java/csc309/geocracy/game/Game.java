package csc309.geocracy.game;

import android.opengl.GLES30;
import android.util.Log;

import csc309.geocracy.EventBus;
import csc309.geocracy.Util;
import csc309.geocracy.graphics.OrbitCamera;
import csc309.geocracy.graphics.ScreenTextureRenderer;
import csc309.geocracy.world.Territory;
import csc309.geocracy.world.World;
import glm_.vec2.Vec2;
import glm_.vec2.Vec2i;
import glm_.vec3.Vec3;

import static glm_.Java.glm;

public class Game {

    private long startT; // time the game was started
    private long lastT; // time last frame happened
    private World world;
    private OrbitCamera camera;
    public Vec2 swipeDelta; // TODO: replace this with proper input handling
    private int fbHandle;
    private int colorTexHandle;
    private int idTexHandle;
    private int depthRBHandle;
    private ScreenTextureRenderer screenRenderer;

    public Game() {
        world = new World(0); // TODO: seed should not be predefined

        // Setup camera
        camera = new OrbitCamera(glm.radians(60.0f), 0.01f, 6.0f, 1.0f, 1.5f, 5.0f, 3.0f);

        EventBus.subscribe("CAMERA_ZOOM_EVENT", this, e -> camera.easeElevation((float)e));

        swipeDelta = new Vec2();

        screenRenderer = new ScreenTextureRenderer();

        startT = System.nanoTime();
        lastT = 0;
    }

    // May be called more than once during app execution (waking from sleep, for instance)
    // In this method we need to create/recreate any GPU resources
    public boolean loadOpenGL() {
        GLES30.glClearColor(0.0f, 0.5f, 1.0f, 1.0f); // background color
        GLES30.glEnable(GLES30.GL_DEPTH_TEST); // enable depth testing (close things rendered on top of far things)
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

        if (!screenRenderer.load()) {
            Log.e("Game", "Failed to load screen renderer");
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

        update(t, dt);
        render(t, dt);

        lastT = t;
    }

    public void screenResized(Vec2i size) {
        if (!reloadFrameBuffer(size)) {
            Log.e("Game", "Failed to reload frame buffer");
        }

        GLES30.glViewport(0, 0, size.x, size.y);
        camera.setAspectRatio((float)size.x / (float)size.y);
    }

    // The core game logic
    private float accumDT = 10.0f;
    private void update(long t, float dt) {
        // TODO: replace with proper input system
        synchronized (this) {
            if (!Util.isZero(swipeDelta)) {
                camera.move(swipeDelta.times(-0.01f));
                swipeDelta.x = 0.0f; swipeDelta.y = 0.0f;
            }
        }

        accumDT += dt;
        if (accumDT >= 10.0f) {
            world.deselectTerritory();
            world.unhighlightTerritories();
            Territory terr = world.getTerritories()[(int)(Math.random() * world.getTerritories().length)];
            world.selectTerritory(terr);
            world.highlightTerritories(terr.getAdjacentTerritories());
            accumDT = 0.0f;
        }
    }

    // Render the game
    private void render(long t, float dt) {
        // First pass
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fbHandle);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        Vec3 lightDir = camera.getOrientMatrix().times((new Vec3(-1.0f, -1.0f, -1.0f)).normalizeAssign());
        world.render(t, camera, lightDir);

        // Second pass
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT);
        screenRenderer.render(colorTexHandle);
    }

    private boolean reloadFrameBuffer(Vec2i size) {
        // Destroy existing frame buffer if any (as in the case of the screen being resized)
        if (fbHandle != 0) {
            GLES30.glDeleteFramebuffers(1, new int[]{ fbHandle }, 0);
            if (colorTexHandle != 0) {
                GLES30.glDeleteTextures(1, new int[]{ colorTexHandle }, 0);
            }
            if (idTexHandle != 0) {
                GLES30.glDeleteTextures(1, new int[]{ idTexHandle }, 0);
            }
            if (depthRBHandle != 0) {
                GLES30.glDeleteRenderbuffers(1, new int[]{ depthRBHandle }, 0);
            }
        }

        // Setup color texture
        int[] colorTexHandleArr = { 0 };
        GLES30.glGenTextures(1, colorTexHandleArr, 0);
        colorTexHandle = colorTexHandleArr[0];
        if (colorTexHandle == 0) {
            Log.e("Game", "Failed to generate color texture");
            return false;
        }
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, colorTexHandle);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexStorage2D(GLES30.GL_TEXTURE_2D, 1, GLES30.GL_RGBA8, size.x, size.y);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);

        // Setup identity texture
        int[] idTexHandleArr = { 0 };
        GLES30.glGenTextures(1, idTexHandleArr, 0);
        idTexHandle = idTexHandleArr[0];
        if (idTexHandle == 0) {
            Log.e("Game", "Failed to generate identity texture");
            return false;
        }
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, idTexHandle);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexStorage2D(GLES30.GL_TEXTURE_2D, 1, GLES30.GL_R8UI, size.x, size.y);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);

        // Setup depth render buffer
        int[] depthRBHandlArr = { 0 };
        GLES30.glGenRenderbuffers(1, depthRBHandlArr, 0);
        depthRBHandle = depthRBHandlArr[0];
        if (depthRBHandle == 0) {
            Log.e("Game", "Failed to generate depth render buffer");
            return false;
        }
        GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, depthRBHandle);
        GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER, GLES30.GL_DEPTH_COMPONENT32F, size.x, size.y);
        GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, 0);

        // Setup frame buffer and attachments
        int[] fbHandleArr = { 0 };
        GLES30.glGenFramebuffers(1, fbHandleArr, 0);
        fbHandle = fbHandleArr[0];
        if (fbHandle == 0) {
            Log.e("Game", "Failed to generate framebuffer");
            return false;
        }
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fbHandle);
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, colorTexHandle, 0);
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT1, GLES30.GL_TEXTURE_2D, idTexHandle, 0);
        GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT, GLES30.GL_RENDERBUFFER, depthRBHandle);
        GLES30.glDrawBuffers(2, new int[]{ GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_COLOR_ATTACHMENT1 }, 0);
        if (GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER) != GLES30.GL_FRAMEBUFFER_COMPLETE) {
            Log.e("Game", "Framebuffer is incomplete");
            return false;
        }
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);

        // Check for OpenGL errors
        if (Util.isGLError()) {
            return false;
        }

        return true;
    }

}
