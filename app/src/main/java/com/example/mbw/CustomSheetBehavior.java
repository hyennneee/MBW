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


    //Change sheetBehavior so that collapsed state height is 200.
    //Make the sheet Hideable.
    //Make it so the user can drag the sheet to the half expanded state.
    private void setUpCustomSheetBehavior()
    {
        //sheetBehavior.setHideable(true);
        //setSheetState(BottomSheetBehavior.STATE_HIDDEN);
        //sheetBehavior.setPeekHeight(200);
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()
        {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {
                switch (newState)
                {
                    //When the user is moving the sheet it enters the dragging state.
                    // Setting a flag here makes it possible to differentiate between user interaction and other ways the sheet is moved.
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

            //Set state based on where the user drops the sheet. This is hackish and prone to bugs.
            //This should have been done using a different BottomSheetBehavior.
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset)
            {
                //Using the flag userIsChangingSheetState we can know if the user was dragging the sheet or if it was moved in other ways.
                //Testing for the state settling we can know if the user has dropped the sheet.
                if (sheetBehavior.getState() == BottomSheetBehavior.STATE_SETTLING && userIsChangingSheetState)
                {
                    //Expanded is slideOffset 1, HalfExpanded is slideOffset 0.5, Collapsed is slideOffset 0.
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
                    //The user has let dropped the sheet and is no longer changing the sheet state.
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
        //Make sure the system know the user is not moving the sheet manually
        //Set last sheet state because it's more reliable than getting current state.
        //Change navigation options.
        //Change if the keyboard should be visible.
        userIsChangingSheetState = false;
        sheetBehavior.setState(sheetState);
        lastSheetState = sheetState;
    }

}