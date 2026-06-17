package com.example.monopass.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.monopass.R

class GeneratorFragment : Fragment() {
    private lateinit var tvPassword: TextView
    private lateinit var tvLengthLabel: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var cbUppercase: CheckBox
    private lateinit var cbLowercase: CheckBox
    private lateinit var cbNumbers: CheckBox
    private lateinit var cbSymbols: CheckBox
    private lateinit var tvMinNumbers: TextView
    private lateinit var tvMinSpecial: TextView
    private var minNumbers = 0
    private var minSpecial = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_generator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvPassword = view.findViewById(R.id.tvGeneratedPassword)
        tvLengthLabel = view.findViewById(R.id.tvLengthLabel)
        seekBar = view.findViewById(R.id.seekBarLength)
        cbUppercase = view.findViewById(R.id.cbUppercase)
        cbLowercase = view.findViewById(R.id.cbLowercase)
        cbNumbers = view.findViewById(R.id.cbNumbers)
        cbSymbols = view.findViewById(R.id.cbSymbols)
        tvMinNumbers = view.findViewById(R.id.tvMinNumbers)
        tvMinSpecial = view.findViewById(R.id.tvMinSpecial)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                tvLengthLabel.text = getString(R.string.length_label) + progress
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        tvLengthLabel.text = getString(R.string.length_label) + seekBar.progress

        view.findViewById<Button>(R.id.btnMinNumPlus).setOnClickListener {
            minNumbers++
            tvMinNumbers.text = minNumbers.toString()
        }
        view.findViewById<Button>(R.id.btnMinNumMinus).setOnClickListener {
            if (minNumbers > 0) {
                minNumbers--;
                tvMinNumbers.text = minNumbers.toString()
            }
        }

        view.findViewById<Button>(R.id.btnMinSpecPlus).setOnClickListener {
            minSpecial++
            tvMinSpecial.text = minSpecial.toString()
        }
        view.findViewById<Button>(R.id.btnMinSpecMinus).setOnClickListener {
            if (minSpecial > 0) {
                minSpecial--;
                tvMinSpecial.text = minSpecial.toString()
            }
        }

        view.findViewById<Button>(R.id.btnGenerate).setOnClickListener {
            generatePassword()
        }
    }

    private fun generatePassword() {
        val length = seekBar.progress

        val uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val lowercase = "abcdefghijklmnopqrstuvwxyz"
        val numbers = "0123456789"
        val symbols = "!@#%^*?"

        var pool = ""
        if (cbUppercase.isChecked) {
            pool += uppercase
        }
        if (cbLowercase.isChecked) {
            pool += lowercase
        }
        if (cbNumbers.isChecked) {
            pool += numbers
        }
        if (cbSymbols.isChecked) {
            pool += symbols
        }

        if (pool.isEmpty()) {
            Toast.makeText(requireContext(), "Select at least one character type", Toast.LENGTH_SHORT).show()
            return
        }

        if (minNumbers + minSpecial > length) {
            Toast.makeText(requireContext(), getString(R.string.length_too_short), Toast.LENGTH_SHORT).show()
            return
        }

        val result = mutableListOf<Char>()

        repeat(minNumbers) {
            result.add(numbers.random())
        }
        repeat(minSpecial) {
            result.add(symbols.random())
        }

        repeat(length - result.size) {
            result.add(pool.random())
        }

        result.shuffle()

        tvPassword.text = result.joinToString("")
    }
}