package aze.talmir.ibandetector.sample

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import aze.talmir.ibandetector.sample.databinding.ActivityMainBinding
import aze.talmir.ibandetektor.iban.IbanScannerActivity
import aze.talmir.ibandetektor.id.IDScannerActivity
import aze.talmir.ibandetektor.scan.BaseScannerActivity
import aze.talmir.ibandetektor.sepaqr.SepaQrScannerActivity
import aze.talmir.ibandetektor.util.isGmsAvailable
import aze.talmir.ibandetektor.util.isHmsAvailable

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnActMainIbanScanner.setOnClickListener {
            startActivity(
                BaseScannerActivity.createIntent<IbanScannerActivity>(
                    this,
                    getSelectedMLService()
                )
            )
        }

        binding.btnActMainMrzScanner.setOnClickListener {
            startActivity(
                BaseScannerActivity.createIntent<IDScannerActivity>(
                    this,
                    getSelectedMLService()
                )
            )
        }

        binding.btnActMainQRScanner.setOnClickListener {
            startActivity(
                BaseScannerActivity.createIntent<SepaQrScannerActivity>(
                    this,
                    getSelectedMLService()
                )
            )
        }

        val gmsAvailable = isGmsAvailable()
        val hmsAvailable = isHmsAvailable()

        if (gmsAvailable) {
            binding.ivActMainGMSAvailable.setImageResource(R.drawable.ic_baseline_check_24dp_white)
            binding.ivActMainGMSAvailable.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this,
                    R.color.md_green
                ))
        } else {
            binding.ivActMainGMSAvailable.setImageResource(R.drawable.ic_baseline_clear_24dp_white)
            binding.ivActMainGMSAvailable.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.md_red))
            binding.rgActMainMobileService.removeView(binding.rbActMainMobileGMS)
        }

        if (hmsAvailable) {
            binding.ivActMainHMSAvailable.setImageResource(R.drawable.ic_baseline_check_24dp_white)
            binding.ivActMainHMSAvailable.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.md_green))
        } else {
            binding.ivActMainHMSAvailable.setImageResource(R.drawable.ic_baseline_clear_24dp_white)
            binding.ivActMainHMSAvailable.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.md_red))
            binding.rgActMainMobileService.removeView(binding.rbActMainMobileHMS)
        }

        val isOneServiceAvailable = gmsAvailable or hmsAvailable
        binding.btnActMainIbanScanner.isEnabled = isOneServiceAvailable
        binding.btnActMainMrzScanner.isEnabled = isOneServiceAvailable
        binding.btnActMainQRScanner.isEnabled = gmsAvailable //hms has no qr recogniser like gms

        binding.rgActMainMobileService.setOnCheckedChangeListener { _, i ->
            binding.btnActMainQRScanner.isEnabled = i != binding.rbActMainMobileHMS.id
        }

        when {
            gmsAvailable -> binding.rbActMainMobileGMS.isChecked = true
            hmsAvailable -> binding.rbActMainMobileHMS.isChecked = true
        }
    }

    private fun getSelectedMLService(): aze.talmir.ibandetektor.analyser.BaseAnalyser.MLService =
        when {
            binding.rbActMainMobileGMS.isChecked -> aze.talmir.ibandetektor.analyser.BaseAnalyser.MLService.GMS
            binding.rbActMainMobileHMS.isChecked -> aze.talmir.ibandetektor.analyser.BaseAnalyser.MLService.HMS
            else -> throw RuntimeException("Either GMS or HMS is available on this device!")
        }
}
