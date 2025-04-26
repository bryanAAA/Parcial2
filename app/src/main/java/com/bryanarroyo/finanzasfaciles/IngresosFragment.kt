package com.bryanarroyo.finanzasfaciles

import android.content.ContentValues
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bryanarroyo.finanzasfaciles.databinding.FragmentIngresosBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IngresosFragment : Fragment() {

    private var _binding: FragmentIngresosBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: BaseDatosHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIngresosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = BaseDatosHelper(requireContext())

        binding.btnGuardarIngreso.setOnClickListener {
            val descripcion = binding.editDescripcionIngreso.text.toString()
            val monto = binding.editMontoIngreso.text.toString().toDoubleOrNull()

            if (descripcion.isNotEmpty() && monto != null) {
                val db = dbHelper.writableDatabase
                val values = ContentValues().apply {
                    put(BaseDatosHelper.COL_DESCRIPCION, descripcion)
                    put(BaseDatosHelper.COL_MONTO, monto)
                    put(BaseDatosHelper.COL_FECHA, obtenerFechaActual())
                }
                db.insert(BaseDatosHelper.TABLA_INGRESOS, null, values)
                db.close()

                Toast.makeText(requireContext(), "Ingreso guardado correctamente", Toast.LENGTH_SHORT).show()

                binding.editDescripcionIngreso.text.clear()
                binding.editMontoIngreso.text.clear()
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
