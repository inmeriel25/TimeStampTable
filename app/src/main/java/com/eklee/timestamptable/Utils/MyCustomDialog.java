package com.eklee.timestamptable.Utils;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.eklee.timestamptable.EditActivity;
import com.eklee.timestamptable.R;

import androidx.annotation.Nullable;

/**
 * Created by eklee on 2018-12-27.
 * referred to CodingWithMitch in Youtube https://www.youtube.com/watch?v=--dJm6z5b0s
 */

public class MyCustomDialog extends DialogFragment{



    private static final String TAG = "MyCustomDialog";

    //widgets
    private EditText mInput;
    private TextView mActionYes, mActionNo;

    //vars


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_custom_text, container, false);
        mActionYes = view.findViewById(R.id.action_yes);
        mActionNo = view.findViewById(R.id.action_no);
        mInput = view.findViewById(R.id.input);

        mActionNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: closing dialog");
                getDialog().dismiss();
            }
        });

        mActionYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: capturing input");

                String input = mInput.getText().toString();

                if(!input.equals("")){
                    //Easiest way : just set the value
                    ((EditActivity)getActivity()).custom_edit_text.setText(input);
                }

                getDialog().dismiss();
            }
        });

        return view;
    }
}
