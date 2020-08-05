package com.wgsoft.game.stonewar.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.wgsoft.game.stonewar.objects.loading.LoadingBubble;

import static com.wgsoft.game.stonewar.Const.*;

public class LoadingScreen implements Screen {
    public Skin skin;

    private Stage backgroundStage;
    private Stage uiStage;

    public float progress, prevProgress;

    private Label loadingLabel;
    private Label titleLabel;

    private InputMultiplexer inputMultiplexer;

    public LoadingScreen(){
        loadingScreen = this;

        skin = new Skin(Gdx.files.internal("loading/skin.json"));

        game.assetManager.load("img/skin.json", Skin.class);

        backgroundStage = new Stage(new ScreenViewport(), game.batch);
        uiStage = new Stage(new ScreenViewport(), game.batch);

        inputMultiplexer = new InputMultiplexer(uiStage, backgroundStage);

        for(int i = 0; i < LOADING_BUBBLE_COUNT; i++){
            backgroundStage.addActor(new LoadingBubble());
        }

        Table backRootTable = new Table(skin);
        backRootTable.setFillParent(true);

        backRootTable.add().grow();
        backRootTable.row();

        loadingLabel = new Label("loading.loading", skin, "regularSmall");
        backRootTable.add(loadingLabel).padBottom(LOADING_PADDING_BOTTOM);

        uiStage.addActor(backRootTable);

        Table frontRootTable = new Table(skin);
        frontRootTable.setFillParent(true);

        titleLabel = new Label("title", skin, "bold");
        frontRootTable.add(titleLabel).expand();

        uiStage.addActor(frontRootTable);
    }

    public void localize(){
        loadingLabel.setText(game.bundle.get("loading.loading"));
        titleLabel.setText(game.bundle.get("title"));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
        Gdx.gl.glClearColor(0.925490196f, 0.945098039f, 0.945098039f, 1f);
    }

    @Override
    public void render(float delta) {
        prevProgress = progress;
        progress += LOADING_BUBBLE_SPEED*delta;
        while(progress >= 1f){
            progress -= 1f;
        }
        backgroundStage.act(delta);
        uiStage.act(delta);
        backgroundStage.draw();
        uiStage.draw();

        if(game.assetManager.update()){
            game.skin = game.assetManager.get("img/skin.json");
            ObjectMap.Entries<String, BitmapFont> entries = new ObjectMap.Entries<>(game.skin.getAll(BitmapFont.class));
            for (ObjectMap.Entry<String, BitmapFont> entry : entries){
                entry.value.setUseIntegerPositions(false);
                Array.ArrayIterator<TextureRegion> iterator = new Array.ArrayIterator<>(entry.value.getRegions());
                for(TextureRegion region : iterator){
                    region.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                }
            }
            game.loaded = true;
            game.mainMenuScreen = new MainMenuScreen();
            game.localize();
            game.setScreen(game.mainMenuScreen);
        }
    }

    @Override
    public void resize(int width, int height) {
        if((float)width/height > (float)SCREEN_WIDTH/SCREEN_HEIGHT){
            ((ScreenViewport)backgroundStage.getViewport()).setUnitsPerPixel((float)SCREEN_HEIGHT/height);
            ((ScreenViewport)uiStage.getViewport()).setUnitsPerPixel((float)SCREEN_HEIGHT/height);
        }else{
            ((ScreenViewport)backgroundStage.getViewport()).setUnitsPerPixel((float)SCREEN_WIDTH/width);
            ((ScreenViewport)uiStage.getViewport()).setUnitsPerPixel((float)SCREEN_WIDTH/width);
        }
        backgroundStage.getViewport().update(width, height, true);
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        dispose();
    }

    @Override
    public void dispose() {
        skin.dispose();
    }
}