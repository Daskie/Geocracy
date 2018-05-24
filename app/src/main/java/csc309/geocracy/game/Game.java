package csc309.geocracy.game;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.Random;

import csc309.geocracy.EventBus;
import csc309.geocracy.Util;
import csc309.geocracy.space.SpaceRenderer;
import csc309.geocracy.states.DefaultState;
import csc309.geocracy.states.GameEvent;
import csc309.geocracy.states.GameState;
import csc309.geocracy.states.IntentToAttackState;
import csc309.geocracy.states.SelectedAttackTargetTerritoryState;
import csc309.geocracy.states.SelectedTerritoryState;
import csc309.geocracy.world.Territory;
import csc309.geocracy.world.World;
import glm_.vec2.Vec2;
import glm_.vec2.Vec2i;
import glm_.vec3.Vec3;

import static csc309.geocracy.states.GameAction.CANCEL_ACTION;
import static csc309.geocracy.states.GameAction.TERRITORY_SELECTED;

//import csc309.geocracy.states.CurrentState;

public class Game {

    public static final int MAX_ARMIES_PER_TERRITORY = 20;

    private long startT; // time the game was started
    private long lastT; // time last frame happened
    private World world;
    private Player[] players;
    private SpaceRenderer spaceRenderer;
    public CameraController cameraController;
    private int idFBHandle;
    private int idValueTexHandle;
    private int idDepthRBHandle;
    private Vec2i screenSize;
    private Vec2i swipeDelta;
    private Vec2i tappedPoint;
    private float zoomFactor;
    private ByteBuffer readbackBuffer;

    static public GameData gameData;
    private GameActivity activity;

//    public CurrentState state;

    GameState State;

    public GameState DefaultState;
    public GameState SelectedTerritoryState;
    public GameState IntentToAttackState;
    public GameState SelectedAttackTargetTerritoryState;

    public Game(GameActivity activity) {
        this.activity = activity;

        world = new World(this, 0); // TODO: seed should not be predefined

        // TODO: the following is just for testing and should be temporary
        // Create players
        players = new Player[8];
        Vec3[] playerColors = Util.genDistinctColors(players.length, 0.0f);
        for (int i = 0; i < players.length; ++i) {
            players[i] = new Player(i + 1, playerColors[i]);
        }
        // Randomly assign territories players
        Random rand = new Random();
        for (Territory terr : world.getTerritories()) {
            terr.setOwner(players[rand.nextInt(players.length)]);
            terr.setNArmies(rand.nextInt(MAX_ARMIES_PER_TERRITORY) + 1);
        }

        DefaultState = new DefaultState(this);
        SelectedTerritoryState = new SelectedTerritoryState(this);
        IntentToAttackState = new IntentToAttackState(this);
        SelectedAttackTargetTerritoryState = new SelectedAttackTargetTerritoryState(this);

        setState(DefaultState);

        spaceRenderer = new SpaceRenderer();
        cameraController = new CameraController();
        EventBus.subscribe("CAMERA_ZOOM_EVENT", this, e -> wasZoom((float)e));

        readbackBuffer = ByteBuffer.allocateDirect(1);

        startT = System.nanoTime();
        lastT = 0;

        EventBus.subscribe("USER_ACTION", this, event -> {
            handleUserAction((GameEvent) event);
        });
    }

    private void handleUserAction(GameEvent event) {

        switch (event.action) {

            case TOGGLE_SETTINGS_VISIBILITY:
                System.out.println("TOGGLE SETTINGS VISIBILITY ACTION");
                GameActivity.toggleSettingsFragment();
                break;

            case TERRITORY_SELECTED:

                System.out.println("USER SELECTED TERRITORY");
                Territory selectedTerritory = (Territory) event.payload;
                System.out.println(selectedTerritory);


                if (getState() == this.IntentToAttackState) {
                    getState().selectTargetTerritory(selectedTerritory);
                } else {
                    getState().selectOriginTerritory(selectedTerritory);
                }

                getState().initState();


//                if (attackSelection == false) {
//                    state.setCurrentState(new SelectedTerritoryState(selectedTerritory));

//                } else {
//                    System.out.println("ATTACK MODE ACTIVE, TERRITORY SELECTED, CHECK FOR ADJACENCY TO ORIGIN TERRITORY");
//                    if (currentTerritorySelection.getAdjacentTerritories().contains(selectedTerritory)) {
//                        System.out.println("SHOW ATTACK OPTIONS FOR ADJACENT TERRITORY ");
//                        Bundle args = new Bundle();
//                        args.putSerializable("territory", selectedTerritory);
//                        System.out.println(selectedTerritory);
//                        GameActivity.showBottomPaneFragment(TroopSelectionFragment.newInstance(selectedTerritory));
//                        currentTerritorySelection = selectedTerritory;
//                        GameActivity.game.world.selectTerritory(selectedTerritory);
//                        GameActivity.game.world.unhighlightTerritories();
//                        GameActivity.game.cameraController.targetTerritory(selectedTerritory);
//                    } else {
//                        state.setCurrentState(new DefaultState());
////                        cancelAction();
//                    }
//
//                }

                break;

            case ATTACK_TAPPED:
                System.out.println("USER TAPPED ATTACK");
                getState().enableAttackMode();
                getState().initState();

//                if (previousAction == event.action.TERRITORY_SELECTED && currentTerritorySelection != null) {
//                    System.out.println("TERRITORY SELECTED -> ATTACK");
//                    GameActivity.game.world.highlightTerritories(currentTerritorySelection.getAdjacentTerritories());
//                    attackSelection = true;
//                } else {
//                    System.out.println("TERRITORY NOT SELECTED -> UNABLE TO DO ANYTHING");
//                }

                break;

            case CANCEL_ACTION:
                getState().cancelAction();
                getState().initState();
                break;

            default:
                break;

        }

//        previousAction = event.action;
    }

    public void setState(GameState state) {
        this.State = state;
    }

    public GameState getState() {
        return this.State;
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
                    EventBus.publish("USER_ACTION", new GameEvent(TERRITORY_SELECTED, terr));
                }
                else {
                    EventBus.publish("USER_ACTION", new GameEvent(CANCEL_ACTION, null));
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
        Vec3 lightDir = cameraController.getCamera().getOrientMatrix().times((new Vec3(-0.75f, -0.75f, -1.0f)).normalizeAssign());
        world.render(t, cameraController.getCamera(), lightDir, spaceRenderer.getCubemapHandle());
        spaceRenderer.render(cameraController.getCamera());
    }

    private boolean reloadIdFrameBuffer() {
        // Destroy existing frame buffer if any (as in the case of the screen being resized)
        if (idFBHandle != 0) {
            GLES30.glDeleteFramebuffers(1, new int[]{idFBHandle}, 0);
            if (idValueTexHandle != 0) {
                GLES30.glDeleteTextures(1, new int[]{idValueTexHandle}, 0);
            }
            if (idDepthRBHandle != 0) {
                GLES30.glDeleteRenderbuffers(1, new int[]{ idDepthRBHandle }, 0);
            }
        }

        // Setup value texture
        int[] idValueTexHandleArr = { 0 };
        GLES30.glGenTextures(1, idValueTexHandleArr, 0);
        idValueTexHandle = idValueTexHandleArr[0];
        if (idValueTexHandle == 0) {
            Log.e("Game", "Failed to generate identity value texture");
            return false;
        }
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, idValueTexHandle);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexStorage2D(GLES30.GL_TEXTURE_2D, 1, GLES30.GL_R8UI, screenSize.x, screenSize.y);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);

        // Setup depth render buffer
        int[] depthRBHandlArr = { 0 };
        GLES30.glGenRenderbuffers(1, depthRBHandlArr, 0);
        idDepthRBHandle = depthRBHandlArr[0];
        if (idDepthRBHandle == 0) {
            Log.e("Game", "Failed to generate identity depth render buffer");
            return false;
        }
        GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, idDepthRBHandle);
        GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER, GLES30.GL_DEPTH24_STENCIL8, screenSize.x, screenSize.y);
        GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, 0);

        // Setup frame buffer and attachments
        int[] fbHandleArr = { 0 };
        GLES30.glGenFramebuffers(1, fbHandleArr, 0);
        idFBHandle = fbHandleArr[0];
        if (idFBHandle == 0) {
            Log.e("Game", "Failed to generate identity frame buffer");
            return false;
        }
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, idFBHandle);
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, idValueTexHandle, 0);
        GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_STENCIL_ATTACHMENT, GLES30.GL_RENDERBUFFER, idDepthRBHandle);
        if (GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER) != GLES30.GL_FRAMEBUFFER_COMPLETE) {
            Log.e("Game", "Identity frame buffer is incomplete");
            return false;
        }
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);

        // Check for OpenGL errors
        if (Util.isGLError()) {
            return false;
        }

        return true;
    }

    public World getWorld() {
        return world;
    }

    public Player[] getPlayers() {
        return players;
    }

}