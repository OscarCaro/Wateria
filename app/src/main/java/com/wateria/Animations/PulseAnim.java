package com.wateria.Animations;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;

public class PulseAnim implements Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {

    private LottieAnimationView lottie;

    public void show(LottieAnimationView lottie){
        this.lottie = lottie;
        lottie.addAnimatorListener(this);
        lottie.addAnimatorUpdateListener(this);

        lottie.setVisibility(View.VISIBLE);
        lottie.playAnimation();
    }

    @Override
    public void onAnimationStart(Animator animator) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {
        lottie.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        if(((Float) valueAnimator.getAnimatedValue() * 100) >= 60){
            lottie.cancelAnimation();
        }
    }
}
