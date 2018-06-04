package csc_cccix.geocracy.game;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Random;

import csc_cccix.geocracy.EventBus;
import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.fragments.GameInfoFragment;
import csc_cccix.geocracy.space.SpaceRenderer;
import csc_cccix.geocracy.states.BattleResultsState;
import csc_cccix.geocracy.states.DefaultState;
import csc_cccix.geocracy.states.DiceRollState;
import csc_cccix.geocracy.states.GainArmyUnitsState;
import csc_cccix.geocracy.states.GameEvent;
import csc_cccix.geocracy.states.GameState;
import csc_cccix.geocracy.states.IntentToAttackState;
import csc_cccix.geocracy.states.SelectedAttackTargetTerritoryState;
import csc_cccix.geocracy.states.SelectedTerritoryState;
import csc_cccix.geocracy.states.SetUpInitTerritoriesState;
import csc_cccix.geocracy.world.Territory;
import csc_cccix.geocracy.world.World;
import glm_.vec2.Vec2;
import glm_.vec2.Vec2i;
import glm_.vec3.Vec3;

import static csc_cccix.geocracy.states.GameAction.CANCEL_ACTION;
import static csc_cccix.geocracy.states.GameAction.TERRITORY_SELECTED;

public class Game implements Serializable {

    public static final int MAX_ARMIES_PER_TERRITORY = 15;
    public static final transient String user_action = "USER_ACTION";

    public Player[] players;
    public int currentPlayer;

    public transient CameraController cameraController;

    private long startT; // time the game was started
    private long lastT; // time last frame happened

    private transient World world;
    private transient SpaceRenderer spaceRenderer;

    private transient int idFBHandle;
    private transient int idValueTexHandle;
    private transient int idDepthRBHandle;

    private transient Vec2i screenSize;
    private transient Vec2i swipeDelta;
    private transient Vec2i tappedPoint;
    private transient float zoomFactor;
    private transient ByteBuffer readbackBuffer;

    public GameData gameData;
    public transient GameActivity activity;

    transient GameState state;

    public transient GameState defaultState;
    public transient GameState selectedTerritoryState;
    public transient GameState intentToAttackState;
    public transient GameState selectedAttackTargetTerritoryState;
    public transient GameState setUpInitTerritoriesState;
    public transient GameState gainArmyUnitsState;

    public transient GameState diceRollState;
    public transient GameState battleResultsState;

    public Game() {

    }

    public Game(GameActivity activity) {
        this.activity = activity;

        world = new World(this, 0); // TODO: seed should not be predefined

        players = new Player[8];
        Vec3[] playerColors = Util.genDistinctColors(players.length, 0.0f);

        players[0] = new HumanPlayer(1, playerColors[0]);

        for (int i = 1; i < players.length; ++i) {
            players[i] = new AIPlayer(i + 1, playerColors[i]);
        }

        currentPlayer = 0;

        defaultState = new DefaultState(this);
        selectedTerritoryState = new SelectedTerritoryState(this);
        intentToAttackState = new IntentToAttackState(this);
        selectedAttackTargetTerritoryState = new SelectedAttackTargetTerritoryState(this);
        diceRollState = new DiceRollState(this);
        battleResultsState = new BattleResultsState(this);
        setUpInitTerritoriesState = new SetUpInitTerritoriesState(this, activity);
        gainArmyUnitsState = new GainArmyUnitsState(this, activity);

        setState(setUpInitTerritoriesState);

        spaceRenderer = new SpaceRenderer();
        cameraController = new CameraController();
        EventBus.subscribe("CAMERA_ZOOM_EVENT", this, e -> wasZoom((float)e));

        readbackBuffer = ByteBuffer.allocateDirect(1);

        EventBus.subscribe(user_action, this, event -> handleUserAction((GameEvent) event));

        // Should be last in constructor
        startT = System.nanoTime();
        lastT = 0;
    }

    private void handleUserAction(GameEvent event) {

        switch (event.action) {

            case TOGGLE_SETTINGS_VISIBILITY:
                Log.i("", "TOGGLE SETTINGS VISIBILITY ACTION");
                activity.showOverlayFragment(GameActivity.settingsFragment);
                break;

            case TOGGLE_GAME_INFO_VISIBILITY:
                Log.i("", "TOGGLE GAME INFO VISIBILITY ACTION");
                activity.showOverlayFragment(GameInfoFragment.newInstance(this.players));
                break;

            case TERRITORY_SELECTED:
                Log.i("", "USER SELECTED TERRITORY");
                Territory selectedTerritory = (Territory) event.payload;
                if(selectedTerritory == null)
                    return;
                Log.d("", "USER SELECTED TERRITORY:" + selectedTerritory.getId());

                if (getState() == this.intentToAttackState) {
                    getState().selectTargetTerritory(selectedTerritory);
                }
                else if (getState() == this.gainArmyUnitsState) {
                    getState().selectTargetTerritory(selectedTerritory);
                }
                else {
                    getState().selectOriginTerritory(selectedTerritory);
                    getState().initState();
                }

                break;

            case ATTACK_TAPPED:
                Log.i("", "USER TAPPED ATTACK");
                getState().enableAttackMode();
                getState().initState();
                break;

            case ADD_UNIT_TAPPED:
                Log.i("", "ADD UNIT TAPPED");
                getState().addToSelectedTerritoryUnitCount(1);
                break;

            case REMOVE_UNIT_TAPPED:
                Log.i("", "REMOVE UNIT TAPPED");
                getState().addToSelectedTerritoryUnitCount(-1);
                break;

            case CONFIRM_UNITS_TAPPED:
                if(getState()==gainArmyUnitsState)
                    setState(selectedTerritoryState);
                Log.i("", "CONFIRM UNITS TAPPED");
                getState().performDiceRoll(null, null);
                getState().initState();
                break;

            case CONFIRM_ACTION:
                Log.i("", "CONFIRM TAPPED");
                getState().confirmAction();
                getState().initState();
                break;

            case CANCEL_ACTION:
                Log.i("", "CANCEL ACTION TAPPED");
                getState().cancelAction();
                getState().initState();
                break;

            default:
                break;

        }

    }

    public void setState(GameState state) {
        this.state = state;
    }
    public GameState getState() {
        return this.state;
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
            Log.e("", "Failed to setup OpenGL state");
            return false;
        }

        if (!world.load()) {
            Log.e("", "Failed to load world");
            return false;
        }

        if (!spaceRenderer.load()) {
            Log.e("", "Failed to load space renderer");
            return false;
        }

        // Check for OpenGL errors
        return !Util.isGLError();
    }

    // One iteration of the game loop
    public void step() {
        long t = System.nanoTime() - startT;
        float dt = (t - lastT) * 1e-9f;
//        Log.i(TAG, "FPS: " + (1.0f / dt));

        update(t, dt);
        render(t, dt);

        lastT = t;
    }

    public void screenResized(Vec2i size) {
        screenSize = size;

        if (!reloadIdFrameBuffer()) {
            Log.e("", "Failed to reload frame buffer");
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

        if(players[currentPlayer] instanceof HumanPlayer)
            handleInput();
        else
            handleComputerInput();

        cameraController.update(dt);
    }

    private void handleComputerInput() {
        GameState currState = getState();
        if(currState == setUpInitTerritoriesState){
            Random rand = new Random();
            int rand_num = rand.nextInt(world.getNTerritories());
            Territory terr = world.getUnoccTerritory(rand_num);
            EventBus.publish(user_action, new GameEvent(TERRITORY_SELECTED, terr));
        }
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
                    EventBus.publish(user_action, new GameEvent(TERRITORY_SELECTED, terr));
                }
                else {
                    EventBus.publish(user_action, new GameEvent(CANCEL_ACTION, null));
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
            Log.e("", "Failed to generate identity depth render buffer");
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
            Log.e("", "Failed to generate identity frame buffer");
            return false;
        }
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, idFBHandle);
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, idValueTexHandle, 0);
        GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_STENCIL_ATTACHMENT, GLES30.GL_RENDERBUFFER, idDepthRBHandle);
        if (GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER) != GLES30.GL_FRAMEBUFFER_COMPLETE) {
            Log.e("", "Identity frame buffer is incomplete");
            return false;
        }
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);

        // Check for OpenGL errors
        return !Util.isGLError();
    }

    public World getWorld() {
        return world;
    }

    public Player[] getPlayers() {
        return players;
    }

}