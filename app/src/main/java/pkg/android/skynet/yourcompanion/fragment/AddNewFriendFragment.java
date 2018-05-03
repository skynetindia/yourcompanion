package pkg.android.skynet.yourcompanion.fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import libs.indexablelistview.IndexableListView;
import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.adapter.ContactAdapter;
import pkg.android.skynet.yourcompanion.adapter.ContactListAdapter;
import pkg.android.skynet.yourcompanion.app.Constants;
import pkg.android.skynet.yourcompanion.app.SessionManager;
import pkg.android.skynet.yourcompanion.app.YourCompanion;
import pkg.android.skynet.yourcompanion.callbacks.ReqResAPICallBacks;
import pkg.android.skynet.yourcompanion.callbacks.ReqRespCallBack;
import pkg.android.skynet.yourcompanion.models.ContactData;

/**
 * Created by ST-3 on 28-09-2017.
 */

public class AddNewFriendFragment extends Fragment implements View.OnClickListener, TextWatcher {

    private LinearLayout mSideIndexLayout;
    private IndexableListView mContactRec;
    private EditText mSearchEdt;

    private List<ContactData> mContactDatas = new ArrayList<>();
    public ArrayList<String> mSelectedContacts = new ArrayList<>();
    private Map<String, Integer> mapIndex;

    private ContactAdapter mContactListAdapter;

    private YourCompanion yourCompanion;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new_friend, container, false);

        yourCompanion = (YourCompanion)getActivity().getApplication();
        sessionManager = new SessionManager(getActivity());

        initControls(view);

        return view;
    }


    /**
     * Used to initialize all the controls..
     * @param view
     */
    private void initControls(View view) {
        mSideIndexLayout = (LinearLayout) view.findViewById(R.id.ll_side_index);

        mContactRec = (IndexableListView)view.findViewById(R.id.rec_contacts);

        mSearchEdt = (EditText)view.findViewById(R.id.edt_search);
        mSearchEdt.addTextChangedListener(this);

        view.findViewById(R.id.img_back).setOnClickListener(this);
        view.findViewById(R.id.txt_add_selected).setOnClickListener(this);
        view.findViewById(R.id.txt_add_new_contact).setOnClickListener(this);
    }


    /**
     * Used to set the contact adapter..
     */
    private void setContactAdapter() {
        mContactListAdapter = new ContactAdapter(AddNewFriendFragment.this, mContactDatas);
        mContactRec.setAdapter(mContactListAdapter);
    }


    /**
     * Used to get all the contact list..
     */
    private void getContactList() {
        mContactDatas.clear();

        ContentResolver cr = getActivity().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur != null && cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id));
                Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);

                    if (pCur != null && pCur.getCount() > 0) {

                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                            phoneNo = phoneNo.replaceAll("[^0-9+]+", "");

                            ContactData data = new ContactData();
                            data.setName(name);
                            data.setPhoto(photoUri);
                            data.setNumber(phoneNo);
                            mContactDatas.add(data);
                        }
                        pCur.close();
                    }
                }
            }
        } else {
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "No Contact Found", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cur.close();
    }


    private void getIndexList(List<ContactData> contactDatas) {
        mapIndex = new LinkedHashMap<>();
        for (int i = 0; i < contactDatas.size(); i++) {
            String fruit = contactDatas.get(i).getName();
            String index = fruit.substring(0, 1);

            if (mapIndex.get(index) == null  && index.equals(index.toUpperCase()))
                mapIndex.put(index, i);
        }
    }


    /**
     * Used to display All the first letter at the right side..
     */
    private void displayIndex() {
        TextView textView;
        List<String> indexList = new ArrayList<>(mapIndex.keySet());
        for (String index : indexList) {
            textView = (TextView) getActivity().getLayoutInflater().inflate(
                    R.layout.row_side_index, null);
            textView.setText(index);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView selectedIndex = (TextView) v;
                    //list_contact.setSelection(mapIndex.get(selectedIndex.getText()));
                }
            });
            mSideIndexLayout.addView(textView);
        }
    }


    /**
     * Used to check is the contact contain countrycode or not...
     */
    private boolean checkForCountryCode() {
        for (int i = 0; i < mSelectedContacts.size(); i++) {
            if (!mSelectedContacts.get(i).contains("+")) {
                return false;
            }
        }
        return true;
    }


    /**
     * Used to add friends...
     */
    private void addFriends() {
        HashMap<String, String> user = sessionManager.getUserDetails();
        String mobileNo = user.get(SessionManager.PHONE);

        try {
            ReqResAPICallBacks reqResAPICallBacks = new ReqResAPICallBacks();
            reqResAPICallBacks.inviteFriends(getActivity(), new ReqRespCallBack() {
                @Override
                public void onResponse(Object resObj, String resMessage, String resCode) {

                }

                @Override
                public void onResponse(String response, String resMessage, String resCode) {
                    if (resCode.equals(Constants.FAILURE)) {
                        yourCompanion.showAlertDialog(getActivity(), resMessage, "Ok");
                    }else {
                        Toast.makeText(getActivity(), resMessage, Toast.LENGTH_LONG).show();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_body, new MyFriendsFragment()).commit();
                    }
                }
            }, mobileNo, mSelectedContacts, true);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                getActivity().getSupportFragmentManager().popBackStack();
                break;

            case R.id.txt_add_selected:
                if (mSelectedContacts.size() == 0)
                    yourCompanion.showAlertDialog(getActivity(), getString(R.string.err_select_contact), "Ok");
                else{
                    if (checkForCountryCode())
                        addFriends();
                    else
                        yourCompanion.showAlertDialog(getActivity(), getString(R.string.err_contain_countrycode), "Ok");
                }
                break;

            case R.id.txt_add_new_contact:
                Intent addContactIntent = new Intent(Intent.ACTION_INSERT);
                addContactIntent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                addContactIntent.putExtra(ContactsContract.Intents.Insert.NAME,"");
                addContactIntent.putExtra(ContactsContract.Intents.Insert.PHONE,"");
                startActivity(addContactIntent);

                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        mContactListAdapter.filter(charSequence.toString());
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }


    /**
     * Used to get all the contact list and set in to adapter..
     */
    class ContactListAsync extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(getActivity(), null, "Loading...");
        }

        @Override
        protected Void doInBackground(Void... params) {
            getContactList();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (dialog.isShowing())
                dialog.dismiss();

            Collections.sort(mContactDatas, new Comparator<ContactData>() {

                @Override
                public int compare(ContactData data1, ContactData data2) {
                    return data1.getName().toLowerCase().compareTo(data2.getName().toLowerCase());
                }
            });

            setContactAdapter();

            //getIndexList(mContactDatas);
            //displayIndex();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        new ContactListAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
