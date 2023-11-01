package com.minkiapps.scanner.id

import android.os.Bundle
import com.minkiapps.scanner.analyser.BaseAnalyser
import com.minkiapps.scanner.overlay.ScannerOverlayImpl
import com.minkiapps.scanner.scan.BaseScannerActivity

class IDScannerActivity : BaseScannerActivity<IDResult>() {

    override fun initImageAnalyser(mlService: BaseAnalyser.MLService): BaseAnalyser<IDResult> =
        IDAnalyser(binding.olActScanner, mlService)

    override fun getScannerType(): ScannerOverlayImpl.Type = ScannerOverlayImpl.Type.ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (analyser as IDAnalyser).mrzBlockLiveData.observe(this) {
            scannerOverlay().drawGraphicBlocks(listOf(it))
        }
    }
}
