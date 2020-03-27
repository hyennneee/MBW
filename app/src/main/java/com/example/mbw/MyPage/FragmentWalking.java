package com.example.mbw.MyPage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mbw.DB.SpeedDBHelper;
import com.example.mbw.R;

public class FragmentWalking extends Fragment {

    TextView walkingSpeed;
    Button speedBtn;
    SpeedDBHelper speedDBHelper;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_walking, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        speedDBHelper = new SpeedDBHelper(getContext());
        walkingSpeed = view.findViewById(R.id.walkingSpeedText);
        speedBtn = view.findViewById(R.id.speedButton);

        //현재 저장된 speed 값 가져오기
        int cnt = speedDBHelper.count();
        String speed;
        if(cnt == 0){
            speed = "2.1";
            speedDBHelper.insertSpeed(0, speed);
        }
        else{
            speed = speedDBHelper.getSpeed();
        }
        walkingSpeed.setText("" + speed);

        walkingSpeed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager =(InputMethodManager)getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
        walkingSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText)view).setInputType(EditorInfo.TYPE_CLASS_NUMBER); // setCursorVisible(true); 도 가능하다.
            }

        });

        speedBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (walkingSpeed.isFocused()) {
                        Rect outRect = new Rect();
                        walkingSpeed.getGlobalVisibleRect(outRect);
                        if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                            walkingSpeed.clearFocus();
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                    }
                }
                return false;
            }
        });

        speedBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newSpeed = walkingSpeed.getText().toString();
                if(newSpeed.isEmpty()){
                    Toast.makeText(getContext(), "사용자 도보 속력을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else{
                    try{
                    speedDBHelper.insertSpeed(0, newSpeed);
                    Toast.makeText(getContext(), "등록되었습니다", Toast.LENGTH_SHORT).show();
                    walkingSpeed.setText("" + newSpeed);
                    walkingSpeed.setInputType(EditorInfo.TYPE_NULL);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
