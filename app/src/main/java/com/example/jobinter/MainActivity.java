package com.example.jobinter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RvInterface {
    RecyclerView rv;
    ArrayList<String> dateArray=new ArrayList<>();
    LinearLayoutManager linearManager;
    RvAdapter myAdapter;
    String stringBufferToString="";
    int idLeg=-1;
    public  JSONObject jObject;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        rv=(RecyclerView) findViewById(R.id.horizontelRv);
        //get the url ad parse
        String a = "https://3294c784-38b0-494b-96ee-d933fa6d7808.mock.pstmn.io/config";
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(a);
            URLConnection conn = url.openConnection();
            //read stirng from url to convert to json
            // open the stream and put it into BufferedReader
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            StringBuffer stringBuffer = new StringBuffer();
            String inputLine;
            inputLine = br.readLine();
            while (inputLine!=null)
            {
                stringBuffer.append(inputLine);
                inputLine=br.readLine();
            }

            stringBufferToString=stringBuffer.toString();
            jObject = new JSONObject(stringBufferToString);
            //get the monitor type array from json
            JSONArray jArray = jObject.getJSONArray("MonitorType");
            //build the button by monitor type
            for (int i=0; i < jArray.length(); i++)
            {
                JSONObject oneObject = jArray.getJSONObject(i);
                // Pulling items from the array
                dateArray.add(oneObject.getString("Name"));
            }

        }
        catch (Exception exp)
        {

        }
        //build the horizontal recyclerView with my custom adapter
        linearManager=new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);
        myAdapter=new RvAdapter(this, dateArray,this);
        rv.setLayoutManager(linearManager);
        rv.setAdapter(myAdapter);

    }

    @Override
    public void onItemSelected(int position) throws JSONException {
        jObject = new JSONObject(stringBufferToString);
        ArrayList<String> theMonitor=new ArrayList<>();
        JSONArray jArray = jObject.getJSONArray("MonitorType");
        String monitorTypeId="";
        //find the right id for the monitor and save the id number and legend id( i assume that its not necessarily in order and start with 0)
        for (int i=0; i < jArray.length(); i++)
        {
            JSONObject oneObject = jArray.getJSONObject(i);
            // Pulling items from the array
            //theMonitor.add(oneObject.getString("Name"));
           if(oneObject.getString("Name").equals(dateArray.get(position))) {
               monitorTypeId = oneObject.getString("Id");
               idLeg = oneObject.getInt("LegendId");
           }
//
        }


        JSONArray jArrayMonitor = jObject.getJSONArray("Monitor");
        //build a list of monitors of the specific monitor type
        for (int i=0; i < jArrayMonitor.length(); i++)
        {
            JSONObject oneObject = jArrayMonitor.getJSONObject(i);
            // Pulling items from the array
            if(monitorTypeId.equals(oneObject.getString("MonitorTypeId")))
                    theMonitor.add(oneObject.getString("Name"));
        }
        ContextThemeWrapper ctw = new ContextThemeWrapper(MainActivity.this, R.style.CustomPopupTheme);
        PopupMenu popupMenu=null;
        TextView point =findViewById(R.id.pointforpopup);
        //if there is more buttons the the screen we cant put the popup menu above it so i put it on the size of screen
        try {
            popupMenu = new PopupMenu(ctw, rv.getChildAt(position));

            //popupMenu = new PopupMenu(ctw,point);

        //add the monitor specific list to the popup menu
            for (int i=0;i<theMonitor.size();i++)
                popupMenu.getMenu().add(0,i,0,theMonitor.get(i));

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {

            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                //open a dialog with new recycler view for the index
                Dialog dialog  = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.legen);
                TextView title=(TextView)dialog.findViewById(R.id.includeDialog);
                title.setText(item.getTitle());

                RecyclerView rvLeg;
                rvLeg=(RecyclerView) dialog.findViewById(R.id.colorRv);
                ArrayList<String> dateArrayLeg=new ArrayList<>();
                JSONObject legObj=null;
                JSONArray legArray=null;
                JSONArray tagArray=null;
                try {
                    //pick the right legend id and add it to list for the rv
                   legArray= jObject.getJSONArray("Legends");
                    for (int i=0; i < legArray.length(); i++)
                    {
                        if(legArray.getJSONObject(i).getString("Id").equals(""+idLeg))
                            legObj = legArray.getJSONObject(i);

                    }
                   //legObj=legArray.getJSONObject(idLeg);
                   tagArray=legObj.getJSONArray("tags");
                    for (int i=0; i < tagArray.length(); i++)
                    {

                        JSONObject oneObject = tagArray.getJSONObject(i);
                        //put the color and the string in the same string by ':' separate
                        dateArrayLeg.add(oneObject.getString("Color")+":"+oneObject.getString("Label"));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LinearLayoutManager linearManagerLeg;
                RvAdapterForLeg myAdapterLeg;
                linearManagerLeg=new LinearLayoutManager(MainActivity.this,RecyclerView.VERTICAL,false);
                myAdapterLeg=new RvAdapterForLeg(MainActivity.this, dateArrayLeg,MainActivity.this);
                rvLeg.setLayoutManager(linearManagerLeg);
                rvLeg.setAdapter(myAdapterLeg);
                dialog.show();

                //final byte[] buffer;
                //final int size;


                return true;
            }
        });
            popupMenu.show();
        } catch (Exception exp) {
            //if the buttons go out of the window we need to try again and to put the popup menu on textview 'point for popup'
            popupMenu = new PopupMenu(ctw, point);
            for (int i=0;i<theMonitor.size();i++)
                popupMenu.getMenu().add(0,i,0,theMonitor.get(i));

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
            {

                @Override
                public boolean onMenuItemClick(MenuItem item)
                {
                    //open a dialog with new recycler view for the index
                    Dialog dialog  = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.legen);
                    TextView title=(TextView)dialog.findViewById(R.id.includeDialog);
                    title.setText(item.getTitle());

                    RecyclerView rvLeg;
                    rvLeg=(RecyclerView) dialog.findViewById(R.id.colorRv);
                    ArrayList<String> dateArrayLeg=new ArrayList<>();
                    JSONObject legObj=null;
                    JSONArray legArray=null;
                    JSONArray tagArray=null;
                    try {
                        //pick the right legend id and add it to list for the rv
                        legArray= jObject.getJSONArray("Legends");
                        for (int i=0; i < legArray.length(); i++)
                        {
                            if(legArray.getJSONObject(i).getString("Id").equals(""+idLeg))
                                legObj = legArray.getJSONObject(i);

                        }
                        //legObj=legArray.getJSONObject(idLeg);
                        tagArray=legObj.getJSONArray("tags");
                        for (int i=0; i < tagArray.length(); i++)
                        {

                            JSONObject oneObject = tagArray.getJSONObject(i);
                            //put the color and the string in the same string by ':' separate
                            dateArrayLeg.add(oneObject.getString("Color")+":"+oneObject.getString("Label"));
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    LinearLayoutManager linearManagerLeg;
                    RvAdapterForLeg myAdapterLeg;
                    linearManagerLeg=new LinearLayoutManager(MainActivity.this,RecyclerView.VERTICAL,false);
                    myAdapterLeg=new RvAdapterForLeg(MainActivity.this, dateArrayLeg,MainActivity.this);
                    rvLeg.setLayoutManager(linearManagerLeg);
                    rvLeg.setAdapter(myAdapterLeg);
                    dialog.show();

                    //final byte[] buffer;
                    //final int size;


                    return true;
                }
            });
        }




    }


}