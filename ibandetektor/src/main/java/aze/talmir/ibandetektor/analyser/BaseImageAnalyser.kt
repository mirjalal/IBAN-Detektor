package aze.talmir.ibandetektor.analyser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Rect
import android.media.Image
import android.util.Size
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.*
import aze.talmir.ibandetektor.overlay.ScannerOverlay
import aze.talmir.ibandetektor.scan.FrameMetadata
import aze.talmir.ibandetektor.util.BitmapUtil
import aze.talmir.ibandetektor.util.YuvNV21Util
import timber.log.Timber

abstract class BaseAnalyser<T>(
    private val scannerOverlay: ScannerOverlay,
    protected val mlService : MLService
) : ImageAnalysis.Analyzer, LifecycleObserver {

    private val imageMutableData = MutableLiveData<Bitmap>()
    private val mutableLiveData = MutableLiveData<T>()
    private val errorData = MutableLiveData<Exception>()
    private val debugInfoData = MutableLiveData<String>()

    fun liveData() : LiveData<T> = mutableLiveData
    fun errorLiveData() : LiveData<Exception> = errorData
    fun bitmapLiveData() : LiveData<Bitmap> = imageMutableData
    fun debugInfoLiveData() : LiveData<String> = debugInfoData

    private var emitDebugInfo : Boolean = true

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        try {
            val imageProxyReadyEpoch = System.currentTimeMillis()
            val rotation = imageProxy.imageInfo.rotationDegrees
            Timber.d("New image from proxy width : ${imageProxy.width} height : ${imageProxy.height} format : ${imageProxy.format} rotation: $rotation")
            val scannerRect = getScannerRectToPreviewViewRelation(Size(imageProxy.width, imageProxy.height), rotation)

            val image = imageProxy.image!!
            val cropRect = image.getCropRectAccordingToRotation(scannerRect, rotation)
            image.cropRect = cropRect

            val byteArray = YuvNV21Util.yuv420toNV21(image)
            val bitmap = BitmapUtil.getBitmap(byteArray, FrameMetadata(cropRect.width(), cropRect.height(), rotation))
            Timber.d("Bitmap prepared width: ${cropRect.width()} height: ${cropRect.height()}")
            val imagePreparedReadyEpoch = System.currentTimeMillis()

            imageMutableData.postValue(bitmap)

            onBitmapPrepared(bitmap)

            val imageProcessedEpoch = System.currentTimeMillis()

            if(emitDebugInfo) {
                debugInfoData.postValue("""
                   Image proxy (${imageProxy.width},${imageProxy.height}) format : ${imageProxy.format} rotation: $rotation 
                   Cropped Image (${bitmap.width},${bitmap.height}) Preparing took: ${imagePreparedReadyEpoch - imageProxyReadyEpoch}ms
                   OCR Processing took : ${imageProcessedEpoch - imagePreparedReadyEpoch}ms Using Service: $mlService
                """.trimIndent())
            }

            imageProxy.close()
        } catch (e : Exception) {
            errorData.postValue(e)
        }
    }

    protected fun postResult(value : T?) {
        mutableLiveData.postValue(value)
    }

    private fun getScannerRectToPreviewViewRelation(proxySize : Size, rotation : Int): ScannerRectToPreviewViewRelation {
        return when(rotation) {
            0, 180 -> {
                val size = scannerOverlay.size
                val width = size.width
                val height = size.height
                val previewHeight = width / (proxySize.width.toFloat() / proxySize.height)
                val heightDeltaTop = (previewHeight - height) / 2

                val scannerRect = scannerOverlay.scanRect
                val rectStartX = scannerRect.left
                val rectStartY = heightDeltaTop + scannerRect.top

                ScannerRectToPreviewViewRelation(
                    rectStartX / width,
                    rectStartY / previewHeight,
                    scannerRect.width() / width,
                    scannerRect.height() / previewHeight
                )
            }
            90, 270 -> {
                val size = scannerOverlay.size
                val width = size.width
                val height = size.height
                val previewWidth = height / (proxySize.width.toFloat() / proxySize.height)
                val widthDeltaLeft = (previewWidth - width) / 2

                val scannerRect = scannerOverlay.scanRect
                val rectStartX = widthDeltaLeft + scannerRect.left
                val rectStartY = scannerRect.top

                ScannerRectToPreviewViewRelation(
                    rectStartX / previewWidth,
                    rectStartY / height,
                    scannerRect.width() / previewWidth,
                    scannerRect.height() / height
                )
            }
            else -> throw IllegalArgumentException("Rotation degree ($rotation) not supported!")
        }
    }

    abstract fun onBitmapPrepared(bitmap: Bitmap)

    private data class ScannerRectToPreviewViewRelation(
        val relativePosX: Float,
        val relativePosY: Float,
        val relativeWidth: Float,
        val relativeHeight: Float
    )

    private fun Image.getCropRectAccordingToRotation(scannerRect: ScannerRectToPreviewViewRelation, rotation: Int) : Rect =
        when(rotation) {
            0 -> {
                val startX = (scannerRect.relativePosX * width).toInt()
                val numberPixelW = (scannerRect.relativeWidth * width).toInt()
                val startY = (scannerRect.relativePosY * height).toInt()
                val numberPixelH = (scannerRect.relativeHeight * height).toInt()
                Rect(startX, startY, startX + numberPixelW, startY + numberPixelH)
            }
            90 -> {
                val startX = (scannerRect.relativePosY * width).toInt()
                val numberPixelW = (scannerRect.relativeHeight * width).toInt()
                val numberPixelH = (scannerRect.relativeWidth * height).toInt()
                val startY = height - (scannerRect.relativePosX * height).toInt() - numberPixelH
                Rect(startX, startY, startX + numberPixelW, startY + numberPixelH)
            }
            180 -> {
                val numberPixelW = (scannerRect.relativeWidth * width).toInt()
                val startX = (width - scannerRect.relativePosX * width - numberPixelW).toInt()
                val numberPixelH = (scannerRect.relativeHeight * height).toInt()
                val startY = (height - scannerRect.relativePosY * height - numberPixelH).toInt()
                Rect(startX, startY, startX + numberPixelW, startY + numberPixelH)
            }
            270 -> {
                val numberPixelW = (scannerRect.relativeHeight * width).toInt()
                val numberPixelH = (scannerRect.relativeWidth * height).toInt()
                val startX = (width - scannerRect.relativePosY * width - numberPixelW).toInt()
                val startY = (scannerRect.relativePosX * height).toInt()
                Rect(startX, startY, startX + numberPixelW, startY + numberPixelH)
            }
            else -> throw IllegalArgumentException("Rotation degree ($rotation) not supported!")
        }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    abstract fun close()

    enum class MLService {
        GMS, HMS
    }
}