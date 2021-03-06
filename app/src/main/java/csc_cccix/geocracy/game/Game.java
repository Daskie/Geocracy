package csc_cccix.geocracy.game;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import androidx.fragment.app.FragmentManager;
import android.util.Log;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;

import androidx.lifecycle.ViewModelProviders;
import csc_cccix.geocracy.EventBus;
import csc_cccix.geocracy.Global;
import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.game.ui_states.IGameplayState;
import csc_cccix.geocracy.game.ui_states.SelectDefenseState;
import csc_cccix.geocracy.game.view_models.GameViewModel;
import csc_cccix.geocracy.space.SpaceRenderer;
import csc_cccix.geocracy.game.ui_states.GameEvent;
import csc_cccix.geocracy.world.Territory;
import csc_cccix.geocracy.world.World;
import glm_.vec2.Vec2;
import glm_.vec2.Vec2i;
import glm_.vec3.Vec3;

import static csc_cccix.geocracy.game.ui_states.GameAction.CANCEL_TAPPED;
import static csc_cccix.geocracy.game.ui_states.GameAction.TERRITORY_SELECTED;

public class Game implements Serializable {

    // IF CHANGING INSTANCE VARIABLES, INCREMENT serialVersionUID !!!
    private static final long serialVersionUID = 0L; // INCREMENT IF INSTANCE VARIABLES ARE CHANGED

    public static final String TAG = "GAME";
    public static final int MAX_N_PLAYERS = 8;
    public static final int MIN_N_PLAYERS = 2;
    public static final int DEFAULT_N_PLAYERS = 4;
    public static final int MAX_ARMIES_PER_TERRITORY = 25;
    public static final String USER_ACTION = "USER_ACTION";
    public static final String SAVE_FILE_NAME = "save";
    public static final float TAP_DISTANCE_THRESHOLD = 10.0f;

    // IF CHANGING INSTANCE VARIABLES, INCREMENT serialVersionUID !!!
    private World world;
    public GameData gameData;

    private long lastT; // Time of the previous game update / frame relative to the start of the game

    private GameStateMachine StateMachine;

    private transient GameActivity activity;
    public transient GameUI UI; // User Interface
    private GameViewModel gameViewModel;
    public transient Notifications Notifications;

    private transient SpaceRenderer spaceRenderer;
    private transient CameraController cameraController;

    private transient int idFBHandle;
    private transient int idValueTexHandle;
    private transient int idDepthRBHandle;
    private transient ByteBuffer readbackBuffer;

    private transient Vec2i screenSize;
    private transient Vec2i swipeDelta;
    private transient float swipeDistance;
    private transient Vec2i tapDownPoint;
    private transient Vec2i tapUpPoint;
    private transient float zoomFactor;

    private transient long lastTimestamp;
    private transient float cooldown;

    private transient FragmentManager manager;


    public Game(GameActivity activity, String playerName, int nPlayers, Vec3 mainPlayerColor, long seed) {
        this.activity = activity;
        world = new World(this, seed);


        Player[] players = new Player[nPlayers];
        Vec3[] playerColors = Util.genDistinctColors(players.length, Util.getHue(mainPlayerColor));
        players[0] = new HumanPlayer(playerName,1, playerColors[0]);
        for (int i = 1; i < players.length; ++i) {
            players[i] = new AIPlayer(i + 1, playerColors[i]);
        }

        gameViewModel = ViewModelProviders.of(activity).get(GameViewModel.class);
        gameData = new GameData(this, players);

        lastT = 0;

        manager = activity.getSupportFragmentManager();

        UI = new GameUI(activity, manager);
        Notifications = new Notifications(this);

        // Create New State Machine Implementation and Start it
        StateMachine = new GameStateMachine(this);
        StateMachine.Start();

        spaceRenderer = new SpaceRenderer();
        cameraController = new CameraController();
        readbackBuffer = ByteBuffer.allocateDirect(1);

        EventBus.subscribe("CAMERA_ZOOM_EVENT", this, e -> wasZoom((float)e));
        EventBus.subscribe(USER_ACTION, this, event -> StateMachine.HandleEvent((GameEvent) event));

        lastTimestamp = System.nanoTime();

        UI.showCurrentPlayerFragment();

    }


    // GETTERS
    public GameActivity getActivity() { return activity; }
    public GameStateMachine getStateMachine() { return StateMachine; }
    public CameraController getCameraController() { return cameraController; }
    public World getWorld() { return world; }
    public GameData getGameData() { return gameData; }
    public GameViewModel getGameViewModel() { return gameViewModel; }


    public Player getControllingPlayer() {
        // If a defense needs to be selected, set controlling player to defending territory owner
        if (StateMachine.CurrentState() instanceof SelectDefenseState) {
            SelectDefenseState selectDefenseState = (SelectDefenseState) StateMachine.CurrentState();
            return selectDefenseState.getDefendingTerritory().getOwner();
        } else {
            return gameData.getCurrentPlayer();
        }
    }

    public boolean currentPlayerIsHuman() {
        return gameData.getCurrentPlayer() instanceof HumanPlayer;
    }

    // Increments current player index
    public void nextPlayer() {
        boolean wasHuman = currentPlayerIsHuman();
        gameData.nextPlayerIndex();
//        UI.updateCurrentPlayerFragment();

        //  if that last player was a human player and the new current player is an AI, set cooldown time... (is this neeeded?)
        if (wasHuman && gameData.getCurrentPlayer() instanceof AIPlayer) cooldown = 1.0f;
    }

    public void setFirstPlayer(){
        gameData.setFirstPlayer();
//        UI.showCurrentPlayerFragment();
//        UI.updateCurrentPlayerFragment();
    }

    // The core game logic
    private void update(long t, float dt) {
        if (getControllingPlayer() instanceof HumanPlayer) {
            handleInput();
        }
        else {
            if (cooldown <= 0.0f) {
                handleComputerInput();
                cooldown = 1.0f;
            }
            cooldown -= dt;
        }

        cameraController.update(dt);
    }

    public void setupFromLoad(GameActivity activity) {
        this.activity = activity;
        this.manager = activity.getSupportFragmentManager();
        this.UI = new GameUI(this.activity, this.manager);
        this.spaceRenderer = new SpaceRenderer();
        this.cameraController = new CameraController();
        this.readbackBuffer = ByteBuffer.allocateDirect(1);

        EventBus.subscribe("CAMERA_ZOOM_EVENT", this, e -> wasZoom((float)e));
        EventBus.subscribe(USER_ACTION, this, event -> StateMachine.HandleEvent((GameEvent) event));

        this.lastTimestamp = System.nanoTime();

        StateMachine.Advance(StateMachine.previousState);
//        StateMachine.currentState.InitializeState();
    }

    public static boolean saveGame(Game game) {
        try {
            FileOutputStream fos = Global.getContext().openFileOutput(SAVE_FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(game);
            oos.close();
            fos.close();
            return true;
        } catch (IOException e) {
            Log.e("", Log.getStackTraceString(e));
            return false;
        }
    }

    public static Game loadGame() {
        try {
            FileInputStream fis = Global.getContext().openFileInput(SAVE_FILE_NAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Game game = (Game)ois.readObject();
            ois.close();
            fis.close();
            return game;
        } catch (IOException | ClassNotFoundException e) {
            Log.e("", Log.getStackTraceString(e));
            return null;
        }
    }

    public static boolean isSavedGame() {
        return Arrays.binarySearch(Global.getContext().fileList(), SAVE_FILE_NAME) >= 0;
    }

    // Called during deserialization
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
//        constructTransient();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
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
        long currentTimestamp = System.nanoTime();
        long deltaT = currentTimestamp - lastTimestamp;
        long t = lastT + deltaT;
        float dt = (float)deltaT * 1e-9f;

        update(t, dt);
        render(t, dt);

        lastT = t;
        lastTimestamp = currentTimestamp;
    }

    public void screenResized(Vec2i size) {
        screenSize = size;

        if (!reloadIdFrameBuffer()) {
            Log.e("", "Failed to reload frame buffer");
        }

        GLES30.glViewport(0, 0, screenSize.x, screenSize.y);
        cameraController.getCamera().setAspectRatio((float)screenSize.x / (float)screenSize.y);
    }

    /* TOUCH INPUT HANDLING */

    public void wasTapDown(Vec2i p) {
        synchronized (this) {
            tapDownPoint = p;
            tapUpPoint = null;
            swipeDistance = 0.0f;
        }
    }

    public void wasTapUp(Vec2i p) {
        synchronized (this) {
            tapUpPoint = p;
        }
    }

    public void wasSwipe(Vec2i d) {
        synchronized (this) {
            swipeDelta = d;
            swipeDistance += d.x * d.x + d.y * d.y;
        }
    }

    public void wasZoom(float factor) {
        synchronized (this) {
            zoomFactor = factor;
        }
    }

    // Handles AI Input
    private void handleComputerInput() {
        IGameplayState currentState = (IGameplayState) StateMachine.CurrentState();
        AIPlayer.handleComputerInputWithState(this, currentState); // TODO: will probably want to change this to non static method as different AI could have different behaviors then...
    }

    // Handles User Input
    private void handleInput() {
        synchronized (this) {
            if (swipeDelta != null && (swipeDelta.x != 0 || swipeDelta.y != 0)) {
                Vec2 delta = new Vec2(swipeDelta);
                delta.timesAssign(-1.0f);
                cameraController.move(delta);
                swipeDelta = null;
            }
            if (tapDownPoint != null && tapUpPoint != null) {
                if (swipeDistance <= TAP_DISTANCE_THRESHOLD) {
                    GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, idFBHandle);
                    GLES30.glReadPixels(tapDownPoint.x, screenSize.y - tapDownPoint.y - 1, 1, 1, GLES30.GL_RED_INTEGER, GLES30.GL_UNSIGNED_BYTE, readbackBuffer);
                    int downTerrId = readbackBuffer.get(0);
                    GLES30.glReadPixels(tapUpPoint.x, screenSize.y - tapUpPoint.y - 1, 1, 1, GLES30.GL_RED_INTEGER, GLES30.GL_UNSIGNED_BYTE, readbackBuffer);
                    int upTerrId = readbackBuffer.get(0);
                    GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
                    if (downTerrId == upTerrId && upTerrId > 0) {
                        Territory terr = world.getTerritory(upTerrId);
                        EventBus.publish(USER_ACTION, new GameEvent(TERRITORY_SELECTED, terr));
                    }
                    else {
                        EventBus.publish(USER_ACTION, new GameEvent(CANCEL_TAPPED, null));
                    }
                }
                tapDownPoint = null;
            }
            tapUpPoint = null;
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
        world.render(t, dt, cameraController.getCamera(), lightDir, spaceRenderer.getCubemapHandle());
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

}