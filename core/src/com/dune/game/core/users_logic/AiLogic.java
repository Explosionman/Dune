package com.dune.game.core.users_logic;

import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.GameController;
import com.dune.game.core.gui.GuiAiInfo;
import com.dune.game.core.units.AbstractUnit;
import com.dune.game.core.units.BattleTank;
import com.dune.game.core.units.Harvester;
import com.dune.game.core.units.types.Owner;
import com.dune.game.core.units.types.UnitType;

import java.util.ArrayList;
import java.util.List;

public class AiLogic extends BaseLogic {
    private static class ResourceCell {
        private int x, y;
        private boolean isBlocked;
        private boolean isHaveResources;

        public ResourceCell(int x, int y) {
            this.x = x;
            this.y = y;
            this.isBlocked = false;
            this.isHaveResources = false;
        }
    }


    private float timer;
    private Vector2 temp;
    private Vector2 temp2;
    private List<BattleTank> tmpAiBattleTanks;
    private List<Harvester> tmpAiHarvesters;
    private List<Harvester> tmpPlayerHarvesters;
    private List<BattleTank> tmpPlayerBattleTanks;
    private ResourceCell[][] cells;
    GuiAiInfo aiInfo;

    public AiLogic(GameController gc, GuiAiInfo aiInfo) {
        this.gc = gc;
        this.aiInfo = aiInfo;
        this.money = 1000;
        this.unitsCount = 10;
        this.unitsMaxCount = 100;
        this.ownerType = Owner.AI;
        this.tmpAiBattleTanks = new ArrayList<>();
        this.tmpAiHarvesters = new ArrayList<>();
        this.tmpPlayerHarvesters = new ArrayList<>();
        this.tmpPlayerBattleTanks = new ArrayList<>();
        this.timer = 10000.0f;
        this.temp = new Vector2();
        this.temp2 = new Vector2();
        this.cells = new ResourceCell[gc.getMap().COLUMNS_COUNT][gc.getMap().ROWS_COUNT];
        scanCells();
    }

    public void update(float dt) {
        timer += dt;
        if (timer > 2.0f) {
            timer = 0.0f;
            gc.getUnitsController().collectTanks(tmpAiBattleTanks, gc.getUnitsController().getAiUnits(), UnitType.BATTLE_TANK);
            gc.getUnitsController().collectTanks(tmpAiHarvesters, gc.getUnitsController().getAiUnits(), UnitType.HARVESTER);
            gc.getUnitsController().collectTanks(tmpPlayerHarvesters, gc.getUnitsController().getPlayerUnits(), UnitType.HARVESTER);
            gc.getUnitsController().collectTanks(tmpPlayerBattleTanks, gc.getUnitsController().getPlayerUnits(), UnitType.BATTLE_TANK);
            for (int i = 0; i < tmpAiBattleTanks.size(); i++) {
                BattleTank aiBattleTank = tmpAiBattleTanks.get(i);
                aiBattleTank.commandAttack(findNearestTarget(aiBattleTank, tmpPlayerBattleTanks));
            }
            for (int i = 0; i < tmpAiHarvesters.size(); i++) {
                Harvester aiHarvester = tmpAiHarvesters.get(i);
                if (gc.getMap().getResourceCount(aiHarvester.getPosition()) == 0) {
                    scanCells();
                    collectNearestResources(aiHarvester);
                }
                if (aiHarvester.getContainer() == aiHarvester.getContainerCapacity()) {
                    goToBase(aiHarvester);
                }
            }
        }
    }

    public void scanCells() {
        for (int i = 0; i < gc.getMap().COLUMNS_COUNT; i++) {
            for (int j = 0; j < gc.getMap().ROWS_COUNT; j++) {
                this.cells[i][j] = new ResourceCell(i, j);
                if (gc.getMap().getResourceCount(temp.set(i * gc.getMap().CELL_SIZE, j * gc.getMap().CELL_SIZE)) > 0) {
                    this.cells[i][j].isHaveResources = true;
                }
            }
        }
    }


    public <T extends AbstractUnit> T findNearestTarget(AbstractUnit currentTank, List<T> possibleTargetList) {
        T target = null;
        float minDist = 1000000.0f;
        for (int i = 0; i < possibleTargetList.size(); i++) {
            T possibleTarget = possibleTargetList.get(i);
            float currentDst = currentTank.getPosition().dst(possibleTarget.getPosition());
            if (currentDst < minDist) {
                target = possibleTarget;
                minDist = currentDst;
            }
        }
        return target;
    }

    public void collectNearestResources(Harvester currentTank) {
        float minDist = 1000000.0f;
        for (int i = 0; i < gc.getMap().COLUMNS_COUNT; i++) {
            for (int j = 0; j < gc.getMap().ROWS_COUNT; j++) {
                if (cells[i][j].isHaveResources) {
                    float currentDst = currentTank.getPosition().dst(temp.set(i * gc.getMap().CELL_SIZE, j * gc.getMap().CELL_SIZE));
                    if (currentDst < minDist && !cells[i][j].isBlocked) {
                        temp.set(i * gc.getMap().CELL_SIZE, j * gc.getMap().CELL_SIZE);
                        minDist = currentDst;
                        cells[i][j].isBlocked = true;
                        currentTank.commandMoveTo(temp, false);
                    }
                }
            }
        }
    }

    public void goToBase(Harvester currentTank) {
        float dst = gc.getBuildingsController().getAiBase().getPosition().dst(currentTank.getPosition());
        currentTank.commandMoveTo(gc.getBuildingsController().getAiBase().getPosition(), false);
        if (dst < 160.0f) {
            float colLengthD2 = (165 - dst) / 2;
            temp.set(currentTank.getPosition()).sub(gc.getBuildingsController().getAiBase().getPosition()).nor().scl(colLengthD2);
            temp.scl(-1);
            currentTank.moveBy(temp);
        }
    }
}

