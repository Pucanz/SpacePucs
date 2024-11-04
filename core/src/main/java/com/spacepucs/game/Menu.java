package com.spacepucs.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import java.awt.*;

public class Menu {

    private Texture background;
    private BitmapFont font;
    private Rectangle startButton;
    private Rectangle exitButton;
    private boolean startGame;

    public Menu() {
        background = new Texture("background.png");

        // Buttons fonts
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 42;
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 2;
        parameter.color = Color.WHITE;

        font = generator.generateFont(parameter);
        generator.dispose();

        // Buttons rectangles
        startButton = new Rectangle(500, 360, 200, 50);
        exitButton = new Rectangle(500, 280, 200, 50);

        startGame = false;

    }

    public void render(SpriteBatch batch) {

        // Draw background and buttons
        batch.draw(background,0, 0);
        font.draw(batch, "Start Game", startButton.x + 20, startButton.y + startButton.height - 10);
        font.draw(batch, "Exit", exitButton.x + 80, exitButton.y + exitButton.height - 10);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            // Check if the mouse click on start button
            if (startButton.contains(mouseX, mouseY)) {
                startGame = true;
            }
            // Check if the mouse click on exit button
            else if (exitButton.contains(mouseX, mouseY))
                Gdx.app.exit();;
        }
    }

    public boolean isStartGame() {
        return startGame;
    }

    public void dispose() {
        background.dispose();
        if (font != null) {
            font.dispose();
        }
    }

}
