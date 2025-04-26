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
import com.bryanarroyo.finanzasfaciles.databinding.FragmentGastosBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GastosFragment : Fragment() {

    private var _binding: FragmentGastosBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: BaseDatosHelper
    private var detallesVisibles = false

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

        configurarSpinnerMetodoPago()
        configurarBotonDetalles()

        actualizarResumen()

        binding.btnGuardarGasto.setOnClickListener {
            guardarGasto()
        }
    }

    private fun configurarSpinnerMetodoPago() {
        val metodos = arrayOf("Efectivo", "Tarjeta")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, metodos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMetodoPago.adapter = adapter
    }

    private fun configurarBotonDetalles() {
        binding.btnToggleDetalles.setOnClickListener {
            detallesVisibles = !detallesVisibles
            if (detallesVisibles) {
                binding.layoutDetalles.visibility = View.VISIBLE
                binding.btnToggleDetalles.text = "Ocultar detalles ▲"
            } else {
                binding.layoutDetalles.visibility = View.GONE
                binding.btnToggleDetalles.text = "Más detalles ▼"
            }
        }
    }

    private fun guardarGasto() {
        val descripcion = binding.editDescripcionGasto.text.toString()
        val monto = binding.editMontoGasto.text.toString().toDoubleOrNull()

        val fechaDetalle = binding.editFechaGasto.text.toString().ifEmpty { obtenerFechaActual() }
        val lugarDetalle = binding.editLugarGasto.text.toString()
        val metodoPagoDetalle = binding.spinnerMetodoPago.selectedItem.toString()

        if (descripcion.isNotEmpty() && monto != null) {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(BaseDatosHelper.COL_DESCRIPCION, descripcion)
                put(BaseDatosHelper.COL_MONTO, monto)
                put(BaseDatosHelper.COL_FECHA, fechaDetalle)
                // Aquí podrías agregar también lugar y método pago si quieres expandir tu BD
            }
            db.insert(BaseDatosHelper.TABLA_GASTOS, null, values)
            db.close()

            Toast.makeText(requireContext(), "Gasto guardado correctamente", Toast.LENGTH_SHORT).show()

            binding.editDescripcionGasto.text.clear()
            binding.editMontoGasto.text.clear()
            binding.editFechaGasto.text.clear()
            binding.editLugarGasto.text.clear()
            binding.spinnerMetodoPago.setSelection(0)

            actualizarResumen()
        } else {
            Toast.makeText(requireContext(), "Por favor complete Descripción y Monto", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarResumen() {
        val db = dbHelper.readableDatabase

        // Actualizar número de gastos
        val cursorCantidad: Cursor = db.rawQuery(
            "SELECT COUNT(${BaseDatosHelper.COL_ID}) FROM ${BaseDatosHelper.TABLA_GASTOS}", null)
        var cantidad = 0
        if (cursorCantidad.moveToFirst()) {
            cantidad = cursorCantidad.getInt(0)
        }
        cursorCantidad.close()

        binding.textResumenGastos.text = "Gastos Registrados: $cantidad"

        // Actualizar lista de gastos
        binding.listaGastos.removeAllViews()

        val cursor: Cursor = db.rawQuery(
            "SELECT ${BaseDatosHelper.COL_FECHA}, ${BaseDatosHelper.COL_DESCRIPCION}, ${BaseDatosHelper.COL_MONTO} FROM ${BaseDatosHelper.TABLA_GASTOS}", null)

        if (cursor.moveToFirst()) {
            do {
                val fecha = cursor.getString(0)
                val descripcion = cursor.getString(1)
                val monto = cursor.getDouble(2)

                val textView = TextView(requireContext()).apply {
                    text = "Fecha: $fecha\nDescripción: $descripcion\nMonto: ₡${"%.2f".format(monto)}"
                    textSize = 16f
                    setPadding(0, 8, 0, 8)
                }
                binding.listaGastos.addView(textView)

                val separador = View(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        4
                    ).apply {
                        setMargins(0, 16, 0, 16)
                    }
                    setBackgroundColor(Color.parseColor("#CCCCCC"))
                }
                binding.listaGastos.addView(separador)

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
