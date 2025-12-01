package com.learining.JokesQuotes

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.learining.JokesQuotes.RoomDB.DataBaseBuilder
import com.learining.JokesQuotes.RoomDB.MyDatabase
import com.learining.JokesQuotes.RoomDB.User
import com.learining.JokesQuotes.Utils.sendEmail
import com.learining.JokesQuotes.databinding.SignupPageBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SignUpFragment : Fragment() {
    private var _binding: SignupPageBinding? = null
    private val binding get() = _binding!!
    private var db: MyDatabase? = null
    private var signUpCode: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SignupPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val finalDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.etDate.setText(finalDate)
            },
            year, month, day
        )
        datePicker.show()
    }

    private fun checkDate(dateStr: String): Int {
        return try {
            if (dateStr.isEmpty()) return -1
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false
            val birthDate = sdf.parse(dateStr) ?: return -1

            if (birthDate.after(Date())) return -1

            val today = Calendar.getInstance()
            val birthCal = Calendar.getInstance()
            birthCal.time = birthDate

            var years = today.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR)
            if (today.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
                years--
            }
            years
        } catch (e: Exception) {
            -1
        }
    }

    private fun checkUserPassword(password: String, confirmPassword: String): String {
        val trimPassword = password.trim()
        val trimConfirm = confirmPassword.trim()

        if (trimPassword.isEmpty() || trimConfirm.isEmpty()) return "passwordsAreWeak"

        return if (trimPassword.length > 6 && trimPassword.any { it.isLetter() } && trimPassword.any { it.isDigit() }) {
            if (trimPassword == trimConfirm) "passwordsIsTrue"
            else "passwordsIsFalse"
        } else "passwordsAreWeak"
    }

    private fun generateRandomCode(length: Int = 6): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }

    var timer: CountDownTimer? = null
    private fun startTimer() {
        timer = object : CountDownTimer(20000, 1000) {
            override fun onFinish() {
                binding.tvGetCode.text = "get code"
                binding.tvGetCode.isClickable = true
            }

            override fun onTick(milliSeconds: Long) {
                val seconds = milliSeconds / 1000
                binding.tvGetCode.text = "send again after 00:$seconds"
            }

        }.start()
    }

    private fun moveToLogin() {
        val options = NavOptions.Builder()
            .setEnterAnim(android.R.anim.slide_in_left)
            .setExitAnim(android.R.anim.slide_out_right)
            .setPopEnterAnim(android.R.anim.slide_in_left)
            .setPopExitAnim(android.R.anim.slide_out_right)
            .build()
        findNavController().navigate(
            R.id.action_signUpFragment_to_loginFragment,
            null, options
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = DataBaseBuilder.getInstance(requireContext())

        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnSignup.setOnClickListener {
            val checkFields = listOf(
                binding.etUsername to binding.tilUsername,
                binding.etDate to binding.tilDate,
                binding.etEmail to binding.tilEmail,
                binding.etPassword to binding.tilPassword,
                binding.etConfirmPassword to binding.tilConfirmPassword,
                binding.etCode to binding.tilCode
            )

            lifecycleScope.launch {
                for ((input, layout) in checkFields) {
                    if (input.text?.isEmpty() == true) {
                        layout.error = "can't be empty"
                        return@launch
                    } else layout.error = null
                }

                val age = checkDate(binding.etDate.text.toString())
                if (age == -1) {
                    binding.tilDate.error = "Invalid or future format"
                    return@launch
                }
                if (age < 8) {
                    binding.tilDate.error = "Under 8 years not allowed"
                    return@launch
                }
                binding.tilDate.error = null

                val existingUser = db!!.UserDAO().getUserByName(binding.etUsername.text.toString())
                if (existingUser != null) {
                    binding.tilUsername.error = "Username Already Taken"
                } else {

                    val password = binding.etPassword.text?.toString() ?: ""
                    val confirmPassword = binding.etConfirmPassword.text?.toString() ?: ""

                    when (checkUserPassword(password, confirmPassword)) {
                        "passwordsAreWeak" -> {
                            binding.tilPassword.error = "password weak use chars & numbers"
                            return@launch
                        }

                        "passwordsIsFalse" -> {
                            binding.tilPassword.error = "not match"
                            binding.tilConfirmPassword.error = "not match"
                            return@launch
                        }
                    }
                    binding.tilPassword.error = null
                    binding.tilConfirmPassword.error = null

                    if (signUpCode != binding.etCode.text.toString()) {
                        binding.tilCode.error = "Code is wrong"
                        return@launch
                    } else binding.tilCode.error = null

                    val user = User(
                        username = binding.etUsername.text.toString(),
                        mail = binding.etEmail.text.toString(),
                        password = binding.etPassword.text.toString(),
                        dateOfBirth = binding.etDate.text.toString()
                    )

                    db!!.UserDAO().addUser(user)
                    Toast.makeText(requireContext(), "Signed Up Successfully", Toast.LENGTH_LONG)
                        .show()
                    moveToLogin()
                }
            }
        }

        binding.tvGetCode.setOnClickListener {
            if (binding.etEmail.text.toString().contains("@gmail.com")) {
                binding.tilEmail.error = null
                signUpCode = generateRandomCode()
                Snackbar.make(
                    binding.root, "Code sent successfully, check mail",
                    Snackbar.LENGTH_LONG
                ).show()
                sendEmail(binding.etEmail.text.toString(), signUpCode!!)
                binding.tvGetCode.isClickable = false
                startTimer()
            } else binding.tilEmail.error = "Please enter gmail"
        }

        binding.tvLogin.setOnClickListener {
            moveToLogin()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        _binding = null
    }
}