package com.ice.game.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ice.game.R
import com.ice.game.core.library.ViewBindingDialog
import com.ice.game.databinding.DialogGameOverBinding
import com.ice.game.domain.Shared

class DialogGameOver: ViewBindingDialog<DialogGameOverBinding>(DialogGameOverBinding::inflate) {
    private val sp by lazy {
        Shared(requireContext())
    }
    private val args: DialogGameOverArgs by navArgs()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Dialog_No_Border)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setCancelable(false)
        dialog!!.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                findNavController().popBackStack(R.id.fragmentMain, false, false)
                true
            } else {
                false
            }
        }

        binding.score.text = args.scores.toString()
        binding.bestScore.text = sp.getBest().toString()

        binding.close.setOnClickListener {
            findNavController().popBackStack(R.id.fragmentMain, false, false)
        }
        binding.restart.setOnClickListener {
            findNavController().popBackStack(R.id.fragmentMain, false, false)
            findNavController().navigate(R.id.action_fragmentMain_to_fragmentIce)
        }
    }
}