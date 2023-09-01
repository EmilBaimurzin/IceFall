package com.ice.game.ui.ice

import android.annotation.SuppressLint
import android.content.Context.VIBRATOR_SERVICE
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ice.game.R
import com.ice.game.ui.other.ViewBindingFragment
import com.ice.game.databinding.FragmentIceBinding
import com.ice.game.domain.Shared
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class FragmentIce : ViewBindingFragment<FragmentIceBinding>(FragmentIceBinding::inflate) {
    private val viewModel: IceViewModel by viewModels()
    private val cbViewModel: CBVM by activityViewModels()
    private val xy by lazy {
        val display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        Pair(size.x, size.y)
    }
    private val sp by lazy {
        Shared(requireContext())
    }
    private var canNavigate: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHammer()

        binding.menu.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.pause.setOnClickListener {
            viewModel.stop()
            viewModel.pauseState = true
            findNavController().navigate(R.id.action_fragmentIce_to_dialogPause)
        }

        cbViewModel.callback = {
            viewModel.pauseState = false
            lifecycleScope.launch {
                delay(20)
                viewModel.start(xy.first, dpToPx(60), binding.line.y.toInt())
            }
        }

        viewModel.vibroCallback = {
            if (sp.getVibro()) {
                if (Build.VERSION.SDK_INT >= 26) {
                    (requireActivity().getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(
                        VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE)
                    )
                } else {
                    (requireActivity().getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(150)
                }
            }
        }

        viewModel.symbols.observe(viewLifecycleOwner) {
            binding.symbolsLayout.removeAllViews()
            it.forEach { symbol ->
                val symbolView = ImageView(requireContext())
                symbolView.layoutParams = ViewGroup.LayoutParams(dpToPx(60), dpToPx(60))
                symbolView.setImageResource(
                    when (symbol.symbol) {
                        1 -> R.drawable.symbol01
                        2 -> R.drawable.symbol02
                        3 -> R.drawable.symbol03
                        4 -> R.drawable.symbol04
                        5 -> R.drawable.symbol05
                        6 -> R.drawable.symbol06
                        7 -> R.drawable.symbol07
                        else -> R.drawable.bomb
                    }
                )
                symbolView.x = symbol.x
                symbolView.y = symbol.y
                binding.symbolsLayout.addView(symbolView)
            }
        }

        viewModel.endCallback = {
            lifecycleScope.launch(Dispatchers.Main) {
                if (canNavigate) {
                    canNavigate = false
                    viewModel.stop()
                    viewModel.gameState = false
                    if (sp.getBest() < viewModel.scores.value!!) {
                        sp.setBest(viewModel.scores.value!!)
                    }
                    findNavController().navigate(
                        FragmentIceDirections.actionFragmentIceToDialogGameOver(
                            viewModel.scores.value!!
                        )
                    )
                }
            }
        }

        viewModel.explosions.observe(viewLifecycleOwner) {
            binding.explosionsLayout.removeAllViews()
            it.forEach { explosion ->
                val explosionView = ImageView(requireContext())
                explosionView.layoutParams = ViewGroup.LayoutParams(dpToPx(60), dpToPx(60))
                explosionView.setImageResource(R.drawable.explosion)
                explosionView.x = explosion.x
                explosionView.y = explosion.y
                binding.explosionsLayout.addView(explosionView)
            }
        }

        viewModel.hammerPosition.observe(viewLifecycleOwner) {
            binding.hammer.x = it.x
            binding.hammer.y = it.y
        }

        viewModel.scores.observe(viewLifecycleOwner) {
            binding.scores.text = it.toString()
        }

        lifecycleScope.launch {
            delay(20)
            if (viewModel.gameState && !viewModel.pauseState) {
                viewModel.start(xy.first, dpToPx(60), binding.line.y.toInt())
            }

            if (viewModel.hammerPosition.value!!.x == 0f) {
                viewModel.setHammerPosition(
                    (xy.first / 2 - (binding.hammer.width / 2)).toFloat(),
                    (xy.second / 2 - (binding.hammer.height / 2)).toFloat()
                )
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setHammer() {
        binding.hammerLayout.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN || motionEvent.action == MotionEvent.ACTION_MOVE) {
                viewModel.punch(motionEvent.x, motionEvent.y, dpToPx(30), dpToPx(60))
            }
            true
        }
    }

    private fun dpToPx(dp: Int): Int {
        val displayMetrics = resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stop()
    }
}