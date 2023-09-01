package com.ice.game.ui.starting

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ice.game.ui.other.ViewBindingFragment
import com.ice.game.R
import com.ice.game.databinding.FragmentStartingBinding

class FragmentStarting : ViewBindingFragment<FragmentStartingBinding>(FragmentStartingBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            start.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentMain_to_fragmentIce)
            }
            options.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentMain_to_fragmentOptions)
            }
            exit.setOnClickListener {
                requireActivity().finish()
            }
        }

        binding.privacyText.setOnClickListener {
            requireActivity().startActivity(
                Intent(
                    ACTION_VIEW,
                    Uri.parse("https://www.google.com")
                )
            )
        }
    }
}