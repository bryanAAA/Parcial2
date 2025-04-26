package com.bryanarroyo.finanzasfaciles

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bryanarroyo.finanzasfaciles.databinding.FragmentTipsBinding

class TipsFragment : Fragment() {

    private var _binding: FragmentTipsBinding? = null
    private val binding get() = _binding!!

    private val listaTips = listOf(
        "Ahorra al menos el 10% de tus ingresos cada mes.",
        "Evita deudas innecesarias y tarjetas de crédito.",
        "Haz un presupuesto mensual y respétalo.",
        "Invierte en tu educación financiera.",
        "No gastes más de lo que ganas.",
        "Ten un fondo de emergencia equivalente a 3 meses de gastos.",
        "Compara precios antes de comprar productos grandes.",
        "Elabora metas financieras claras y alcanzables.",
        "Revisa tus gastos pequeños, también suman.",
        "Automatiza tus ahorros siempre que sea posible."
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTipsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mostrarTipAleatorio()

        binding.btnNuevoTip.setOnClickListener {
            mostrarTipAleatorio()
        }
    }

    private fun mostrarTipAleatorio() {
        val tipAleatorio = listaTips.random()
        binding.textViewTip.text = tipAleatorio
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
