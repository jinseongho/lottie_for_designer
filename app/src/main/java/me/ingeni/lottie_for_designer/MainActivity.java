package me.ingeni.lottie_for_designer;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;

import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import me.ingeni.lottie_for_designer.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_FILE = 1004;

    @BindView(R.id.root)
    RelativeLayout mRlRoot;
    @BindView(R.id.root2)
    RelativeLayout mRlRoot2;
    @BindView(R.id.animation_view)
    LottieAnimationView mAnimationView;
    @BindView(R.id.button_control_layout)
    LinearLayout mLlTestButtonControlLayout;

    private RelativeLayout mButtonLView;
    private ActivityMainBinding mBinding;
    private int mButtonWidth = (int) dpToPx(46);
    private int mButtonHeight = (int) dpToPx(46);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_upload: {
                Intent importFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                importFileIntent.setType("*/*");
                importFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(importFileIntent, REQUEST_FILE);
                return true;
            }
            case R.id.menu_button_test: {
                onDrawTestButton((int) dpToPx(46), (int) dpToPx(46));
                return true;
            }

            case R.id.menu_animation_preview: {
                mRlRoot2.removeAllViews();

                RelativeLayout.LayoutParams rootParams =
                        new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rootParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                mAnimationView = new LottieAnimationView(MainActivity.this);
                mAnimationView.setLayoutParams(rootParams);
                mRlRoot2.addView(mAnimationView);
                mLlTestButtonControlLayout.setVisibility(View.GONE);
                return true;
            }

            case R.id.menu_background: {
                mRlRoot.setSelected(!mRlRoot.isSelected());
                mRlRoot.setBackground(ContextCompat.getDrawable(this, mRlRoot.isSelected() ? android.R.color.white : android.R.color.black));
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == REQUEST_FILE) {
                if (getFileName(data.getData()).substring(getFileName(data.getData()).lastIndexOf(".") + 1, getFileName(data.getData()).length()).equals("json")) {
                    try {
                        JsonData jsonData = new JsonData(getFileName(data.getData()), "json");
                        mBinding.setJsonData(jsonData);
                        mAnimationView.setImageAssetsFolder("/storage/emulated/0/Download/");
                        InputStream fis = getContentResolver().openInputStream(data.getData());
                        LottieComposition.Factory
                                .fromInputStream(this, fis, new OnCompositionLoadedListener() {
                                    @Override
                                    public void onCompositionLoaded(LottieComposition composition) {
                                        setComposition(composition);
                                    }
                                });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, getString(R.string.message_error_01), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.message_error_02), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setComposition(LottieComposition composition) {
        mAnimationView.setComposition(composition);
        mAnimationView.loop(true);
        mAnimationView.playAnimation();
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @OnTextChanged(R.id.test_button_width_edit)
    public void onButtonWidthTextChanged(final CharSequence s) {
        if (!TextUtils.isEmpty(s)) {
            try {
                mButtonWidth = Integer.valueOf(s.toString());
                onDrawTestButton(mButtonWidth, mButtonHeight);
            } catch (NumberFormatException e) {
                mButtonWidth = (int) dpToPx(46);
            }
        }
    }

    @OnTextChanged(R.id.test_button_height_edit)
    public void onButtonHeightTextChanged(CharSequence s) {
        if (!TextUtils.isEmpty(s)) {
            try {
                mButtonHeight = Integer.valueOf(s.toString());
                onDrawTestButton(mButtonWidth, mButtonHeight);
            } catch (NumberFormatException e) {
                mButtonHeight = (int) dpToPx(46);
            }
        }
    }

    @OnTextChanged(R.id.test_button_color_edit)
    public void onButtonColorTextChanged(CharSequence s) {
        if (!TextUtils.isEmpty(s)) {
            onDrawTestButtonColor(mButtonLView, s.toString());
        }
    }

    private void onDrawTestButtonColor(RelativeLayout buttonView, String hexValue) {
        try {
            buttonView.setBackgroundColor(Color.parseColor("#" + hexValue));
        } catch (IllegalArgumentException e) {
            //
        }
    }

    private void onDrawTestButton(int width, int height) {
        mRlRoot2.removeAllViews();
        mLlTestButtonControlLayout.setVisibility(View.VISIBLE);
        RelativeLayout buttonLView = new RelativeLayout(this);
        RelativeLayout.LayoutParams buttonParams =
                new RelativeLayout.LayoutParams(width, height);
        buttonParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        buttonLView.setLayoutParams(buttonParams);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.GRAY);
        gd.setCornerRadius(10);
        gd.setStroke(2, Color.WHITE);
        buttonLView.setBackground(gd);

        mAnimationView = new LottieAnimationView(MainActivity.this);
        buttonLView.addView(mAnimationView);
        mAnimationView.cancelAnimation();
        buttonLView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAnimationView.playAnimation();
                mAnimationView.loop(false);
            }
        });
        mButtonLView = buttonLView;
        onDrawTestButtonColor(buttonLView, "ff0000");
        mRlRoot2.addView(buttonLView);
    }

    private float dpToPx(float dp) {
        return dp * Resources.getSystem().getDisplayMetrics().density;
    }
}

