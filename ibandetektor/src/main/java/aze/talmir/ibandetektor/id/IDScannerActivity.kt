package aze.talmir.ibandetektor.id

import android.os.Bundle
import aze.talmir.ibandetektor.analyser.BaseAnalyser
import aze.talmir.ibandetektor.overlay.ScannerOverlayImpl
import aze.talmir.ibandetektor.scan.BaseScannerActivity

class IDScannerActivity : BaseScannerActivity<IDResult>() {

    override fun getScannerType(): ScannerOverlayImpl.Type = ScannerOverlayImpl.Type.ID

    override fun initImageAnalyser(mlService: BaseAnalyser.MLService): BaseAnalyser<IDResult> =
        IDAnalyser(binding.olActScanner, mlService)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (analyser as IDAnalyser).mrzBlockLiveData.observe(this) {
            scannerOverlay().drawGraphicBlocks(listOf(it))
        }
    }
}
