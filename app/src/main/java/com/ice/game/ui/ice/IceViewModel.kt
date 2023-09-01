package com.ice.game.ui.ice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ice.game.domain.Explosion
import com.ice.game.domain.Symbol
import com.ice.game.core.library.XYImpl
import com.ice.game.core.library.random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class IceViewModel : ViewModel() {
    private val _hammerPosition = MutableLiveData(XYImpl(0f, 0f))
    val hammerPosition: LiveData<XYImpl> = _hammerPosition

    private val _symbols = MutableLiveData<List<Symbol>>(emptyList())
    val symbols: LiveData<List<Symbol>> = _symbols

    private val _explosions = MutableLiveData<List<Explosion>>(emptyList())
    val explosions: LiveData<List<Explosion>> = _explosions

    private val _scores = MutableLiveData(0)
    val scores: LiveData<Int> = _scores

    var gameState = true
    var pauseState = false

    var isSpawning = false
    var canExplode = true

    private var spawnDelay = 1000L
    private var moveDistance = 10

    var endCallback: (() -> Unit)? = null
    var vibroCallback: (() -> Unit)? = null

    private var gameScope = CoroutineScope(Dispatchers.IO)

    fun setHammerPosition(x: Float, y: Float) {
        _hammerPosition.value = XYImpl(x, y)
    }

    fun punch(x: Float, y: Float, hammerSize: Int, symbolSize: Int) {
        _hammerPosition.postValue(XYImpl((x - hammerSize), (y - hammerSize)))
        viewModelScope.launch(Dispatchers.Default) {
            val currentList = _symbols.value!!.toMutableList()
            val newList = mutableListOf<Symbol>()
            currentList.forEach {symbol ->
                val symbolX = symbol.x.toInt()..(symbol.x.toInt() + symbolSize)
                val symbolY = symbol.y.toInt()..(symbol.y.toInt() + symbolSize)

                val hammerX = (x - hammerSize).toInt()..((x - hammerSize).toInt() + hammerSize)
                val hammerY = (y - hammerSize).toInt()..((y - hammerSize).toInt() + hammerSize)

                if (symbolX.any { it in hammerX } && symbolY.any { it in hammerY}) {
                    if (symbol.symbol < 8) {
                        if (canExplode) {
                            _scores.postValue(_scores.value!! + 5)
                        }
                    } else {
                        if (canExplode) {
                            endCallback?.invoke()
                        }
                    }

                    viewModelScope.launch {
                        if (canExplode) {
                            canExplode = false
                            vibroCallback?.invoke()
                            viewModelScope.launch {
                                delay(16)
                                canExplode = true
                            }
                            val explosion = Explosion(symbol.x, symbol.y)
                            val currentExList = _explosions.value!!.toMutableList()
                            currentExList.add(explosion)
                            _explosions.postValue(currentExList)
                            delay(500)
                            val currentExList2 = _explosions.value!!.toMutableList()
                            currentExList2.removeAll { it.id == explosion.id }
                            _explosions.postValue(currentExList2)
                        }
                    }
                } else {
                    newList.add(symbol)
                }
            }
            _symbols.postValue(newList)
        }
    }

    fun start(maxX: Int, symbolSize: Int, lineY: Int) {
        gameScope = CoroutineScope(Dispatchers.IO)
        isSpawning = false

        letBeFaster()
        generateSymbols(maxX, symbolSize)
        letSymbolsFall(symbolSize, lineY)
    }

    fun stop() {
        gameScope.cancel()
    }

    private fun letBeFaster() {
        gameScope.launch {
            while (true) {
                delay(4000)
                if (spawnDelay > 100) {
                    spawnDelay -= 50
                    moveDistance += 1
                }
            }
        }
    }

    private fun generateSymbols(maxX: Int, symbolSize: Int) {
        gameScope.launch {
            while (true) {
                delay(spawnDelay)
                isSpawning = true
                val currentList = _symbols.value!!.toMutableList()
                val x = (0 random (maxX - symbolSize)).toFloat()
                currentList.add(Symbol(x, 0f - symbolSize.toFloat(), 1 random 8))
                _symbols.postValue(currentList)
                isSpawning = false
            }
        }
    }

    private fun letSymbolsFall(symbolSize: Int, lineY: Int) {
        gameScope.launch {
            while (true) {
                delay(16)
                if (!isSpawning && canExplode) {
                    val currentList = _symbols.value!!.toMutableList()
                    val newList = mutableListOf<Symbol>()
                    currentList.forEach { symbol ->
                        symbol.y = symbol.y + moveDistance

                        if (symbol.y + symbolSize < lineY) {
                            newList.add(symbol)
                        } else {
                            if (symbol.symbol != 8) {
                                endCallback?.invoke()
                            }
                        }
                    }
                    _symbols.postValue(newList)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stop()
    }
}