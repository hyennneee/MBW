package com.example.mbw;


import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class CustomSheetBehavior
{
    private DetailPathActivity parentActivity;
    private View fillerSpaceForToolbar;
    private View bottomSheet;

    private BottomSheetBehavior sheetBehavior;
    private int lastSheetState;
    private boolean userIsChangingSheetState = false;

    public CustomSheetBehavior(View view, DetailPathActivity parentActivity)
    {
        this.bottomSheet = view;
        this.parentActivity = parentActivity;
        sheetBehavior = BottomSheetBehavior.from(view);
        setUpCustomSheetBehavior();
    }

    private void setUpCustomSheetBehavior()
    {

        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()
        {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {
                switch (newState)
                {

                    case BottomSheetBehavior.STATE_DRAGGING:
                    {
                        userIsChangingSheetState = true;
                    }
                    case BottomSheetBehavior.STATE_HIDDEN:
                    {
                        //setSheetState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset)
            {
                if (sheetBehavior.getState() == BottomSheetBehavior.STATE_SETTLING && userIsChangingSheetState)
                {
                    if ((slideOffset > 0.5 && lastSheetState == BottomSheetBehavior.STATE_HALF_EXPANDED) || (slideOffset >= 0.70 && lastSheetState == BottomSheetBehavior.STATE_COLLAPSED))
                    {
                        setSheetState(BottomSheetBehavior.STATE_EXPANDED);
                    } else if ((slideOffset > 0 && lastSheetState == BottomSheetBehavior.STATE_COLLAPSED) || (slideOffset < 1 && lastSheetState == BottomSheetBehavior.STATE_EXPANDED))
                    {
                        setSheetState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                    } else if ((slideOffset < 0.5 && lastSheetState == BottomSheetBehavior.STATE_HALF_EXPANDED) || (slideOffset <= 0.40 && lastSheetState == BottomSheetBehavior.STATE_EXPANDED))
                    {
                        setSheetState(BottomSheetBehavior.STATE_COLLAPSED);
                    } else
                    {
                        setSheetState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                    userIsChangingSheetState = false;
                }
            }
        });
    }


    //Move sheet to new state and change appearance accordingly.
    public void setSheetState(int state)
    {
        switch (state)
        {
            case BottomSheetBehavior.STATE_EXPANDED:
            {
                onStateChange(BottomSheetBehavior.STATE_EXPANDED);
                break;
            }
            case BottomSheetBehavior.STATE_HALF_EXPANDED:
            {
                onStateChange(BottomSheetBehavior.STATE_HALF_EXPANDED);
                break;
            }
            case BottomSheetBehavior.STATE_COLLAPSED:
            {
                onStateChange(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            }
            case BottomSheetBehavior.STATE_HIDDEN:
            {
                onStateChange(BottomSheetBehavior.STATE_HIDDEN);
                break;
            }
            case BottomSheetBehavior.STATE_DRAGGING:
            {
                userIsChangingSheetState = true;
                break;
            }
        }
    }

    private void onStateChange(int sheetState)
    {
        userIsChangingSheetState = false;
        sheetBehavior.setState(sheetState);
        lastSheetState = sheetState;
    }

}