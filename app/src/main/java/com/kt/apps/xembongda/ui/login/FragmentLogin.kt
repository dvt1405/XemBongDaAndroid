package com.kt.apps.xembongda.ui.login

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.BaseFragment
import com.kt.apps.xembongda.databinding.FragmentLoginBinding
import javax.inject.Inject

class FragmentLogin : BaseFragment<FragmentLoginBinding>() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel by lazy {
        ViewModelProvider(requireActivity(), factory)[LoginViewModel::class.java]
    }

    override val layoutResId: Int
        get() = R.layout.fragment_login

    override val screenName: String
        get() = "FragmentLogin"

    override fun initView(savedInstanceState: Bundle?) {
        binding.viewModel = viewModel
    }

    override fun initAction(savedInstanceState: Bundle?) {

    }
}