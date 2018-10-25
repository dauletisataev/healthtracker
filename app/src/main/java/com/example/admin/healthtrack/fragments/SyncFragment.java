package com.example.admin.healthtrack.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.admin.healthtrack.miBandHelper.util.MiBand;
import com.example.admin.healthtrack.miBandHelper.util.BM;
import com.example.admin.healthtrack.miBandHelper.util.MiBand;
import com.example.admin.healthtrack.miBandHelper.util.Profile;
import com.example.admin.healthtrack.miBandHelper.util.Protocol;
import com.example.admin.healthtrack.miBandHelper.util.UserInfo;
import com.example.admin.healthtrack.R;

import es.dmoral.toasty.Toasty;

public class SyncFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MiBand miBand;
    private View rootView;
    private Button syncBtn, vibrateBtn;
    private ProgressBar progressBar;
    BM bManager;
    public SyncFragment() {
        // Required empty public constructor
    }

    public static SyncFragment newInstance(String param1, String param2) {
        SyncFragment fragment = new SyncFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_sync, container, false);
        miBand = new MiBand();
        syncBtn = (Button) rootView.findViewById(R.id.syncButton);
        vibrateBtn = (Button) rootView.findViewById(R.id.vibrateBtn);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        syncBtn.setOnClickListener(this);
        vibrateBtn.setOnClickListener(this);
        bManager = new BM();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        return rootView;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.syncButton:

                syncBtn.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Toasty.warning(getContext(), "Ваше устройство не поддерживает Bluetooth").show();
                    return;
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        mBluetoothAdapter.enable();
                    }
                }
                miBand.setBluetoothDevice(bManager.getAdapter().getRemoteDevice("C8:0F:10:3E:3B:40"));
                if (miBand.getBluetoothDevice() != null) {
                    bManager.initializeCallBack(miBand);
                    bManager.setBluetoothGatt(miBand.getBluetoothDevice().connectGatt(getContext(),false, bManager.getCallback(), BluetoothDevice.TRANSPORT_LE));

                    if (bManager.getBluetoothGatt().connect()) {
                        Toasty.success(getContext(), "Success").show();
                        progressBar.setVisibility(View.INVISIBLE);
                        vibrateBtn.setVisibility(View.VISIBLE);
                    }
                } else{
                    Toasty.warning(getContext(), "Не удается найти браслет");
                }
                break;
            case R.id.vibrateBtn:
                //bManager.getBluetoothGatt().connect();
                bManager.getCallback().writeCharacteristic(Profile.UUID_SERVICE_VIBRATION, Profile.UUID_CHAR_VIBRATION, Protocol.VIBRATION_WITHOUT_LED);
                break;
        }
    }

}
