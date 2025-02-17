package com.kmm.clappygc.domain

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.kmm.clappygc.util.Platform
import com.russhwolf.settings.ObservableSettings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.random.Random


//
// Created by Code For Android on 14/01/25.
// Copyright (c) 2025 CFA. All rights reserved.
//
const val SCORE_KEY = "score"

data class Game(
    val platform: Platform,
    val screenWidth: Int = 0,
    val screenHeight: Int = 0,
    val gravity: Float = 0.7f,
    val beeRadius: Float = 30f,
    val beeJumpImpulse: Float = -12f,
    val beeMaxVelocity: Float = if (platform == Platform.Android) 25f else 10f,

    val pipeWidth: Float = 150f,
    val pipeVelocity: Float = if (platform == Platform.Android) 5f else 2.5f,
    val pipeGapSize: Float = if (platform == Platform.Android) 250f else 300f,
) : KoinComponent {

    private val settings: ObservableSettings by inject()
    private val audioPlayer: AudioPlayer by inject()


    var status by mutableStateOf(GameStatus.Idle)
        private set


    var beeVelocity by mutableStateOf(0f)
        private set

    var bee by mutableStateOf(
        Bee(
            x = (screenWidth / 4).toFloat(),
            y = (screenHeight / 2).toFloat(),
            radius = beeRadius
        )
    )
        private set


    var pipePairs = mutableStateListOf<PipePair>()

    var currentScore by mutableStateOf(0)
        private set

    var highScore by mutableStateOf(0)
        private set


    private var isFallingSoundPlayed = false

    init {
        highScore = settings.getInt(
            key = SCORE_KEY,
            defaultValue = 0
        )

        settings.addIntListener(
            key = SCORE_KEY,
            defaultValue = 0
        ) {
            highScore = it
        }

    }

    fun start() {
        status = GameStatus.Started
        audioPlayer.playGameSoundInLoop()
    }

    fun gameOver() {
        status = GameStatus.Over
        audioPlayer.stopGameSound()
        saveScore()
        isFallingSoundPlayed = false
    }

    private fun saveScore() {
        if (highScore < currentScore) {
            settings.putInt(SCORE_KEY, currentScore)
            highScore = currentScore
        }
    }

    fun jump() {
        beeVelocity = beeJumpImpulse
        audioPlayer.playJumpSound()
        isFallingSoundPlayed = false
    }

    fun restartGame() {
        resetBeePosition()
        removePipes()
        resetScore()
        start()
        isFallingSoundPlayed = false
    }

    private fun resetScore() {
        currentScore = 0
    }


    private fun resetBeePosition() {
        bee = bee.copy(
            y = (screenHeight / 2).toFloat(),
        )
        beeVelocity = 0f
    }


    fun updateGameProgress() {

        pipePairs.forEach { pipePair ->
            if (isCollision(pipePair)) {
                gameOver()
                return
            }

            if (!pipePair.scored && bee.x > pipePair.x + pipeWidth / 2) {
                pipePair.scored = true
                currentScore += 1
            }

        }


        if (bee.y < 0) {
            stopTheBee()
            return
        } else if (bee.y > screenHeight) {
            gameOver()
            return
        }
        beeVelocity = (beeVelocity + gravity).coerceIn(-beeMaxVelocity, beeMaxVelocity)
        bee = bee.copy(y = bee.y + beeVelocity)

        if (beeVelocity > (beeVelocity / 1.1)){
            if (!isFallingSoundPlayed){
                audioPlayer.playFallingSound()
                isFallingSoundPlayed = true
            }
        }

        spawnPipes()
    }

    private fun spawnPipes() {
        pipePairs.forEach { it.x -= pipeVelocity }
        pipePairs.removeAll { it.x + pipeWidth < 0 }

        val isLandscape = screenWidth > screenHeight
        var spawnThreshold = if (isLandscape) screenWidth / 1.25 else screenWidth / 2.0

        if (pipePairs.isEmpty() || pipePairs.last().x < spawnThreshold) {
            val initialPipeX = screenWidth.toFloat() + pipeWidth
            val topHeight = Random.nextFloat() * (screenHeight / 2)
            val bottomHeight = screenHeight - topHeight - pipeGapSize
            val newPipePair = PipePair(
                x = initialPipeX,
                y = topHeight + pipeGapSize / 2,
                topHeight = topHeight,
                bottomHeight = bottomHeight

            )
            pipePairs.add(newPipePair)
        }

    }

    fun stopTheBee() {
        beeVelocity = 0f
    }


    private fun isCollision(pipePair: PipePair): Boolean {
        // check horizontal collision
        val beeRightEdge = bee.x + bee.radius
        val beeLeftEdge = bee.x - bee.radius

        val pipeLeftEdge = pipePair.x - pipeWidth / 2
        val pipeRightEdge = pipePair.x + pipeWidth / 2

        val horizontalCollision = beeRightEdge > pipeLeftEdge && beeLeftEdge < pipeRightEdge

        // check vertical collision
        val beeTopEdge = bee.y - bee.radius
        val beeBottomEdge = bee.y + bee.radius


        val gapTopEdge = pipePair.y - pipeGapSize / 2
        val gapBottomEdge = pipePair.y + pipeGapSize / 2

        val beeInGap = beeTopEdge > gapTopEdge && beeBottomEdge < gapBottomEdge

        return horizontalCollision && !beeInGap

    }

    private fun removePipes() {
        pipePairs.clear()
    }

    fun cleanup() {
        audioPlayer.release()
    }


}