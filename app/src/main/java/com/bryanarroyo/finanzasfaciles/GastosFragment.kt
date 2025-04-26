package com.bryanarroyo.finanzasfaciles

import android.content.ContentValues
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bryanarroyo.finanzasfaciles.databinding.FragmentGastosBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GastosFragment : Fragment() {

    private var _binding: FragmentGastosBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: BaseDatosHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGastosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = BaseDatosHelper(requireContext())

        binding.btnGuardarGasto.setOnClickListener {
            val descripcion = binding.editDescripcionGasto.text.toString()
            val monto = binding.editMontoGasto.text.toString().toDoubleOrNull()

            if (descripcion.isNotEmpty() && monto != null) {
                val db = dbHelper.writableDatabase
                val values = ContentValues().apply {
                    put(BaseDatosHelper.COL_DESCRIPCION, descripcion)
                    put(BaseDatosHelper.COL_MONTO, monto)
                    put(BaseDatosHelper.COL_FECHA, obtenerFechaActual())
                }
                db.insert(BaseDatosHelper.TABLA_GASTOS, null, values)
                db.close()

                Toast.makeText(requireContext(), "Gasto guardado correctamente", Toast.LENGTH_SHORT).show()

                binding.editDescripcionGasto.text.clear()
                binding.editMontoGasto.text.clear()
            } else {
                Toast.makeText(requireContext(), "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun obtenerFechaActual(): String {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formato.format(Date())
    }
}
