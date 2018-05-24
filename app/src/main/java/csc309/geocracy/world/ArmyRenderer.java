package csc309.geocracy.world;

import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import csc309.geocracy.Util;
import csc309.geocracy.game.Game;
import csc309.geocracy.graphics.Camera;
import csc309.geocracy.graphics.Mesh;
import csc309.geocracy.graphics.MeshMaker;
import glm_.vec3.Vec3;

public class ArmyRenderer {

    private World world;
    private ArmyShader shader;
    private Mesh mesh;
    private int instanceVBOHandle;
    private int nArmies;

    public ArmyRenderer(World world) {
        this.world = world;
        shader = new ArmyShader();
        mesh = MeshMaker.makeSphereIndexed("Army", 1);
    }

    public boolean load() {
        unload();

        if (!shader.load()) {
            Log.e("ArmyRenderer", "Failed to load shader");
        }
        shader.setActive();
        shader.setPlayerColors(world.game.getPlayers());

        if (!mesh.load()) {
            Log.e("ArmyRenderer", "Failed to load mesh");
        }

        // Create instance vbo
        int[] instanceVBOHandleArr = { 0 };
        GLES30.glGenBuffers(1, instanceVBOHandleArr, 0);
        instanceVBOHandle = instanceVBOHandleArr[0];
        if (instanceVBOHandle == 0) {
            Log.e("ArmyRenderer", "Failed to generate instance vbo");
            return false;
        }
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, instanceVBOHandle);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, world.getNTerritories() * Game.MAX_ARMIES_PER_TERRITORY * (3 * 4 + 4), null, GLES30.GL_DYNAMIC_DRAW);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        // Check for OpenGL errors
        if (Util.isGLError()) {
            Log.e("ArmyRenderer", "Failed to update vao");
            return false;
        }

        // Update VAO
        int vaoHandle = mesh.getVAOHandle();
        GLES30.glBindVertexArray(vaoHandle);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, instanceVBOHandle);
        GLES30.glEnableVertexAttribArray(2);
        GLES30.glEnableVertexAttribArray(3);
        int instanceSize = 3 * 4 + 4;
        GLES30.glVertexAttribPointer(2, 3, GLES30.GL_FLOAT, false, instanceSize, 0); // location
        GLES30.glVertexAttribIPointer(3, 1, GLES30.GL_INT, instanceSize, 3 * 4); // player
        GLES30.glVertexAttribDivisor(2, 1);
        GLES30.glVertexAttribDivisor(3, 1);
        GLES30.glBindVertexArray(0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glVertexAttribDivisor(2, 0);
        GLES30.glVertexAttribDivisor(3, 0);
        GLES30.glDisableVertexAttribArray(2);
        GLES30.glDisableVertexAttribArray(3);
        // Check for OpenGL errors
        if (Util.isGLError()) {
            Log.e("ArmyRenderer", "Failed to update vao");
            return false;
        }

        return true;
    }

    public void render(Camera camera, Vec3 lightDir, boolean armyChange) {
        if (armyChange) {
            refresh();
        }
        shader.setActive();
        shader.setViewMatrix(camera.getViewMatrix());
        shader.setProjectionMatrix(camera.getProjectionMatrix());
        shader.setCameraLocation(camera.getLocation());
        shader.setLightDirection(lightDir);
        GLES30.glBindVertexArray(mesh.getVAOHandle());
        GLES30.glDrawElementsInstanced(GLES30.GL_TRIANGLES, mesh.getNumIndices(), GLES30.GL_UNSIGNED_INT, 0, nArmies);
        GLES30.glBindVertexArray(0);
    }

    public void unload() {
        shader.unload();
        mesh.unload();
        if (instanceVBOHandle != 0) {
            GLES30.glDeleteBuffers(1, new int[]{ instanceVBOHandle }, 0);
        }
    }

    private void refresh() {
        this.nArmies = world.getTotalNArmies();
        ByteBuffer instanceData = genInstanceData();
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, instanceVBOHandle);
        GLES30.glBufferSubData(GLES30.GL_ARRAY_BUFFER, 0, instanceData.limit(), instanceData);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
    }

    private ByteBuffer genInstanceData() {
        ByteBuffer bb = ByteBuffer.allocateDirect(nArmies * (3 * 4 + 4));
        bb.order(ByteOrder.nativeOrder());

        for (Territory terr : world.getTerritories()) {
            Vec3[] armyLocations = world.getTerrain().getTerritoryArmyLocations(terr.getId());
            int playerID = terr.getOwner().getId();
            for (int li = 0; li < terr.getNArmies(); ++li) {
                Vec3 location = armyLocations[li];
                bb.putFloat(location.x);
                bb.putFloat(location.y);
                bb.putFloat(location.z);
                bb.putInt(playerID);
            }

        }

        bb.flip();
        return bb;
    }
}
