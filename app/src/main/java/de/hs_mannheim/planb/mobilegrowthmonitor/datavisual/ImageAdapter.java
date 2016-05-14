package de.hs_mannheim.planb.mobilegrowthmonitor.datavisual;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

import de.hs_mannheim.planb.mobilegrowthmonitor.R;
import de.hs_mannheim.planb.mobilegrowthmonitor.imageprocessing.ImageProcess;

/**
 * Created by eikood on 05.05.2016.
 */
public class ImageAdapter extends BaseAdapter{

    private Context context;
    private Animator currentAnimator;
    private ArrayList<Bitmap> bitmapList;
    private int shortAnimationDuration = 350;
    private ImageProcess imageProcess;

    public ImageAdapter(Context context, ArrayList<Bitmap> bitmapList) {
        this.context = context;
        this.bitmapList = bitmapList;
        imageProcess = new ImageProcess(context);
    }

    @Override
    public int getCount() {
        return this.bitmapList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(this.context);
            imageView.setLayoutParams(new GridView.LayoutParams(115, 115));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageBitmap(this.bitmapList.get(position));
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                zoomImageFromThumb(view, position);
            }
        });
        return imageView;
    }

    private void zoomImageFromThumb(final View thumbView, int position) {
        // If there's an animation in progress, cancel it immediately and
        // proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }
        // Load the high-resolution "zoomed-in" image and Buttons for OpenCV-Stuff
        final ImageView fullscreenImageView = (ImageView) ((Activity) context).findViewById(R.id.iv_fullscreenImage);
        final Button sizeMeasurement = (Button) ((Activity) context).findViewById(R.id.btn_size_measurement);

        fullscreenImageView.setImageBitmap(this.bitmapList.get(position));

        String path = GalleryView.pathList.get(position);

        ImgAdptOnClickListener imgAdptOnClickListener = new ImgAdptOnClickListener(path);
        sizeMeasurement.setOnClickListener(imgAdptOnClickListener);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step
        // involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();
        // The start bounds are the global visible rectangle of the thumbnail,
        // and the
        // final bounds are the global visible rectangle of the container view.
        // Also
        // set the container view's offset as the origin for the bounds, since
        // that's
        // the origin for the positioning animation properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        ((Activity) context).findViewById(R.id.container).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);
        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the
        // "center crop" technique. This prevents undesirable stretching during
        // the animation.
        // Also calculate the start scaling factor (the end scaling factor is
        // always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }
        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins,
        // it will position the zoomed-in view in the place of the thumbnail.
        thumbView.setAlpha(0f);
        fullscreenImageView.setVisibility(View.VISIBLE);
        sizeMeasurement.setVisibility(View.VISIBLE);
        // Set the pivot point for SCALE_X and SCALE_Y transformations to the
        // top-left corner of
        // the zoomed-in view (the default is the center of the view).
        fullscreenImageView.setPivotX(0f);
        fullscreenImageView.setPivotY(0f);
        // Construct and run the parallel animation of the four translation and
        // scale properties
        // (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set.play(
                ObjectAnimator.ofFloat(fullscreenImageView, View.X, startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(fullscreenImageView, View.Y, startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(fullscreenImageView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(fullscreenImageView, View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;
        // Upon clicking the zoomed-in image, it should zoom back down to the
        // original bounds
        // and show the thumbnail instead of the expanded image.
        final float startScaleFinal = startScale;
        fullscreenImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }
                // Animate the four positioning/sizing properties in parallel,
                // back to their
                // original values.
                AnimatorSet set = new AnimatorSet();
                set.play(
                        ObjectAnimator.ofFloat(fullscreenImageView, View.X,
                                startBounds.left))
                        .with(ObjectAnimator.ofFloat(fullscreenImageView, View.Y,
                                startBounds.top))
                        .with(ObjectAnimator.ofFloat(fullscreenImageView,
                                View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator.ofFloat(fullscreenImageView,
                                View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        fullscreenImageView.setVisibility(View.GONE);
                        sizeMeasurement.setVisibility(View.GONE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        fullscreenImageView.setVisibility(View.GONE);
                        sizeMeasurement.setVisibility(View.GONE);
                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;
            }
        });
    }

    public class ImgAdptOnClickListener implements View.OnClickListener {
        String path;
        public ImgAdptOnClickListener(String path) {
            this.path = path;
        }
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_size_measurement:
                    imageProcess.sizeMeasurement(path);
                    break;
            }
        }
    }

}
