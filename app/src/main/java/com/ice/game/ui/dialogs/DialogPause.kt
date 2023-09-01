package com.ice.game.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ice.game.R
import com.ice.game.core.library.ViewBindingDialog
import com.ice.game.databinding.DialogPauseBinding
import com.ice.game.domain.Shared
import com.ice.game.ui.ice.CBVM

class DialogPause: ViewBindingDialog<DialogPauseBinding>(DialogPauseBinding::inflate) {
    private val cbViewModel: CBVM by activityViewModels()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Dialog_No_Border)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setCancelable(false)
        dialog!!.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                findNavController().popBackStack()
                cbViewModel.callback?.invoke()
                true
            } else {
                false
            }
        }
        binding.play.setOnClickListener {
            findNavController().popBackStack()
            cbViewModel.callback?.invoke()
        }
    }
}