package com.learining.JokesQuotes

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.learining.JokesQuotes.RoomDB.DataBaseBuilder
import com.learining.JokesQuotes.RoomDB.MyDatabase
import com.learining.JokesQuotes.databinding.LoginPageBinding
import kotlinx.coroutines.launch

class LoginFragment:Fragment() {
    private var _binding : LoginPageBinding? = null
    private val binding get() = _binding!!
    private var db : MyDatabase? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LoginPageBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DataBaseBuilder.getInstance(requireContext())

        binding.btnLogin.setOnClickListener {
            lifecycleScope.launch {
                val user = db!!.UserDAO().getUserByName(binding.etUsername.text.toString())
                if(user == null){
                    binding.tilUsername.error = "No User Exist With this name"
                    return@launch
                }else binding.tilUsername.error = null

                if (user.password != binding.etPassword.text.toString()){
                    binding.tilPassword.error = "Password is inCorrect"
                    return@launch
                }else binding.tilPassword.error = null

                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.putExtra("username", user.username)
                startActivity(intent)
                requireActivity().overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )
            }
        }

        val options = NavOptions.Builder()
            .setEnterAnim(android.R.anim.slide_in_left)
            .setExitAnim(android.R.anim.slide_out_right)
            .setPopEnterAnim(android.R.anim.slide_in_left)
            .setPopExitAnim(android.R.anim.slide_out_right)
            .build()

        binding.tvSignup.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment,
                null, options)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null    // important to avoid memory leaks
    }
}