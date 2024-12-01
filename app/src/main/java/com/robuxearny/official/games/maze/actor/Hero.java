package com.robuxearny.official.games.maze.actor;

import static com.badlogic.gdx.Gdx.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.robuxearny.official.games.maze.BaseActor;

public class Hero extends BaseActor {

    Animation north;
    Animation south;
    Animation east;
    Animation west;

    private final float animationScale;
    private float roomWidth, roomHeight; // Add room dimensions

    public Hero(float x, float y, Stage s, float roomWidth, float roomHeight) { // Add room dimensions to constructor

        super(x, y, s);
        this.roomWidth = roomWidth;
        this.roomHeight = roomHeight;

        String fileName = "maze/hero.png";
        Texture texture = new Texture(Gdx.files.internal(fileName), true);

        // Calculate animation scale based on room dimensions
        animationScale = Math.min(roomWidth, roomHeight) / texture.getWidth(); // Adjust scaling factor as needed

        int rows = 4;
        int cols = 3;
        int frameWidth = texture.getWidth() / cols;
        int frameHeight = texture.getHeight() / rows;
        TextureRegion[][] temp = TextureRegion.split(texture, frameWidth, frameHeight);

        Array<TextureRegion> textureArray = new Array<>();

        float frameDuration = 0.2f;

        for (int c = 0; c < cols; c++)
            textureArray.add(temp[0][c]);
        south = new Animation(frameDuration, textureArray, Animation.PlayMode.LOOP_PINGPONG);

        textureArray.clear();
        for (int c = 0; c < cols; c++)
            textureArray.add(temp[1][c]);
        west = new Animation(frameDuration, textureArray, Animation.PlayMode.LOOP_PINGPONG);

        textureArray.clear();
        for (int c = 0; c < cols; c++)
            textureArray.add(temp[2][c]);
        east = new Animation(frameDuration, textureArray, Animation.PlayMode.LOOP_PINGPONG);

        textureArray.clear();
        for (int c = 0; c < cols; c++)
            textureArray.add(temp[3][c]);
        north = new Animation(frameDuration, textureArray, Animation.PlayMode.LOOP_PINGPONG);

        setAnimation(south);

        // set after animation established
        setBoundaryPolygon(8, getWidth(), getHeight()); // Pass width and height

        float maxSpeed = 110 * Math.max(roomWidth, roomHeight) / 100f; // Adjust scaling factor as needed

        setAcceleration(800);
        setMaxSpeed(maxSpeed);
        setDeceleration(800);
        // Adjust size based on room dimensions
        setSize(roomWidth / 2f * animationScale, roomHeight / 2f * animationScale); // Adjust scaling factor as needed
    }

    public void act(float deltaTime) {

        super.act(deltaTime);

        // hero movement controls
        if (input.isKeyPressed(Keys.LEFT)) accelerateAtAngle(180);
        if (input.isKeyPressed(Keys.RIGHT)) accelerateAtAngle(0);
        if (input.isKeyPressed(Keys.UP)) accelerateAtAngle(90);
        if (input.isKeyPressed(Keys.DOWN)) accelerateAtAngle(270);

        // pause animation when character not moving
        if (getSpeed() == 0) {
            setAnimationPaused(true);
        } else {
            setAnimationPaused(false);

            // set direction animation
            float angle = getMotionAngle();

            if (angle >= 45 && angle <= 135) {
                setAnimation(north);
            } else if (angle > 135 && angle < 225) {
                setAnimation(west);
            } else if (angle >= 225 && angle <= 315) {
                setAnimation(south);
            } else {
                setAnimation(east);
            }
        }

        setSize(roomWidth / 2f * animationScale, roomHeight / 2f * animationScale); // Adjust scaling factor as needed

        applyPhysics(deltaTime);
    }
}
