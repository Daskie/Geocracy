package csc_cccix.geocracy.world;

import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.graphics.Camera;
import csc_cccix.geocracy.graphics.Mesh;
import csc_cccix.geocracy.graphics.MeshMaker;
import glm_.mat3x3.Mat3;
import glm_.vec3.Vec3;

public class ArmyRenderer {

    private World world;
    private ArmyShader shader;
    private Mesh mesh;
    private int instanceVBOHandle;
    private int nArmies;
    private ByteBuffer[][] armyData;


    public ArmyRenderer(World world) {
        this.world = world;
        shader = new ArmyShader();
        mesh = MeshMaker.makeCube("Army");
        mesh.translate(new Vec3(0.0f, 0.0f, 1.0f));
        mesh.unindex();
        genArmyData();
    }

    public boolean load() {
        unload();

        if (!shader.load()) {
            Log.e("", "Failed to load shader");
        }
        shader.setActive();
        shader.setPlayerColors(world.game.getPlayers());

        if (!mesh.load()) {
            Log.e("", "Failed to load mesh");
        }

        // Create instance vbo
        int[] instanceVBOHandleArr = { 0 };
        GLES30.glGenBuffers(1, instanceVBOHandleArr, 0);
        instanceVBOHandle = instanceVBOHandleArr[0];
        if (instanceVBOHandle == 0) {
            Log.e("", "Failed to generate instance vbo");
            return false;
        }
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, instanceVBOHandle);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, world.getNTerritories() * Game.MAX_ARMIES_PER_TERRITORY * (3 * 4 + 3 * 3 * 4 + 4), null, GLES30.GL_DYNAMIC_DRAW);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        // Check for OpenGL errors
        if (Util.isGLError()) {
            Log.e("", "Failed to update vao");
            return false;
        }

        // Update VAO
        int vaoHandle = mesh.getVAOHandle();
        GLES30.glBindVertexArray(vaoHandle);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, instanceVBOHandle);
        GLES30.glEnableVertexAttribArray(2);
        GLES30.glEnableVertexAttribArray(3);
        GLES30.glEnableVertexAttribArray(4);
        GLES30.glEnableVertexAttribArray(5);
        GLES30.glEnableVertexAttribArray(6);
        int instanceSize = 3 * 4 + 3 * 3 * 4 + 4;
        int offset = 0;
        GLES30.glVertexAttribPointer(2, 3, GLES30.GL_FLOAT, false, instanceSize, offset); // location
        offset += 3 * 4;
        GLES30.glVertexAttribPointer(3, 3, GLES30.GL_FLOAT, false, instanceSize, offset); // orientation
        offset += 3 * 4;
        GLES30.glVertexAttribPointer(4, 3, GLES30.GL_FLOAT, false, instanceSize, offset); // orientation continued
        offset += 3 * 4;
        GLES30.glVertexAttribPointer(5, 3, GLES30.GL_FLOAT, false, instanceSize, offset); // orientation continued
        offset += 3 * 4;
        GLES30.glVertexAttribIPointer(6, 1, GLES30.GL_INT, instanceSize, offset); // player
        offset += 4;
        GLES30.glVertexAttribDivisor(2, 1);
        GLES30.glVertexAttribDivisor(3, 1);
        GLES30.glVertexAttribDivisor(4, 1);
        GLES30.glVertexAttribDivisor(5, 1);
        GLES30.glVertexAttribDivisor(6, 1);
        GLES30.glBindVertexArray(0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glVertexAttribDivisor(2, 0);
        GLES30.glVertexAttribDivisor(3, 0);
        GLES30.glVertexAttribDivisor(4, 0);
        GLES30.glVertexAttribDivisor(5, 0);
        GLES30.glVertexAttribDivisor(6, 0);
        GLES30.glDisableVertexAttribArray(2);
        GLES30.glDisableVertexAttribArray(3);
        GLES30.glDisableVertexAttribArray(4);
        GLES30.glDisableVertexAttribArray(5);
        GLES30.glDisableVertexAttribArray(6);
        // Check for OpenGL errors
        if (Util.isGLError()) {
            Log.e("", "Failed to update vao");
            return false;
        }

        return true;
    }

    public void render(Camera camera, Vec3 lightDir, boolean armyChange, boolean ownerChange) {
        if (armyChange || ownerChange) {
            refresh();
        }
        shader.setActive();
        shader.setViewMatrix(camera.getViewMatrix());
        shader.setProjectionMatrix(camera.getProjectionMatrix());
        shader.setCameraLocation(camera.getLocation());
        shader.setLightDirection(lightDir);
        GLES30.glBindVertexArray(mesh.getVAOHandle());
        GLES30.glDrawArraysInstanced(GLES30.GL_TRIANGLES, 0, mesh.getNumVertices(), nArmies);
        GLES30.glBindVertexArray(0);
    }

    public void unload() {
        shader.unload();
        mesh.unload();
        if (instanceVBOHandle != 0) {
            GLES30.glDeleteBuffers(1, new int[]{ instanceVBOHandle }, 0);
        }
    }

    private void genArmyData() {
        armyData = new ByteBuffer[world.getNTerritories()][];
        for (int ti = 0; ti < world.getNTerritories(); ++ti) {
            armyData[ti] = new ByteBuffer[Game.MAX_ARMIES_PER_TERRITORY];
            Territory terr = world.getTerritories()[ti];
            Vec3[] armyLocations = world.getTerrain().getTerritoryArmyLocations(terr.getId());
            Mat3[] armyOrientations = world.getTerrain().getTerritoryArmyOrientations(terr.getId());
            for (int ai = 0; ai < Game.MAX_ARMIES_PER_TERRITORY; ++ai) {
                ByteBuffer bb = ByteBuffer.allocateDirect(3 * 4 + 3 * 3 * 4);
                bb.order(ByteOrder.nativeOrder());
                Vec3 location = armyLocations[ai];
                Mat3 orientation = armyOrientations[ai];
                bb.putFloat(location.x);
                bb.putFloat(location.y);
                bb.putFloat(location.z);
                bb.putFloat(orientation.get(0, 0));
                bb.putFloat(orientation.get(0, 1));
                bb.putFloat(orientation.get(0, 2));
                bb.putFloat(orientation.get(1, 0));
                bb.putFloat(orientation.get(1, 1));
                bb.putFloat(orientation.get(1, 2));
                bb.putFloat(orientation.get(2, 0));
                bb.putFloat(orientation.get(2, 1));
                bb.putFloat(orientation.get(2, 2));
                bb.flip();
                armyData[ti][ai] = bb;
            }
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
        ByteBuffer bb = ByteBuffer.allocateDirect(nArmies * (3 * 4 + 3 * 3 * 4 + 4));
        bb.order(ByteOrder.nativeOrder());

        for (int ti = 0; ti < world.getNTerritories(); ++ti) {
            Territory terr = world.getTerritories()[ti];
            int playerID = terr.hasOwner() ? terr.getOwner().getId() : 0;
            for (int ai = 0; ai < terr.getNArmies(); ++ai) {
                bb.put(armyData[ti][ai]);
                armyData[ti][ai].flip();
                bb.putInt(playerID);
            }
        }

        bb.flip();
        return bb;
    }
}
