package com.numberinput.one_new

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

private const val KEYBOARD_MIN_HEIGHT_RATIO = 0.15
object KeyboardStateEvent {
    @Suppress("unused")
    @JvmStatic
    fun setEventListener(
        activity: Activity,
        lifecycleOwner: LifecycleOwner,
        listener: KeyboardStateEventListener
    ) {

        val unregistrar = registerEventListener(activity, listener)
        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                lifecycleOwner.lifecycle.removeObserver(this)
                unregistrar.unregister()
            }
        })
    }

    /**
     * Set keyboard visibility change event listener.
     * This automatically remove registered event listener when the Activity is destroyed
     *
     * @param activity Activity
     * @param listener KeyboardStateEventListener
     */
    @Suppress("unused")
    @JvmStatic
    fun setEventListener(
        activity: Activity,
        listener: KeyboardStateEventListener
    ) {

        val unregistrar = registerEventListener(activity, listener)
        activity.application
            .registerActivityLifecycleCallbacks(object :
                AutoActivityLifecycleCallback(activity) {
                override fun onTargetActivityDestroyed() {
                    unregistrar.unregister()
                }
            })
    }

    /**
     * Set keyboard visibility change event listener.
     *
     * @param activity Activity
     * @param listener KeyboardStateEventListener
     * @return Unregistrar
     */
    fun registerEventListener(
        activity: Activity?,
        listener: KeyboardStateEventListener?
    ): UnregisterEvent {

        if (activity == null) {
            throw NullPointerException("Parameter:activity must not be null")
        }

        val softInputAdjust =
            activity.window.attributes.softInputMode and WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST

        // fix for #37 and #38.
        // The window will not be resized in case of SOFT_INPUT_ADJUST_NOTHING
        val isNotAdjustNothing =
            softInputAdjust and WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING != WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        require(isNotAdjustNothing) { "Parameter:activity window SoftInputMethod is SOFT_INPUT_ADJUST_NOTHING. In this case window will not be resized" }

        if (listener == null) {
            throw NullPointerException("Parameter:listener must not be null")
        }

        val activityRoot = getActivityRoot(activity)

        val layoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {

            private var wasOpened = false

            override fun onGlobalLayout() {

                val isOpen = isKeyboardVisible(activity)

                if (isOpen == wasOpened) {
                    // keyboard state has not changed
                    return
                }

                wasOpened = isOpen

                listener.onVisibilityChanged(isOpen)
            }
        }
        activityRoot.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)

        return UnregisterCallback(activity, layoutListener)
    }

    /**
     * Determine if keyboard is visible
     *
     * @param activity Activity
     * @return Whether keyboard is visible or not
     */
    fun isKeyboardVisible(activity: Activity): Boolean {
        val r = Rect()

        val activityRoot = getActivityRoot(activity)

        activityRoot.getWindowVisibleDisplayFrame(r)

        val location = IntArray(2)
        getContentRoot(activity).getLocationOnScreen(location)

        val screenHeight = activityRoot.rootView.height
        val heightDiff = screenHeight - r.height() - location[1]

        return heightDiff > screenHeight * KEYBOARD_MIN_HEIGHT_RATIO
    }

    internal fun getActivityRoot(activity: Activity): View {
        return getContentRoot(activity).rootView
    }

    private fun getContentRoot(activity: Activity): ViewGroup {
        return activity.findViewById(android.R.id.content)
    }
}