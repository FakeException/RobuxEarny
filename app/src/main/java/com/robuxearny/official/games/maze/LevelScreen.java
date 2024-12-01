/*
 * Created by FakeException on 2024/12/01 12:13
 * Copyright (c) 2024. All rights reserved.
 * Last modified 2024/12/01 12:13
 */

package com.robuxearny.official.games.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.robuxearny.official.games.maze.actor.Coin;
import com.robuxearny.official.games.maze.actor.Ghost;
import com.robuxearny.official.games.maze.actor.Hero;


public class LevelScreen extends BaseScreen {

    Maze maze;
    Hero hero;

    Label coinsLabel;
    Label messageLabel;

    Sound coinSound;
    Music windMusic;

    private float roomWidth, roomHeight; // Add room dimensions

    private Touchpad touchpad;

    public void initialize() {

        BaseActor background = new BaseActor(0, 0, mainStage);
        background.loadTexture("white.png");
        background.setColor(Color.GRAY);
        background.setSize(768, 700);

        maze = new Maze(mainStage);
        roomWidth = (float) Gdx.graphics.getWidth() / maze.roomCountX;
        roomHeight = (float) Gdx.graphics.getHeight() / maze.roomCountY;

        for (BaseActor room : BaseActor.getList(mainStage, "com.robuxearny.official.games.maze.actor.Room")) {
            Coin coin = new Coin(0, 0, mainStage, roomWidth, roomHeight);
            coin.centerAtActor(room);
        }


        hero = new Hero(0, 0, mainStage, roomWidth, roomHeight);
        hero.centerAtActor(maze.getRoom(0, 0));

        // add multiple Ghosts to mainStage
        for (int i = 1; i <= 3; i++) {
            int startX = maze.roomCountX - i;
            int startY = maze.roomCountY - i;
            Ghost ghost = new Ghost(0, 0, mainStage, roomWidth, roomHeight);
            ghost.centerAtActor(maze.getRoom(startX, startY));
            ghost.speed = ghost.speed + (i * 5);
            ghost.toFront();
        }

        coinsLabel = new Label("Coins left:", BaseMazeGame.labelStyle);
        coinsLabel.setColor(Color.GOLD);
        messageLabel = new Label("...", BaseMazeGame.labelStyle);
        messageLabel.setFontScale(2);
        messageLabel.setVisible(false);

        uiTable.pad(10);
        uiTable.add(coinsLabel);
        uiTable.row();
        uiTable.add(messageLabel).expandY();

        coinSound = Gdx.audio.newSound(Gdx.files.internal("maze/coin.wav"));
        windMusic = Gdx.audio.newMusic(Gdx.files.internal("maze/wind.mp3"));
        windMusic.setLooping(true);
        windMusic.setVolume(0.1f);
        windMusic.play();


        Skin touchpadSkin = new Skin();
        //Set background image
        touchpadSkin.add("touchBackground", new Texture("touchBackground.png"));
        //Set knob image
        touchpadSkin.add("touchKnob", new Texture("touchKnob.png"));
        //Create TouchPad Style
        Touchpad.TouchpadStyle touchpadStyle = new Touchpad.TouchpadStyle();
        //Apply the Drawables to the TouchPad Style
        touchpadStyle.background = touchpadSkin.getDrawable("touchBackground");
        touchpadStyle.knob = touchpadSkin.getDrawable("touchKnob");
        //Create new TouchPad with the created style
        touchpad = new Touchpad(10, touchpadStyle);
        // Calculate touchpad size and position
        float touchpadSize = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) / 4f; // 1/4 of the smaller screen dimension
        float touchpadX = Gdx.graphics.getWidth() - touchpadSize - 15; // Right edge with padding
        float touchpadY = 15; // Bottom edge with padding

        // Set touchpad bounds
        touchpad.setBounds(touchpadX, touchpadY, touchpadSize, touchpadSize);

        uiStage.addActor(touchpad);
    }


    // Game Loop
    public void update(float deltaTime) {

        if (touchpad.isTouched()) {
            float knobX = touchpad.getKnobPercentX();
            float knobY = touchpad.getKnobPercentY();

            hero.accelerateAtAngle(MathUtils.atan2(knobY, knobX) * MathUtils.radiansToDegrees);
        } else {
            // Decelerate hero when touchpad is not touched
            hero.setDeceleration(800); // Adjust deceleration as needed
        }

        for (BaseActor wall : BaseActor.getList(mainStage, "com.robuxearny.official.games.maze.actor.Wall")) {
            hero.preventOverlap(wall);
        }

        for (BaseActor coin : BaseActor.getList(mainStage, "com.robuxearny.official.games.maze.actor.Coin")) {
            if (hero.overlaps(coin)) {
                coinSound.play(0.10f);
                coin.remove();
            }
        }

        int coins = BaseActor.count(mainStage, "com.robuxearny.official.games.maze.actor.Coin");
        coinsLabel.setText("Coins left: " + coins);

        if (coins == 0) {
            for (BaseActor ghost : BaseActor.getList(mainStage, "com.robuxearny.official.games.maze.actor.Ghost")) {
                ghost.remove();
                ghost.clearActions();
                ghost.addAction(Actions.forever(Actions.delay(1)));
            }
            messageLabel.setText("You win!");
            messageLabel.setColor(Color.GREEN);
            messageLabel.setVisible(true);
            windMusic.stop();
        }

        for (BaseActor actor : BaseActor.getList(mainStage, "com.robuxearny.official.games.maze.actor.Ghost")) {

            Ghost ghost = (Ghost) actor;

            if (hero.overlaps(ghost)) {
                hero.remove();
                ghost.clearActions();
                ghost.addAction(Actions.forever(Actions.delay(1)));
                messageLabel.setText("Game Over");
                messageLabel.setColor(Color.RED);
                messageLabel.setVisible(true);
                windMusic.stop();
            }

            if (ghost.getActions().size == 0) {
                maze.resetRooms();
                ghost.findPath(maze.getRoom(ghost), maze.getRoom(hero));
            }

            if (!messageLabel.isVisible()) {
                float distance = new Vector2(hero.getX() - ghost.getX(), hero.getY() - ghost.getY()).len();
                float volume = -(distance - 64) / (300 - 64) + 1;
                volume = MathUtils.clamp(volume, 0.10f, 1.00f);
                windMusic.setVolume(volume);
            }
        }
    }

    public boolean keyDown(int keyCode) {
        if (keyCode == Input.Keys.R) {
            BaseMazeGame.setActiveScreen(new LevelScreen());
        }

        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }
}
