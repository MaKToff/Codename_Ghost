package com.cypress.GameObjects.Enemies

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.cypress.CGHelpers.AssetLoader
import com.cypress.GameObjects.*

/** */
class Warrior(public override val position : Vector2, private val player : Player) : Character() {
    public override val isEnemy  = true
    public override val width    = 115
    public override val height   = 180
    public override val bounds   = Rectangle(0f, 0f, width.toFloat(), height.toFloat())
    public override val velocity = Vector2(4f, 12f)
    public override val offsetY  = 18f
    public override val offsetX  = 10f

    public override var health          = 50
    public override var shouldGoToLeft  = false
    public override var delta           = 0f
    public override var shouldJump      = false
    public override var shouldGoToRight = false
    public override var stayRight       = false
    public override var onGround        = true
    public override var gunType         = "uzi"
    public override var isDead          = false

    private val assets  = AssetLoader.getInstance()
    private val uziLeft = TextureRegion(assets.guns, 281, 95, 90, 58)

    private var warriorGoesLeft  = Animation(0.1f, Array<TextureRegion>())
    private var warriorGoesRight = Animation(0.1f, Array<TextureRegion>())
    private var warriorStayRight = TextureRegion(assets.warrior, 25, 11, width, height)
    private var warriorStayLeft  = TextureRegion(assets.warrior, 201, 11, width, height)
    private var counter          = 0
    private var canShoot         = false
    private var startValue       = 0f

    init {
        // setting animation
        val posR = arrayOf(Pair( 28, 235), Pair(350, 237), Pair(674, 236), Pair( 30, 445), Pair(361, 445),
                           Pair(678, 445), Pair( 23, 662), Pair(349, 662), Pair(688, 663))
        val posL = arrayOf(Pair(200, 236), Pair(529, 236), Pair(871, 236), Pair(199, 445), Pair(519, 442),
                           Pair(864, 442), Pair(204, 662), Pair(528, 661), Pair(853, 661))

        val warriorsRight = Array<TextureRegion>()
        val warriorsLeft  = Array<TextureRegion>()

        warriorsRight.addAll(
            Array(9, {i -> TextureRegion(assets.warrior, posR[i].first, posR[i].second, width, height)}), 0, 8
        )
        warriorsLeft.addAll(
            Array(9, {i -> TextureRegion(assets.warrior, posL[i].first, posL[i].second, width, height)}), 0, 8
        )

        warriorGoesRight = Animation(0.1f, warriorsRight, Animation.PlayMode.LOOP)
        warriorGoesLeft  = Animation(0.1f, warriorsLeft, Animation.PlayMode.LOOP)
    }

    /** Defines actions of warrior. */
    public fun update(delta : Float) {
        counter++

        // movement of warrior
        if (position.y <= 80f) {
            onGround = true
            position.y = 80f
            velocity.y = 12f
        }
        // falling
        else {
            if (velocity.y < -9) velocity.y = -9f
            position.y += velocity.y
            velocity.y -= 0.2f
        }

        val distance = player.getX() - position.x
        if (counter % 50 == 0) {
            canShoot = true
            counter = 0
        } else canShoot = false

        // warrior pursues player
        if (stayRight && distance > 0 && distance < 300f && Math.abs(player.getY() - position.y) < 500) {
            shouldGoToRight = false
            shouldGoToLeft = false
            if (canShoot) {
                assets.bulletsList.add(Bullet(this))
                assets.shot[0]?.stop()
                if (assets.musicOn) assets.shot[0]?.play()
            }
        } else if (stayRight && distance >= 300f && distance < 500f) {
            position.x += velocity.x
            shouldGoToRight = true
            shouldGoToLeft = false
        } else if (!stayRight && distance < 0 && distance > -300f && Math.abs(player.getY() - position.y) < 500) {
            shouldGoToRight = false
            shouldGoToLeft = false
            if (canShoot) {
                assets.bulletsList.add(Bullet(this))
                assets.shot[0]?.stop()
                if (assets.musicOn) assets.shot[0]?.play()
            }
        } else if (!stayRight && distance <= -300f && distance > -500f) {
            position.x -= velocity.x
            shouldGoToRight = false
            shouldGoToLeft = true
        } else if (distance == 0f) stayRight = !stayRight

        // warrior can't find player and looks around
        else {
            shouldGoToRight = false
            shouldGoToLeft = false
            if (delta.hashCode() % 100 == 0) stayRight = !stayRight
        }
    }

    /** Draws warrior. */
    public fun draw(delta : Float, batcher : SpriteBatch) {
        if (!isDead) startValue = delta

        batcher.begin()

        // warrior is dead
        if (isDead) {
            batcher.draw(death.getKeyFrame(delta - startValue), position.x, position.y, 155f, 155f)
            if (delta - startValue > 0.5f) isDead = false
        }
        else {
            if (shouldGoToLeft || !stayRight)
                batcher.draw(uziLeft, position.x - 55, position.y + 25, 90f, 58f)

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
                batcher.draw(warriorGoesLeft.getKeyFrame(delta), position.x, position.y, width.toFloat(), height.toFloat())
            }

            // warrior should go to right
            else if (shouldGoToRight) {
                stayRight = true
                batcher.draw(warriorGoesRight.getKeyFrame(delta), position.x, position.y, width.toFloat(), height.toFloat())
            }
        }
        batcher.end()
    }

    /** Returns position of warrior on Ox axis. */
    public override fun getX(): Float = position.x

    /** Returns position of warrior on Oy axis. */
    public override fun getY(): Float = position.y

    /** Returns bounds of player. */
    public fun getBound() : Rectangle = bounds
}
