package com.fatbook.fatbookapp.ui.activity.fill_additional_info;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.ActivityFillAdditionalInfoBinding;
import com.fatbook.fatbookapp.util.UserUtil;

import org.apache.commons.io.IOUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FillAdditionalInfoActivity extends AppCompatActivity {

    private ActivityFillAdditionalInfoBinding binding;

    private ActivityResultLauncher<String> choosePhotoFromGallery;

    private static final int PICK_IMAGE_REQUEST = 9544;

    String part_image;
    Uri selectedImage;

    private User user;

    private File image;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFillAdditionalInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FillAdditionalInfoViewModel viewModel =
                new ViewModelProvider(this).get(FillAdditionalInfoViewModel.class);

        Intent introduceIntent = getIntent();
        user = (User) introduceIntent.getSerializableExtra(UserUtil.USER);

        binding.buttonFillAddSave.setOnClickListener(view -> {
            user.setName(binding.editTextFillAddName.getText().toString());
//            user.setBirthday();
            user.setBio(binding.editTextFillAddBio.getText().toString());
            viewModel.saveUser(binding.getRoot(), user, image);
        });


//        choosePhotoFromGallery = registerForActivityResult(new ActivityResultContracts.GetContent(),
//                uri -> {
//
//                    verifyStoragePermissions(this);
////                    binding.imagebuttonFillAddPhoto.setImageURI(uri);
//                    user.setUri(uri);
//
//                    String[] imageProjection = {MediaStore.Images.Media.DATA};
//                    Cursor cursor = getContentResolver().query(uri, imageProjection, null, null, null);
//                    if (cursor != null) {
//                        cursor.moveToFirst();
//                        int indexImage = cursor.getColumnIndex(imageProjection[0]);
//                        part_image = cursor.getString(indexImage);
////                        imgPath.setText(part_image);                                                        // Get the image file absolute path
//                        Bitmap bitmap = null;
//                        try {
//                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//                            System.out.println();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        binding.imagebuttonFillAddPhoto.setImageBitmap(bitmap);                                                   // Set the ImageView with the bitmap of the image
//                    }
//                });

        binding.imagebuttonFillAddPhoto.setOnClickListener(view -> {
//            choosePhotoFromGallery.launch("image/*");
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Open Gallery"), PICK_IMAGE_REQUEST);
        });
    }

    @SneakyThrows
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                selectedImage = data.getData();
//                ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(selectedImage, "r", null);
//                FileInputStream fileInputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
//                File file4 = new File(getCacheDir(), getContentResolver());
//                FileOutputStream fileOutputStream = new FileOutputStream(file4);
//                fileInputStream.


                user.setUri(selectedImage);
                File file = new File(selectedImage.getPath());
//                String[] imageProjection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
//                    int indexImage = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int indexImage = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                    part_image = cursor.getString(indexImage);


//                    FileInputStream fileInputStream = new FileInputStream(getContentResolver().openFileDescriptor(selectedImage, "r", null).getFileDescriptor());
                    image = new File(getCacheDir(), part_image);
//                    FileOutputStream fileOutputStream = new FileOutputStream(image);
//                    IOUtils.copy(fileInputStream, fileOutputStream);
//                    imgPath.setText(part_image);                                                        // Get the image file absolute path
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    binding.imagebuttonFillAddPhoto.setImageBitmap(bitmap);                                                         // Set the ImageView with the bitmap of the image

//                    File file1 = new File("path");
//                    OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
//                    os.close();
                }



                                                                      // Create a file using the absolute path of the image
//                RequestBody reqBody = RequestBody.create(MediaType.parse("multipart/form-file"), imageFile);
//                MultipartBody.Part partImage = MultipartBody.Part.createFormData("file", imageFile.getName(), reqBody);

                System.out.println();

            }
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }


    }
}
