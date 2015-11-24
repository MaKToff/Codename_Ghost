package com.cypress.GameObjects

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.cypress.CGHelpers.AssetLoader

/** Contains definition of bullet. */
public class Bullet(private val character : Character) {
    
    private val assets    = AssetLoader.getInstance()
    private val type      = character.gunType
    private var startPos  = Vector2(character.getX(), character.getY())
    private var position  = Vector2(0f, 0f)
    private var direction = 0f

    private var uziBullet       = TextureRegion()
    private var shotgunBullet   = TextureRegion()
    private var rifleBullet     = TextureRegion()
    private var plasmagunBullet = TextureRegion()
    private var lasergunBullet = TextureRegion()
    private var rocket          = TextureRegion()

    //private var correction = if (Math.random() mod 2 > 0.5) (Math.random() mod 50).toFloat()
    //                         else -(Math.random() mod 50).toFloat()

    init {
        when (type) {
            assets.gunsNames[0] -> startPos.y += 77
            assets.gunsNames[1] -> startPos.y += 75
            assets.gunsNames[2] -> startPos.y += 80
            assets.gunsNames[3] -> startPos.y += 80
            assets.gunsNames[4] -> startPos.y += 75
            assets.gunsNames[5] -> startPos.y += 85
        }

        if (character.shouldGoToRight || character.stayRight) {
            when (type) {
                assets.gunsNames[0] -> startPos.x += 145
                assets.gunsNames[1] -> startPos.x += 165
                assets.gunsNames[2] -> startPos.x += 200
                assets.gunsNames[3] -> startPos.x += 175
                assets.gunsNames[4] -> startPos.x += 170
                assets.gunsNames[5] -> startPos.x += 160
            }
            direction = 15f

            uziBullet       = TextureRegion(assets.bullets, 0, 6, 45, 25)
            shotgunBullet   = TextureRegion(assets.bullets, 0, 37, 50, 42)
            rifleBullet     = TextureRegion(assets.bullets, 0, 88, 50, 26)
            plasmagunBullet = TextureRegion(assets.bullets, 0, 121, 52, 16)
            lasergunBullet = TextureRegion(assets.bullets, 0, 145, 52, 38)
            rocket          = TextureRegion(assets.bullets, 0, 188, 128, 32)
        }
        else {
            when (type) {
                assets.gunsNames[0] -> startPos.x -= 25
                assets.gunsNames[1] -> startPos.x -= 45
                assets.gunsNames[2] -> startPos.x -= 60
                assets.gunsNames[3] -> startPos.x -= 65
                assets.gunsNames[4] -> startPos.x -= 65
                assets.gunsNames[5] -> startPos.x -= 105
            }
            startPos.y -= 5
            direction = -15f

            uziBullet       = TextureRegion(assets.bullets, 83, 6, 45, 25)
            shotgunBullet   = TextureRegion(assets.bullets, 78, 37, 50, 42)
            rifleBullet     = TextureRegion(assets.bullets, 78, 88, 50, 26)
            plasmagunBullet = TextureRegion(assets.bullets, 76, 121, 52, 16)
            lasergunBullet = TextureRegion(assets.bullets, 76, 145, 52, 38)
            rocket          = TextureRegion(assets.bullets, 0, 224, 128, 32)
        }

        position.x = startPos.x
        position.y = startPos.y
    }

    /** Draws bullet. */
    public fun draw(delta: Float, batcher : SpriteBatch) {
        position.x += direction
        if (character.shouldGoToLeft) position.x -= 4
        else if (character.shouldGoToRight) position.x += 4

        // drawing bullet
        batcher.begin()
        when (type) {
            assets.gunsNames[0] -> batcher.draw(uziBullet, position.x, position.y, 23f, 13f)
            assets.gunsNames[1] -> batcher.draw(shotgunBullet, position.x, position.y, 25f, 21f)
            assets.gunsNames[2] -> batcher.draw(rifleBullet, position.x, position.y, 25f, 13f)
            assets.gunsNames[3] -> batcher.draw(plasmagunBullet, position.x, position.y, 26f, 8f)
            assets.gunsNames[4] -> batcher.draw(lasergunBullet, position.x, position.y, 26f, 16f)
            assets.gunsNames[5] -> batcher.draw(rocket, position.x, position.y, 85f, 21f)
        }
        batcher.end()
    }

    /** Returns distance from player to bullet. */
    public fun distance(): Float {
        return Math.abs(position.x - startPos.x)
    }
}