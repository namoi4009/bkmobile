package com.example.flowerapp.ui.camera.capturepicture

data class Sticker (
    val name: String,
    val imageId: Int,
    var x: Float = 0f,
    var y: Float = 0f,
    var scale: Float = 1f
) {
}