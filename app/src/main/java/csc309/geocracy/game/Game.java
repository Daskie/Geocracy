package csc309.geocracy;

import android.opengl.GLES30;
import android.util.Log;

import csc309.geocracy.noise.NoiseTest;
import csc309.geocracy.world.World;
import glm_.vec2.Vec2;
import glm_.vec2.Vec2i;
import glm_.vec3.Vec3;

import static glm_.Java.glm;

public class Game {

    private long lastT; // timestamp of last frame
    private World world;
    private NoiseTest noiseTest;
    private OrbitCamera camera;
    public Vec2 swipeDelta; // TODO: replace this with proper input handling

    public Game() {
        world = new World(0); // TODO: seed should not be predefined
        //noiseTest = new NoiseTest();

        // Setup camera
        camera = new OrbitCamera(glm.radians(90.0f), 0.01f, 10.0f, 1.0f, 2.0f);
        //camera.setLocation(new Vec3(0.0f, -1.0f, 0.0f));

        swipeDelta = new Vec2();

        lastT = System.nanoTime();
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
        //if (!noiseTest.load()) {
        //    Log.e("Game", "Failed to load noise test");
        //    return false;
        //}

        // Check for OpenGL errors
        if (Util.isGLError()) {
            return false;
        }

        return true;
    }

    // One iteration of the game loop
    public void step() {
        long t = System.nanoTime();
        float dt = (t - lastT) * 1e-9f;
        //System.out.println("FPS: " + (1.0f / dt));

        update(dt);
        render();

        lastT = t;
    }

    public void screenResized(Vec2i size) {
        GLES30.glViewport(0, 0, size.x, size.y);
        camera.setAspectRatio((float)size.x / (float)size.y);
    }

    // The core game logic
    private void update(float dt) {
        // TODO: replace with proper input system
        synchronized (this) {
            if (!Util.isZero(swipeDelta)) {
                camera.move(swipeDelta.times(-0.01f));
                swipeDelta.x = 0.0f; swipeDelta.y = 0.0f;
            }
        }
    }

    // Render the game
    private void render() {
        // Redraw background color
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        Vec3 lightDir = camera.getOrientMatrix().times((new Vec3(1.0f, 1.0f, 1.0f)).normalizeAssign());
        world.render(camera, lightDir);
        //noiseTest.render();
    }

}
