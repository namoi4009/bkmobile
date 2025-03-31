package com.example.flowerapp.ui.camera.capturepicture

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class CameraViewModel(
    private val savePhoto: SavePhoto
) : ViewModel() {

    private val _state = MutableStateFlow(CameraState())
    val state = _state.asStateFlow()

    private var lastPhotoUri: Uri? = null
    fun getLastPhotoUri(): Uri? = lastPhotoUri

    fun storePhotoInGallery(bitmap: Bitmap) {
        viewModelScope.launch {
            savePhoto.call(bitmap).fold(
                onSuccess = { uri ->
                    lastPhotoUri = uri  // Store the URI when photo is saved successfully
                    updateCapturedPhotoState(bitmap)
                },
                onFailure = { exception ->
                    // Handle failure - you might want to log this or show an error message
                    println("Failed to save photo: ${exception.message}")
                    // Still update the UI with the bitmap, even if saving failed
                    updateCapturedPhotoState(bitmap)
                }
            )
        }
    }

    private fun updateCapturedPhotoState(updatedPhoto: Bitmap?) {
        _state.value.capturedImage?.recycle()
        _state.value = _state.value.copy(capturedImage = updatedPhoto)
    }

    override fun onCleared() {
        _state.value.capturedImage?.recycle()
        super.onCleared()
    }
}