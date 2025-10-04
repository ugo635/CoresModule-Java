package com.me.coresmodule.utils.events.EventBus;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

/**
 * Represents an event where an entity is loaded (spawned) or unloaded (removed) in the client world.
 */
public class EntityLoadEvent {
    private final Entity entity;
    private final ClientWorld world;

    public EntityLoadEvent(Entity entity, ClientWorld world) {
        this.entity = entity;
        this.world = world;
    }

    public Entity getEntity() {
        return entity;
    }

    public ClientWorld getWorld() {
        return world;
    }
}
