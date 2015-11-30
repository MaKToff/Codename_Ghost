package com.cypress.Screens

import com.badlogic.gdx.Gdx
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

/** Contains definition of screen of settings. */
public class SettingsScreen(private val game : CGGame) : Screen {

    private val assets  = AssetLoader.getInstance()
    private val batcher = SpriteBatch()
    private val stage   = Stage()

    init {
        // style of big text buttons
        val font            = assets.generateFont("Calibri.ttf", 32, Color.GREEN)
        val textButtonStyle = assets.getTextButtonStyle(314, 128, 41, 128, 191, 127, font)

        // initializing table
        val table = Table()
        table.setFillParent(true)

        // initializing buttons
        val sounds =
                when (assets.musicOn) {
                    true  -> ImageButton(assets.getImageButtonStyle(684, 181, 789, 181, 90, 90, true))
                    false -> ImageButton(assets.getImageButtonStyle(789, 181, 684, 181, 90, 90, true))
                }
        val language =
                when (assets.language) {
                    "english" -> TextButton("Language:\n" + assets.language, textButtonStyle)
                    else      -> TextButton("Язык:\n" + assets.language, textButtonStyle)
                }
        val about =
                when (assets.language) {
                    "english" -> TextButton("About", textButtonStyle)
                    else      -> TextButton("Об игре", textButtonStyle)
                }
        val back = ImageButton(assets.getImageButtonStyle(525, 116, 663, 116, 129, 65, false))

        language.addListener(object : ClickListener() {
            override fun touchDown(event : InputEvent?, x : Float, y : Float, ptr : Int, button : Int) = true

            override fun touchUp(event : InputEvent?, x : Float, y : Float, ptr : Int, button : Int) {
                when (assets.language) {
                    "english" -> {
                        assets.language = "русский"
                        language.setText("Язык:\n" + assets.language)
                        about.setText("Об игре")
                    }
                    else      -> {
                        assets.language = "english"
                        language.setText("Language:\n" + assets.language)
                        about.setText("About")
                    }
                }
            }
        })

        sounds.addListener(object : ClickListener() {
            override fun touchDown(event : InputEvent?, x : Float, y : Float, ptr : Int, button : Int) = true

            override fun touchUp(event : InputEvent?, x : Float, y : Float, ptr : Int, button : Int) {
                if (assets.musicOn) {
                    assets.musicOn = false
                    assets.mainTheme?.stop()
                }
                else {
                    assets.musicOn = true
                    assets.mainTheme?.play()
                }
            }
        })

        about.addListener(object : ClickListener() {
            override fun touchDown(event : InputEvent?, x : Float, y : Float, ptr : Int, button : Int) = true

            override fun touchUp(event : InputEvent?, x : Float, y : Float, ptr : Int, button : Int) {
                game.screen = AboutScreen(game)
                dispose()
            }
        })

        back.addListener(object : ClickListener() {
            override fun touchDown(event : InputEvent?, x : Float, y : Float, ptr : Int, button : Int) = true

            override fun touchUp(event : InputEvent?, x : Float, y : Float, ptr : Int, button : Int) {
                game.screen = MainScreen(game)
                dispose()
            }
        })

        table.add(sounds)
        table.row()
        table.add(language)
        table.row()
        table.add(about)
        table.setPosition(-230f, 30f)
        back.setPosition(10f, 10f)

        stage.addActor(table)
        stage.addActor(back)

        Gdx.input.inputProcessor = stage
        Gdx.input.isCatchBackKey = true
    }

    /** Draws screen of settings. */
    public override fun render(delta : Float) {
        // drawing background color
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // drawing picture
        batcher.begin()
        batcher.disableBlending()
        batcher.draw(assets.settings, 0f, 0f, 800f, 480f)
        batcher.end()

        // playing main theme
        if (!(assets.activeMusic?.isPlaying ?: false) && assets.musicOn) assets.activeMusic?.play()

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