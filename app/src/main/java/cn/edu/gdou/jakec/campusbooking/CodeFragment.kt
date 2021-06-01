package cn.edu.gdou.jakec.campusbooking

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentCodeBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import timber.log.Timber

class CodeFragment : Fragment() {

    private lateinit var binding: FragmentCodeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_code,
            container,
            false
        )

        binding.layout.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val title = CodeFragmentArgs.fromBundle(requireArguments()).title
        val subtitle = CodeFragmentArgs.fromBundle(requireArguments()).subtitle
        val text = CodeFragmentArgs.fromBundle(requireArguments()).text

        binding.titleText.text = title
        binding.subtitleText.text = subtitle

        val bitmap = generadeCode(text)
        binding.image.setImageBitmap(bitmap)

        return binding.root

    }

    private fun generadeCode(text: String): Bitmap {
        val width = 500
        val height = 500
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val codeWriter = MultiFormatWriter()
        try {
            val bitMatrix = codeWriter.encode(text, BarcodeFormat.QR_CODE, width, height)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        } catch (e: WriterException) {
            Timber.d("generateQRCode: %s", e.message)
        }
        return bitmap
    }
}