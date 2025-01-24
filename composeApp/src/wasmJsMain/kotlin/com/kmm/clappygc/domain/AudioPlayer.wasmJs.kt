package com.kmm.clappygc.domain

import org.w3c.dom.Audio

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class AudioPlayer {

    private val audioElement = mutableMapOf<String, Audio>()


    actual fun playGameOverSound() {
        stopFallingSound()
        playSound(filename = "game_over.wav")
    }

    actual fun playJumpSound() {
        stopFallingSound()
        playSound(filename = "jump.wav")
    }

    actual fun playFallingSound() {
        playSound(filename = "falling.wav")

    }

    actual fun stopFallingSound() {
        stopSound(filename = "falling.wav")
    }

    actual fun playGameSoundInLoop() {
        stopAllSounds()
        playSound(filename = "game_sound.wav", loop = true)
    }

    actual fun stopGameSound() {
        playGameOverSound()
        stopSound(filename = "game_sound.wav")
    }

    actual fun release() {
        stopAllSounds()
        audioElement.clear()
    }

    private fun stopSound(filename: String) {
        audioElement[filename]?.let { audio ->
            audio.pause()
            audio.currentTime = 0.0
        }
    }

    private fun stopAllSounds() {
        audioElement.values.forEach { audio ->
            audio.pause()
            audio.currentTime = 0.0
        }
    }


    private fun playSound(filename: String, loop: Boolean = false) {
        val audio = audioElement[filename] ?: createAudioElement(filename).also {
            audioElement[filename] = it
        }
        audio.loop = loop
        audio.play().catch {
            println("Error playing audio file: $filename")
            it
        }

    }


    private fun createAudioElement(filename: String): Audio {
        val path = "composeResources/clappygamelearning.composeapp.generated.resources/files/$filename"
        //  val path = "src/commonMain/composeResources/files/$filename"
        return Audio(path).apply {
            onerror = { _, _, _, _, _ ->
                println("Error loading audio file: $path")
                null
            }
        }

    }
}