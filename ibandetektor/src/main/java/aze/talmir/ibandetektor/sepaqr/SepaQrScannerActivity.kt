package aze.talmir.ibandetektor.sepaqr

import android.os.Bundle
import aze.talmir.ibandetektor.analyser.BaseAnalyser
import aze.talmir.ibandetektor.overlay.ScannerOverlayImpl
import aze.talmir.ibandetektor.scan.BaseScannerActivity

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
