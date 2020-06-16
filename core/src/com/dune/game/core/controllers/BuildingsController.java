package com.dune.game.core.controllers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dune.game.core.Building;
import com.dune.game.core.GameController;
import com.dune.game.core.units.types.Owner;
import com.dune.game.core.utils.ObjectPool;
import com.dune.game.core.users_logic.BaseLogic;

public class BuildingsController extends ObjectPool<Building> {
    private GameController gc;

    @Override
    protected Building newObject() {
        return new Building(gc);
    }

    public BuildingsController(GameController gc) {
        this.gc = gc;
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).render(batch);
        }
    }

    public void setup(int cellX, int cellY, BaseLogic ownerLogic) {
        Building b = activateObject();
        b.setup(ownerLogic, cellX, cellY);
    }

    public void update(float dt) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt);
        }
        checkPool();
    }

    public Building getAiBase() {
        for (int i = 0; i < activeList.size(); i++) {
            if (activeList.get(i).getOwnerLogic().getOwnerType() == Owner.AI && activeList.get(i).getBuildingType() == Building.Type.STOCK) {
                Building b = activeList.get(i);
                return b;
            }
        }
        return null;
    }
}
