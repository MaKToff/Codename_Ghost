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

/** Contains definition of screen with stats. */
public class StatsScreen(private val game : CGGame, private val data : Array<Int>) : Screen {
    private val assets  = AssetLoader.getInstance()
    private val batcher = SpriteBatch()
    private val stage   = Stage()

    init {
        // style of big text buttons
        val font            = assets.generateFont("Calibri.ttf", 32, Color.GREEN)
        val textButtonStyle = assets.getTextButtonStyle(314, 128, 41, 128, 191, 127, font)

        // style of labels
        val titleStyle = Label.LabelStyle()
        titleStyle.font = assets.generateFont("American_TextC.ttf", 50, Color.valueOf("36ba29"))

        val textStyle = Label.LabelStyle()
        textStyle.font = assets.generateFont("Academia Roman.ttf", 30, Color.WHITE)

        val infoText  =
                when (assets.language) {
                    "english" -> com.cypress.Locale.en.score()
                    else      -> com.cypress.Locale.ru.score()
                }
        var statsText = ""
        for (i in 0 .. 4) statsText += "${data[i]}\n"
        statsText += "${data[5]}%"
        val statsPos =
                when (assets.language) {
                    "english" -> 300f
                    else      -> 370f
                }

        // initializing labels
        val title =
                when (assets.language) {
                    "english" -> Label("Level complete!", titleStyle)
                    else      -> Label("Уровень пройден!", titleStyle)
                }
        val info  = Label(infoText, textStyle)
        val stats = Label(statsText, textStyle)

        // initializing buttons
        val continueButton =
                when (assets.language) {
                    "english" -> TextButton("Continue", textButtonStyle)
                    else      -> TextButton("Продолжить", textButtonStyle)
                }

        continueButton.addListener(object : ClickListener() {
            override fun touchDown(event : InputEvent?, x : Float, y : Float, ptr : Int, button : Int) = true

            override fun touchUp(event : InputEvent?, x : Float, y : Float, ptr : Int, button : Int) {
                if ((assets.activeMusic?.isPlaying ?: false)) assets.activeMusic?.stop()
                assets.activeMusic = assets.mainTheme
                game.screen = LevelsScreen(game)
                dispose()
            }
        })

        title.setPosition(280f, 390f)
        info.setPosition(100f, 75f)
        stats.setPosition(statsPos, 75f)
        continueButton.setPosition(575f, 10f)

        stage.addActor(title)
        stage.addActor(info)
        stage.addActor(stats)
        stage.addActor(continueButton)

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
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if ((assets.activeMusic?.isPlaying ?: false)) assets.activeMusic?.stop()
            assets.activeMusic = assets.mainTheme
            game.screen = LevelsScreen(game)
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