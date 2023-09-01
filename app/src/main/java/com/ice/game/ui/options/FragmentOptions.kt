package com.ice.game.ui.options

import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.slider.Slider
import com.ice.game.R
import com.ice.game.databinding.FragmentOptionsBinding
import com.ice.game.domain.Shared
import com.ice.game.ui.other.MainActivity
import com.ice.game.ui.other.ViewBindingFragment

class FragmentOptions: ViewBindingFragment<FragmentOptionsBinding>(FragmentOptionsBinding::inflate) {
    private val sp by lazy {
        Shared(requireContext())
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            setButtons()

            close.setOnClickListener {
                findNavController().popBackStack()
            }
            ok.setOnClickListener {
                findNavController().popBackStack()
            }

            yes.setOnClickListener {
                setVibroState(true)
            }

            no.setOnClickListener {
                setVibroState(false)
            }

            musicSlider.value = sp.getVolume().toFloat()
            musicSlider.setCustomThumbDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.music01,
                    null
                )!!
            )
            musicSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {

                }

                override fun onStopTrackingTouch(slider: Slider) {
                    sp.setVolume(slider.value.toInt())
                    (requireActivity() as MainActivity).saveSec()
                    (requireActivity() as MainActivity).startMusic()
                }
            })
        }
    }

    private fun setVibroState(value: Boolean) {
        sp.setVibro(value)
        setButtons()
    }

    private fun setButtons() {
        binding.no.alpha = if (sp.getVibro()) 0.4f else 1f
        binding.yes.alpha = if (sp.getVibro()) 1f else 0.4f
    }
}