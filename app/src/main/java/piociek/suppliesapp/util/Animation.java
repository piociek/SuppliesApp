package piociek.suppliesapp.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

public class Animation {

    private static final int ANIMATION_LENGTH_MSEC = 500;
    private static final float MAX_ALPHA = 1f;
    private static final float MIN_ALPHA = 0f;

    private Animation() {
    }

    public static void animateTransitionBetweenViews(View toShow, View toFade) {
        toShow.setAlpha(MIN_ALPHA);
        toShow.setVisibility(View.VISIBLE);

        toShow.animate()
                .alpha(MAX_ALPHA)
                .setDuration(ANIMATION_LENGTH_MSEC)
                .setListener(null);

        toFade.animate()
                .alpha(MIN_ALPHA)
                .setDuration(ANIMATION_LENGTH_MSEC)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        toFade.setVisibility(View.GONE);
                    }
                });
    }
}
