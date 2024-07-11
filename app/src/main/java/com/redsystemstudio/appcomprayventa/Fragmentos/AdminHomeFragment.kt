package com.redsystemstudio.appcomprayventa

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.redsystemstudio.appcomprayventa.databinding.ActivityAdminHomeFragmentBinding

class AdminHomeFragment : Fragment() {

    private var _binding: ActivityAdminHomeFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityAdminHomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonManageProducts.setOnClickListener {
            startActivity(Intent(activity, ManageProductsActivity::class.java))
        }

        binding.buttonManageUsers.setOnClickListener {
            // Si tienes otra actividad para gestionar usuarios, abre esa actividad aquí
            // startActivity(Intent(activity, ManageUsersActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
