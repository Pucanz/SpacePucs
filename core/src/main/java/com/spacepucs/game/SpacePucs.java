package com.spacepucs.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.Iterator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;


public class SpacePucs extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture background, tSpaceship, tFireball, tEnemy;
    private Sprite spaceShip, fireball;
    private Menu menu;
    private boolean isMenuActive;
    private float posX, posY, velocity, xFireball, yFireball;
    private boolean attack, gameOver;
    private Array<Rectangle> enemies;
    private long lastEnemyTime;
    private int score, lives, numEnemies;
    private HighScoreManager highScoreManager;
    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private BitmapFont bitmap;

    @Override
    public void create() {
        batch = new SpriteBatch();
        menu = new Menu();
        isMenuActive = true;
        highScoreManager = new HighScoreManager();

        background = new Texture("background.png");
        tSpaceship = new Texture("Spaceship.png");
        spaceShip = new Sprite(tSpaceship);
        posX = 0;
        posY = 0;
        velocity = 10;

        tFireball = new Texture("Fireball.png");
        fireball = new Sprite(tFireball);
        xFireball = spaceShip.getWidth() / 2;
        yFireball = spaceShip.getHeight() / 2;
        attack = false;

        tEnemy = new Texture("Enemy.png");
        enemies = new Array<Rectangle>();
        lastEnemyTime = 0;
        score = 0;
        lives = 3;
        numEnemies = 799999999;

        generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        parameter.borderWidth = 1;
        parameter.borderColor = Color.BLACK;
        parameter.color = Color.WHITE;
        bitmap = generator.generateFont(parameter);
        gameOver = false;

    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        if (isMenuActive) {
            menu.render(batch);
            if (menu.isStartGame()) {
                isMenuActive = false;
                initializeGame();
            }
        } else {
            batch.draw(background, 0, 0);
            if (!gameOver) {
                moveSpaceship();
                moveFireball();
                moveEnemies();
                if (attack) {
                    batch.draw(fireball, xFireball + spaceShip.getWidth() / 2, yFireball);
                }
                batch.draw(spaceShip, posX, posY);
                for (Rectangle enemy : enemies) {
                    batch.draw(tEnemy, enemy.x, enemy.y);
                }
                bitmap.draw(batch, "Score: " + score, Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() - 20);
                bitmap.draw(batch, "High Score: " + highScoreManager.getHighScore(), 20, Gdx.graphics.getHeight() - 20);
                bitmap.draw(batch, "Lives: " + lives, Gdx.graphics.getWidth() - 150, Gdx.graphics.getHeight() - 20);
            } else {
                bitmap.draw(batch, "GAME OVER", 540, 360);
                highScoreManager.updateHighScore(score);
                if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
                    resetGame();
                }
            }
        }
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        tSpaceship.dispose();
        tFireball.dispose();
        tEnemy.dispose();
        menu.dispose();
        if (bitmap != null) {
            bitmap.dispose();
        }
        if (generator != null) {
            generator.dispose();
        }
    }

    private void moveSpaceship() {
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (posX < Gdx.graphics.getWidth() - spaceShip.getRegionWidth()) {
                posX += velocity;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (posX > 0) {
                posX -= velocity;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if (posY < Gdx.graphics.getHeight() - spaceShip.getHeight()) {
                posY += velocity;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            if (posY > 0) {
                posY -= velocity;
            }
        }
    }

    private void moveFireball() {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && !attack) {
            attack = true;
            yFireball = posY + (spaceShip.getHeight() / 2 - fireball.getHeight() / 2);
        }
        if (attack) {
            if (xFireball < Gdx.graphics.getWidth()) {
                xFireball += 40;
            } else {
                xFireball = posX;
                attack = false;
            }
        } else {
            xFireball = posX;
        }
    }

    private void spawnEnemies() {
        Rectangle enemy = new Rectangle(Gdx.graphics.getWidth(), MathUtils.random(0, Gdx.graphics.getHeight() - tEnemy.getHeight()), tEnemy.getWidth(), tEnemy.getHeight());
        enemies.add(enemy);
        lastEnemyTime = TimeUtils.nanoTime();
    }

    private void moveEnemies() {
        if (TimeUtils.nanoTime() - lastEnemyTime > numEnemies)
            this.spawnEnemies();

        for (Iterator<Rectangle> iter = enemies.iterator(); iter.hasNext(); ) {
            Rectangle enemy = iter.next();
            enemy.x -= 400 * Gdx.graphics.getDeltaTime();

            // Collide with the fireball
            if (collide(enemy.x, enemy.y, enemy.width, enemy.height, xFireball, yFireball, fireball.getWidth(), fireball.getHeight()) && attack) {
                ++score;
                if (score % 10 == 0) {
                    numEnemies -= 10000;
                }
                //System.out.println("Score: " + ++score);
                attack = false;
                iter.remove();

                // Collide with the spaceship
            } else if (collide(enemy.x, enemy.y, enemy.width, enemy.height, posX, posY, spaceShip.getWidth(), spaceShip.getHeight()) && !gameOver) {
                --lives;
                if (lives <= 0) {
                    gameOver = true;
                }
                //System.out.println("Collide");
                iter.remove();
            }
            if (enemy.x + tEnemy.getWidth() < 0) {
                iter.remove();
            }
        }
    }

    private boolean collide(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2) {
        boolean xOverlap = x1 < x2 + w2 && x1 + w1 > x2;
        boolean yOverlap = y1 < y2 + h2 && y1 + h1 > y2;

        return xOverlap && yOverlap;
    }

    private void initializeGame() {
        score = 0;
        lives = 3;
        posX = 0;
        posY = 20;
        attack = false;
        enemies.clear();
        gameOver = false;
    }

    private void resetGame() {
        score = 0;
        lives = 3;
        posX = 0;
        posY = 20;
        attack = false;
        enemies.clear();
        gameOver = false;
    }
}
