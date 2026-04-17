package com.example.allgoods.UI.Customer.MyCards;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.allgoods.R;

public class MyCardsFragment extends Fragment {

    public MyCardsFragment() {}


    public static MyCardsFragment newInstance(String param1, String param2) {
        MyCardsFragment fragment = new MyCardsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_cards, container, false);
    }
}