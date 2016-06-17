package com.hdfc.caretaker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.hdfc.app42service.StorageService;
import com.hdfc.app42service.UploadService;
import com.hdfc.app42service.UserService;
import com.hdfc.caretaker.fragments.AddDependentFragment;
import com.hdfc.caretaker.fragments.ConfirmFragment;
import com.hdfc.config.Config;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.Utils;
import com.hdfc.models.CustomerModel;
import com.hdfc.views.CustomViewPager;
import com.hdfc.views.RoundedImageView;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.upload.Upload;
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Admin on 15-06-2016.
 */
public class ActivityGuruPersonalInfo extends AppCompatActivity {

    public static RoundedImageView imgButtonCamera;

    public static String strCustomerImgName = "";
    public static Bitmap bitmap = null;
    private static int idregisterflag = 0;
    public static Uri uri;
    public static String citizenshipVal;
    private static Thread backgroundThreadCamera, backgroundThread;
    private static Handler backgroundThreadHandler;
    private static String strName, strEmail, strConfirmPass, strContactNo, strDate,strCountryCode,strCountry;//,strAddress;
    public static String strPass;
    private static ProgressDialog mProgress = null;
    String strMess = "";
    private static String jsonDocId;
    private EditText editName, editEmail, editPass, editConfirmPass, editContactNo, editTextDate, editAreaCode, editCountryCode;
    //editAddress
    private Utils utils;
    private RadioButton mobile;
    private Spinner citizenship;
    private String strCustomerImageUrl = "";
    public static int uploadSize, uploadingCount=0;
    ImageButton back;

    private String strAreaCode = "";
    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            // selectedDateTime = date.getDate()+"-"+date.getMonth()+"-"+date.getYear()+" "+
            // date.getTime();
            // Do something with the date. This Date object contains
            // the date and time that the user has selected.

            strDate = Utils.writeFormatActivityYear.format(date);
            // String _strDate = Utils.readFormat.format(date);
            editTextDate.setText(strDate);
        }

        @Override
        public void onDateTimeCancel() {
            // Overriding onDateTimeCancel() is optional.
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guru_personal_info);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        utils = new Utils(ActivityGuruPersonalInfo.this);

        back = (ImageButton)findViewById(R.id.buttonBack);

        imgButtonCamera = (RoundedImageView) findViewById(R.id.imageButtonCamera);

        editName = (EditText) findViewById(R.id.editName);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPass = (EditText)  findViewById(R.id.editPassword);
        editConfirmPass = (EditText) findViewById(R.id.editConfirmPassword);
        editContactNo = (EditText) findViewById(R.id.editContactNo);
        editAreaCode = (EditText) findViewById(R.id.editAreaCode);
        editCountryCode = (EditText) findViewById(R.id.editCountryCode);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(idregisterflag==1&&idregisterflag==2&&idregisterflag==3) {
                    goBack();
                }
            }
        });

        mobile = (RadioButton) findViewById(R.id.radioMobile);
        //RadioButton landline = (RadioButton) rootView.findViewById(R.id.radioLandline);

        mobile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mobile.isChecked()) {
                    editAreaCode.setVisibility(View.GONE);
                } else {
                    editAreaCode.setVisibility(View.VISIBLE);
                }
            }
        });

       /* editAreaCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChromeHelpPopup chromeHelpPopup = new ChromeHelpPopup(getContext(),"Country Code");
                chromeHelpPopup.show(v);
            }
        });*/

        ///
        /*TelephonyManager telephonyManager = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        editContactNo.setText( telephonyManager.getNetworkCountryIso());*/

        editTextDate = (EditText) findViewById(R.id.editDOB);

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SlideDateTimePicker.Builder(ActivityGuruPersonalInfo.this.getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();
            }
        });

        Button buttonContinue = (Button) findViewById(R.id.buttonContinue);
        //editAddress = (EditText) rootView.findViewById(R.id.editAddress);

        mProgress = new ProgressDialog(this);

        imgButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                utils.selectImage(String.valueOf(calendar.getTimeInMillis()) + ".jpeg", null, ActivityGuruPersonalInfo.this);
            }
        });

        if (buttonContinue != null) {
            buttonContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    validateUser();
                }
            });
        }


        citizenship = (Spinner) findViewById(R.id.input_citizenship);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityGuruPersonalInfo.this, R.layout.spinner_item, Config.countryNames);
        citizenship.setAdapter(adapter);
        citizenship.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editCountryCode.setText(Config.countryAreaCodes[position]);
                editAreaCode.setText(Config.countryCodes[position]);
                citizenshipVal = citizenship.getSelectedItem().toString();
                //System.out.println("Citizenship value is : "+citizenshipVal);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void uploadImage() {

        try {

            if (utils.isConnectingToInternet()) {

                UploadService uploadService = new UploadService(ActivityGuruPersonalInfo.this);

                /*if (mProgress.isShowing())
                    mProgress.setProgress(uploadSize+1);*/

                uploadService.uploadImageCommon(strCustomerImgName,
                        strName, "Profile Picture",
                        strEmail,
                        UploadFileType.IMAGE, new App42CallBack() {

                            public void onSuccess(Object response) {

                                Utils.log(response.toString()," TAG ");
                                if(response!=null) {
                                    Upload upload = (Upload) response;
                                    ArrayList<Upload.File> fileList = upload.getFileList();

                                    if (fileList.size() > 0) {
                                        if (mProgress.isShowing())
                                            mProgress.dismiss();
                                        strCustomerImageUrl = fileList.get(0).getUrl();
                                        Config.customerModel.setStrImgUrl(strCustomerImageUrl);
                                        //checkStorage();
                                        idregisterflag=1;
                                            uploadData();
                                    } else {
                                        if (mProgress.isShowing())
                                            mProgress.dismiss();

                                        utils.toast(2, 2, ((Upload) response).getStrResponse());
                                        //uploadImage();
                                    }
                                }else{
                                    if (mProgress.isShowing())
                                        mProgress.dismiss();
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                            }

                            @Override
                            public void onException(Exception e) {

                                Utils.log(e.getMessage()," TAG ");

                                if (mProgress.isShowing())
                                    mProgress.dismiss();
                                if(e!=null) {
                                    App42Exception exception = (App42Exception) e;
                                    int appErrorCode = exception.getAppErrorCode();

                                    if (appErrorCode == 2100) {
                                        if (mProgress.isShowing())
                                            mProgress.dismiss();

                                        //checkStorage();
                                        //uploadData();
                                    }
                                 /*   else {
                                        utils.toast(2, 2, getString(R.string.error));
                                        uploadImage();
                                    }*/
                                }else{
                                    if (mProgress.isShowing())
                                        mProgress.dismiss();
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                            }
                        });
            } else {
                if (mProgress.isShowing())
                    mProgress.dismiss();
                utils.toast(2, 2, getString(R.string.warning_internet));
            }
        }catch (Exception e){
            e.printStackTrace();
            if (mProgress.isShowing())
                mProgress.dismiss();
            utils.toast(2, 2, getString(R.string.error));
        }
    }


    public void uploadData() {

        boolean isRegistered = prepareData(strCustomerImageUrl);

        mProgress.setMessage(ActivityGuruPersonalInfo.this.getResources().getString(R.string.uploading));
        mProgress.setCancelable(false);
        mProgress.show();

        if (utils.isConnectingToInternet()) {

            if (isRegistered) {

                StorageService storageService = new StorageService(ActivityGuruPersonalInfo.this);

                storageService.insertDocs(Config.jsonCustomer,
                        new AsyncApp42ServiceApi.App42StorageServiceListener() {

                            @Override
                            public void onDocumentInserted(Storage response) {

                                if (response != null){
                                    Utils.log(response.toString(), "");
                                    if (response.isResponseSuccess()) {
                                        Storage.JSONDocument jsonDocument = response.getJsonDocList().get(0);
                                        jsonDocId = jsonDocument.getDocId();
                                        idregisterflag = 2;
                                            createUser();

                                    }
                                }else{
                                    if (mProgress.isShowing())
                                        mProgress.dismiss();
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                         }

                            @Override
                            public void onUpdateDocSuccess(Storage response) {
                            }

                            @Override
                            public void onFindDocSuccess(Storage response) {
                            }

                            @Override
                            public void onInsertionFailed(App42Exception ex) {
                                if (mProgress.isShowing())
                                    mProgress.dismiss();

                                if(ex!=null){
                                    //int appErrorCode = ex.getAppErrorCode();
                                    Utils.log(ex.getMessage(), "");
                                    utils.toast(2, 2, getString(R.string.error_register));
                                }else{
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                            }

                            @Override
                            public void onFindDocFailed(App42Exception ex) {
                            }

                            @Override
                            public void onUpdateDocFailed(App42Exception ex) {
                            }
                        }, Config.collectionCustomer);
            } else {
                if (mProgress.isShowing())
                    mProgress.dismiss();
                utils.toast(2, 2, getString(R.string.error_register));
            }
        } else {
            if (mProgress.isShowing())
                mProgress.dismiss();
            utils.toast(2, 2, getString(R.string.warning_internet));
        }
    }

    private void createUser(){
        UserService userService = new UserService(ActivityGuruPersonalInfo.this);

        ArrayList<String> roleList = new ArrayList<>();
        roleList.add("customer");

        userService.onCreateUser(strEmail,
                strPass, strEmail,
                new App42CallBack() {

                    @Override
                    public void onSuccess(Object o) {
                        if (mProgress.isShowing())
                            mProgress.dismiss();
                        if (o != null) {

                            //chk this
                                       /* utils.retrieveDependants();//strEmail

                                        CustomViewPager.setPagingEnabled(true);

                                        if (AddDependentFragment.adapter != null)
                                            AddDependentFragment.adapter.notifyDataSetChanged();

                                        //utils.retrieveConfirmDependants();

                                        if (ConfirmFragment.adapter != null) {
                                            ConfirmFragment.setListView();
                                            //ConfirmFragment.adapter.notifyDataSetChanged();
                                        }*/

                            if (mProgress.isShowing())
                                mProgress.dismiss();

                            utils.toast(1, 1, getString(R.string.your_details_saved));

                            //Config.clientModels.setCustomerModel(Config.customerModel);

//                                        SignupActivity._mViewPager.setCurrentItem(1);
                            idregisterflag = 3;
                            Intent next = new Intent(ActivityGuruPersonalInfo.this,SignupActivity.class);
                            startActivity(next);

                        } else {
                            if (mProgress.isShowing())
                                mProgress.dismiss();
                            utils.toast(2, 2, getString(R.string.warning_internet));
                        }
                    }

                    @Override
                    public void onException(Exception e) {
                        if(mProgress.isShowing())
                            mProgress.dismiss();
                        if (e != null) {

                            try {
                                JSONObject jsonObject = new JSONObject(e.getMessage());
                                JSONObject jsonObjectError = jsonObject.getJSONObject("app42Fault");

                                int appErrorCode = ((App42Exception)e).getAppErrorCode();

                                if (appErrorCode == 2001) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityGuruPersonalInfo.this);
                                    builder.setTitle("Care Taker");
                                    builder.setMessage(getString(R.string.email__already_exists))
                                            .setCancelable(false)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Intent i = new Intent(ActivityGuruPersonalInfo.this,LoginActivity.class);
                                                    startActivity(i);                                                            }
                                            })
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();


//                                                utils.toast(2,2,getString(R.string.email__already_exists));
                                }else {
                                    strMess = jsonObjectError.getString("message");
                                    utils.toast(2, 2, e.getMessage());
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }

                        }

                    }
                }, roleList);

    }

    public boolean prepareData(String strCustomerImageUrl) {

        boolean isFormed;

        try {

            Config.jsonCustomer = new JSONObject();

            Config.jsonCustomer.put("customer_name", Config.customerModel.getStrName());
            Config.jsonCustomer.put("customer_address", Config.customerModel.getStrCountryCode());//
            Config.jsonCustomer.put("customer_city", "");
            Config.jsonCustomer.put("customer_state", "");
            Config.jsonCustomer.put("customer_contact_no", Config.customerModel.getStrContacts());
            Config.jsonCustomer.put("customer_email", Config.customerModel.getStrEmail());
            Config.jsonCustomer.put("customer_profile_url", strCustomerImageUrl);
            Config.jsonCustomer.put("paytm_account", "paytm_account");
            Config.jsonCustomer.put("customer_register", false);


            Config.jsonCustomer.put("customer_dob", Config.customerModel.getStrDob());
            Config.jsonCustomer.put("customer_country", Config.customerModel.getStrCountryCode());
            Config.jsonCustomer.put("customer_country_code", Config.customerModel.getStrCountryIsdCode());
            Config.jsonCustomer.put("customer_area_code", Config.customerModel.getStrCountryAreaCode());


            isFormed = true;

        } catch (JSONException e) {
            e.printStackTrace();
            isFormed = false;
        }

        return isFormed;
    }

    private void validateUser() {

        editName.setError(null);
        editEmail.setError(null);
        editPass.setError(null);
        editConfirmPass.setError(null);
        editContactNo.setError(null);
        //editAddress.setError(null);
        editTextDate.setError(null);
        //citizenship.setError(null);
        editAreaCode.setError(null);
        //editCountryCode.setError(null);

        strName = editName.getText().toString().trim();
        strEmail = editEmail.getText().toString().trim();
        strPass = editPass.getText().toString().trim();
        strConfirmPass = editConfirmPass.getText().toString().trim();
        strContactNo = editContactNo.getText().toString().trim();
        strDate = editTextDate.getText().toString().trim();


        if (editAreaCode.getVisibility() == View.VISIBLE) {
            strAreaCode = editAreaCode.getText().toString().trim();
        }

        strCountryCode = editCountryCode.getText().toString().trim();

        //strAddress = editAddress.getText().toString().trim();
        //final String strDob = editTextDate.getText().toString().trim();
        strCountry = citizenship.getSelectedItem().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(strCustomerImgName)) {
            /* && Config.customerModel != null
                && Config.customerModel.getStrName().equalsIgnoreCase("")*/
            utils.toast(1, 1, getString(R.string.warning_profile_pic));
            focusView = imgButtonCamera;
            cancel = true;

        } else {

            if (TextUtils.isEmpty(strContactNo)) {
                editContactNo.setError(getString(R.string.error_field_required));
                focusView = editContactNo;
                cancel = true;
            } else if (!utils.validCellPhone(strContactNo)) {
                editContactNo.setError(getString(R.string.error_invalid_contact_no));
                focusView = editContactNo;
                cancel = true;
            }

            if (editAreaCode.getVisibility() == View.VISIBLE) {

                if (TextUtils.isEmpty(strAreaCode)) {
                    editAreaCode.setError(getString(R.string.error_field_required));
                    focusView = editAreaCode;
                    cancel = true;
                } else if (!utils.validCellPhone(strAreaCode)) {
                    editAreaCode.setError(getString(R.string.error_invalid_area_code));
                    focusView = editAreaCode;
                    cancel = true;
                }
            }

           /* if (TextUtils.isEmpty(strCountryCode)||strCountryCode.equalsIgnoreCase("0")) {
                editCountryCode.setError(getString(R.string.error_field_required));
                focusView = editCountryCode;
                cancel = true;
            }*/

            if (TextUtils.isEmpty(strCountry) || strCountry.equalsIgnoreCase("Select Country")) {
                //editAddress.setError(getString(R.string.error_field_required));
                focusView = citizenship;
                cancel = true;
                utils.toast(2, 2, getString(R.string.select_country));
            }

         /*   if (TextUtils.isEmpty(strAddress)) {
                editAddress.setError(getString(R.string.error_field_required));
                focusView = editAddress;
                cancel = true;
            }*/

            if (!TextUtils.isEmpty(strPass) && utils.isPasswordValid(strPass)
                    && !TextUtils.isEmpty(strConfirmPass) && utils.isPasswordValid(strConfirmPass)) {

                if (!strPass.trim().equalsIgnoreCase(strConfirmPass.trim())) {
                    editConfirmPass.setError(getString(R.string.error_confirm_password));
                    focusView = editConfirmPass;
                    cancel = true;
                }
            }

            if (TextUtils.isEmpty(strConfirmPass)) {
                editConfirmPass.setError(getString(R.string.error_field_required));
                focusView = editConfirmPass;
                cancel = true;
            }

            if (TextUtils.isEmpty(strPass)) {
                editPass.setError(getString(R.string.error_field_required));
                focusView = editPass;
                cancel = true;
            }

            if (TextUtils.isEmpty(strDate)) {
                editTextDate.setError(getString(R.string.error_field_required));
                focusView = editTextDate;
                cancel = true;
            }
            if (TextUtils.isEmpty(strName)) {
                editName.setError(getString(R.string.error_field_required));
                focusView = editName;
                cancel = true;
            }

            if (TextUtils.isEmpty(strEmail)) {
                editEmail.setError(getString(R.string.error_field_required));
                focusView = editEmail;
                cancel = true;
            } else if (!utils.isEmailValid(strEmail)) {
                editEmail.setError(getString(R.string.error_invalid_email));
                focusView = editEmail;
                cancel = true;
            }


        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            try {
                //
                if (utils.isConnectingToInternet()) {

                    mProgress.setMessage(getResources().getString(R.string.loading));
                    mProgress.setCancelable(false);
                    mProgress.show();

                    //
                    if (Config.customerModel != null
                            && Config.customerModel.getStrName() != null
                            && !Config.customerModel.getStrName().equalsIgnoreCase("")) {

                        Config.customerModel.setStrName(strName);

                                       /* if (!Config.customerModel.getStrAddress().
                                                equalsIgnoreCase(""))
                                            Config.customerModel.setStrAddress(strAddress);*/

                        Config.customerModel.setStrContacts(strContactNo);
                        Config.customerModel.setStrEmail(strEmail);

                        if (!strCustomerImgName.equalsIgnoreCase(""))
                            Config.customerModel.setStrImgPath(strCustomerImgName);

                    } else {

                        Config.customerModel = new CustomerModel(strName, "", "",
                                "", strContactNo, strEmail, "",
                                strCustomerImgName);

                        //strAddress
                    }

                    Config.customerModel.setStrDob(strDate);
                    Config.customerModel.setStrCountryCode(strCountry);
                    Config.customerModel.setStrAddress(strCountry);
                    Config.customerModel.setStrCountryIsdCode(strCountryCode);
                    Config.customerModel.setStrCountryAreaCode(strAreaCode);

                    if (strConfirmPass != null && !strConfirmPass.equalsIgnoreCase(""))
                        SignupActivity.strCustomerPass = strConfirmPass;


                    //

                    if(idregisterflag==0)
                        uploadImage();

                    if(idregisterflag==1)
                        uploadData();

                    if(idregisterflag==2)
                        createUser();


                    /*
                    userService.getUser(strEmail, new App42CallBack() {
                        @Override
                        public void onSuccess(Object o) {
                            if (mProgress.isShowing())
                                mProgress.dismiss();
                            if(o!=null) {
                                utils.toast(2, 2, getString(R.string.email_exists));
                            }else{
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                        }

                        @Override
                        public void onException(Exception e) {

                            if(e!=null) {
                                String strMess = "";

                                try {
                                    JSONObject jsonObject = new JSONObject(e.getMessage());
                                    JSONObject jsonObjectError = jsonObject.getJSONObject("app42Fault");
                                    strMess = jsonObjectError.getString("message");
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }

                                if (!strMess.equalsIgnoreCase("")
                                        && strMess.equalsIgnoreCase("Not Found")) {

                                    if (Config.customerModel != null
                                            && Config.customerModel.getStrName() != null
                                            && !Config.customerModel.getStrName().equalsIgnoreCase("")) {

                                        Config.customerModel.setStrName(strName);

//                                        if (!Config.customerModel.getStrAddress().
//                                                equalsIgnoreCase(""))
//                                            Config.customerModel.setStrAddress(strAddress);

                                        Config.customerModel.setStrContacts(strContactNo);
                                        Config.customerModel.setStrEmail(strEmail);

                                        if (!strCustomerImgName.equalsIgnoreCase(""))
                                            Config.customerModel.setStrImgPath(strCustomerImgName);

                                    } else {

                                        Config.customerModel = new CustomerModel(strName, "", "",
                                                "", strContactNo, strEmail, "",
                                                strCustomerImgName);

                                        //strAddress
                                    }

                                    Config.customerModel.setStrDob(strDate);
                                    Config.customerModel.setStrCountryCode(strCountry);
                                    Config.customerModel.setStrAddress(strCountry);
                                    Config.customerModel.setStrCountryIsdCode(strCountryCode);
                                    Config.customerModel.setStrCountryAreaCode(strAreaCode);

                                    if (strConfirmPass != null && !strConfirmPass.equalsIgnoreCase(""))
                                        SignupActivity.strCustomerPass = strConfirmPass;

                                    //chk this
                                    utils.retrieveDependants();//strEmail

                                    CustomViewPager.setPagingEnabled(true);

                                    if (AddDependentFragment.adapter != null)
                                        AddDependentFragment.adapter.notifyDataSetChanged();

                                    //utils.retrieveConfirmDependants();

                                    if (ConfirmFragment.adapter != null) {
                                        ConfirmFragment.setListView();
                                        //ConfirmFragment.adapter.notifyDataSetChanged();
                                    }

                                    if (mProgress.isShowing())
                                        mProgress.dismiss();

                                    utils.toast(1, 1, getString(R.string.your_details_saved));

                                    //Config.clientModels.setCustomerModel(Config.customerModel);

                                    SignupActivity._mViewPager.setCurrentItem(1);

                                }else {
                                    if (mProgress.isShowing())
                                        mProgress.dismiss();
                                    utils.toast(2, 2, getString(R.string.error));
                                }
                            }else{
                                if (mProgress.isShowing())
                                    mProgress.dismiss();
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                        }
                    });  */

                } else {
                    if (mProgress.isShowing())
                        mProgress.dismiss();
                    utils.toast(2, 2, getString(R.string.warning_internet));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) { //&& data != null
            try {

                mProgress.setMessage(getString(R.string.loading));
                mProgress.setCancelable(false);
                mProgress.show();
                switch (requestCode) {
                    case Config.START_CAMERA_REQUEST_CODE:
                        backgroundThreadHandler = new BackgroundThreadHandler();
                        strCustomerImgName = Utils.customerImageUri.getPath();
                        backgroundThreadCamera = new BackgroundThreadCamera();
                        backgroundThreadCamera.start();
                        break;

                    case Config.START_GALLERY_REQUEST_CODE:
                        if (intent.getData() != null) {
                            backgroundThreadHandler = new BackgroundThreadHandler();
                            uri = intent.getData();
                            //strCustomerImgName = Utils.customerImageUri.getPath();
                            backgroundThread = new BackgroundThread();
                            backgroundThread.start();
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class BackgroundThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            mProgress.dismiss();
            if (imgButtonCamera != null && strCustomerImgName != null
                    && !strCustomerImgName.equalsIgnoreCase("") && bitmap != null)
                imgButtonCamera.setImageBitmap(bitmap);
        }
    }

    //
    public class BackgroundThread extends Thread {
        @Override
        public void run() {

            try {
                if (uri != null) {

                    Calendar calendar = Calendar.getInstance();
                    String strFileName = String.valueOf(calendar.getTimeInMillis()) + ".jpeg";

                    File galleryFile = utils.createFileInternalImage(strFileName);
                    strCustomerImgName = galleryFile.getAbsolutePath();
                    InputStream is = ActivityGuruPersonalInfo.this.getContentResolver().openInputStream(uri);
                    utils.copyInputStreamToFile(is, galleryFile);
                    utils.compressImageFromPath(strCustomerImgName, Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);
                    bitmap = utils.getBitmapFromFile(strCustomerImgName, Config.intWidth,
                            Config.intHeight);
                }
                backgroundThreadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class BackgroundThreadCamera extends Thread {
        @Override
        public void run() {

            try {
                if (strCustomerImgName != null && !strCustomerImgName.equalsIgnoreCase("")) {
                    utils.compressImageFromPath(strCustomerImgName, Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);
                    bitmap = utils.getBitmapFromFile(strCustomerImgName, Config.intWidth,
                            Config.intHeight);
                }
                backgroundThreadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
       /*
        moveTaskToBack(true);*/
        if(idregisterflag==1&&idregisterflag==2&&idregisterflag==3) {
            goBack();
        }
    }

    private void goBack() {
        //moveTaskToBack(true);
        Intent selection = new Intent(ActivityGuruPersonalInfo.this, MainActivity.class);
        startActivity(selection);
        finish();
    }

}
