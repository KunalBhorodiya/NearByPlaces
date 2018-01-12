package com.example.kunal.food.Profile;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.kunal.food.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Category_ extends Fragment {


    RecyclerView placesList;
    ArrayList<String> arrayList;
    MyAdapter1 myAdapter;
    Button logout;

    public Category_() {
        // Required empty public constructor
    }

    public class MyAdapter1 extends RecyclerView.Adapter<MyAdapter1.ViewHolder>{
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.placelist_button_, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            String placeName = arrayList.get(position);

            placeName = placeName.replaceAll("_", " ");

            placeName = placeName.substring(0,1).toUpperCase() + placeName.substring(1);

            holder.button.setText(placeName);

            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String getName = holder.button.getText().toString();
                    getName = getName.replaceAll(" ", "_");
                    getName = getName.toLowerCase();
                    Intent intent = new Intent(getActivity(), Home_.class);
                    intent.putExtra("placeName", getName);
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            Button button;
            public ViewHolder(View itemView) {
                super(itemView);
                button = itemView.findViewById(R.id.placeButton);
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category_, container, false);

        arrayList = new ArrayList<>();
        placesList = (RecyclerView) view.findViewById(R.id.placesList);
        myAdapter = new MyAdapter1();
        logout = getActivity().findViewById(R.id.logout_M);
        logout.setVisibility(View.VISIBLE);

        arrayList.add("school");
        arrayList.add("hospital");
        arrayList.add("restaurant");
        arrayList.add("atm");
        arrayList.add("bank");
        arrayList.add("cafe");
        arrayList.add("book_store");
        arrayList.add("bus_station");
        arrayList.add("shopping_mall");
        arrayList.add("jewelry_store");
        arrayList.add("bakery");
        arrayList.add("shoe_store");
        arrayList.add("hindu_temple");
        arrayList.add("gym");
        arrayList.add("gas_station");
        arrayList.add("electrician");
        arrayList.add("park");
        arrayList.add("clothing_store");
        arrayList.add("car_dealer");
        arrayList.add("car_repair");
        arrayList.add("insurance_agency");
        arrayList.add("pet_store");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
        linearLayoutManager.setReverseLayout(false);
        placesList.setLayoutManager(linearLayoutManager);
        placesList.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if(i == KeyEvent.KEYCODE_BACK){
                    getActivity().finish();
                }
                return false;
            }
        });
    }
}
