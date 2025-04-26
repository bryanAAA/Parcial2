package com.bryanarroyo.finanzasfaciles

import android.content.ContentValues
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bryanarroyo.finanzasfaciles.databinding.FragmentIngresosBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IngresosFragment : Fragment() {

    private var _binding: FragmentIngresosBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: BaseDatosHelper
    private var detallesVisibles = false

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

        configurarSpinnerMetodoPago()
        configurarBotonDetalles()

        actualizarResumen()

        binding.btnGuardarIngreso.setOnClickListener {
            guardarIngreso()
        }
    }

    private fun configurarSpinnerMetodoPago() {
        val metodos = arrayOf("Efectivo", "Tarjeta")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, metodos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMetodoPagoIngreso.adapter = adapter
    }

    private fun configurarBotonDetalles() {
        binding.btnToggleDetallesIngreso.setOnClickListener {
            detallesVisibles = !detallesVisibles
            if (detallesVisibles) {
                binding.layoutDetallesIngreso.visibility = View.VISIBLE
                binding.btnToggleDetallesIngreso.text = "Ocultar detalles ▲"
            } else {
                binding.layoutDetallesIngreso.visibility = View.GONE
                binding.btnToggleDetallesIngreso.text = "Más detalles ▼"
            }
        }
    }

    private fun guardarIngreso() {
        val descripcion = binding.editDescripcionIngreso.text.toString()
        val monto = binding.editMontoIngreso.text.toString().toDoubleOrNull()

        val fechaDetalle = binding.editFechaIngreso.text.toString().ifEmpty { obtenerFechaActual() }
        val lugarDetalle = binding.editLugarIngreso.text.toString()
        val metodoPagoDetalle = binding.spinnerMetodoPagoIngreso.selectedItem.toString()

        if (descripcion.isNotEmpty() && monto != null) {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(BaseDatosHelper.COL_DESCRIPCION, descripcion)
                put(BaseDatosHelper.COL_MONTO, monto)
                put(BaseDatosHelper.COL_FECHA, fechaDetalle)
                // igual que gastos: lugar/metodoPago opcionales
            }
            db.insert(BaseDatosHelper.TABLA_INGRESOS, null, values)
            db.close()

            Toast.makeText(requireContext(), "Ingreso guardado correctamente", Toast.LENGTH_SHORT).show()

            binding.editDescripcionIngreso.text.clear()
            binding.editMontoIngreso.text.clear()
            binding.editFechaIngreso.text.clear()
            binding.editLugarIngreso.text.clear()
            binding.spinnerMetodoPagoIngreso.setSelection(0)

            actualizarResumen()
        } else {
            Toast.makeText(requireContext(), "Por favor complete Descripción y Monto", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarResumen() {
        val db = dbHelper.readableDatabase

        // Actualizar número de ingresos
        val cursorCantidad: Cursor = db.rawQuery(
            "SELECT COUNT(${BaseDatosHelper.COL_ID}) FROM ${BaseDatosHelper.TABLA_INGRESOS}", null)
        var cantidad = 0
        if (cursorCantidad.moveToFirst()) {
            cantidad = cursorCantidad.getInt(0)
        }
        cursorCantidad.close()

        binding.textResumenIngresos.text = "Ingresos Registrados: $cantidad"

        // Actualizar lista de ingresos
        binding.listaIngresos.removeAllViews()

        val cursor: Cursor = db.rawQuery(
            "SELECT ${BaseDatosHelper.COL_FECHA}, ${BaseDatosHelper.COL_DESCRIPCION}, ${BaseDatosHelper.COL_MONTO} FROM ${BaseDatosHelper.TABLA_INGRESOS}", null)

        if (cursor.moveToFirst()) {
            do {
                val fecha = cursor.getString(0)
                val descripcion = cursor.getString(1)
                val monto = cursor.getDouble(2)

                val texto = buildString {
                    append("Fecha: $fecha\n")
                    append("Descripción: $descripcion\n")
                    append("Monto: ₡${"%.2f".format(monto)}")
                }

                val textView = TextView(requireContext()).apply {
                    text = texto
                    textSize = 16f
                    setPadding(0, 8, 0, 8)
                }
                binding.listaIngresos.addView(textView)

                val separador = View(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        4
                    ).apply {
                        setMargins(0, 16, 0, 16)
                    }
                    setBackgroundColor(Color.parseColor("#CCCCCC"))
                }
                binding.listaIngresos.addView(separador)

            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
    }

    private fun obtenerFechaActual(): String {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formato.format(Date())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
