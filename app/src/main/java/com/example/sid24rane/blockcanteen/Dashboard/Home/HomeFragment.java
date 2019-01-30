package com.example.sid24rane.blockcanteen.Dashboard.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.sid24rane.blockcanteen.QRGeneratorActivity;
import com.example.sid24rane.blockcanteen.QRScannerActivity;
import com.example.sid24rane.blockcanteen.R;


public class HomeFragment extends Fragment {

    private Button send;
    private Button receive;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        send = (Button) view.findViewById(R.id.send);
        receive = (Button) view.findViewById(R.id.receive);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),QRScannerActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        });

        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),QRGeneratorActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        });

        return view;
    }

}
