package ru.surf.learn2invest.utils

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object Icons {
    var ok: Drawable? = null
    var no: Drawable? = null
}


fun ImageView.isOk(): Boolean = this.drawable == Icons.ok

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

fun ImageView.gotoCenter(
    truePIN: Boolean,
    needReturn: Boolean,
    lifecycleScope: LifecycleCoroutineScope,
    doAfter: () -> Unit = {}
) {
    val home = (this.layoutParams as ConstraintLayout.LayoutParams).horizontalBias
    val gotoCenter = ValueAnimator.ofFloat(
        home,
        0.5f
    ).also {
        it.duration = 300
        it.addUpdateListener { animator ->
            val biasValue = animator.animatedValue as Float
            val params = this.layoutParams as ConstraintLayout.LayoutParams
            params.horizontalBias = biasValue
            this.layoutParams = params
        }
        it.startDelay
    }

    val goPoDomam = ValueAnimator.ofFloat(
        0.5f,
        home
    ).also {
        it.duration = 300
        it.addUpdateListener { animator ->
            val biasValue = animator.animatedValue as Float
            val params = this.layoutParams as ConstraintLayout.LayoutParams
            params.horizontalBias = biasValue
            this.layoutParams = params
        }
    }

    goPoDomam.doOnEnd {
        doAfter()
    }

    gotoCenter.start()

    gotoCenter.doOnEnd {
        lifecycleScope.launch(Dispatchers.Main) {
            this@gotoCenter.drawable.setTint(
                if (truePIN) {
                    Color.GREEN
                } else {
                    Color.RED
                }
            )
            delay(800)
            if (needReturn || !truePIN) {
                goPoDomam.doOnStart {
                    this@gotoCenter.drawable.setTint(Color.WHITE)
                }
                goPoDomam.start()
            }
        }

    }
}

