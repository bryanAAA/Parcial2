package com.bryanarroyo.finanzasfaciles

import android.database.Cursor
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
    private lateinit var dbHelper: BaseDatosHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = BaseDatosHelper(requireContext())

        actualizarResumen()

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

    private fun actualizarResumen() {
        val db = dbHelper.readableDatabase

        // Sumar Ingresos
        val cursorIngresos: Cursor = db.rawQuery(
            "SELECT SUM(${BaseDatosHelper.COL_MONTO}) FROM ${BaseDatosHelper.TABLA_INGRESOS}", null)
        var totalIngresos = 0.0
        if (cursorIngresos.moveToFirst()) {
            totalIngresos = cursorIngresos.getDouble(0)
        }
        cursorIngresos.close()

        // Sumar Gastos
        val cursorGastos: Cursor = db.rawQuery(
            "SELECT SUM(${BaseDatosHelper.COL_MONTO}) FROM ${BaseDatosHelper.TABLA_GASTOS}", null)
        var totalGastos = 0.0
        if (cursorGastos.moveToFirst()) {
            totalGastos = cursorGastos.getDouble(0)
        }
        cursorGastos.close()

        db.close()

        // Mostrar en pantalla
        val balance = totalIngresos - totalGastos

        binding.textTotalIngresos.text = "Total Ingresos: ₡${"%.2f".format(totalIngresos)}"
        binding.textTotalGastos.text = "Total Gastos: ₡${"%.2f".format(totalGastos)}"
        binding.textBalance.text = "Balance: ₡${"%.2f".format(balance)}"
    }

    override fun onResume() {
        super.onResume()
        // Actualizar resumen cada vez que vuelve al Home
        actualizarResumen()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
