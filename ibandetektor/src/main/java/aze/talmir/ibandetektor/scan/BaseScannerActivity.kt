package aze.talmir.ibandetektor.scan

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Size
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.core.resolutionselector.ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.get
import aze.talmir.ibandetector.R
import aze.talmir.ibandetector.databinding.ActivityScannerBinding
import aze.talmir.ibandetektor.analyser.BaseAnalyser
import aze.talmir.ibandetektor.overlay.ScannerOverlayImpl
import aze.talmir.ibandetektor.util.extraSerializableOrThrow
import timber.log.Timber
import java.util.concurrent.Executors

abstract class BaseScannerActivity<T> : AppCompatActivity() {

    protected lateinit var binding: ActivityScannerBinding

    private var torchOn : Boolean = false
    private val analyserExecutor = Executors.newSingleThreadExecutor()

    private val mlService : BaseAnalyser.MLService by extraSerializableOrThrow(EXTRA_MOBILE_SERVICE)

    protected val analyser : BaseAnalyser<T> by lazy {
        initImageAnalyser(mlService)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Request camera permissions
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it)
                startCamera()
            else {
                Toast.makeText(this, "Unable to start camera.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }.launch(Manifest.permission.CAMERA)

        binding.olActScanner.type = getScannerType()
        binding.olActScanner.mlService = mlService
        setUp()
    }

    private fun setUp() {
        analyser.bitmapLiveData().observe(this) {
            binding.ivActScannerCroppedPreview.setImageBitmap(it)
        }

        analyser.errorLiveData().observe(this) { e ->
            Timber.e(e, "Analysing failed")
            Toast.makeText(this, "Scanner failed, reason: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }

        analyser.liveData().observe(this) { result ->
            binding.tvActScannerScannedResult.text = ""
            result?.let {
                binding.tvActScannerScannedResult.text = it.toString()
            }
        }

        analyser.debugInfoLiveData().observe(this) {
            val surfaceView = binding.pvActScanner[0]
            val info = "$it\nPreview Size (${surfaceView.width}, ${surfaceView.height}) " +
                    "Translation (${surfaceView.translationX}, ${surfaceView.translationY}) " +
                    "Scale (${surfaceView.scaleX}, ${surfaceView.scaleY}) " +
                    "Pivot (${surfaceView.pivotX}, ${surfaceView.pivotY}) " +
                    "Rotation (${surfaceView.rotation}) " +
                    "Container Size (${binding.pvActScanner.width}, ${binding.pvActScanner.height})"
            binding.tvActScannerDebugInfo.text = info
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            lifecycle.addObserver(analyser)

            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val resolutionSelector = ResolutionSelector.Builder()
                .setResolutionStrategy(
                    ResolutionStrategy(
                        Size(TARGET_PREVIEW_WIDTH, TARGET_PREVIEW_HEIGHT),
                        FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                    )
                )
                .build()

            // Preview
            val preview = Preview.Builder()
                .setResolutionSelector(resolutionSelector)
                .build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .setResolutionSelector(resolutionSelector)
                .build()
                .also {
                    it.setAnalyzer(analyserExecutor, analyser)
                }

            // Select back camera
            val cameraSelector = CameraSelector
                .Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                val camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
                preview.setSurfaceProvider(binding.pvActScanner.surfaceProvider)

                binding.fabActScannerTorch.setOnClickListener {
                    torchOn = !torchOn
                    camera.cameraControl.enableTorch(torchOn)
                    setTorchUI()
                }
                setTorchUI()
            } catch (exc: Exception) {
                Timber.e(exc,"Use case binding failed")
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun setTorchUI() {
        binding.fabActScannerTorch.setImageResource(if(torchOn) R.drawable.ic_baseline_flash_off_24dp_white else R.drawable.ic_baseline_flash_on_24dp_white)
    }

    protected fun scannerOverlay() : ScannerOverlayImpl = binding.olActScanner

    abstract fun initImageAnalyser(mlService: BaseAnalyser.MLService): BaseAnalyser<T>

    abstract fun getScannerType(): ScannerOverlayImpl.Type

    companion object {
        const val EXTRA_MOBILE_SERVICE = "EXTRA_MOBILE_SERVICE"

        private const val TARGET_PREVIEW_WIDTH = 960
        private const val TARGET_PREVIEW_HEIGHT = 1280

        inline fun <reified T : AppCompatActivity> createIntent(
            context: Context, mobileService: BaseAnalyser.MLService
        ): Intent = Intent(context, T::class.java).apply {
            putExtra(EXTRA_MOBILE_SERVICE, mobileService)
        }
    }
}
