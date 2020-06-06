package com.dune.game.core;

import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.units.AbstractUnit;
import com.dune.game.core.units.UnitType;

import java.util.List;

public class AiLogic {
    private GameController gc;
    private Vector2 temp;

    public AiLogic(GameController gc) {
        this.gc = gc;
        temp = new Vector2();
    }

    public void update(float dt) {
        if (!gc.getUnitsController().getAiUnits().isEmpty()) {
            for (int i = 0; i < gc.getUnitsController().getAiUnits().size(); i++) {
                AbstractUnit u = gc.getUnitsController().getAiUnits().get(i);
                if ((u.getUnitType() == UnitType.HARVESTER)) {
                    //Если в точке, где стоит танк, нет ресурсов, он едет их собирать туда, где они есть
                    if (gc.getMap().getResourceCount(u.getPosition()) < 1) {
                        u.commandMoveTo(collectResources());
                    }
                }
            }
        }
    }

    public void unitProcessing(AbstractUnit unit) {
    }

    public Vector2 collectResources() {
        List<Vector2> resources = gc.getMap().getResourcesList();
        if (!resources.isEmpty()) {
            for (int i = 0; i < resources.size(); i++) {
                if (gc.getMap().getResourceCount(resources.get(i)) > 0) {
                    temp.set(resources.get(i));
                } else continue;
            }
        }
        return temp;
    }
}

