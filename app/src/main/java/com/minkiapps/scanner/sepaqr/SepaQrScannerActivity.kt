package com.minkiapps.scanner.sepaqr

import android.os.Bundle
import com.minkiapps.scanner.analyser.BaseAnalyser
import com.minkiapps.scanner.overlay.ScannerOverlayImpl
import com.minkiapps.scanner.scan.BaseScannerActivity

class SepaQrScannerActivity : BaseScannerActivity<SepaData>() {

    override fun getScannerType(): ScannerOverlayImpl.Type = ScannerOverlayImpl.Type.SEPAQR

    override fun initImageAnalyser(mlService: BaseAnalyser.MLService): BaseAnalyser<SepaData> =
        SepaQRAnalyser(scannerOverlay(), mlService)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (analyser as SepaQRAnalyser).qrRecognizedLiveData().observe(this) {
            binding.olActScanner.drawBlueRect = it
        }
    }
}
