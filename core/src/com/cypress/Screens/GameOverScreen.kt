package com.cypress.Screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.cypress.CGHelpers.AssetLoader
import com.cypress.codenameghost.CGGame
import com.cypress.Levels.*
import com.cypress.GameObjects.Player

/** Contains definition of pause menu. */
public class GameOverScreen(private val game : CGGame) : Screen {
    private val assets  = AssetLoader.getInstance()
    private val batcher = SpriteBatch()
    private val stage   = Stage()

    init {
        // style of big text buttons
        val font            = assets.generateFont("Calibri.ttf", 32, Color.GREEN)
        val textButtonStyle = assets.getTextButtonStyle(314, 128, 41, 128, 191, 127, font)

        // style of labels
        val titleStyle = Label.LabelStyle()
        titleStyle.font = assets.generateFont("American_TextC.ttf", 70, Color.valueOf("36ba29"))

        // initializing labels
        val title =
                when (assets.language) {
                    "english" -> Label("  Game over!  ", titleStyle)
                    else      -> Label("Игра окончена!", titleStyle)
                }

        // initializing buttons
        val restart =
                when (assets.language) {
                    "english" -> TextButton("Restart", textButtonStyle)
                    else      -> TextButton("Заново", textButtonStyle)
                }
        val backToMain =
                when (assets.language) {
                    "english" -> TextButton("Back to \nmain menu", textButtonStyle)
                    else      -> TextButton("В главное \nменю", textButtonStyle)
                }

        backToMain.addListener(object : ClickListener() {
            override fun touchDown(event : InputEvent?, x : Float, y : Float, ptr : Int, button : Int) =  true

            override fun touchUp(event : InputEvent?, x : Float, y : Float, ptr : Int, button : Int) {
                assets.activeMusic?.stop()
                game.screen = MainScreen(game)
                dispose()
            }
        })

        restart.addListener(object : ClickListener() {
            override fun touchDown(event : InputEvent?, x : Float, y : Float, ptr : Int, button : Int) = true

            override fun touchUp(event : InputEvent?, x : Float, y : Float, ptr : Int, button : Int) {
                if ((assets.activeMusic?.isPlaying ?: false) && assets.musicOn) assets.activeMusic?.stop()

                assets.loadLevel1()
                val player  = Player(Vector2(20f, 1500f), 120, 177, 6196f)
                game.screen = Level1(game, player)
                dispose()
            }
        })

        title.setPosition(200f, 220f)
        backToMain.setPosition(25f, 10f)
        restart.setPosition(575f, 10f)

        stage.addActor(title)
        stage.addActor(restart)
        stage.addActor(backToMain)

        Gdx.input.inputProcessor = stage
        Gdx.input.isCatchBackKey = true
    }

    /** Draws pause menu. */
    public override fun render(delta : Float) {
        // drawing background color
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // drawing picture
        batcher.begin()
        batcher.disableBlending()
        batcher.draw(assets.about, 0f, 0f, 800f, 480f)
        batcher.end()

        // playing music
        if (!(assets.activeMusic?.isPlaying ?: false) && assets.musicOn) assets.activeMusic?.play()

        // keyboard control
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            assets.activeMusic?.stop()
            game.screen = MainScreen(game)
            dispose()
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if ((assets.activeMusic?.isPlaying ?: false) && assets.musicOn) assets.activeMusic?.stop()
            assets.loadLevel1()
            val player  = Player(Vector2(20f, 1500f), 120, 177, 6196f)
            game.screen = Level1(game, player)
            dispose()
        }

        // drawing stage
        stage.act(delta)
        stage.draw()
    }

    public override fun resize(width : Int, height : Int) {}
    public override fun show() {}
    public override fun hide() {}
    public override fun pause() {}
    public override fun resume() {}

    /** Clears this screen. */
    public override fun dispose() {
        stage.dispose()
        game.dispose()
    }
}