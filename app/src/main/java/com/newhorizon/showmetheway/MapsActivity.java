package com.newhorizon.showmetheway;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String SP_FILE = "com.newhorizon.showmetheway.SP";

    private GoogleMap mMap;
    private List<LatLng> route = new ArrayList<LatLng>();
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        context = this;

        FloatingActionButton save = (FloatingActionButton) findViewById(R.id.add_route);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                if (route.size() < 2) {
                    builder.setMessage(R.string.error_empty_route);
                    builder.setTitle(R.string.missing_route);
                    builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing, dismiss dialog
                        }
                    });
                } else {
                    SharedPreferences sp = getSharedPreferences(SP_FILE, MODE_PRIVATE);
                    int mode = sp.getInt("MODE_MAPS", -1);
                    if (mode != -1) {
                        String route_crds = "";

                        for (int i = 0; i < route.size(); i++) {
                            route_crds += route.get(i).toString() + ";;";
                        }

                        updateRoute(mode, route_crds);
                    } else {
                        LayoutInflater inflater = getLayoutInflater();
                        builder.setTitle(R.string.route_nickname);
                        builder.setView(inflater.inflate(R.layout.save_route_dialog, null));
                        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences sp = getSharedPreferences(SP_FILE, MODE_PRIVATE);

                                Dialog f = (Dialog) dialog;
                                EditText et_route_name = (EditText) f.findViewById(R.id.route_nickname);

                                String route_name = et_route_name.getText().toString();
                                String route_crds = "";
                                String route_owner = sp.getString("EMAIL", null);

                                for (int i = 0; i < route.size(); i++) {
                                    route_crds += route.get(i).toString() + ";;";
                                }

                                insertRoute(route_name, route_crds, route_owner);
                            }
                        });
                    }
                }
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        FloatingActionButton clear = (FloatingActionButton) findViewById(R.id.clr_route);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                route = new ArrayList<LatLng>();
                mMap.clear();
            }
        });
    }

    private void savingSuccessfull() {
        finish();
        startActivity(new Intent(context, MainActivity.class));
    }

    private void savingFailed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.error_saving_failed);
        builder.setTitle(R.string.error_saving_fail_title);
        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing, dismiss dialog
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void insertRoute(String name, String crds, String owner) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://54.233.119.112:8080/axis2/services/HelloClass/insert_route?";
        try {
            url = url + "nickname=" + URLEncoder.encode(name, "UTF-8");
            url = url + "&coords=" + URLEncoder.encode(crds, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        url = url + "&owner=" + owner;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("Route Saved")) {
                            savingSuccessfull();
                        } else {
                            savingFailed();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                savingFailed();
            }
        });

        queue.add(stringRequest);
    }

    private void updateRoute(int id, String crds) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://54.233.119.112:8080/axis2/services/HelloClass/update_route?";
        try {
            url = url + "id=" + id;
            url = url + "&coords=" + URLEncoder.encode(crds, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("Route Saved")) {
                            savingSuccessfull();
                        } else {
                            savingFailed();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                savingFailed();
            }
        });

        queue.add(stringRequest);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        SharedPreferences sp = getSharedPreferences(SP_FILE, MODE_PRIVATE);
        int mode = sp.getInt("MODE_MAPS", -1);
        if (mode != -1) {
            getRoute(mode);
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                route.add(latLng);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.draggable(true);
                int value = route.size()-1;
                markerOptions.snippet("ID=" + value);
                mMap.addMarker(markerOptions);

                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.addAll(route);
                mMap.addPolyline(polylineOptions);
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                // Do nothing
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                // Do nothing
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                String id = marker.getSnippet();
                id = id.replace("ID=", "");
                int number = Integer.parseInt(id);
                route.set(number, marker.getPosition());
                mMap.clear();

                for (int i = 0; i < route.size(); i++) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(route.get(i));
                    markerOptions.draggable(true);
                    markerOptions.snippet("ID=" + i);
                    mMap.addMarker(markerOptions);
                }

                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.addAll(route);
                mMap.addPolyline(polylineOptions);
            }
        });
    }

    private void requestFailed() {

    }

    private void getRoute(int mode) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://54.233.119.112:8080/axis2/services/HelloClass/request_route?id=" + mode;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String[] partsTotal = response.split("<ns:return>");
                        String route_crds = partsTotal[1].replace("</ns:return>","").replace("amp;", "").replace("</ns:request_routeResponse>", "");
                        route_crds = route_crds.substring(0, route_crds.length() - 2);
                        String[] parts = route_crds.split(";;");
                        for (int i = 0; i < parts.length; i++) {
                            String value = parts[i].substring(10, parts[i].length() - 1);
                            String[] latLng = value.split(",");
                            double latitude = Double.parseDouble(latLng[0]);
                            double longitude = Double.parseDouble(latLng[1]);

                            LatLng location = new LatLng(latitude, longitude);
                            route.add(location);

                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(location);
                            markerOptions.draggable(true);
                            markerOptions.snippet("ID=" + i);
                            mMap.addMarker(markerOptions);
                        }

                        PolylineOptions polylineOptions = new PolylineOptions();
                        polylineOptions.addAll(route);
                        mMap.addPolyline(polylineOptions);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestFailed();
            }
        });

        queue.add(stringRequest);
    }
}
