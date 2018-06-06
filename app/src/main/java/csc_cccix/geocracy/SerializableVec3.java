package csc_cccix.geocracy;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import glm_.vec3.Vec3;

public class SerializableVec3 implements Serializable {

    private transient Vec3 v;

    public SerializableVec3(Vec3 v) {
        this.v = v;
    }

    public Vec3 get() {
        return v;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeFloat(v.x);
        out.writeFloat(v.y);
        out.writeFloat(v.z);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        v = new Vec3();
        v.x = in.readFloat();
        v.y = in.readFloat();
        v.z = in.readFloat();
    }

}
