package com.bryanarroyo.finanzasfaciles

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bryanarroyo.finanzasfaciles.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Botón Ingresos
        binding.btnIngresos.setOnClickListener {
            findNavController().navigate(R.id.ingresosFragment)
        }

        // Botón Gastos
        binding.btnGastos.setOnClickListener {
            findNavController().navigate(R.id.gastosFragment)
        }

        // Botón Tips
        binding.btnTips.setOnClickListener {
            findNavController().navigate(R.id.tipsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
