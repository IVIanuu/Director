package com.ivianuu.director.sample.changehandler

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import com.ivianuu.director.ChangeData
import com.ivianuu.director.common.changehandler.AnimatorChangeHandler

class ScaleFadeChangeHandler : AnimatorChangeHandler() {

    override fun getAnimator(
        changeData: ChangeData,
        toAddedToContainer: Boolean
    ): Animator {
        val (_, from, to) = changeData

        val animator = AnimatorSet()
        if (to != null) {
            val start = if (toAddedToContainer) 0f else to.alpha
            animator.play(ObjectAnimator.ofFloat(to, View.ALPHA, start, 1f))
        }

        if (from != null) {
            animator.play(ObjectAnimator.ofFloat(from, View.ALPHA, 0f))
            animator.play(ObjectAnimator.ofFloat(from, View.SCALE_X, 0.8f))
            animator.play(ObjectAnimator.ofFloat(from, View.SCALE_Y, 0.8f))
        }

        return animator
    }

    override fun resetFromView(from: View) {
    }
}
