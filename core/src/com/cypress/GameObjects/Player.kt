package com.cypress.GameObjects

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.cypress.CGHelpers.AssetLoader

/** Contains definition of player. */
public class Player(override val position : Vector2, override protected  val width : Int,
                    override protected  val height : Int, val mapLength: Float)  : Character() {

    public override val isEnemy = false
    public override val bounds  = Rectangle(0f, 0f, width.toFloat(), height.toFloat())
    public override val offsetY = 18f
    public override val offsetX = 10f

    protected override var velocity = Vector2(4f, 12f)

    private val assets       = AssetLoader.getInstance()
    private val acceleration = Vector2(0f, 0.2f)
    private val gun          = Gun(this, assets.gunsNames[0])

    public override var health          = 100
    public override var delta           = 0f
    public override var shouldJump      = false
    public override var shouldGoToLeft  = false
    public override var shouldGoToRight = false
    public override var stayRight       = true
    public override var onGround        = false
    public override var gunType         = assets.gunsNames[0]

    public val availableGuns = Array(7, { false })
    public val ammoCounter   = Array(7, { Pair(0, 0) })

    public var lives = 2

    private var playerGoesLeft  = Animation(0.2f, Array<TextureRegion>())
    private var playerGoesRight = Animation(0.2f, Array<TextureRegion>())
    private var playerStayRight = TextureRegion(assets.player, 47, 802, width, height)
    private var playerStayLeft  = TextureRegion(assets.player, 880, 802, width, height)

    init {
        // setting animation
        val rightPos = arrayOf(219, 47, 391, 219)
        val leftPos  = arrayOf(707, 880, 540, 707)

        val playersRight = Array<TextureRegion>()
        val playersLeft  = Array<TextureRegion>()

        playersRight.addAll(Array(4, {i -> TextureRegion(assets.player, rightPos[i], 802, width, height)}), 0, 3)
        playersLeft.addAll(Array(4, {i -> TextureRegion(assets.player, leftPos[i], 802, width, height)}), 0, 3)

        playerGoesRight = Animation(0.2f, playersRight)
        playerGoesRight.playMode = Animation.PlayMode.LOOP_PINGPONG

        playerGoesLeft = Animation(0.2f, playersLeft)
        playerGoesLeft.playMode = Animation.PlayMode.LOOP_PINGPONG

        availableGuns[0] = true
        availableGuns[1] = true
        availableGuns[2] = true
        availableGuns[4] = true
        availableGuns[5] = true
        ammoCounter[0] = Pair(assets.maxCapacity[0], 30)
        ammoCounter[1] = Pair(assets.maxCapacity[1], 30)
        ammoCounter[2] = Pair(assets.maxCapacity[2], 300)
        ammoCounter[4] = Pair(assets.maxCapacity[4], 15)
        ammoCounter[5] = Pair(assets.maxCapacity[5], 50)
    }

    /** Updates position of player. */
    public fun update(){
        val oldY = position.y

        if (position.y <= 80f) {
            onGround   = true
            position.y = 80f
            velocity.y = 12f
            acceleration.y = 0.2f
        }
        else {
            if(velocity.y < -9) velocity.y = -9f
            position.y += velocity.y
            velocity.y -= acceleration.y
        }

        if (shouldGoToRight) position.x += velocity.x
        if (shouldGoToLeft)  position.x -= velocity.x

        if (shouldJump) {
            //need to change because of collision
            velocity.y = 12f
            position.y += 60f
            shouldJump = false
            onGround   = false
        }

        //if player reach right side
        if (position.x > mapLength) position.x = mapLength

        // if player reach left side
        if (position.x < 2f) position.x = 2f

        delta = position.y - oldY
        bounds.setPosition(position.x, position.y)

    }

    /** Draws player. */
    public fun draw(delta: Float, batcher : SpriteBatch) {
        // drawing gun
        gun.update(gunType)
        gun.draw(delta, batcher)

        batcher.begin()

        // player should stay still ...
        if (!shouldGoToLeft && !shouldGoToRight && !shouldJump) {
            when (stayRight) {
                // ... turning to the right side
                true ->
                    batcher.draw(playerStayRight, position.x, position.y, width.toFloat(), height.toFloat())
                // ... turning to the left side
                false ->
                    batcher.draw(playerStayLeft, position.x, position.y, width.toFloat(), height.toFloat())
            }
        }

        // player should go to left
        else if (shouldGoToLeft) {
            stayRight = false
            if (shouldJump) onGround = false

            if (!onGround)
                batcher.draw(playerStayLeft, position.x, position.y, width.toFloat(), height.toFloat())
            else batcher.draw(playerGoesLeft.getKeyFrame(delta), position.x, position.y, width.toFloat(), height.toFloat())
        }

        // player should go to right
        else if (shouldGoToRight) {
            stayRight = true
            if (shouldJump) onGround = false

            if (!onGround)
                batcher.draw(playerStayRight, position.x, position.y, width.toFloat(), height.toFloat())
            else batcher.draw(playerGoesRight.getKeyFrame(delta), position.x, position.y, width.toFloat(), height.toFloat())
        }

        // player should jump
        else if (shouldJump) {
            onGround = false
            batcher.draw(playerStayRight, position.x, position.y, width.toFloat(), height.toFloat())
        }

        update()
        batcher.end()
    }

    /** Returns position of player on Ox axis. */
    public override fun  getX() : Float = position.x

    /** Returns position of player on Oy axis. */
    public override fun getY() : Float = position.y

    public fun getBound() : Rectangle = bounds
}