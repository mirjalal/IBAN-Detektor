package aze.talmir.ibandetektor.iban

import android.os.Bundle
import aze.talmir.ibandetektor.analyser.BaseAnalyser
import aze.talmir.ibandetektor.overlay.ScannerOverlayImpl
import aze.talmir.ibandetektor.scan.BaseScannerActivity

class IbanScannerActivity : BaseScannerActivity<String>() {

    override fun getScannerType(): ScannerOverlayImpl.Type = ScannerOverlayImpl.Type.IBAN

    override fun initImageAnalyser(mlService: BaseAnalyser.MLService): BaseAnalyser<String> =
        IBANAnalyser(scannerOverlay(), mlService)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (analyser as IBANAnalyser).textRecognizedLiveData().observe(this) {
            scannerOverlay().drawBlueRect = it
        }
    }
}
