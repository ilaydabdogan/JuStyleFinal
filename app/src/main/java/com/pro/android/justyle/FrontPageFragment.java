package com.pro.android.justyle;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FrontPageFragment  extends Fragment implements View.OnClickListener {
    private ImageView mMarketplaceView;
    private ImageView mWardrobeView;


    private FragmentActionListener mFragmentActionListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_front_page, container, false);
        mMarketplaceView = rootView.findViewById(R.id.MarketPlaceView);
        mWardrobeView = rootView.findViewById(R.id.WardrobeView);

        mMarketplaceView.setOnClickListener(this);
        mWardrobeView.setOnClickListener(this);

        initUI();

        return rootView;
    }

    /**
     *
     * @param fragmentActionListener is an instance of the Interface FragmentActionListener
     * it sets the variable mFragmentActionListener to be fragmentActionListener
     */
    public void setFragmentActionListener(FragmentActionListener fragmentActionListener){
        this.mFragmentActionListener = fragmentActionListener;
    }

    /**
     * initUI is a method that calls other methods so it does not have to be done more than once
     * it calls onClick methods for the MyWardrobeView imageView and the MarketplaceView imageView
     */
    private void initUI(){
        Context context = getContext();
        mWardrobeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentActionListener.onWardrobeFragmentClicked();
            }
        });
        mMarketplaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentActionListener.onMarketplaceFragmentClicked();
            }
        });
    }

    /**
     *
     * @param v is the position that the onClick requires on the UI for the method to be called
     *          this onClick is empty and not connected to a piece of UI directly
     */
    @Override
    public void onClick(View v) {

    }
}
