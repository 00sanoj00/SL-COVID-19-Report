package com.sanoj.Devildev.slcovid_19report;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allenliu.androidsharelib.AndroidShare;
import com.allenliu.androidsharelib.AndroidSharePlatform;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.kd.dynamic.calendar.generator.ImageGenerator;
import com.sanoj.Devildev.schreenshot.ScreenshotUtil;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import iammert.com.library.ConnectionStatusView;
import iammert.com.library.Status;


public class MainActivity extends AppCompatActivity  {


    private ConnectionStatusView statusView;
    private RequestQueue mQueue;
    private TextView totalcase,newcase,inhospital,deth,recovery,datesync;
    private RelativeLayout parentView,totoalshearit;
    private static final int APP_PERMISSION_REQUEST = 102;
    ImageGenerator mImageGenerator;
    private Button share,global;
    private Bitmap bitmap;
    private ImageView sample;
    Calendar mCurrentDate;
    Bitmap mGeneratedDateIcon;
    ImageView mDisplayGeneratedImage;
    private SharedPreferences.Editor editor;
    private SwipeRefreshLayout swipeContainer;

    private TextView totoalcase_dialog,newcase_dialog,deth_dialog ,recovery_dialog;

    private String totalCasesset = "111";
    private String newcaseset = "";
    private String dethset = "";
    private String dischargeset = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample);

        parentView = findViewById(R.id.parentView);
        editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();


        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);




        try {
            SharedPreferences prefs = this.getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
            String whymod = prefs.getString("1time", "");
            if(whymod.equals("")){
                dialog();
            }else if(whymod.equals("ok")) {
            }else {
                dialog();
            }
        }catch (Exception o){
        }

        mImageGenerator = new ImageGenerator(this);
        mDisplayGeneratedImage = (ImageView) findViewById(R.id.imgGenerated);
        mImageGenerator.setIconSize(50, 50);
        mImageGenerator.setDateSize(30);
        mImageGenerator.setMonthSize(10);
        mImageGenerator.setDatePosition(42);
        mImageGenerator.setMonthPosition(14);
        mImageGenerator.setDateColor(Color.parseColor("#3c6eaf"));
        mImageGenerator.setMonthColor(Color.WHITE);
        mImageGenerator.setStorageToSDCard(true);
        mCurrentDate = Calendar.getInstance();
        int mYear = mCurrentDate.get(Calendar.YEAR);
        int mMonth = mCurrentDate.get(Calendar.MONTH);
        final int mDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);
        mCurrentDate.set(mYear,mMonth,mDay);
        mGeneratedDateIcon = mImageGenerator.generateDateImage(mCurrentDate, R.drawable.empty_calendar);
        mDisplayGeneratedImage.setImageBitmap(mGeneratedDateIcon);
        global=findViewById(R.id.global);

        global.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_showMessage(share);
            }
        });



        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();

        totalcase = findViewById(R.id.mcase);
        newcase = findViewById(R.id.mncase);
        inhospital = findViewById(R.id.minhos);
        deth = findViewById(R.id.mdeth);
        recovery = findViewById(R.id.mrecov);
        datesync = findViewById(R.id.sync);
        share = findViewById(R.id.share);
        mQueue = Volley.newRequestQueue(this);
        jsonParse();
        statusView = findViewById(R.id.status);





        sample = findViewById(R.id.imgGenerated);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = ScreenshotUtil.getInstance().takeScreenshotForView(parentView);
                sample.setImageBitmap(bitmap);

                Date currentTime = Calendar.getInstance().getTime();
                String mPath = Environment.getExternalStorageDirectory().toString() + "/" +currentTime+"new1.png";
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(mPath);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    Toast.makeText(MainActivity.this, mPath, Toast.LENGTH_SHORT).show();
                    fos.flush();
                    fos.close();

                    Log.e("ImageSave", "Saveimage");
                } catch (FileNotFoundException e) {
                    Log.e("GREC", e.getMessage(), e);
                } catch (IOException e) {
                    Log.e("GREC", e.getMessage(), e);
                }

                Uri bmpUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(mPath));
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                intent.setType("image/*");
                startActivity(Intent.createChooser(intent, "Share Image:"));

            }
        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                jsonParse();

            }
        });

    }





    private void jsonParse() {

        String url = "https://hpb.health.gov.lk/api/get-current-statistical";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonArray = response.getJSONObject("data");
                            String totoalcasees = jsonArray.getString("local_total_cases");
                            String newcasess = jsonArray.getString("local_new_cases");
                            String inhospitals = jsonArray.getString("local_total_number_of_individuals_in_hospitals");
                            String deths = jsonArray.getString("local_deaths");
                            String discharge = jsonArray.getString("local_recovered");
                            String udatee = jsonArray.getString("update_date_time");
                            int totoalcaseesx = jsonArray.getInt("global_total_cases");
                            int newcasessx = jsonArray.getInt("global_new_cases");
                            int dethsx = jsonArray.getInt("global_deaths");
                            int dischargex = jsonArray.getInt("global_recovered");


                            totalCasesset = String.format("%,d", totoalcaseesx);
                            newcaseset = String.format("%,d", newcasessx);
                            dethset = String.format("%,d", dethsx);
                            dischargeset = String.format("%,d", dischargex);

                            totalcase.setText(totoalcasees);
                            newcase.setText(newcasess);
                            inhospital.setText(inhospitals);
                            deth.setText(deths);
                            recovery.setText(discharge);
                            datesync.setText(udatee);


                            statusView.setStatus(Status.COMPLETE);
                            swipeContainer.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            statusView.setStatus(Status.ERROR);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_PERMISSION_REQUEST && resultCode == RESULT_OK) {
            Toast.makeText(this, "Draw over other app permission enable.", Toast.LENGTH_SHORT).show();
        } else {

        }
    }
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {

        }
        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(MainActivity.this, "These permissions are required to proceed. Please try again\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    private void dialog(){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);

        mBuilder.setTitle("COVID-19 Situation Report");
        mBuilder.setMessage("\n" +
                "\uD83D\uDCA2 The API is used on the Health Promotion Bureau's website.\n" +
                "\uD83D\uDCA2 API & website update \"Arimac\"\n" +
                "\uD83D\uDCA2 App design & developing \"Sanoj Prabath\"\n\n" +
                "Hope of the app\n" +
                "\n" +
                "\uD83D\uDCA2 Making the public aware of the situation inside Sri Lanka due to COVID-19 virus" +
                "\n\n" +
                "Thank You !!\n\n" +
                "\"Arimac\"" +
                "");
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                editor.putString("1time", "ok");
                editor.apply();

            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();

        if(getDialogStatus()){
            mDialog.hide();
        }else{
            mDialog.show();
        }
    }


    private boolean getDialogStatus(){
        SharedPreferences mSharedPreferences = getSharedPreferences("CheckItem", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("item", false);

    }
    public void btn_showMessage(View view){
        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this,R.style.CustomDialog);
        View mView = getLayoutInflater().inflate(R.layout.dialogbox,null);
        Button btn_okay = (Button)mView.findViewById(R.id.btn_okay);
        Button funny = (Button)mView.findViewById(R.id.global);
        Button share = (Button)mView.findViewById(R.id.it);
        totoalshearit = mView.findViewById(R.id.totoalshare);
        totoalcase_dialog = mView.findViewById(R.id.totoalcase_info);
        newcase_dialog = mView.findViewById(R.id.totoalnew_info);
        deth_dialog=mView.findViewById(R.id.totoaldeth_info);
        recovery_dialog=mView.findViewById(R.id.totoalrec_info);
        totoalcase_dialog.setText(totalCasesset);
        newcase_dialog.setText(newcaseset);
        deth_dialog.setText(dethset);
        recovery_dialog.setText(dischargeset);


        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        funny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, globle_sample.class));
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = ScreenshotUtil.getInstance().takeScreenshotForView(totoalshearit);

                Date currentTime = Calendar.getInstance().getTime();
                String mPath = Environment.getExternalStorageDirectory().toString() + "/" +currentTime+"new2.png";
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(mPath);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    Toast.makeText(MainActivity.this, mPath, Toast.LENGTH_SHORT).show();
                    fos.flush();
                    fos.close();

                    Log.e("ImageSave", "Saveimage");
                } catch (FileNotFoundException e) {
                    Log.e("GREC", e.getMessage(), e);
                } catch (IOException e) {
                    Log.e("GREC", e.getMessage(), e);
                }

                Uri bmpUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(mPath));
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                intent.setType("image/*");
                startActivity(Intent.createChooser(intent, "Share Image:"));
            }
        });
        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //myCustomMessage.setText(txt_inputText.getText().toString());
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

}
