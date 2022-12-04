package com.kt.apps.xembongda.ui.comment

import android.os.Bundle
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.BaseFragment
import com.kt.apps.xembongda.databinding.FragmenCommentBinding

class FragmentComment : BaseFragment<FragmenCommentBinding>() {
    override val layoutResId: Int
        get() = R.layout.fragment_live_score

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initAction(savedInstanceState: Bundle?) {
    }

    override val screenName: String
        get() = "FragmentComment"
}