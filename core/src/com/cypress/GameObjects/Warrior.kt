package com.cypress.GameObjects

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.cypress.CGHelpers.AssetLoader

class Warrior(override val position : Vector2, override protected  val width : Int,
            override protected val height : Int, private val player : Player) : Character() {

    override val isEnemy = true
    private val assets = AssetLoader.getInstance()

    override val bounds = Rectangle(0f, 0f, width.toFloat(), height.toFloat())
    override var delta = 0f
    override  var shouldJump: Boolean = false
    override val velocity: Vector2 = Vector2()
    private val gun          = Gun(this, assets.gunsNames[0])

    override val offsetY = 18f
    override val offsetX = 10f

    public override var health = 100
    public override var shouldGoToLeft = false
    public override var shouldGoToRight = false
    public override var stayRight = false
    public override var onGround = true
    public override var gunType = "uzi"

    private var warriorGoToLeft  = Animation(0.1f, Array<TextureRegion>())
    private var warriorGoToRight = Animation(0.1f, Array<TextureRegion>())
    private var warriorStayRight = TextureRegion(assets.warrior, 25, 11, width, height)
    private var warriorStayLeft  = TextureRegion(assets.warrior, 201, 11, width, height)

    init {
        // setting animation
        val rightPos = arrayOf(Pair(28, 235), Pair(350, 237), Pair(674, 236), Pair(30, 445),
               Pair(361, 445), Pair(678, 445), Pair(23, 662), Pair(349, 662), Pair(688, 663))
        val leftPos  = arrayOf(Pair(200, 236), Pair(529, 236), Pair(871, 236), Pair(199, 445),
               Pair(519, 442), Pair(864, 442), Pair(204, 662), Pair(528, 661), Pair(853, 661))

        val warriorsRight = Array<TextureRegion>()
        val warriorsLeft  = Array<TextureRegion>()

        warriorsRight.addAll(
            Array(9, {i -> TextureRegion(assets.warrior, rightPos[i].first, rightPos[i].second, width, height)}), 0, 8
        )
        warriorsLeft.addAll(
            Array(9, {i -> TextureRegion(assets.warrior, leftPos[i].first, leftPos[i].second, width, height)}), 0, 8
        )

        warriorGoToRight = Animation(0.1f, warriorsRight)
        warriorGoToRight.playMode = Animation.PlayMode.LOOP

        warriorGoToLeft = Animation(0.1f, warriorsLeft)
        warriorGoToLeft.playMode = Animation.PlayMode.LOOP
    }

    /** Updates position of warrior. */
    public fun update(delta : Float) {
        //
        var canShot = false

        val oldY = position.y

        if(delta.hashCode() % 72 == 0) canShot = true
        //canShot = delta.toInt() % 2 == 0

        if (position.y <= 80f) {
            onGround   = true
            position.y = 80f
            velocity.y = 12f
        }
        else {
            if(velocity.y < -9) velocity.y = -9f
            position.y += velocity.y
            velocity.y -= 0.2f
        }

        if (player.getX() > position.x && stayRight
                && Math.abs(player.getX() - position.x) < 300f) {
            shouldGoToRight = false
            shouldGoToLeft = false
            if (canShot) {
                val bullet = Bullet(this)
                assets.bulletsList.add(bullet)
                canShot = false
            }
        }
        if (player.getX() > position.x && stayRight
                && Math.abs(player.getX() - position.x) >= 300f && Math.abs(player.getX() - position.x) < 500f) {
            position.x += 2
            shouldGoToRight = true
            shouldGoToLeft = false
        }
        else if (player.getX() < position.x && !stayRight
                && Math.abs(player.getX() - position.x) < 300f) {
            shouldGoToRight = false
            shouldGoToLeft = false
            println("!")
            if (canShot) {
                val bullet = Bullet(this)
                assets.bulletsList.add(bullet)
                canShot = false
            }
        }
        else if (player.getX() < position.x && !stayRight
                && Math.abs(player.getX() - position.x) >= 300f && Math.abs(player.getX() - position.x) < 500f) {
            position.x -= 2
            shouldGoToRight = false
            shouldGoToLeft = true
        }
        else if (player.getX() == position.x) stayRight = !stayRight
        else {
            shouldGoToRight = false
            shouldGoToLeft = false
            if (delta.toInt() % 10 == 0) {
                if (stayRight) stayRight = false
                else stayRight = true
            }
        }
    }

    /** Draws warrior. */
    public fun draw(delta: Float, batcher : SpriteBatch) {

        gun.update(gunType)
        gun.draw(delta, batcher)

        batcher.begin()
            // warrior should stay still ...
            if (!shouldGoToLeft && !shouldGoToRight) {
                when (stayRight) {
                // ... turning to the right side
                    true ->
                        batcher.draw(warriorStayRight, position.x, position.y, width.toFloat(), height.toFloat())
                // ... turning to the left side
                    false ->
                        batcher.draw(warriorStayLeft, position.x, position.y, width.toFloat(), height.toFloat())
                }
            }

            // warrior should go to left
            else if (shouldGoToLeft) {
                stayRight = false
                batcher.draw(warriorGoToLeft.getKeyFrame(delta), position.x, position.y, width.toFloat(), height.toFloat())
            }

            // warrior should go to right
            else if (shouldGoToRight) {
                stayRight = true
                batcher.draw(warriorGoToRight.getKeyFrame(delta), position.x, position.y, width.toFloat(), height.toFloat())
            }

            batcher.end()
    }
    /** Returns position of warrior on Ox axis. */
    public override fun getX(): Float = position.x
    /** Returns position of warrior on Oy axis. */
    public override fun getY(): Float = position.y

    public fun getBound() : Rectangle = bounds
}
