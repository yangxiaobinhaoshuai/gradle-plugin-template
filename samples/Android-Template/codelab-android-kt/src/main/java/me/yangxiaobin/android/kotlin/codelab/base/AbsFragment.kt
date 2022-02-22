package me.yangxiaobin.android.kotlin.codelab.base

import android.animation.Animator
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import me.yangxiaobin.android.kotlin.codelab.log.L
import me.yangxiaobin.kotlin.codelab.log.logD
import me.yangxiaobin.kotlin.codelab.log.logE
import me.yangxiaobin.kotlin.codelab.log.logI

abstract class AbsFragment : Fragment() {

    open val AbsFragment.TAG: String
        get() = "AbsFragment:${this.javaClass.simpleName.take(11)}"

    protected val logI by lazy { L.logI(TAG) }
    protected val logD by lazy { L.logD(TAG) }
    protected val logE by lazy { L.logE(TAG) }

    protected abstract val layoutResId: Int

    protected val handleBackPress: Boolean = true


    private val backPressCallback by lazy {
        object : OnBackPressedCallback(handleBackPress) {
            override fun handleOnBackPressed() {
                this@AbsFragment.onBackPress()
            }
        }
    }

    init {
        logI("${this.hashCode()} onConstruct")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        logI("${this.hashCode()} onAttach, context : $context")
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressCallback)
    }

    @Deprecated("Consider beforeViewCreated or beforeViewReturned")
    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        beforeViewCreated(inflater, container, savedInstanceState)
        return inflater.inflate(layoutResId, container, false)
            .let(this::beforeViewReturned)
            .apply { this.isClickable = true }
            .also { logI("${this.hashCode()} onAttach, rootView : $it") }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logI("${this.hashCode()} onViewCreated, view : $view, savedInstanceState: $savedInstanceState")
        afterViewCreated(view)
    }

    open fun afterViewCreated(view: View) {
        logI("${this.hashCode()} afterViewCreated, view :$view")
    }

    open fun beforeViewReturned(view: View): View {
        logI("${this.hashCode()} beforeViewReturned, view :$view")
        return view
    }

    open fun beforeViewCreated(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        logI("${this.hashCode()} beforeViewCreated, bundle :$savedInstanceState")
    }

    open fun onBackPress() {
        logI("${this.hashCode()} onBackPress")
        parentFragmentManager.commit { remove(this@AbsFragment) }
    }

    override fun onStart() {
        super.onStart()
        logI("${this.hashCode()} onStart")
    }

    override fun onResume() {
        super.onResume()
        logI("${this.hashCode()} onResume")
    }

    override fun onPause() {
        super.onPause()
        logI("${this.hashCode()} onPause")
    }


    override fun onStop() {
        super.onStop()
        logI("${this.hashCode()} onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        logI("${this.hashCode()} onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        logI("${this.hashCode()} onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        logI("${this.hashCode()} onDetach")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        logI("${this.hashCode()} onConfigurationChanged, newConfig: $newConfig")
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return super.onCreateAnimation(transit, enter, nextAnim)
            .also { logI("${this.hashCode()} onCreateAnimation") }
    }

    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator? {
        return super.onCreateAnimator(transit, enter, nextAnim)
            .also { logI("${this.hashCode()} onCreateAnimator") }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        logI("${this.hashCode()} onLowMemory")
    }

}
