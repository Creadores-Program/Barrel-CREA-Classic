package org.barrelmc.barrel.player;

import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;

public class Entity {
    public final String name;
    public final long rEId;
    public final String type;
    public volatile float x, y, z;
    public volatile float yaw, pitch;
    public Entity(String name, long rEId, String type, Vector3f pos, Vector2f rot){
        this.name = name;
        this.rEId = rEId;
        this.type = type;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.yaw = rot.getX();
        this.pitch = rot.getY();
    }
}
