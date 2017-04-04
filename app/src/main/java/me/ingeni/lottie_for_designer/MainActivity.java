package me.ingeni.lottie_for_designer;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;

import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.ingeni.lottie_for_designer.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int REQUEST_FILE = 1004;

    @BindView(R.id.root)
    RelativeLayout mLlRoot;
    @BindView(R.id.animation_view)
    LottieAnimationView mAnimationView;
    @BindView(R.id.bg_btn)
    ImageButton mIbBgBtn;


    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ButterKnife.bind(this);
        mIbBgBtn.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.upload, menu);
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

    public String getFileName(Uri uri) {
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

    @Override
    public void onClick(View v) {
        mLlRoot.setBackground(ContextCompat.getDrawable(this, android.R.color.white));
    }

    //    @SuppressLint("NewApi")
//    public String getRealPathFromURI_API19(Context context, Uri uri) {
//        String filePath = "";
//        String wholeID = DocumentsContract.getDocumentId(uri);
//
//        // Split at colon, use second item in the array
//        String id = wholeID.split(":")[1];
//
//        String[] column = {MediaStore.Images.Media.DATA};
//
//        // where id is equal to
//        String sel = MediaStore.Images.Media._ID + "=?";
//
//        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                column, sel, new String[]{id}, null);
//
//        int columnIndex = cursor.getColumnIndex(column[0]);
//
//        if (cursor.moveToFirst()) {
//            filePath = cursor.getString(columnIndex);
//        }
//        cursor.close();
//        return filePath;
//    }
//
//
//    @SuppressLint("NewApi")
//    public String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
//        String[] proj = {MediaStore.Images.Media.DATA};
//        String result = null;
//
//        CursorLoader cursorLoader = new CursorLoader(
//                context,
//                contentUri, proj, null, null, null);
//        Cursor cursor = cursorLoader.loadInBackground();
//
//        if (cursor != null) {
//            int column_index =
//                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            result = cursor.getString(column_index);
//        }
//        return result;
//    }
//
//    public String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri) {
//        String[] proj = {MediaStore.Images.Media.DATA};
//        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
//        int column_index
//                = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
////        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
////        }
//    }
//
//    public boolean isStoragePermissionGranted() {
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
//                    == PackageManager.PERMISSION_GRANTED &&
//                    checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                            == PackageManager.PERMISSION_GRANTED) {
//                return true;
//            } else {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//                return false;
//            }
//        } else {
//            return true;
//        }
//    }
}

