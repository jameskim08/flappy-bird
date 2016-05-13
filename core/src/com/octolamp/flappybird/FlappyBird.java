package com.octolamp.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;


public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	//ShapeRenderer shapeRenderer;

	Texture gameover;

	//Declaring bird-related variables
	Texture[] birds;
	int flapState = 0;
	float birdY = 0;
	float velocity = 0;
	Circle birdCircle;
	int score = 0;
	int scoringTube = 0;
	BitmapFont font;

	//Bird Animation code
	Animation birdAnimation;
	TextureRegion[] birdFrames;
	TextureRegion currentFrame;
	float stateTime;

	int gameState = 0;
	float gravity = 2;

	//Declaring tube-related variables
	Texture bottomTube;
	Texture topTube;
	float gap = 400;
	float maxTubeOffset;
	Random randomGenerator;
	float tubeVelocity = 6;
	int numberOfTubes = 4;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTubes;
	Rectangle[] topTubeRectangle;
	Rectangle[] bottomTubeRectangle;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		gameover = new Texture("gameover.png");
		//shapeRenderer = new ShapeRenderer();

		//Initializing bird-related variables
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");

		birdCircle = new Circle();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		//Bird Animation code
		birdFrames = new TextureRegion[2];
		birdFrames[0] = new TextureRegion(birds[0]);
		birdFrames[1] = new TextureRegion(birds[1]);
		birdAnimation = new Animation(0.2f,birdFrames); //Change the float here to slow down/increase speed of animation

		//Initializing tube-related variables
		bottomTube = new Texture("bottomtube.png");
		topTube = new Texture("toptube.png");
		maxTubeOffset = Gdx.graphics.getHeight()/2 - gap/2 - 100;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth()* 3 / 4;
		topTubeRectangle = new Rectangle[numberOfTubes];
		bottomTubeRectangle = new Rectangle[numberOfTubes];

		startGame();

	}

	public void startGame() {
		birdY = (Gdx.graphics.getHeight()/2)-(birds[0].getHeight()/2);

		//sets position of tubes
		for (int i = 0; i < numberOfTubes; i++){
			//sets the y-coordinate of the gap
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

			//sets the x-coordinate and separation distance of the tubes
			tubeX[i] = Gdx.graphics.getWidth()/2 - topTube.getWidth()/2 + Gdx.graphics.getWidth() + i*distanceBetweenTubes;
			//define tube hitbox
			topTubeRectangle[i] = new Rectangle();
			bottomTubeRectangle[i] = new Rectangle();
		}
	}

	@Override
	public void render () {

		batch.begin();
		//display background to full screen size
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 1) {

			if (tubeX[scoringTube] < Gdx.graphics.getWidth()/2){
				score++;

				if (scoringTube < numberOfTubes - 1){
					scoringTube++;
				} else {
					scoringTube = 0;
				}
			}

			if (Gdx.input.justTouched()) {
				velocity = -30;
			}

			if (birdY > Gdx.graphics.getHeight() - birds[0].getHeight()){
				birdY = Gdx.graphics.getHeight()- birds[0].getHeight();
			}

			if (birdY > 0) {

				velocity = velocity + gravity;
				birdY -= velocity;

			} else {
				gameState = 2;
			}

			for (int i = 0; i < numberOfTubes; i++) {

				if (tubeX[i] < -topTube.getWidth()){
					tubeX[i] += numberOfTubes * distanceBetweenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
				} else {
					tubeX[i] -= tubeVelocity;

				}

				//draw tubes
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight()/ 2 - gap/2 - bottomTube.getHeight() + tubeOffset[i]);
				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[i]);

				//setting location of tube hitbox
				topTubeRectangle[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
				bottomTubeRectangle[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight()/ 2 - gap/2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
			}

		} else if (gameState ==0) {

			if (Gdx.input.justTouched()) {
				gameState = 1;
			}

		} else if (gameState == 2){


			velocity = velocity + gravity;
			birdY -= velocity;

			if (birdY <0){
				velocity -= gravity;
				birdY = 0;
			}

			for (int i = 0; i < numberOfTubes; i++) {
				//draw tubes
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);
				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
			}

			batch.draw(gameover, Gdx.graphics.getWidth()/2 - gameover.getWidth()/2, Gdx.graphics.getHeight() * 3/5 - gameover.getHeight()/2);

			if (Gdx.input.justTouched() && birdY <= 0) {
				gameState = 1;
				startGame();
				score = 0;
				scoringTube = 0;
				velocity = 0;
			}

		}

		if (flapState == 0) {
			flapState = 1;
		} else {
			flapState = 0;
		}


		//display bird at centre of screen
		//batch.draw(birds[flapState], Gdx.graphics.getWidth()/2 - birds[flapState].getWidth()/2, Gdx.graphics.getHeight()/2 - birds[flapState].getHeight()/2);

		//Animation code
		stateTime += Gdx.graphics.getDeltaTime();
		currentFrame = birdAnimation.getKeyFrame(stateTime, true);
		batch.draw(currentFrame, (Gdx.graphics.getWidth() / 2) - (birds[flapState].getWidth() / 2), birdY, 0, 0, birds[flapState].getWidth(), birds[flapState].getHeight(), 1, 1, -(float)(velocity/2));

		//Drawing font
		font.draw(batch, String.valueOf(score), 100, 200);

		batch.end();



		//setting hitbox
		birdCircle.set(Gdx.graphics.getWidth()/2, birdY + birds[flapState].getHeight()/2 - velocity, birds[flapState].getHeight()/2);


		//colours the bird hitbox
		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);
		//shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

		for (int i = 0; i < numberOfTubes; i++){

			//colours the tube hitbox
			//shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
			//shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight()/ 2 - gap/2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

			//checks for collision
			if (Intersector.overlaps(birdCircle, topTubeRectangle[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangle[i])){
				gameState = 2;
			}

		}

		//shapeRenderer.end();

	}
}
