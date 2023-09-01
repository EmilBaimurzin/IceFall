package com.ice.game.ui.ice

import androidx.lifecycle.ViewModel

class CBVM: ViewModel() {
    var callback: (() -> Unit)? = null
}