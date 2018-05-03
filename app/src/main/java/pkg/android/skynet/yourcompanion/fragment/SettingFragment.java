package pkg.android.skynet.yourcompanion.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.activity.MainActivity;
import pkg.android.skynet.yourcompanion.activity.SignUpActivity;
import pkg.android.skynet.yourcompanion.app.Constants;
import pkg.android.skynet.yourcompanion.app.SessionManager;
import pkg.android.skynet.yourcompanion.app.YourCompanion;
import pkg.android.skynet.yourcompanion.callbacks.ReqResAPICallBacks;
import pkg.android.skynet.yourcompanion.callbacks.ReqRespCallBack;
import pkg.android.skynet.yourcompanion.models.UserDetails;

/**
 * Created by ST-3 on 28-09-2017.
 */

public class SettingFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, TextWatcher {

    private EditText mUsernameEdt, mEmailEdt, mPhoneEdt, mPasswordEdt, mConfirmPasswordEdt, mHelpMessageEdt, mAddressEdt;
    private TextView mAlarmDelayTxt;
    private CircleImageView mUserPicImg;
    private RadioButton mMaleRdo, mFemaleRdo, mOtherRdo;

    private String mGender = "", mIsSocial = "", mGetPicturePath = "";

    private static final int REQUEST_GALLERY = 2;
    private static final int REQUEST_CAMERA = 1;

    private YourCompanion yourCompanion;
    private SessionManager sessionManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        yourCompanion = (YourCompanion)getActivity().getApplication();
        sessionManager = new SessionManager(getActivity());

        initControls(view);
        getUserDetail();

        onBackKeyPressed(view);
        return view;
    }


    private void initControls(View view) {
        mUsernameEdt = (EditText) view.findViewById(R.id.txt_username);
        mEmailEdt = (EditText)view.findViewById(R.id.edt_email);
        mPhoneEdt = (EditText)view.findViewById(R.id.edt_phone);
        mPasswordEdt = (EditText)view.findViewById(R.id.edt_password);
        mConfirmPasswordEdt = (EditText)view.findViewById(R.id.edt_confirm_password);
        mAddressEdt = (EditText)view.findViewById(R.id.edt_address);

        mHelpMessageEdt = (EditText)view.findViewById(R.id.edt_help_message);
        mHelpMessageEdt.addTextChangedListener(this);

        mAlarmDelayTxt = (TextView)view.findViewById(R.id.txt_alarm_delay);

        mUserPicImg = (CircleImageView)view.findViewById(R.id.img_userpicture);
        mUserPicImg.setOnClickListener(this);

        mMaleRdo = (RadioButton)view.findViewById(R.id.rdo_male);
        mMaleRdo.setOnCheckedChangeListener(this);

        mFemaleRdo = (RadioButton)view.findViewById(R.id.rdo_female);
        mFemaleRdo.setOnCheckedChangeListener(this);

        mOtherRdo = (RadioButton)view.findViewById(R.id.rdo_other);
        mOtherRdo.setOnCheckedChangeListener(this);

        view.findViewById(R.id.txt_logout).setOnClickListener(this);
        view.findViewById(R.id.btn_save).setOnClickListener(this);
        view.findViewById(R.id.ll_alarm_delay).setOnClickListener(this);
    }


    /**
     * Used to check the field validation...
     * @return
     */
    private boolean isValid() {
        String[] name = yourCompanion.getEditTextValue(mUsernameEdt).split(" ");

        if (yourCompanion.isEditTextEmpty(mUsernameEdt) || !yourCompanion.getEditTextValue(mUsernameEdt).contains(" ")) {
            mUsernameEdt.requestFocus();
            yourCompanion.showAlertDialog(getActivity(), getString(R.string.err_enter_fullname), "Ok");
            return false;

        }else if (name.length < 2) {
            mUsernameEdt.requestFocus();
            yourCompanion.showAlertDialog(getActivity(), getString(R.string.err_enter_email), "Ok");
            return false;

        }else if (yourCompanion.isEditTextEmpty(mEmailEdt)) {
            mEmailEdt.requestFocus();
            yourCompanion.showAlertDialog(getActivity(), getString(R.string.err_enter_email), "Ok");
            return false;

        }else if (!yourCompanion.isValidEmail(yourCompanion.getEditTextValue(mEmailEdt))) {
            mEmailEdt.requestFocus();
            yourCompanion.showAlertDialog(getActivity(), getString(R.string.err_enter_valid_email), "Ok");
            return false;

        }else if (yourCompanion.isEditTextEmpty(mPhoneEdt)) {
            mPhoneEdt.requestFocus();
            yourCompanion.showAlertDialog(getActivity(), getString(R.string.err_enter_phone), "Ok");
            return false;

        }else if (yourCompanion.getEditTextValue(mPhoneEdt).length() < 6) {
            yourCompanion.showAlertDialog(getActivity(), getString(R.string.err_enter_valid_phone), "Ok");
            mPhoneEdt.requestFocus();
            return false;

        }else if (!yourCompanion.isEditTextEmpty(mPasswordEdt) || !yourCompanion.isEditTextEmpty(mConfirmPasswordEdt)) {

            if (yourCompanion.isEditTextEmpty(mPasswordEdt)) {
                mPasswordEdt.requestFocus();
                yourCompanion.showAlertDialog(getActivity(), getString(R.string.err_enter_password), "Ok");
                return false;

            }else if (yourCompanion.isEditTextEmpty(mConfirmPasswordEdt)) {
                mConfirmPasswordEdt.requestFocus();
                yourCompanion.showAlertDialog(getActivity(), getString(R.string.err_enter_confirm_password), "Ok");
                return false;

            }else if (!yourCompanion.getEditTextValue(mConfirmPasswordEdt).equals(yourCompanion.getEditTextValue(mPasswordEdt))) {
                mConfirmPasswordEdt.requestFocus();
                yourCompanion.showAlertDialog(getActivity(), getString(R.string.err_diff_password), "Ok");
                return false;
            }
            return true;
        }else return true;
    }


    public Bitmap Changetosketch(Bitmap bmp){
        Bitmap Copy,Invert,Result;
        Copy =bmp;
        Copy = toGrayscale(Copy);
        Invert = createInvertedBitmap(Copy);
        Invert = Blur.blur(getActivity(), Invert);
        Result = ColorDodgeBlend(Invert, Copy);

        return Result;
    }



    /**
     * Used to set the user data...
     * @param userDetailses
     */
    private void setData(List<UserDetails> userDetailses) {
        mUsernameEdt.setText(userDetailses.get(0).getFirstName() + " " + userDetailses.get(0).getLastName());
        mEmailEdt.setText(userDetailses.get(0).getEmail());
        mPhoneEdt.setText(userDetailses.get(0).getPhone());
        mHelpMessageEdt.setText(userDetailses.get(0).getHelpMessage());
        mAddressEdt.setText(userDetailses.get(0).getAddress());

        mIsSocial = userDetailses.get(0).getFbUser();

        if (userDetailses.get(0).getGender().equals(getString(R.string.txt_male)))
            mMaleRdo.setChecked(true);
        else if (userDetailses.get(0).getGender().equals(getString(R.string.txt_female)))
            mFemaleRdo.setChecked(true);
        else if (userDetailses.get(0).getGender().equals(getString(R.string.txt_other)))
            mOtherRdo.setChecked(true);

        if (!userDetailses.get(0).getUserImg().equals("")) {
            Picasso.with(getActivity())
                    .load(userDetailses.get(0).getUserImg())
                    .resize(200, 200)
                    .placeholder(R.drawable.ic_profile_male)
                    .into(mUserPicImg);

        }

        HashMap<String, String> user = sessionManager.getUserDetails();

        mAlarmDelayTxt.setText(user.get(SessionManager.ALARM_DELAY));
    }


    /**
     *  Mehtod is used to show the option dialog while upload logo...
     */
    public void showDialogChoise() {
        final CharSequence[] items = { "Take Photo", "Select from Gallery"};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Choose Option");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    if (YourCompanion.checkCameraPermission(getActivity())) {
                        yourCompanion.createDirectory();
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        long fileName = System.currentTimeMillis();

                        File outputFile = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.IMAGE_PATH + File.separator + "IMG" + fileName + ".jpg");

                        Uri mImageCaptureUri = Uri.fromFile(outputFile);
                        mGetPicturePath = outputFile.getPath();
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        startActivityForResult(intent, REQUEST_CAMERA);
                    }
                } else if (items[item].equals("Select from Gallery")) {
                    if (YourCompanion.checkStoragePermission(getActivity())) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(intent, REQUEST_GALLERY);
                    }
                }
            }
        });
        builder.show();
    }


    /**
     *  Mehtod is used to show the option dialog while upload logo...
     */
    public void alarmDelayTimes() {
        final CharSequence[] items = { "1 Sec", "2 Sec", "3 Sec", "4 Sec", "5 Sec", "6 Sec", "7 Sec", "8 Sec", "9 Sec", "10 Sec"};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Alarm Delay");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                mAlarmDelayTxt.setText(items[item]);

                sessionManager.updateAlarmDelay(items[item].toString());
            }
        });
        builder.show();
    }


    /**
     * Used to rotate captured image clockwise..
     * @param imagePath
     * @return
     */
    private static int getImageOrientation(String imagePath){
        int rotate = 0;
        try {
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }


    /**
     * Used to get real path from URL..
     * @param contentUri
     * @return
     */
    public String getRealPathFromURI(Uri contentUri) {
        String path = null;
        String[] proj = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(column_index);
        }
        cursor.close();
        return path;
    }


    /**
     * Used to get the user detail...
     */
    private void getUserDetail() {
        HashMap<String, String> user = sessionManager.getUserDetails();
        String userId = user.get(SessionManager.USER_ID);

        try {
            ReqResAPICallBacks reqResAPICallBacks = new ReqResAPICallBacks();
            reqResAPICallBacks.getUser(getActivity(), new ReqRespCallBack() {
                @Override
                public void onResponse(Object resObj, String resMessage, String resCode) {
                    if (resCode.equals(Constants.SUCCESS)) {
                        List<UserDetails> userDetailses = (List)resObj;
                        setData(userDetailses);
                    }
                }

                @Override
                public void onResponse(String response, String resMessage, String resCode) {
                    if (resCode.equals(Constants.FAILURE)) {
                        yourCompanion.showAlertDialog(getActivity(), resMessage, "Ok");
                    }
                }
            }, userId, true);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Used to save the profile data..
     */
    private void updateProfile() {
        HashMap<String, String> user = sessionManager.getUserDetails();
        String userId = user.get(SessionManager.USER_ID);

        try {
            ReqResAPICallBacks reqResAPICallBacks = new ReqResAPICallBacks();
            reqResAPICallBacks.updateUser(getActivity(), new ReqRespCallBack() {
                @Override
                public void onResponse(Object resObj, String resMessage, String resCode) {
                    if (resCode.equals(Constants.SUCCESS)) {
                        List<UserDetails> userDetailses = (List)resObj;
                        setData(userDetailses);

                        sessionManager.createLoginSession(userDetailses.get(0).getUserId(), userDetailses.get(0).getFirstName(), userDetailses.get(0).getLastName(),
                                userDetailses.get(0).getEmail(), userDetailses.get(0).getPhone(), userDetailses.get(0).getPrimaryFriendId(), userDetailses.get(0).getHelpMessage());

                        Toast.makeText(getActivity(), resMessage, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onResponse(String response, String resMessage, String resCode) {
                    if (resCode.equals(Constants.FAILURE)) {
                        yourCompanion.showAlertDialog(getActivity(), resMessage, "Ok");
                    }
                }
            }, userId, yourCompanion.getEditTextValue(mUsernameEdt), "", yourCompanion.getEditTextValue(mEmailEdt), yourCompanion.getEditTextValue(mPhoneEdt), mGender,
                    yourCompanion.getEditTextValue(mPasswordEdt), yourCompanion.getEditTextValue(mConfirmPasswordEdt), mIsSocial, yourCompanion.getEditTextValue(mHelpMessageEdt),
                    "", mGetPicturePath, true);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == FragmentActivity.RESULT_OK && !mGetPicturePath.equals("")) {
                try {
                    long length = new File(mGetPicturePath).length();
                    Log.d("image", "length:" + length / 1024);
                    length = length / 1024;

                    if (length > 3000) {

                    } else {
                        Bitmap bitmap;
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        bitmap = BitmapFactory.decodeFile(mGetPicturePath, options);

                        Matrix matrix = new Matrix();
                        matrix.postRotate(getImageOrientation(mGetPicturePath));
                        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                        File outputFile = new File(mGetPicturePath);

                        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                        //myBitmap = Bitmap.createScaledBitmap(myBitmap, 300, 300, true);
                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream);

                        fileOutputStream.flush();
                        fileOutputStream.close();

                        mUserPicImg.setImageBitmap(bitmap);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else if (resultCode == FragmentActivity.RESULT_OK && data != null && data.getData() != null) {
            try{
                mGetPicturePath = getRealPathFromURI(data.getData());

                long length = new File(mGetPicturePath).length();
                Log.d("image", "length:" + length / 1024);
                length = length / 1024;

                if (length > 3000) {

                } else {
                    Bitmap bitmap;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    bitmap = BitmapFactory.decodeFile(mGetPicturePath, options);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);

                    File outputFile = new File(mGetPicturePath.replace(" ", ""));
                    FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream);

                    fileOutputStream.flush();
                    fileOutputStream.close();

                    mUserPicImg.setImageBitmap(bitmap);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_logout:
                yourCompanion.askForLogout(getActivity());
                
                break;

            case R.id.img_userpicture:
                showDialogChoise();
                break;

            case R.id.btn_save:
                if (isValid()) {
                    updateProfile();
                }
                break;

            case R.id.ll_alarm_delay:
                alarmDelayTimes();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.rdo_male:
                if (isChecked)
                    mGender = getString(R.string.txt_male);
                break;

            case R.id.rdo_female:
                if (isChecked)
                    mGender = getString(R.string.txt_female);
                break;

            case R.id.rdo_other:
                if (isChecked)
                    mGender = getString(R.string.txt_other);
                break;
        }
    }


    /**
     * Used to handle back key pressed event..
     * @param view
     */
    private void onBackKeyPressed(View view) {
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener( new View.OnKeyListener(){
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event ){
                if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == MotionEvent.ACTION_UP){
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_body, new MainFragment()).commit();
                    return true;
                }
                return false;
            }
        });
    }



    public static Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public static Bitmap createInvertedBitmap(Bitmap src) {
        ColorMatrix colorMatrix_Inverted =
                new ColorMatrix(new float[] {
                        -1,  0,  0,  0, 255,
                        0, -1,  0,  0, 255,
                        0,  0, -1,  0, 255,
                        0,  0,  0,  1,   0});

        ColorFilter ColorFilter_Sepia = new ColorMatrixColorFilter(
                colorMatrix_Inverted);

        Bitmap bitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();

        paint.setColorFilter(ColorFilter_Sepia);
        canvas.drawBitmap(src, 0, 0, paint);

        return bitmap;
    }
    public Bitmap ColorDodgeBlend(Bitmap source, Bitmap layer) {
        Bitmap base = source.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap blend = layer.copy(Bitmap.Config.ARGB_8888, false);

        IntBuffer buffBase = IntBuffer.allocate(base.getWidth() * base.getHeight());
        base.copyPixelsToBuffer(buffBase);
        buffBase.rewind();

        IntBuffer buffBlend = IntBuffer.allocate(blend.getWidth() * blend.getHeight());
        blend.copyPixelsToBuffer(buffBlend);
        buffBlend.rewind();

        IntBuffer buffOut = IntBuffer.allocate(base.getWidth() * base.getHeight());
        buffOut.rewind();

        while (buffOut.position() < buffOut.limit()) {

            int filterInt = buffBlend.get();
            int srcInt = buffBase.get();

            int redValueFilter = Color.red(filterInt);
            int greenValueFilter = Color.green(filterInt);
            int blueValueFilter = Color.blue(filterInt);

            int redValueSrc = Color.red(srcInt);
            int greenValueSrc = Color.green(srcInt);
            int blueValueSrc = Color.blue(srcInt);

            int redValueFinal = colordodge(redValueFilter, redValueSrc);
            int greenValueFinal = colordodge(greenValueFilter, greenValueSrc);
            int blueValueFinal = colordodge(blueValueFilter, blueValueSrc);


            int pixel = Color.argb(255, redValueFinal, greenValueFinal, blueValueFinal);


            buffOut.put(pixel);
        }

        buffOut.rewind();

        base.copyPixelsFromBuffer(buffOut);
        blend.recycle();

        return base;
    }

    private int colordodge(int in1, int in2) {
        float image = (float)in2;
        float mask = (float)in1;
        return ((int) ((image == 255) ? image:Math.min(255, (((long)mask << 8 ) / (255 - image)))));
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (mHelpMessageEdt.length() > 0)
            mHelpMessageEdt.setTypeface(null, Typeface.NORMAL);
        else mHelpMessageEdt.setTypeface(null, Typeface.ITALIC);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }


    public static class Blur {
        private static final float BITMAP_SCALE = 0.4f;
        private static final float BLUR_RADIUS = 4.5f;

        public static Bitmap blur(View v) {
            return blur(v.getContext(), getScreenshot(v));
        }

        public static Bitmap blur(Context ctx, Bitmap image) {
            Bitmap photo = image.copy(Bitmap.Config.ARGB_8888, true);

            try {
                final RenderScript rs = RenderScript.create( ctx );
                final Allocation input = Allocation.createFromBitmap(rs, photo, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
                final Allocation output = Allocation.createTyped(rs, input.getType());
                final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
                script.setRadius( BLUR_RADIUS ); /* e.g. 3.f */
                script.setInput( input );
                script.forEach( output );
                output.copyTo( photo );
            }catch (Exception e){
                e.printStackTrace();
            }
            return photo;
        }

        private static Bitmap getScreenshot(View v) {
            Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.draw(c);
            return b;
        }

    }

}
