package car.gagan.cobratotrackit.Classes.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import car.gagan.cobratotrackit.Adapters.NotificationAdapter;
import car.gagan.cobratotrackit.Classes.MainActivity;
import car.gagan.cobratotrackit.R;
import car.gagan.cobratotrackit.model.EventsModel;
import car.gagan.cobratotrackit.utills.BaseFragmentHome;
import car.gagan.cobratotrackit.utills.CallBackWebService;
import car.gagan.cobratotrackit.utills.Global_Constants;
import car.gagan.cobratotrackit.utills.Utills_G;
import car.gagan.cobratotrackit.webservice.SuperWebServiceG;

public class Notification extends BaseFragmentHome
{


    private ListView listVnotifications;

    private List<EventsModel> listData;


    public Notification()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
    )
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_notification, container, false);

        setupToolbar(v);

        listVnotifications = (ListView) v.findViewById(R.id.listVnotifications);

        Calendar c = Calendar.getInstance();
        Date date = c.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


        getData("2000-01-01", sdf.format(date), getVehicleID(), "en");


        return v;
    }


    private void setupToolbar(View V)
    {
        Toolbar toolbar = (Toolbar) V.findViewById(R.id.toolbar);

        ((TextView) toolbar.findViewById(R.id.txtvHeading)).setText(R.string.notifications);

    }


    //    private Dialog dialog;

    private void getData(String startDate, String endDate, String vehicleID, String lanuage)
    {


        new SuperWebServiceG(urlEvents(startDate, endDate, vehicleID, lanuage), new HashMap<String, String>(), new CallBackWebService()
        {
            @Override
            public void webOnFinish(String output)
            {

                try
                {

                    listData = new ArrayList<>();


                    JSONObject jObj = new JSONObject(output);

                    if (jObj.getString(Global_Constants.Status).equals(Global_Constants.success))
                    {

                        JSONArray jobjinnerArray = new JSONArray(jObj.getString(Global_Constants.Message));


                        for (int i = 0; i < jobjinnerArray.length(); i++)
                        {

                            JSONObject jobjInner = jobjinnerArray.getJSONObject(i);

                            String EventName = jobjInner.optString("EventName");
                            String MessageTime = jobjInner.optString("MessageTime");
                            String Address = jobjInner.optString("Address");


                            LatLng latlng = null;
                            try
                            {
                                latlng = new LatLng(Double.parseDouble(jobjInner.optString("Latitude")), Double.parseDouble(jobjInner.optString("Longitude")));
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }


                            EventsModel data = new EventsModel(MessageTime, Address, EventName, latlng);

                            listData.add(data);
//                            ShowNotiList(LayoutNoti, data, i);


                        }

                        listVnotifications.setAdapter(new NotificationAdapter(getActivity(), listData));
                        listVnotifications.setOnItemClickListener(new AdapterView.OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                            {
                                Utills_G.startGoogleMaps(getActivity(), listData.get(i).getLatLng());
                            }
                        });


                    }


                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }


            }
        }).execute();
    }


    //    http://112.196.34.42:8091/Customer/GetEventHistory?StartDate=2000-01-01&EndDate=2015-09-27&VehicleId=454&Language=he
    private String urlEvents(String startDate, String endDate, String vehicleID, String lanuage)
    {
        return Global_Constants.URL + "Customer/GetEventHistory?StartDate=" + startDate + "&EndDate=" + endDate + "&VehicleId=" + vehicleID + "&Language=" + lanuage;
    }


}