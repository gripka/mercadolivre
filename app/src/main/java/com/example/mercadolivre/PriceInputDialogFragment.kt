package com.example.mercadolivre

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog

class PriceInputDialogFragment : DialogFragment() {

    interface PriceInputListener {
        fun onPriceInputEntered(minPrice: String, maxPrice: String)
    }

    companion object {
        fun newInstance(title: String): PriceInputDialogFragment {
            val fragment = PriceInputDialogFragment()
            val args = Bundle()
            args.putString("title", title)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_price_input, null)
        val minPriceInput = view.findViewById<EditText>(R.id.min_price_input)
        val maxPriceInput = view.findViewById<EditText>(R.id.max_price_input)

        val title = arguments?.getString("title") ?: "Intervalo de Preço"
        builder.setView(view)
            .setTitle(title)
            .setPositiveButton("OK") { _, _ ->
                val minPrice = minPriceInput.text.toString()
                val maxPrice = maxPriceInput.text.toString()
                (activity as? PriceInputListener)?.onPriceInputEntered(minPrice, maxPrice)
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }

        return builder.create()
    }
}
