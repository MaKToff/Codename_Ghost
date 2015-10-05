package com.cypress.GameObjects

import com.badlogic.gdx.math.Vector2

public open class Scrollable(x : Float, y : Float, width : Int, height : Int, scrollSpeed : Float) {

    // Protected ����� private, �� ��������� ������������� � �������� �������.
    protected var position = Vector2()
    protected var velocity = Vector2()
    protected var width = 0
    protected var height = 0
    protected var isScrolledLeft = false

    init {
        position = Vector2(x, y)
        velocity = Vector2(scrollSpeed, 0f)
        this.width = width
        this.height = height
    }

    public fun update(delta : Float) {
        position.add(velocity.cpy().scl(delta))

        // ���� ������ Scrollable ����� �� �����:
        if (position.x + width < 0) {
            isScrolledLeft = true
        }
    }

    // Reset: ����� �������������� � �������� ������, ���� ���������� �������
    // ������ ���������
    public open fun reset(newX : Float) {
        position.x = newX
        isScrolledLeft = false
    }

    // ������ ������� � ��������� ������
    public fun isScrolledLeft() : Boolean {
        return isScrolledLeft
    }

    public fun getTailX() : Float {
        return position.x + width
    }

    public fun getX() : Float {
        return position.x
    }

    public fun getY() : Float {
        return position.y
    }

}