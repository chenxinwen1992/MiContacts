package org.chenxinwen.micontacts.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import com.bigkoo.quicksidebar.QuickSideBarTipsView;
import com.bigkoo.quicksidebar.QuickSideBarView;
import com.bigkoo.quicksidebar.listener.OnQuickSideBarTouchListener;
import com.thinkcool.circletextimageview.CircleTextImageView;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.chenxinwen.micontacts.CharacterParser;
import org.chenxinwen.micontacts.DividerDecoration;
import org.chenxinwen.micontacts.R;
import org.chenxinwen.micontacts.adapter.ContactsListAdapter;
import org.chenxinwen.micontacts.bean.Contacts;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by chenxinwen on 16/8/9.10:09.
 * Email:191205292@qq.com
 */
public class ContactsFragment extends Fragment implements OnQuickSideBarTouchListener {

    private RecyclerView recyclerView;
    private QuickSideBarTipsView quickSideBarTipsView;
    private QuickSideBarView quickSideBarView;

    private HashMap<String, Integer> letters = new HashMap<>();
    private List<Contacts> contacts = new ArrayList<Contacts>();
    private CharacterParser characterParser;

    public static ContactsFragment newInstance() {
        return new ContactsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        characterParser = CharacterParser.getInstance();
        initView(view);
        return view;
    }

    private void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        quickSideBarTipsView = (QuickSideBarTipsView) view.findViewById(R.id.quickSideBarTipsView);
        quickSideBarView = (QuickSideBarView) view.findViewById(R.id.quickSideBarView);

        //设置监听
        quickSideBarView.setOnQuickSideBarTouchListener(this);


    }


    @Override
    public void onResume() {
        super.onResume();
        if (contacts.size() > 0)
            return;
        checkPermission();
    }

    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;

    private void checkPermission() {
        //版本判断
        if (Build.VERSION.SDK_INT >= 23) {
            //减少是否拥有权限
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_CONTACTS);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSIONS_REQUEST);

            } else {
                initContants();
            }
        } else {
            initContants();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initContants();
                return;
            } else {
                Toast.makeText(getActivity(), "Read Contacts permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void initContants() {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = getActivity().getContentResolver().query(uri,
                new String[]{"display_name", "sort_key"}, null, null, "sort_key");
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                String sortKey = getSortKey(cursor.getString(1));
                Contacts contact = new Contacts();
                contact.setName(name);
                contact.setSortKey(sortKey);
                contacts.add(contact);
            } while (cursor.moveToNext());
        }

        getActivity().startManagingCursor(cursor);//cursor的生命周期托管给activity


        //设置列表数据和浮动header
        final LinearLayoutManager layoutManager = new
                LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Add the sticky headers decoration
        ContactsListWithHeadersAdapter adapter = new ContactsListWithHeadersAdapter();

        ArrayList<String> customLetters = new ArrayList<>();

        int position = 0;
        for (Contacts contact : contacts) {
            String letter = contact.getSortKey();
            //如果没有这个key则加入并把位置也加入
            if (!letters.containsKey(letter)) {
                letters.put(letter, position);
                customLetters.add(letter);
            }
            position++;
        }

        //不自定义则默认26个字母
        quickSideBarView.setLetters(customLetters);
        adapter.addAll(contacts);
        recyclerView.setAdapter(adapter);

        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(adapter);
        recyclerView.addItemDecoration(headersDecor);

        // Add decoration for dividers between list items
        recyclerView.addItemDecoration(new DividerDecoration(getActivity()));
    }

    /**
     * 获取sort key的首个字符，如果是英文字母就直接返回，否则返回#。
     *
     * @param sortKeyString 数据库中读取出的sort key
     * @return 英文字母或者#
     */
    private String getSortKey(String sortKeyString) {


        String pinyin = characterParser.getSelling(sortKeyString);

        String key = pinyin.substring(0, 1).toUpperCase();
        if (key.matches("[A-Z]")) {
            return key;
        }
        return "#";
    }


    @Override
    public void onLetterChanged(String letter, int position, float y) {
        quickSideBarTipsView.setText(letter, position, y);
        //有此key则获取位置并滚动到该位置
        if (letters.containsKey(letter)) {
            recyclerView.scrollToPosition(letters.get(letter));
        }
    }

    @Override
    public void onLetterTouching(boolean touching) {
        //可以自己加入动画效果渐显渐隐
        quickSideBarTipsView.setVisibility(touching ? View.VISIBLE : View.INVISIBLE);
    }


    private class ContactsListWithHeadersAdapter extends ContactsListAdapter<RecyclerView.ViewHolder>
            implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_contacts_item, parent, false);
            return new RecyclerView.ViewHolder(view) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            View itemView = holder.itemView;
            TextView mName = (TextView) itemView.findViewById(R.id.mName);
            CircleTextImageView mUserPhoto = (CircleTextImageView) itemView.findViewById(R.id.mUserPhoto);


            LinearLayout mBottomLayout = (LinearLayout) itemView.findViewById(R.id.mBottomLayout);
            if (position < contacts.size() - 1) {
                if (getItem(position).getSortKey().equals(getItem(position + 1).getSortKey())) {
                    mBottomLayout.setVisibility(View.GONE);
                } else {
                    mBottomLayout.setVisibility(View.VISIBLE);
                }
            } else {
                mBottomLayout.setVisibility(View.GONE);
            }


            String name = getItem(position).getName();

            mName.setText(name);
            if (name.substring(name.length() - 1).equals("(") ||
                    name.substring(name.length() - 1).equals(")") ||
                    name.substring(name.length() - 1).equals("[") ||
                    name.substring(name.length() - 1).equals("]") ||
                    name.substring(name.length() - 1).equals("（") ||
                    name.substring(name.length() - 1).equals("）") ||
                    name.substring(name.length() - 1).equals("【") ||
                    name.substring(name.length() - 1).equals("】")) {
                mUserPhoto.setText(name.substring(name.length() - 2, name.length() - 1));
            } else {
                mUserPhoto.setText(name.substring(name.length() - 1));
            }
        }

        @Override
        public long getHeaderId(int position) {
            return getItem(position).getSortKey().charAt(0);
        }

        @Override
        public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_contacts_head, parent, false);
            return new RecyclerView.ViewHolder(view) {
            };
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
            View itemView = holder.itemView;

            TextView mHead = (TextView) itemView.findViewById(R.id.mHead);
            mHead.setText(String.valueOf(getItem(position).getSortKey()));
//            holder.itemView.setBackgroundColor(getRandomColor());
        }

        private int getRandomColor() {
            SecureRandom rgen = new SecureRandom();
            return Color.HSVToColor(150, new float[]{
                    rgen.nextInt(359), 1, 1
            });
        }

    }
}
