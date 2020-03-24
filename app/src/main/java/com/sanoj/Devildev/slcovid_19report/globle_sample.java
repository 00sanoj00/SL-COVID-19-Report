package com.sanoj.Devildev.slcovid_19report;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lusfold.spinnerloading.SpinnerLoading;

import org.json.JSONException;
import org.json.JSONObject;
import su.levenetc.android.textsurface.Text;
import su.levenetc.android.textsurface.TextBuilder;
import su.levenetc.android.textsurface.TextSurface;
import su.levenetc.android.textsurface.animations.AnimationsSet;
import su.levenetc.android.textsurface.animations.Rotate3D;
import su.levenetc.android.textsurface.animations.Sequential;
import su.levenetc.android.textsurface.animations.TransSurface;
import su.levenetc.android.textsurface.contants.Align;
import su.levenetc.android.textsurface.contants.Pivot;
import su.levenetc.android.textsurface.contants.TYPE;

public class globle_sample extends AppCompatActivity {

    private TextSurface textSurface;
    private RequestQueue mQueue;

    private String totalCasesset = "111";
    private String newcaseset = "";
    private String dethset = "";
    private String dischargeset = "";
    private SpinnerLoading loadings;


    private String thoutotoal = "";
    private String thounewcase = "";
    private String thoudeths = "";
    private String thoudiscarge = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.globle_sample);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        mQueue = Volley.newRequestQueue(this);

        jsonParse();

        loadings = findViewById(R.id.loading);
        loadings.setPaintMode(2);
        loadings.setCircleRadius(40);
        loadings.setItemCount(8);

        textSurface = (TextSurface) findViewById(R.id.text_surface);
        textSurface.postDelayed(new Runnable() {
            @Override public void run() {
                loadings.setVisibility(View.GONE);
                show();
                jsonParse();

            }
        }, 3000);
 }
    private void show() {
        textSurface.reset();
        play(textSurface,getAssets());
        //ShapeRevealLoopSample.play(textSurface);

    }

    private void jsonParse() {

        String url = "https://hpb.health.gov.lk/api/get-current-statistical";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonArray = response.getJSONObject("data");
                            int totoalcasees = jsonArray.getInt("global_total_cases");
                            int newcasess = jsonArray.getInt("global_new_cases");
                            int deths = jsonArray.getInt("global_deaths");
                            int discharge = jsonArray.getInt("global_recovered");
                            String udatee = jsonArray.getString("update_date_time");


                            totalCasesset = String.format("%,d", totoalcasees);
                            newcaseset = String.format("%,d", newcasess);
                            dethset = String.format("%,d", deths);
                            dischargeset = String.format("%,d", discharge);


                        } catch (JSONException e) {
                            e.printStackTrace();

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
    public void play(TextSurface textSurface, AssetManager assetManager) {

        int onet = (int) getResources().getDimension(R.dimen.serfusone);
        int towt = (int) getResources().getDimension(R.dimen.serfustow);
        int message = (int) getResources().getDimension(R.dimen.serfusmessage);


        final Typeface robotoBlack = Typeface.createFromAsset(assetManager, "fonts/Roboto-Black.ttf");
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTypeface(robotoBlack);

        Text one = TextBuilder
                .create("COVID-19 Global")
                .setPaint(paint)
                .setSize(onet)
                .setAlpha(0)
                .setColor(Color.WHITE)
                .setPosition(Align.SURFACE_CENTER).build();

        Text two = TextBuilder
                .create("Situation Report")
                .setPaint(paint)
                .setSize(towt)
                .setAlpha(0)
                .setColor(Color.WHITE)
                .setPosition(Align.BOTTOM_OF, one).build();

        Text Total_Cases = TextBuilder
                .create("Total Cases")
                .setPaint(paint)
                .setSize(message)
                .setAlpha(0)
                .setColor(Color.YELLOW)
                .setPosition(Align.BOTTOM_OF| Align.RIGHT_OF , two).build();

        Text Total_Cases_qty = TextBuilder
                .create(totalCasesset)
                .setPaint(paint)
                .setSize(message)
                .setAlpha(0)
                .setColor(Color.YELLOW)
                .setPosition(Align.BOTTOM_OF , Total_Cases).build();

        Text space1 = TextBuilder
                .create("")
                .setPaint(paint)
                .setSize(message)
                .setAlpha(0)
                .setColor(Color.YELLOW)
                .setPosition(Align.BOTTOM_OF , Total_Cases_qty).build();

        Text New_Cases = TextBuilder
                .create("New Cases")
                .setPaint(paint)
                .setSize(message)
                .setAlpha(0)
                .setColor(Color.GREEN)
                .setPosition(Align.LEFT_OF  , space1).build();


        Text Total_new_qty = TextBuilder
                .create(newcaseset)
                .setPaint(paint)
                .setSize(message)
                .setAlpha(0)
                .setColor(Color.GREEN)
                .setPosition(Align.BOTTOM_OF , New_Cases).build();


        ////////////////


        Text space2 = TextBuilder
                .create("")
                .setPaint(paint)
                .setSize(message)
                .setAlpha(0)
                .setColor(Color.YELLOW)
                .setPosition(Align.BOTTOM_OF| Align.RIGHT_OF , Total_new_qty).build();


        Text Newdeth = TextBuilder
                .create("Deths")
                .setPaint(paint)
                .setSize(message)
                .setAlpha(0)
                .setColor(Color.RED)
                .setPosition( Align.RIGHT_OF  , space2).build();


        Text Total_deth_qty = TextBuilder
                .create(dethset)
                .setPaint(paint)
                .setSize(message)
                .setAlpha(0)
                .setColor(Color.RED)
                .setPosition(Align.BOTTOM_OF , Newdeth).build();

        ///////////////////////////



        Text space3 = TextBuilder
                .create("")
                .setPaint(paint)
                .setSize(message)
                .setAlpha(0)
                .setColor(Color.YELLOW)
                .setPosition(Align.BOTTOM_OF , Total_deth_qty).build();


        Text Newrec = TextBuilder
                .create("Recovered")
                .setPaint(paint)
                .setSize(message)
                .setAlpha(0)
                .setColor(Color.GREEN)
                .setPosition( Align.LEFT_OF  , space3).build();


        Text Total_rec_qty = TextBuilder
                .create(dischargeset)
                .setPaint(paint)
                .setSize(message)
                .setAlpha(0)
                .setColor(Color.GREEN)
                .setPosition(Align.BOTTOM_OF , Newrec).build();






        textSurface.play(
                new Sequential(


                        new AnimationsSet(TYPE.PARALLEL,
                                Rotate3D.showFromSide(one, 500, Pivot.TOP),
                                new TransSurface(500, one, Pivot.CENTER)
                        ),

                        new AnimationsSet(TYPE.PARALLEL,
                                Rotate3D.showFromSide(two, 500, Pivot.TOP),
                                new TransSurface(500,two, Pivot.CENTER)
                        ),
                        new AnimationsSet(TYPE.PARALLEL,
                                Rotate3D.showFromSide(Total_Cases, 500, Pivot.TOP),
                                new TransSurface(500,Total_Cases, Pivot.CENTER)
                        ),
                        new AnimationsSet(TYPE.PARALLEL,
                                Rotate3D.showFromSide(Total_Cases_qty, 500, Pivot.TOP),
                                new TransSurface(500,Total_Cases_qty, Pivot.CENTER)
                        ),



                        new AnimationsSet(TYPE.PARALLEL,
                                Rotate3D.showFromSide(New_Cases, 500, Pivot.TOP),
                                new TransSurface(500,New_Cases, Pivot.CENTER)
                        ),

                        new AnimationsSet(TYPE.PARALLEL,
                                Rotate3D.showFromSide(Total_new_qty, 500, Pivot.TOP),
                                new TransSurface(500,Total_new_qty, Pivot.CENTER)
                        ),







                        new AnimationsSet(TYPE.PARALLEL,
                                Rotate3D.showFromSide(Newdeth, 500, Pivot.TOP),
                                new TransSurface(500,Newdeth, Pivot.CENTER)
                        ),

                        new AnimationsSet(TYPE.PARALLEL,
                                Rotate3D.showFromSide(Total_deth_qty, 500, Pivot.TOP),
                                new TransSurface(500,Total_deth_qty, Pivot.CENTER)
                        ),



                        new AnimationsSet(TYPE.PARALLEL,
                                Rotate3D.showFromSide(Newrec, 500, Pivot.TOP),
                                new TransSurface(500,Newdeth, Pivot.CENTER)
                        ),

                        new AnimationsSet(TYPE.PARALLEL,
                                Rotate3D.showFromSide(Total_rec_qty, 500, Pivot.TOP),
                                new TransSurface(500,Total_deth_qty, Pivot.CENTER)


                        )



                )

        );

    }


}
