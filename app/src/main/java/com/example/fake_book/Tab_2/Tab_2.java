package com.example.fake_book.Tab_2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fake_book.MainActivity;
import com.example.fake_book.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Tab_2 extends Fragment {

    //variables for fab animation
    private FloatingActionButton fab, fab_img, fab_cam;
    private Animation fab_open, fab_close, fab_rotate, fab_rotate_backward;
    private Boolean isFabOpen = false;
    ArrayList<Uri> imagelist;
    private CardAdapter adapter;

    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA= 2;

    private Uri imgUri, photoURI;
    private String mCurrentPhotoPath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_2_main, container, false);
        tedPermission();

        imagelist = MainActivity.imagelist;

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        final RecyclerView recyclerView = view.findViewById(R.id.folder_album_recycler_view) ;
        GridLayoutManager mGridLayoutManager;
        mGridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(mGridLayoutManager);
        adapter = new CardAdapter(imagelist) ;
        recyclerView.setAdapter(adapter);

        //variables for fab animations
        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);
        fab_rotate = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_rotate);
        fab_rotate_backward = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_rotate_backward);

        fab = view.findViewById(R.id.fab_menu);
        fab_img = view.findViewById(R.id.fab_img);
        fab_cam = view.findViewById(R.id.fab_cam);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim();
            }
        });

        fab_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim();
                selectAlbum();
                (adapter).notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(Integer.MAX_VALUE);
            }
        });

        fab_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim();
                takePhoto();
                (adapter).notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(Integer.MAX_VALUE);
            }
        });

        return view;
    }

    private void tedPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() { }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getActivity(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.with(Objects.requireNonNull(getContext()))
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission] ")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }

    private void selectAlbum(){
        //앨범 열기
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    private void takePhoto(){
        // 촬영 후 이미지 가져옴
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(getActivity().getPackageManager())!=null){
                File photoFile = null;
                try{
                    photoFile = createImageFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(photoFile!=null){
                    Uri providerURI = FileProvider.getUriForFile(getContext(),getActivity().getPackageName(),photoFile);
                    imgUri = providerURI;
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, providerURI);
                    startActivityForResult(intent, PICK_FROM_CAMERA);
                }
            }
        }else{
            Log.v("알림", "저장공간에 접근 불가능");
        }
    }

    private File createImageFile() throws IOException {
        String imgFileName = System.currentTimeMillis() + ".jpg";
        File imageFile;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "ireh");
        if(!storageDir.exists()){
            Log.v("알림","storageDir 존재 x " + storageDir.toString());
            storageDir.mkdirs();
        }
        Log.v("알림","storageDir 존재함 " + storageDir.toString());
        imageFile = new File(storageDir,imgFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK){
            Toast.makeText(getActivity(), "result code: "+ resultCode, Toast.LENGTH_SHORT).show();
            return;
        }
        photoURI = imgUri;

        switch (requestCode){
            case PICK_FROM_ALBUM : {
                //앨범에서 가져오기
                if(data.getData()!=null){
                    try{
                        photoURI = data.getData();
                        //이미지뷰에 이미지 셋팅
                        if (!imagelist.add(photoURI))
                            Toast.makeText(getActivity(), "list add failed", Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.v("알림","앨범에서 가져오기 에러");
                    }
                }
                break;
            }

            case PICK_FROM_CAMERA: {
                try{
                    Log.v("알림", "FROM_CAMERA 처리");
                    galleryAddPic();
                    if (!imagelist.add(imgUri))
                        Toast.makeText(getActivity(), "list add failed", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            }
        }

        InputStream in = null;
        ExifInterface exif = null;
        try {
            in = getActivity().getContentResolver().openInputStream(photoURI);
            exif = new ExifInterface(in);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();
    }

    private void galleryAddPic(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        Objects.requireNonNull(getActivity()).sendBroadcast(mediaScanIntent);
        Toast.makeText(getContext(),"사진이 저장되었습니다",Toast.LENGTH_SHORT).show();
    }

    public void anim() {
        if (isFabOpen) {
            fab_cam.startAnimation(fab_close);
            fab_img.startAnimation(fab_close);
            fab_cam.setClickable(false);
            fab_img.setClickable(false);
            fab.startAnimation(fab_rotate_backward);
            isFabOpen = false;
        } else {
            fab_cam.startAnimation(fab_open);
            fab_img.startAnimation(fab_open);
            fab_cam.setClickable(true);
            fab_img.setClickable(true);
            fab.startAnimation(fab_rotate);
            isFabOpen = true;
        }
    }

    public ArrayList<Uri> getImagelist() {
        return imagelist;
    }
}