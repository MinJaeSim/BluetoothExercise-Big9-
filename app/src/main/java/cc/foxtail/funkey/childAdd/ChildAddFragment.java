package cc.foxtail.funkey.childAdd;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cc.foxtail.funkey.OnHeightInputDialogListener;
import cc.foxtail.funkey.R;
import cc.foxtail.funkey.data.Child;
import cc.foxtail.funkey.dialog.HeightInputDialogFragment;

import static cc.foxtail.funkey.navigation.NavigationActivity.soundId;
import static cc.foxtail.funkey.navigation.NavigationActivity.soundPool;


public class ChildAddFragment extends Fragment implements ChildAddContract.View {

    private String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA}; //권한 설정 변수

    private Uri photoUri;

    private static final int PICK_FROM_CAMERA = 1; //카메라 촬영으로 사진 가져오기
    private static final int PICK_FROM_ALBUM = 2; //앨범에서 사진 가져오기
    private static final int CROP_FROM_CAMERA = 3; //가져온 사진 자르기

    private static final int MULTIPLE_PERMISSIONS = 101; //권한 동의 여부 문의 후 CallBack 함수에 쓰일 변수

    private EditText nameEditText;
    private ImageView profileImageView;
    private ImageView nameEditButton;
    private ImageView photoEditButton;
    private ImageView profileImageIcon;
    private EditText weightEditTextView;
    private EditText heightEditTextView;
    private RadioButton boyRadioButton;
    private RadioButton girlRadioButton;
    private TextView ageTextView;
    private String profileImageUrl;
    private ProgressDialog progressDialog;
    private ChildAddPresenter childAddPresenter;
    private TextView birthTextView;
    private TextView predictHeightTextView;
    private Date birthTimeStamp;
    private double heightAverage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child_add, container, false);

        progressDialog = new ProgressDialog(getActivity());
        setHasOptionsMenu(true);
        childAddPresenter = new ChildAddPresenter();
        childAddPresenter.setView(this);

        nameEditText = view.findViewById(R.id.name_text_view);
        nameEditButton = view.findViewById(R.id.name_edit_button);
        weightEditTextView = view.findViewById(R.id.weight_edit_text);
        heightEditTextView = view.findViewById(R.id.height_edit_text);
        boyRadioButton = view.findViewById(R.id.radio_button_boy);
        girlRadioButton = view.findViewById(R.id.radio_button_girl);
        photoEditButton = view.findViewById(R.id.camera_button);
        profileImageIcon = view.findViewById(R.id.profile_image_icon);
        boyRadioButton.setChecked(true);
        profileImageView = view.findViewById(R.id.profile_image_view);

        LinearLayout birthLayout = view.findViewById(R.id.birth_date_layout);

        birthTextView = view.findViewById(R.id.birth_date_text_view);
        ageTextView = view.findViewById(R.id.age_text_view);


        profileImageUrl = "";

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);

                birthTimeStamp = calendar.getTime();
                String myFormat = "yyyy.MM.dd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
                String dateStr = sdf.format(calendar.getTime());
                ageTextView.setText(getAge(year, monthOfYear, dayOfMonth));
                birthTextView.setText(dateStr);
            }

        };

        birthLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundId, 1, 1, 0, 0, 1);
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(getContext(), R.style.DialogTheme, dateSetListener, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        LinearLayout predictHeightLayout = view.findViewById(R.id.predict_height_layout);
        predictHeightTextView = view.findViewById(R.id.predict_height_text_view);
        predictHeightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundPool.play(soundId, 1, 1, 0, 0, 1);
                FragmentManager fm = getFragmentManager();
                HeightInputDialogFragment heightInputDialogFragment = new HeightInputDialogFragment();
                heightInputDialogFragment.setTargetFragment(ChildAddFragment.this, 888);
                heightInputDialogFragment.show(fm, "InputDialog");
                heightInputDialogFragment.setOnHeightInputDialogListener(new OnHeightInputDialogListener() {
                    @Override
                    public void onResult(int average) {
                        heightAverage = average;
                        if (boyRadioButton.isChecked())
                            predictHeightTextView.setText("" + (average + 6.5));
                        else
                            predictHeightTextView.setText("" + (average - 6.5));
                    }
                });
            }
        });

        boyRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundPool.play(soundId, 1, 1, 0, 0, 1);
                if (heightAverage != 0) {
                    predictHeightTextView.setText("" + (heightAverage + 6.5));
                }

            }
        });

        girlRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundPool.play(soundId, 1, 1, 0, 0, 1);
                if (heightAverage != 0) {
                    predictHeightTextView.setText("" + (heightAverage - 6.5));
                }

            }
        });
        Button addButton = view.findViewById(R.id.child_add_button);
        Button cancelButton = view.findViewById(R.id.child_cancel_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundId, 1, 1, 0, 0, 1);
                if (!checkError())
                    childAddPresenter.uploadImages(photoUri);
            }
        });

        photoEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundId, 1, 1, 0, 0, 1);
                checkPermissions();
                setPopupMenu();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundId, 1, 1, 0, 0, 1);
                popBackStack();
            }
        });

        nameEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundPool.play(soundId, 1, 1, 0, 0, 1);
                nameEditText.requestFocus();
                nameEditText.setText("");
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(nameEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });


        if (getArguments() == null) {
            childAddPresenter.setUpdateMode(false);
        } else {
            childAddPresenter.setUpdateMode(true);
            nameEditText.setEnabled(false);
            nameEditButton.setEnabled(false);
            setUserData();
        }

        return view;
    }

    private String getAge(int year, int month, int day) {
        Calendar birth = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        birth.set(year, month, day);

        int calcAge = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
        int calcMonth = (today.get(Calendar.MONTH) + 1) - birth.get(Calendar.MONTH);

        if (calcMonth < 0) {
            calcMonth = 12 - Math.abs(calcMonth);
            calcMonth = (calcAge - 1) * 12 + calcMonth;
        } else {
            calcMonth = calcAge * 12 + calcMonth;
        }


        return (calcAge + 1) + "세/" + calcMonth + "개월";
    }

    private void checkPermissions() {
        List<String> permissionList = new ArrayList<>();

        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(getActivity(), permission);
            if (result != PackageManager.PERMISSION_GRANTED)  //사용자가 해당 권한을 가지고 있지 않을 경우 리스트에 해당 권한명 추가
                permissionList.add(permission);
        }

        if (!permissionList.isEmpty())
            ActivityCompat.requestPermissions(getActivity(), permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);

    }

    private void setPopupMenu() {
        PopupMenu popup = new PopupMenu(getActivity(), photoEditButton);
        popup.getMenuInflater()
                .inflate(R.menu.camera_popup_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.popup_take_picture)
                    takePhoto();
                else if (item.getItemId() == R.id.popup_photo_select)
                    goToAlbum();
                return true;
            }
        });
        popup.show();
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(getActivity(),
                    "foxtail.cc.funkey.provider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, PICK_FROM_CAMERA);
        }
    }

    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "IP" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/test/"); //test라는 경로에 이미지를 저장하기 위함
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == PICK_FROM_ALBUM) {
                if (data == null)
                    return;

                photoUri = data.getData();
                profileImageIcon.setVisibility(View.GONE);
                cropImage();
            } else if (requestCode == PICK_FROM_CAMERA) {
                System.out.println("pick from camera");
                cropImage();
                MediaScannerConnection.scanFile(getActivity(),
                        new String[]{photoUri.getPath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                            }
                        });
            } else if (requestCode == CROP_FROM_CAMERA) {
                try {
                    profileImageView.setImageURI(photoUri);
                    profileImageIcon.setVisibility(View.GONE);
                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage());
                }
            }
        }
    }


    public void cropImage() {
        System.out.println("crop");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            getActivity().grantUriPermission("com.android.camera", photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");

        List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities(intent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            getActivity().grantUriPermission(list.get(0).activityInfo.packageName, photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);


        int size = list.size();
        if (size == 0) {
            Toast.makeText(getActivity(), "취소 되었습니다.", Toast.LENGTH_SHORT).show();
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }

            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);

            File croppedFileName = null;
            try {
                croppedFileName = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            File folder = new File(Environment.getExternalStorageDirectory() + "/test/");
            File tempFile = new File(folder.toString(), croppedFileName.getName());

            photoUri = FileProvider.getUriForFile(getActivity(),
                    "foxtail.cc.funkey.provider", tempFile);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }


            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()); //Bitmap 형태로 받기 위해 해당 작업 진행

            Intent i = new Intent(intent);
            ResolveInfo res = list.get(0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                getActivity().grantUriPermission(res.activityInfo.packageName, photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            startActivityForResult(i, CROP_FROM_CAMERA);
        }

    }


    private boolean checkError() {
        if (Objects.equals(nameEditText.getText().toString(), "이름")) {
            nameEditButton.requestFocus();
            nameEditText.setError("이름을 입력해주세요");
            return true;
        } else if (weightEditTextView.getText().toString().length() == 0) {
            weightEditTextView.requestFocus();
            weightEditTextView.setError("몸무게를 입력해주세요");
            return true;
        } else if (heightEditTextView.getText().toString().length() == 0) {
            heightEditTextView.requestFocus();
            heightEditTextView.setError("키를 입력해주세요");
            return true;
        } else if (predictHeightTextView.getText().toString().length() == 0) {
            predictHeightTextView.requestFocus();
            predictHeightTextView.setError("부모키를 입력해주세요");
            return true;
        } else if (birthTimeStamp == null) {
            birthTextView.requestFocus();
            birthTextView.setError("생년월일을 입력해주세요");
            return true;
        }

        return false;

    }

    public static ChildAddFragment newInstance(Child child, String documentKey) {
        ChildAddFragment childAddFragment = new ChildAddFragment();
        Bundle bundle = new Bundle();
        bundle.putString("NAME", child.getUserName());
        bundle.putString("IMAGE_URL", child.getProfileImageUrl());
        bundle.putString("GENDER", child.getSex());
        bundle.putLong("BIRTH", child.getBirthTimeStamp());
        bundle.putString("NICKNAME", child.getNickName());
        bundle.putInt("WEIGHT", child.getWeight());
        bundle.putInt("HEIGHT", child.getHeight());
        bundle.putString("PREDICT_HEIGHT", child.getPredictHeight());
        bundle.putString("DOCUMENT_KEY", documentKey);
        childAddFragment.setArguments(bundle);
        return childAddFragment;
    }

    @Override
    public void showDialog(String message) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.basic_dialog);

        TextView text = dialog.findViewById(R.id.basic_dialog_text_view);
        text.setText(message);

        Button dialogButton = dialog.findViewById(R.id.basic_dialog_ok_button);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }


    @Override
    public void showProgressDialog(String text) {
        progressDialog.setMessage(text);
        progressDialog.show();
    }

    @Override
    public Child getChildData() {
        String name = nameEditText.getText().toString();
        String gender = boyRadioButton.isChecked() ? "남" : "여";
        String predictHeight = predictHeightTextView.getText().toString();

        int weight = Integer.valueOf(weightEditTextView.getText().toString());
        int height = Integer.valueOf(heightEditTextView.getText().toString());
        String age = ageTextView.getText().toString();

        String uid = FirebaseAuth.getInstance().getUid();

        //새로운 어린이 생성될떄
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i <= 50; i++)
            list.add(0);

        return new Child(name, gender, birthTimeStamp.getTime(), age, "", weight, height, predictHeight, uid, profileImageUrl, list);
    }

    @Override
    public void popBackStack() {
        getFragmentManager().popBackStack();
    }

    @Override
    public void setProfileImageUrl(String imageUrl) {
        profileImageUrl = imageUrl;
    }


    @Override
    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }

    private void setUserData() {
        childAddPresenter.setNickNameChecked(true);
        String name = getArguments().getString("NAME");
        String gender = getArguments().getString("GENDER");
        long birthDate = getArguments().getLong("BIRTH");
        int weight = getArguments().getInt("WEIGHT");
        int height = getArguments().getInt("HEIGHT");
        String predictHeight = getArguments().getString("PREDICT_HEIGHT");
        System.out.println("TEST : " + predictHeight);

        childAddPresenter.setUpdateKey(getArguments().getString("DOCUMENT_KEY"));

        nameEditText.setText(name);

        if (Objects.equals(gender, "남")) {
            boyRadioButton.setChecked(true);
            if (predictHeight != null)
                heightAverage = Double.valueOf(predictHeight) - 6.5;
        } else {
            girlRadioButton.setChecked(true);
            if (predictHeight != null)
                heightAverage = Double.valueOf(predictHeight) + 6.5;
        }

        Date date = new Date(birthDate);
        birthTimeStamp = date;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        profileImageUrl = getArguments().getString("IMAGE_URL");

        ageTextView.setText(getAge(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));

        String myFormat = "yyyy.MM.dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
        String dateStr = sdf.format(calendar.getTime());
        ageTextView.setText(getAge(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
        birthTextView.setText(dateStr);

        Glide.with(profileImageView)
                .load(profileImageUrl)
                .apply(new RequestOptions()
                        .override(150, 150)
                        .placeholder(R.drawable.child_image)
                )
                .into(profileImageView);

        profileImageIcon.setVisibility(View.GONE);

        weightEditTextView.setText("" + weight);
        heightEditTextView.setText("" + height);
        predictHeightTextView.setText(predictHeight);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_bluetooth).setVisible(false);
        menu.findItem(R.id.menu_search).setVisible(false);
    }


}
