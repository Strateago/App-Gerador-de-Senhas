package com.example.keywordapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.app.DatePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.compose.animation.core.updateTransition
import java.util.Calendar
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {

    // 1. Variáveis para conectar elementos do XML (Views)
    private lateinit var dateTextView: TextView
    private lateinit var passwordTextView_DDMM: TextView
    private lateinit var passwordTextView_MMDD: TextView
    private lateinit var selectDateButton: Button
    private lateinit var copyButton_DDMM: Button
    private lateinit var copyButton_MMDD: Button

    private lateinit var resetDate: Button

    // 2. Variável para guardar a data atualmente selecionada
    private var selectedDate: Date = Date()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 3. Conexão das Views usando seus IDs do XML
        // O "findViewById" encontra o elemento visual pelo ID
        dateTextView = findViewById(R.id.textView_date)
        passwordTextView_DDMM = findViewById(R.id.textView_password_DDMM)
        passwordTextView_MMDD = findViewById(R.id.textView_password_MMDD)
        selectDateButton = findViewById(R.id.button_select_date)
        copyButton_DDMM = findViewById(R.id.button_copy_DDMM)
        copyButton_MMDD = findViewById(R.id.button_copy_MMDD)
        resetDate = findViewById(R.id.reset_date)

        // 4. Ações iniciais: Gera e mostra a senha de HOJE ao iniciar
        updateDisplay(selectedDate)

        // 5. Define o que acontece ao clicar nos botões
        copyButton_DDMM.setOnClickListener {
            copyPasswordToClipboard(0)
        }

        copyButton_MMDD.setOnClickListener {
            copyPasswordToClipboard(1)
        }

        selectDateButton.setOnClickListener {
            showDatePickerDialog()
        }

        resetDate.setOnClickListener {
            selectedDate = Date()
            updateDisplay(selectedDate)
        }
    }

    // --- FUNÇÕES PRINCIPAIS DE LÓGICA ---

    // Função 1: Gera a senha e atualiza a tela
    private fun updateDisplay(date: Date) {
        selectedDate = date
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Atualiza a data mostrada na tela
        dateTextView.text = "Data Selecionada: ${dateFormatter.format(date)}"

        // Gera a nova senha
        val passwords = generatePassword(date)

        // Atualiza a senha mostrada e habilita o botão de copiar
        passwordTextView_DDMM.text = passwords.first
        copyButton_DDMM.isEnabled = true

        passwordTextView_MMDD.text = passwords.second
        copyButton_MMDD.isEnabled = true
    }

    // Função 2: Cria a tela de seleção de data (DatePicker)
    private fun showDatePickerDialog() {
        // Pega o ano, mês e dia da data atual para iniciar o seletor
        val calendar = Calendar.getInstance().apply { time = selectedDate }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            // Lambda que é chamada quando a data é selecionada
            { _, selectedYear, selectedMonth, selectedDay ->
                val newCalendar = Calendar.getInstance()
                newCalendar.set(selectedYear, selectedMonth, selectedDay)

                // Atualiza a tela com a nova data selecionada
                updateDisplay(newCalendar.time)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    // Função 3: Copia a senha para a área de transferência do Android
    private fun copyPasswordToClipboard(index: Int) {
        var password = ""
        if (index == 0) {
            password = passwordTextView_DDMM.text.toString()
        }
        else{
            password = passwordTextView_MMDD.text.toString()
        }

        if (password.isNotEmpty() && password != "SENHA") {
            // Usa o serviço ClipboardManager para copiar o texto
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Senha Baseada em Data", password)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(this, "Senha copiada com sucesso!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Nenhuma senha para copiar.", Toast.LENGTH_SHORT).show()
        }
    }
}

// =========================================================================
// ALGORITMO DE GERAÇÃO DE SENHA (FUNÇÃO DE NÍVEL SUPERIOR)
// =========================================================================

fun generatePassword(date: Date): Pair<String , String> {
    // Regra: Dia(Digito1) + Mês(Digito1) + Dia(Digito2) + Mês(Digito2) + HashHexa(Soma)

    val calendar = Calendar.getInstance()
    calendar.time = date

    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val month = calendar.get(Calendar.MONTH) + 1

    // Formata DDMM (Ex: 0911)
    val dayMonthString = String.format("%02d%02d", day, month)

    var sumOfDigits = 0
    for (char in dayMonthString) {
        sumOfDigits += char.toString().toInt()
    }

    // Converte a soma para hexadecimal (Ex: 22 -> 16, 30 -> 1E)
    val hexSum = Integer.toHexString(sumOfDigits).uppercase(Locale.ROOT).padStart(2, '0')

    // 2. Concatenação para formar a senha final (8 caracteres garantidos)

    // Usa os dígitos formatados da data (D1M1D2M2) + o hash hexadecimal
    val fullPassword_1 = String.format(
        "%c%c%c%c%s",
        dayMonthString[0], // D1
        dayMonthString[2], // M1
        dayMonthString[1], // D2
        dayMonthString[3], // M2
        hexSum
    )

    val fullPassword_2 = String.format(
        "%c%c%c%c%s",
        dayMonthString[2], // D1
        dayMonthString[0], // M1
        dayMonthString[3], // D2
        dayMonthString[1], // M2
        hexSum
    )

    return Pair(fullPassword_1, fullPassword_2)
}