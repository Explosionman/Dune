package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.units.Owner;
import com.dune.game.core.units.TargetType;

public class MainBase extends GameObject implements Targetable {
    private TextureRegion progressbarTexture;
    private TextureRegion baseTexture;
    private Owner owner;
    private int hpMax;
    private int hp;
    private int resources;


    public MainBase(GameController gc) {
        super(gc);
        this.baseTexture = Assets.getInstance().getAtlas().findRegion("base");
        this.hpMax = 1000;
        this.hp = hpMax;
        this.resources = 1000;
    }

    public Owner getOwner() {
        return owner;
    }


    @Override
    public TargetType getType() {
        return TargetType.BUILDING;
    }

    public void setup(Owner owner, float x, float y) {
        this.owner = owner;
        this.position.set(x, y);
        this.progressbarTexture = Assets.getInstance().getAtlas().findRegion("progressbar");
    }

    public void render(SpriteBatch batch) {
        batch.draw(baseTexture, position.x - 128.0f, position.y - 128.0f);
        if (hp < hpMax) {
            batch.setColor(0.2f, 0.2f, 0.0f, 1.0f);
            batch.draw(progressbarTexture, position.x + 64, position.y + 180, 130, 12);
            batch.setColor(0.0f, 1.0f, 0.0f, 1.0f);
            float percentage = (float) hp / hpMax;
            batch.draw(progressbarTexture, position.x + 64, position.y + 182, 130 * percentage, 8);
            batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

   // public void take
}
