package com.ice.game.domain

import com.ice.game.core.library.XY

data class Symbol(
    override var x: Float,
    override var y: Float,
    val symbol: Int
): XY