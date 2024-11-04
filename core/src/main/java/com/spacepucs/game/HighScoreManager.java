package com.spacepucs.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;


    public class HighScoreManager {

        private int highScore;

        public HighScoreManager() {
            loadHighScore();
        }

        private void loadHighScore() {
            FileHandle file = Gdx.files.local("highscore.txt");
            if (file.exists()) {
                String scoreString = file.readString().trim();
                if (!scoreString.isEmpty()) {
                    try {
                        highScore = Integer.parseInt(scoreString);
                    } catch (NumberFormatException e) {
                        highScore = 0;
                    }
                } else {
                    highScore = 0;
                }
            } else {
                highScore = 0;
            }
        }

        public int getHighScore() {
            return highScore;
        }

        public void updateHighScore(int score) {
            if (score > highScore) {
                highScore = score;
                saveHighScore();
            }
        }

        private void saveHighScore() {
            FileHandle highScoreFile = Gdx.files.local("highscore.txt");
            highScoreFile.writeString(String.valueOf(highScore), false);
        }
    }

