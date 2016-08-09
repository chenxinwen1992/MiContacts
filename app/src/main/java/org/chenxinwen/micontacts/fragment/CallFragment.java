package org.chenxinwen.micontacts.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thinkcool.circletextimageview.CircleTextImageView;

import org.chenxinwen.micontacts.DividerDecoration;
import org.chenxinwen.micontacts.R;
import org.chenxinwen.micontacts.bean.Contacts;
import org.chenxinwen.micontacts.bean.RecordEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chenxinwen on 16/8/9.10:07.
 * Email:191205292@qq.com
 */
public class CallFragment extends Fragment {


    private RecyclerView recyclerView;
    private List<RecordEntity> recordEntityList = new ArrayList<>();


    public static CallFragment newInstance() {
        return new CallFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call, container, false);
//        CallLog.Calls.INCOMING_TYPE
        initView(view);
        return view;
    }

    private void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (recordEntityList.size() > 0)
            return;
        checkPermission();
    }

    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;

    private void checkPermission() {
        //版本判断
        if (Build.VERSION.SDK_INT >= 23) {
            //减少是否拥有权限
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_CALL_LOG);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_CALL_LOG}, READ_CONTACTS_PERMISSIONS_REQUEST);

            } else {
                initRecord();
            }
        } else {
            initRecord();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initRecord();
                return;
            } else {
                Toast.makeText(getActivity(), "Read Contacts permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void initRecord() {
        Uri uri = CallLog.Calls.CONTENT_URI;
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CALL_LOG}, READ_CONTACTS_PERMISSIONS_REQUEST);
            return;
        }

        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {

                RecordEntity recordEntity = new RecordEntity();
                //号码
                recordEntity.setNumber(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)));
                //呼叫类型
                recordEntity.setType(Integer.parseInt(cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE))));


                SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE))));
                //呼叫时间
                recordEntity.setlDate(sfd.format(date));
                //联系人
                recordEntity.setName(cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME)));

                Log.e("---------->", cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME))+"");

                //通话时间,单位:s
                recordEntity.setDuration(cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION)));

                recordEntityList.add(recordEntity);
            } while (cursor.moveToNext());
        }

        getActivity().startManagingCursor(cursor);//cursor的生命周期托管给activity


        //设置列表数据和浮动header
        final LinearLayoutManager layoutManager = new
                LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        RecordAdapter recordAdapter = new RecordAdapter();
        recyclerView.setAdapter(recordAdapter);

        // Add decoration for dividers between list items
        recyclerView.addItemDecoration(new DividerDecoration(getActivity()));


    }


    class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    getActivity()).inflate(R.layout.adapter_call, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            try {
                if (recordEntityList.get(position).getName().isEmpty()) {
                    holder.mName.setVisibility(View.GONE);
                } else {
                    holder.mName.setVisibility(View.VISIBLE);
                    holder.mName.setText(recordEntityList.get(position).getName());
                }
            }catch (Exception e){
                holder.mName.setVisibility(View.GONE);
            }


            holder.mNumber.setText(recordEntityList.get(position).getNumber());

            if (recordEntityList.get(position).getType() == 1) {
                //incoming
                holder.mTime.setText(recordEntityList.get(position).getlDate()
                        + " 呼入" + recordEntityList.get(position).getDuration() + "秒");
                holder.mName.setTextColor(Color.parseColor("#000000"));
            } else if (recordEntityList.get(position).getType() == 2) {
                //outgoing
                holder.mTime.setText(recordEntityList.get(position).getlDate()
                        + " 呼出" + recordEntityList.get(position).getDuration() + "秒");
                holder.mName.setTextColor(Color.parseColor("#000000"));

            } else if (recordEntityList.get(position).getType() == 3) {
                //missed
                holder.mTime.setText(recordEntityList.get(position).getlDate());

                holder.mName.setTextColor(Color.parseColor("#e63c31"));

            } else if (recordEntityList.get(position).getType() == 4) {
                //voicemails
                holder.mTime.setText(recordEntityList.get(position).getlDate());

                holder.mName.setTextColor(Color.parseColor("#e63c31"));
            }


            try {
                if (recordEntityList.get(position).getName().substring(
                        recordEntityList.get(position).getName().length() - 1).equals("(") ||
                        recordEntityList.get(position).getName().substring(recordEntityList.get(position).getName().length() - 1).equals(")") ||
                        recordEntityList.get(position).getName().substring(recordEntityList.get(position).getName().length() - 1).equals("[") ||
                        recordEntityList.get(position).getName().substring(recordEntityList.get(position).getName().length() - 1).equals("]") ||
                        recordEntityList.get(position).getName().substring(recordEntityList.get(position).getName().length() - 1).equals("（") ||
                        recordEntityList.get(position).getName().substring(recordEntityList.get(position).getName().length() - 1).equals("）") ||
                        recordEntityList.get(position).getName().substring(recordEntityList.get(position).getName().length() - 1).equals("【") ||
                        recordEntityList.get(position).getName().substring(recordEntityList.get(position).getName().length() - 1).equals("】")) {
                    holder.mUserPhoto.setText(recordEntityList.get(position).getName().substring(recordEntityList.get(position).getName().length() - 2, recordEntityList.get(position).getName().length() - 1));
                } else {
                    holder.mUserPhoto.setText(recordEntityList.get(position).getName().substring(recordEntityList.get(position).getName().length() - 1));
                }
            }catch (Exception e){
                holder.mUserPhoto.setText("Mi");
            }


        }

        @Override
        public int getItemCount() {
            return recordEntityList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            LinearLayout mLayout;
            CircleTextImageView mUserPhoto;


            TextView mName;
            TextView mNumber;
            TextView mTime;

            public MyViewHolder(View view) {
                super(view);
                mLayout = (LinearLayout) view.findViewById(R.id.mLayout);
                mUserPhoto = (CircleTextImageView) view.findViewById(R.id.mUserPhoto);
                mName = (TextView) view.findViewById(R.id.mName);
                mNumber = (TextView) view.findViewById(R.id.mNumber);
                mTime = (TextView) view.findViewById(R.id.mTime);
            }
        }
    }

}
