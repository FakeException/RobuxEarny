package com.robuxearny.official.games.maze.actor;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.robuxearny.official.games.maze.BaseActor;

public class Coin extends BaseActor {

    public Coin(float x, float y, Stage stage, float roomWidth, float roomHeight) {
        super(x, y, stage);
        loadTexture("bobux.png");
        float hitboxWidth = getWidth() / 2f; // Half the width
        float hitboxHeight = getHeight() / 2f; // Half the height
        setBoundaryPolygon(6, hitboxWidth, hitboxHeight); // Pass reduced width and
        setSize(roomWidth / 2f, roomHeight / 4f);
    }
}
