package ru.surf.learn2invest.ui

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.widget.TextView

fun TextView.tapOn() {
    val rotating = ValueAnimator.ofFloat(0f, 360f).also {
        it.duration = 500

        it.addUpdateListener { animator ->
            val rotateValue = animator.animatedValue as Float

            this.rotation = rotateValue
        }
    }

    val flexBackground = ValueAnimator.ofFloat(1f, 0f, 1f).also {
        it.duration = 800

        it.addUpdateListener { animator ->
            val rotateValue = animator.animatedValue as Float

            this.alpha = rotateValue
        }
    }

    AnimatorSet().apply {
        playTogether(rotating, flexBackground)
    }.start()

}