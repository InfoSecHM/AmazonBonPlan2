package abp.amazonbonplan.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baoyz.widget.PullRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import abp.amazonbonplan.R;
import abp.amazonbonplan.activity.MainActivity;
import abp.amazonbonplan.adapters.PostAdapter;
import abp.amazonbonplan.models.amazon_data;
import abp.amazonbonplan.receiver.ConnectivityReceiver;
import abp.amazonbonplan.utils.AppController;
import abp.amazonbonplan.utils.Constants;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public LinearLayout coordinatorLayout;
    public boolean isConnected;
    public static final String NA = "NA";
    public RecyclerView recycler_post;
    public PostAdapter adapter;
    List<amazon_data> post_array = new ArrayList<>();
    public PullRefreshLayout swipeRefreshLayout;

    Context c;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);

                return true;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {

                                          }
                                      }
        );
    }

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        setHasOptionsMenu(true);
        checkConnectivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_home,null);
        coordinatorLayout = (LinearLayout) v.findViewById(R.id.coordinatorLayout);
        recycler_post = (RecyclerView) v.findViewById(R.id.recycler_post);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        recycler_post.setLayoutManager(layoutManager);
        recycler_post.setNestedScrollingEnabled(false);
        recycler_post.setItemAnimator(new DefaultItemAnimator());
        swipeRefreshLayout = (PullRefreshLayout) v.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    //  Toast.makeText(getContext(),"u have refreshed the app",Toast.LENGTH_SHORT).show();

                    //when u swipe the app..the getdata method is invoked !
                    getData();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


        try {
            getData();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return v;
    }

    public void getData() throws Exception {
        if (checkConnectivity()){
            try {
                swipeRefreshLayout.setRefreshing(true);
                getAllPosts();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }else {
            swipeRefreshLayout.setRefreshing(false);

            // getAllPosts();
            showSnack();

        }
    }

    public boolean checkConnectivity() {
        return ConnectivityReceiver.isConnected();
    }

    public void showSnack() {

        try {

            Snackbar.make(coordinatorLayout, getString(R.string.no_internet_connected), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.settings), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                        }
                    }).setActionTextColor(Color.RED)
                    .show();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        // Toast.makeText(getContext(),"u have resumed the app",Toast.LENGTH_SHORT).show();
        AppController.getInstance().setConnectivityReceiver(this);
    }



    @Override
    public void onPause() {
        super.onPause();
        // Toast.makeText(getContext(),"u have paused the app",Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onNetworkChange(boolean inConnected) {
        this.isConnected = inConnected;
        // Toast.makeText(getContext(),"the app network have been changed",Toast.LENGTH_SHORT).show();

    }
    public void getAllPosts() throws Exception{
        String TAG = "POSTS";
        String url = Constants.POSTS_URL;
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                parseJson(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {

                    swipeRefreshLayout.setRefreshing(false);
                    Log.e("error", "" +error.getMessage());
                }catch (NullPointerException e)
                {
                    swipeRefreshLayout.setRefreshing(false);
                    e.printStackTrace();
                }


            }
        });


        AppController.getInstance().addToRequestQueue(jsonObjectRequest, TAG);
    }
    public void parseJson(String response){

        try {

            JSONArray array = new JSONArray(response);
            JSONObject jsonObject =null;
            post_array.clear();
            amazon_data p;
            for(int i=0 ; i<array.length() ; i++)
            {
                jsonObject=array.getJSONObject(i);

                String item_title=jsonObject.getString("item_title");
                String item_image=jsonObject.getString("item_image");
                String item_date=jsonObject.getString("item_date");
                String item_url=jsonObject.getString("item_url");
                String item_price=jsonObject.getString("item_price");

                p = new amazon_data();
                p.setItem_title(item_title);
                p.setItem_date(item_date);
                p.setItem_image(item_image);
                p.setItem_url(item_url);
                p.setItem_price(item_price);


                post_array.add(p);
                //realmHelper.save(p);
            }


        }
        catch (JSONException e) {
            swipeRefreshLayout.setRefreshing(false);
            e.printStackTrace();
            //Log.d("error", e.getMessage());
        }
        adapter = new PostAdapter(getContext(), post_array);
        recycler_post.setAdapter(adapter);

        swipeRefreshLayout.setRefreshing(false);

    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
