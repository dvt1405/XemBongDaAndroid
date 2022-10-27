package com.kt.apps.xembongda.ui.login

import android.animation.ObjectAnimator
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IntDef
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionManager
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.BaseBottomSheetFragment
import com.kt.apps.xembongda.databinding.DialogFragmentLoginBinding
import com.kt.apps.xembongda.model.DataState
import com.kt.apps.xembongda.model.UserDTO
import com.kt.apps.xembongda.utils.showErrorDialog
import com.kt.apps.xembongda.utils.showSuccessDialog
import javax.inject.Inject


class DialogFragmentLogin : BaseBottomSheetFragment<DialogFragmentLoginBinding>() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel by lazy {
        ViewModelProvider(requireActivity(), factory)[LoginViewModel::class.java]
    }
    var onDismissListener: (() -> Unit)? = null

    override val resLayout: Int
        get() = R.layout.dialog_fragment_login

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel.setup(this)
    }

    override fun onDetach() {
        viewModel.removeReference()
        super.onDetach()
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    override fun initAction(savedInstanceState: Bundle?) {
        viewModel.loginDataState.observe(this) {
            handleLogin(it)
        }
        binding.signInButton.setOnClickListener {
        }
        binding.root.setOnClickListener {
            requireActivity().currentFocus?.clearFocus()
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)

        }
        binding.registerButton.setOnClickListener {
            if (viewModel.isRegisterMode.get()) {
                viewModel.registerNewUser()
                return@setOnClickListener
            }
            viewModel.isRegisterMode.set(true)
            goto(TRANSITION_TO_REGISTER_MODE)
        }

        binding.btnBack.setOnClickListener {
            viewModel.isRegisterMode.set(false)
            goto(TRANSITION_TO_LOGIN_MODE)

        }
        viewModel.emailInvalid.observe(this) {
            val layoutParams = binding.errorUserName.layoutParams as ConstraintLayout.LayoutParams

            if (it == false && layoutParams.height == ConstraintLayout.LayoutParams.WRAP_CONTENT) {
                return@observe
            }

            if (it != false && layoutParams.height == 1) {
                return@observe
            }

            transitionToNotifyError(it, binding.errorUserName)
        }

        viewModel.passwordInvalid.observe(this) {
            val layoutParams = binding.errorPassword.layoutParams as ConstraintLayout.LayoutParams

            if (it == false && layoutParams.height == ConstraintLayout.LayoutParams.WRAP_CONTENT) {
                return@observe
            }

            if (it != false && layoutParams.height == 1) {
                return@observe
            }

            transitionToNotifyError(it, binding.errorPassword)
        }

        viewModel.retypePasswordInvalid.observe(this) {
            val layoutParams =
                binding.errorRetypePassword.layoutParams as ConstraintLayout.LayoutParams

            if (it == false && viewModel.isRegisterMode.get()
                && layoutParams.height == ConstraintLayout.LayoutParams.WRAP_CONTENT
            ) {
                return@observe
            }

            if (it != false && layoutParams.height == 1) {
                return@observe
            }
            if (viewModel.isRegisterMode.get()) {
                transitionToNotifyError(it, binding.errorRetypePassword)
            }
        }

    }

    private fun transitionToNotifyError(error: Boolean?, view: View) {
        val constrainSet = ConstraintSet()
        constrainSet.clone((binding.root as ConstraintLayout))
        constrainSet.applyTo(binding.root as ConstraintLayout)
        setDefaultNotifyFieldValidViewParams(error, view)

        val transitionRes = R.transition.transtition_to_register
        val transition = TransitionInflater.from(requireContext())
            .inflateTransition(transitionRes)
        TransitionManager.beginDelayedTransition(binding.root as ConstraintLayout, transition)
    }

    private fun handleLogin(it: DataState<UserDTO>?) {
        when (it) {
            is DataState.Success -> {
                showSuccessDialog({
                    dismiss()
                }, content = getString(R.string.login_success))
            }
            is DataState.Loading -> {

            }
            is DataState.Error -> {
                showErrorDialog({

                }, content = it.throwable.message)

            }
            else -> {

            }
        }
    }


    @IntDef(value = [TRANSITION_TO_LOGIN_MODE, TRANSITION_TO_REGISTER_MODE])
    annotation class LayoutMode

    private fun goto(@LayoutMode mode: Int) {
        getConstrainSetForMode(mode)
        startAlphaAnimation(mode)
        startTransitionTo(mode)
    }

    private fun getConstrainSetForMode(@LayoutMode mode: Int) {
        val res = if (mode == TRANSITION_TO_LOGIN_MODE) {
            R.layout.dialog_fragment_login
        } else {
            R.layout.dialog_fragment_login_register_new_acoount_state
        }
        val constrainSet = ConstraintSet()
        constrainSet.clone(requireContext(), res)
        constrainSet.applyTo(binding.root as ConstraintLayout)

        setDefaultNotifyFieldValidViewParams(viewModel.emailInvalid.value, binding.errorUserName)
        setDefaultNotifyFieldValidViewParams(viewModel.passwordInvalid.value, binding.errorPassword)
        if (viewModel.isRegisterMode.get()) {
            setDefaultNotifyFieldValidViewParams(viewModel.retypePasswordInvalid.value, binding.errorRetypePassword)
        }
    }

    private fun setDefaultNotifyFieldValidViewParams(isFieldValid: Boolean?, view: View) {
        val layoutParams = view.layoutParams as ConstraintLayout.LayoutParams
        if (isFieldValid == false) {
            layoutParams.topMargin = (5 * resources.displayMetrics.scaledDensity).toInt()
            layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
        } else {
            layoutParams.topMargin = (0 * resources.displayMetrics.scaledDensity).toInt()
            layoutParams.height = 1
        }
        view.layoutParams = layoutParams
    }


    private fun startAlphaAnimation(@LayoutMode mode: Int) {
        val startAnim = if (mode == TRANSITION_TO_LOGIN_MODE) {
            0f
        } else {
            1f
        }
        ObjectAnimator.ofFloat(startAnim, 1f - startAnim)
            .apply {
                duration = 400
                interpolator = AccelerateInterpolator()
                addUpdateListener {
                    val alpha = if (mode == TRANSITION_TO_LOGIN_MODE) {
                        it.animatedFraction
                    } else {
                        1 - it.animatedFraction
                    }
                    binding.title.alpha = alpha
                    binding.orLabel.alpha = alpha
                    binding.signInButton.alpha = alpha
                    binding.btnLogin.alpha = alpha
                    binding.retypePassword.alpha = 1 - alpha
                    binding.titleRegister.alpha = 1 - alpha
                }
            }
            .start()
    }

    private fun startTransitionTo(transitionToRegisterMode: Int, vararg excludesView: Int) {
        val transitionRes = if (transitionToRegisterMode == TRANSITION_TO_REGISTER_MODE) {
            R.transition.transtition_to_register
        } else {
            R.transition.transition_slide
        }
        val transition = TransitionInflater.from(requireContext())
            .inflateTransition(transitionRes)
        excludesView.forEach {
            transition.excludeTarget(it, true)
        }

        transition.addListener(object : TransitionListenerAdapter() {
            override fun onTransitionStart(transition: Transition) {
                super.onTransitionStart(transition)
                binding.btnBack.isClickable = false
                binding.registerButton.isClickable = false
            }

            override fun onTransitionEnd(transition: Transition) {
                super.onTransitionEnd(transition)
                binding.btnBack.isClickable = true
                binding.registerButton.isClickable = true
                transition.removeListener(this)
            }
        })
        TransitionManager.beginDelayedTransition(binding.root as ConstraintLayout, transition)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.removeReference()
        viewModel.isRegisterMode.set(false)
        onDismissListener?.invoke()
    }

    companion object {
        private const val TRANSITION_TO_LOGIN_MODE = 0
        private const val TRANSITION_TO_REGISTER_MODE = 1
    }
}