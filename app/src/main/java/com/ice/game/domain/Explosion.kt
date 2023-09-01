package com.ice.game.domain

import com.ice.game.core.library.XY
import java.util.Random

data class Explosion(
    override var x: Float,
    override var y: Float,
    val id: Int = Random().nextInt()
): XY