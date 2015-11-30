package com.cypress.Screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.cypress.CGHelpers.AssetLoader
import com.cypress.codenameghost.CGGame

/** Contains definition of main screen. */
public class MainScreen(private val game : CGGame) : Screen {

    private val assets  = AssetLoader.getInstance()
    private val batcher = SpriteBatch()
    private val stage   = Stage()

    init {
        // style of big text buttons
        val font            = assets.generateFont("Calibri.ttf", 60, Color.GREEN)
        val textButtonStyle = assets.getTextButtonStyle(314, 128, 41, 128, 191, 127, font)

        // style of settings button
        val settingsStyle = assets.getImageButtonStyle(517, 194, 596, 194, 70, 70, false)

        // style of labels
        val titleStyle  = Label.LabelStyle()
        titleStyle.font = assets.generateFont("American_TextC.ttf", 100, Color.valueOf("36ba29"))

        // initializing table
        val table = Table()
        table.setFillParent(true)

        // initializing buttons
        val play =
                when (assets.language) {
                    "english" -> TextButton("Play", textButtonStyle)
                    else      -> TextButton("Играть", textButtonStyle)
                }
        val exit =
                when (assets.language) {
                    "english" -> TextButton("Exit", textButtonStyle)
                    else      -> TextButton("Выход", textButtonStyle)
                }
        val settings = ImageButton(settingsStyle)

        // initializing labels
        val title = Label("Codename: Ghost", titleStyle)

        play.addListener(object : ClickListener() {
            override fun touchDown(event : InputEvent?, x : Float, y : Float, ptr : Int, button : Int) = true

            override fun touchUp(event : InputEvent?, x : Float, y : Float, ptr : Int, button : Int) {
                game.screen = LevelsScreen(game)
                dispose()
            }
        })

        exit.addListener(object : ClickListener() {
            override fun touchDown(event : InputEvent?, x : Float, y : Float, ptr : Int, button : Int) = true

            override fun touchUp(event : InputEvent?, x : Float, y : Float, ptr : Int, button : Int) {
                game.dispose()
                Gdx.app.exit()
            }
        })

        settings.addListener(object : ClickListener() {
            override fun touchDown(event : InputEvent?, x : Float, y : Float, ptr : Int, button : Int) = true

            override fun touchUp(event : InputEvent?, x : Float, y : Float, ptr : Int, button : Int) {
                game.screen = SettingsScreen(game)
                dispose()
            }
        })

        table.add(play)
        table.row()
        table.add(exit)
        table.setPosition(170f, -40f)
        settings.setPosition(735f, 0f)
        title.setPosition(100f, 350f)

        stage.addActor(table)
        stage.addActor(settings)
        stage.addActor(title)

        assets.activeMusic = assets.mainTheme

        Gdx.input.inputProcessor = stage
        Gdx.input.isCatchBackKey = true
    }

    /** Draws main screen. */
    public override fun render(delta : Float) {
        // drawing background color
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // playing main theme
        if (!(assets.activeMusic?.isPlaying ?: false) && assets.musicOn) assets.activeMusic?.play()

        // keyboard control
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.dispose()
            Gdx.app.exit()
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.screen = LevelsScreen(game)
            dispose()
        }

        // drawing picture
        batcher.begin()
        batcher.disableBlending()
        batcher.draw(assets.main, 0f, 0f, 800f, 480f)
        batcher.end()

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