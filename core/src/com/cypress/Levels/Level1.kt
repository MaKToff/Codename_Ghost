package com.cypress.Levels

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import com.cypress.CGHelpers.AssetLoader
import com.cypress.CGHelpers.Controls
import com.cypress.GameObjects.*
import com.cypress.GameObjects.Enemies.*
import com.cypress.Screens.GameOverScreen
import com.cypress.Screens.StatsScreen
import com.cypress.codenameghost.CGGame
import java.util.*

/** Contains definition of first level. */
public class Level1(private val game : CGGame, private val player : Player) : Screen {
    private val assets         = AssetLoader.getInstance()
    private val batcher        = SpriteBatch()
    private var runTime        = 0f
    private val controls       = Controls(game, player, this)
    private val blockList      = ArrayList<Block>()
    private val enemyList      = ArrayList<Warrior>()
    private val itemsList      = ArrayList<Item>()
    private val removedBullets = ArrayList<Bullet>()
    private val deadEnemies    = ArrayList<Warrior>()
    private val removedItems   = ArrayList<Item>()
    private val camera         = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

    private var stage      = Stage()
    private var fan        = Animation(0.02f, Array<TextureRegion>())
    private var index      = assets.gunNames.indexOf(player.gunType)
    private var fanSoundOn = false
    private var hmmSoundOn = true
    private var gameStart  = false
    private var counter    = 0
    private var doorOpened = false

    init {
        stage = controls.getStage()
        assets.activeMusic = assets.levelsMusic[1]

        // adding blocks
        val hSnow = TextureRegion(assets.levelsFP[1],  28, 924, 911,  73)
        val vSnow = TextureRegion(assets.levelsFP[1], 907, 174, 100, 618)
        val crate = TextureRegion(assets.levelsFP[1], 316,  46, 256, 128)
        val roof  = TextureRegion(assets.levelsFP[1], 314, 348, 386,  73)
        val roof1 = TextureRegion(assets.levelsFP[1], 311, 451, 214,  46)
        val roof2 = TextureRegion(assets.levelsFP[1], 313, 529, 416,  24)
        val wall  = TextureRegion(assets.levelsFP[1], 754, 201,  25, 225)
        val door  = TextureRegion(assets.levelsFP[1], 742, 448, 152, 257)

        blockList.add(Block(Vector2(  86f, 950f), 911f, 73f, hSnow))
        blockList.add(Block(Vector2(  86f, 656f), 911f, 73f, hSnow))
        blockList.add(Block(Vector2( 997f, 950f), 125f, 73f, hSnow))
        blockList.add(Block(Vector2( 520f, 390f), 465f, 73f, hSnow))
        blockList.add(Block(Vector2(1330f, 374f), 880f, 73f, hSnow))

        blockList.add(Block(Vector2(-166f,  68f), 256f, 128f, crate))
        blockList.add(Block(Vector2(  80f,  68f), 256f, 128f, crate))
        blockList.add(Block(Vector2(-110f, 190f), 256f, 128f, crate))

        blockList.add(Block(Vector2( 664f, 448f), 100f, 550f, vSnow))
        blockList.add(Block(Vector2( 897f, 448f), 100f, 550f, vSnow))
        blockList.add(Block(Vector2(1317f, 892f), 100f, 618f, vSnow))
        blockList.add(Block(Vector2(1317f, 413f), 100f, 618f, vSnow))
        blockList.add(Block(Vector2(2129f, 380f), 100f, 370f, vSnow))

        blockList.add(Block(Vector2(3230f, 403f), 386f, 73f, roof))
        blockList.add(Block(Vector2(4376f, 929f), 386f, 73f, roof))
        blockList.add(Block(Vector2(4763f, 616f), 386f, 73f, roof))
        blockList.add(Block(Vector2(4559f, 357f), 214f, 46f, roof1))
        blockList.add(Block(Vector2(5860f, 434f), 416f, 24f, roof2))
        //blockList.add(Block(Vector2(6005f, 357f), 416f, 24f, roof2))

        blockList.add(Block(Vector2(5860f, 434f), 25f, 225f, wall))
        blockList.add(Block(Vector2(5999f, 100f), 152f, 257f, door))

        // adding enemies
        enemyList.add(Warrior(Vector2(  43f,  364f), player))
        enemyList.add(Warrior(Vector2( 444f,  101f), player))
        enemyList.add(Warrior(Vector2(2120f,  105f), player))
        enemyList.add(Warrior(Vector2(3241f,  110f), player))
        enemyList.add(Warrior(Vector2(3360f,  485f), player))
        enemyList.add(Warrior(Vector2(4394f, 1013f), player))
        enemyList.add(Warrior(Vector2(4608f,  404f), player))
        enemyList.add(Warrior(Vector2(4612f,  110f), player))
        enemyList.add(Warrior(Vector2(4821f,  700f), player))
        enemyList.add(Warrior(Vector2(5850f,  110f), player))

        // adding items
        itemsList.add(Item(Vector2( 525f,  490f), assets.gunNames[1]))
        itemsList.add(Item(Vector2(3405f,  478f), assets.ammoNames[1]))
        itemsList.add(Item(Vector2(4530f, 1005f), "keyCard"))
        itemsList.add(Item(Vector2(4637f,  495f), "medikit"))

        // animation of fan
        val fanPos   = arrayOf(582, 732, 878)
        val fanArray = Array<TextureRegion>()
        fanArray.addAll(Array(3, {i -> TextureRegion(assets.levelsFP[1], fanPos[i], 14, 141, 134)}), 0, 3)
        fan = Animation(0.02f, fanArray, Animation.PlayMode.LOOP)
    }

    /** Updates level information. */
    private fun update() {
        // if level completed
        if (player.getX() >= player.mapLength) {
            assets.snow?.stop()
            assets.fan?.stop()
            player.data[1] = 2 - player.lives
            if (player.data[3] != 0) player.data[5] =
                    (player.data[4].toFloat() / player.data[3].toFloat() * 100).toInt()
            game.availableLevels[2] = true
            game.screen = StatsScreen(game, player.data)
        }

        // playing level1 music and sounds
        if (assets.musicOn) {
            if (!(assets.activeMusic?.isPlaying ?: false)) {
                assets.activeMusic?.volume = 0.5f
                assets.activeMusic?.play()
            }
            if ((player.shouldGoToLeft || player.shouldGoToRight) && player.onGround && player.getX() < 3096f)
                assets.snow?.play()
            else assets.snow?.stop()

            if (player.getX() > 5345f && player.getX() < 6197f) {
                if (!fanSoundOn) {
                    assets.fan?.setVolume(1, 0.5f)
                    assets.fan?.loop()
                    fanSoundOn = true
                }
            }
            else {
                assets.fan?.stop()
                fanSoundOn = false
            }
        }

        // player should shoot
        index = assets.gunNames.indexOf(player.gunType)
        if ((player.shouldShoot||Gdx.input.isKeyPressed(Input.Keys.SPACE)) &&
                counter % assets.rateOfFire[index] == 0 && index != 6) {
            controls.shoot()
            counter = 0
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
        {
            controls.shoot()
        }

        // player has a key
        if (player.hasKey) {
            blockList.removeAt(blockList.size - 1)
            player.hasKey = false
            doorOpened    = true
        }

        // checking collision of bullets with enemies
        for (bullet in assets.bulletsList) {
            for (enemy in enemyList) {
                if (bullet.getBounds().overlaps(enemy.getBound())) {
                    if(!bullet.enemyBulllet) {
                        enemy.health -= bullet.damage
                        if (bullet.damage == assets.bulletDamage[6]) bullet.shouldExplode = true
                        else if (!bullet.shouldExplode) removedBullets.add(bullet)
                        player.data[4]++
                    }
                }
            }
        }

        // checking collision of bullets with player
        for (bullet in assets.bulletsList) {
            if (bullet.getBounds().overlaps(player.getBound()) && bullet.enemyBulllet && !player.isDead ) {
                player.health -= bullet.damage
                if (player.health <= 0) {
                    player.health = 100
                    player.lives -= 1
                    player.isDead = true

                    // game over
                    if (player.lives < 0) {
                        assets.snow?.stop()
                        assets.fan?.stop()
                        assets.activeMusic?.stop()
                        assets.activeMusic = assets.gameOver
                        game.screen = GameOverScreen(game)
                    }
                }
                removedBullets.add(bullet)
            }
        }

        // deleting dead enemy ...
        for (enemy in deadEnemies)
            // ... if he has already drawn his animation
            if (!enemy.isDead) enemyList.remove(enemy)

        // removing picked up items
        for (item in removedItems) itemsList.remove(item)

        // checking collision of bullets with blocks
        for (bullet in assets.bulletsList)
            for (block in blockList)
                if(bullet.getBounds().overlaps(block.getBounds()))
                    if (bullet.damage == assets.bulletDamage[6]) bullet.shouldExplode = true
                    else if (!bullet.shouldExplode) removedBullets.add(bullet)

        // removing bullets, which hit player or block
        for (bullet in removedBullets) assets.bulletsList.remove(bullet)
    }

    /** Draws level. */
    public override fun render(delta: Float) {
        if (player.onGround) gameStart = true
        runTime += delta
        update()
        counter++

        // drawing background color
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // setting camera
        if (!gameStart) camera.position.set(120f, 1243f, 0f)
        else camera.position.set(player.getX() + 100, player.getY() + 220, 0f)
        camera.zoom = assets.zoom
        batcher.projectionMatrix = camera.combined
        camera.update()

        // drawing background
        batcher.begin()
        if (player.getX() < 3096f) batcher.draw(assets.levelsBG[1][0], -400f, 0f, 4096f, 2048f)
        else batcher.draw(assets.levelsBG[1][1], 2696f, 0f, 4096f, 2048f)
        batcher.end()

        // drawing blocks
        for (block in blockList) block.draw(batcher)

        // check collision with picked up items
        for (item in itemsList) {
            item.draw(batcher)
            if (player.getBound().overlaps(item.getBounds())) {
                item.activity(player)
                removedItems.add(item)
            }
        }

        // drawing terminal and hint
        batcher.begin()
        if (doorOpened)
            batcher.draw(TextureRegion(assets.levelsFP[1], 171, 835, 94, 70), 5799f, 162f, 94f, 70f) // info
        else {
            batcher.draw(TextureRegion(assets.levelsFP[1], 28, 835, 94, 70), 5799f, 162f, 94f, 70f) // info
            if (player.getX() >= 5799 && player.getX() <= 5893 && player.getY() <= 100f) {
                var hint = TextureRegion(assets.levelsFP[1], 319, 568, 249, 163)
                if (assets.language != "english")
                    hint = TextureRegion(assets.levelsFP[1], 319, 748, 249, 163)
                batcher.draw(hint, 5831f, 281f, 249f, 163f)
                if (hmmSoundOn) {
                    assets.neutral[(Math.random() * 1000).toInt() % 3]?.play()
                    hmmSoundOn = false
                }
            }
            else hmmSoundOn = true
        }
        batcher.end()

        // drawing bullets
        if (!assets.bulletsList.isEmpty() && assets.bulletsList[0].distance() > 600)
            assets.bulletsList.removeFirst()
        for (b in assets.bulletsList) b.draw(runTime, batcher)

        // drawing player
        if (player.lives >= 0) {
            player.inputUpdate()
            player.checkCollision(blockList)
            player.draw(runTime, batcher)
        }

        // drawing enemies
        for (enemy in enemyList) {
            if(enemy.health <= 0 && !enemy.isDead) {
                enemy.isDead = true
                deadEnemies.add(enemy)
                player.data[0] += 50
                player.data[2]++
            }

            if (enemy.health > 0) {
                enemy.update(runTime)
                enemy.checkCollision(blockList)
            }
            enemy.draw(runTime, batcher)
        }

        // drawing first plan objects
        batcher.begin()
        batcher.enableBlending()
        batcher.draw(TextureRegion(assets.levelsFP[1], 19,   0, 221, 417), 350f, 980f, 221f, 417f) // spruce
        batcher.draw(TextureRegion(assets.levelsFP[1], 29, 437, 236, 356), 6151f, 77f, 236f, 356f) // fence
        batcher.draw(fan.getKeyFrame(runTime), 5885f, 527f, 141f, 132f)
        batcher.end()

        // drawing stage
        if (gameStart && !player.isDead) {
            controls.update()
            stage.act(runTime)
            stage.draw()
        }
    }

    public override fun resize(width : Int, height : Int) {}
    public override fun show() {}
    public override fun hide() {}
    public override fun pause() {}

    /** Restores screen after pause. */
    public override fun resume() {
        game.screen = this
        Gdx.input.inputProcessor = stage
    }

    /** Dispose level 1. */
    public override fun dispose() {
        stage.dispose()
        game.dispose()
    }
}