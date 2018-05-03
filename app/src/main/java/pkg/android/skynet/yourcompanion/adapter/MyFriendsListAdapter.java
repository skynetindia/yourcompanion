package pkg.android.skynet.yourcompanion.adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.app.Constants;
import pkg.android.skynet.yourcompanion.app.SessionManager;
import pkg.android.skynet.yourcompanion.app.YourCompanion;
import pkg.android.skynet.yourcompanion.callbacks.ReqResAPICallBacks;
import pkg.android.skynet.yourcompanion.callbacks.ReqRespCallBack;
import pkg.android.skynet.yourcompanion.fragment.MyFriendsFragment;
import pkg.android.skynet.yourcompanion.models.FriendsData;

public class MyFriendsListAdapter extends ArrayAdapter<FriendsData> {

    private List<FriendsData> mData;
    private Fragment fragment;

    private String primaryContactId = "";

    private SessionManager sessionManager;
    private YourCompanion yourCompanion;

    public MyFriendsListAdapter(Fragment fragment, List<FriendsData> datas, String primaryContactId) {
        super(fragment.getActivity(), R.layout.row_friends_list);
        this.mData = datas;
        this.fragment = fragment;
        this.primaryContactId = primaryContactId;

        sessionManager = new SessionManager(fragment.getActivity());
        yourCompanion = (YourCompanion)fragment.getActivity().getApplication();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mData.size();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.row_friends_list, null);
        }

        TextView friendNameTxt = (TextView) view.findViewById(R.id.txt_friends_name);
        TextView numberTxt = (TextView) view.findViewById(R.id.txt_number);
        CircleImageView friendsImg = (CircleImageView) view.findViewById(R.id.img_friends);
        ImageView trashImg = (ImageView)view.findViewById(R.id.img_trash);

        friendNameTxt.setText(mData.get(position).getFirstName() + " " + mData.get(position).getLastName());
        numberTxt.setText(mData.get(position).getPhone());

        if (!mData.get(position).getUserImg().equals("")) {
            Picasso.with(fragment.getActivity())
                    .load(mData.get(position).getUserImg())
                    .placeholder(R.drawable.ic_profile_male)
                    .error(R.drawable.ic_profile_male)
                    .into(friendsImg);
        }

        trashImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete(mData.get(position).getId(), position);
            }
        });


        HashMap<String, String> user = sessionManager.getUserDetails();

        String friendId = "";
        if (user.get(SessionManager.USER_ID).equals(mData.get(position).getUserId())) {
        }else {
            friendId = mData.get(position).getUserId();
        }

        if (primaryContactId.equals(friendId)) {
            view.setBackgroundColor(fragment.getActivity().getResources().getColor(R.color.colorPrimaryContact));
            trashImg.setVisibility(View.GONE);
            sessionManager.setPrimaryNumber(mData.get(position).getPhone());

        }else {
            view.setBackgroundColor(Color.WHITE);
        }

        final String finalFriendId = friendId;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (primaryContactId.equals(finalFriendId))
                    chooseOption(position, finalFriendId, false);
                else chooseOption(position, finalFriendId, true);
            }
        });

        return view;
    }


    /**
     * Used to confirm logout...
     */
    public void confirmDelete(final String id, final int position) {
        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(fragment.getActivity());

        LayoutInflater inflater = fragment.getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_confirm_dialog, null);
        dialogBuilder.setView(dialogView);

        final android.support.v7.app.AlertDialog alertDialog = dialogBuilder.create();

        alertDialog.show();

        alertDialog.setCancelable(false);
        alertDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

        final TextView titleTxt, messageTxt, positiveTxt, negativeTxt;
        ImageView alertTypeImg = null;

        messageTxt = (TextView)dialogView.findViewById(R.id.txt_message);
        titleTxt = (TextView)dialogView.findViewById(R.id.txt_title);
        positiveTxt = (TextView)dialogView.findViewById(R.id.txt_positive);
        negativeTxt = (TextView)dialogView.findViewById(R.id.txt_negative);

        titleTxt.setText(fragment.getString(R.string.txt_please_confirm));
        positiveTxt.setText("Yes, i'm sure");
        messageTxt.setText(R.string.msg_remove_friend);

        positiveTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFriends(mData.get(position).getId(), position);
                alertDialog.dismiss();
            }
        });

        negativeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }


    /**
     * Used to delete friends..
     * @param id
     * @param position
     */
    private void deleteFriends(String id, final int position) {
        HashMap<String, String> user = sessionManager.getUserDetails();
        String userId = user.get(SessionManager.USER_ID);

        try{
            ReqResAPICallBacks reqResAPICallBacks = new ReqResAPICallBacks();
            reqResAPICallBacks.deleteFriends(fragment.getActivity(), new ReqRespCallBack() {
                @Override
                public void onResponse(Object resObj, String resMessage, String resCode) {

                }

                @Override
                public void onResponse(String response, String resMessage, String resCode) {
                    if (resCode.equals(Constants.FAILURE)) {
                        yourCompanion.showAlertDialog(fragment.getActivity(), resMessage, "Ok");
                    }else {
                        Toast.makeText(fragment.getActivity(), resMessage, Toast.LENGTH_SHORT).show();
                        ((MyFriendsFragment)fragment).getFriends(false);
                        mData.remove(position);
                        notifyDataSetChanged();
                    }
                }
            }, userId, id, Constants.NOTIFY_TYPE_FRIEND_DELETE, true);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Used to choose multiple option..
     * @param position
     */
    private void chooseOption(final int position, final String friendId, boolean isPrimaryShow) {
        HashMap<String, String> user = sessionManager.getUserDetails();

        BottomSheet.Builder bottomSheet = new BottomSheet.Builder(fragment.getActivity());
        bottomSheet.title("Choose Option");

        if (isPrimaryShow)
            bottomSheet.sheet(0,  fragment.getActivity().getString(R.string.txt_primary_contact));

        bottomSheet.sheet(1,  fragment.getActivity().getString(R.string.txt_phone_call));
        bottomSheet.sheet(2,  fragment.getString(R.string.txt_message));

        bottomSheet.listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        updatePrimaryFriends(friendId);
                        break;

                    case 1:
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + mData.get(position).getPhone()));
                        fragment.startActivity(callIntent);
                        break;

                    case 2:
                        Intent msgIntent = new Intent(Intent.ACTION_VIEW);
                        msgIntent.setData(Uri.parse("sms:"+mData.get(position).getPhone()));
                        fragment.startActivity(msgIntent);
                        break;
                }
            }
        });

        bottomSheet.show();
    }


    /**
     * Used to update primary friends..
     * @param friendId
     */
    private void updatePrimaryFriends(String friendId) {
        HashMap<String, String> user = sessionManager.getUserDetails();

        try{
            ReqResAPICallBacks reqResAPICallBacks = new ReqResAPICallBacks();
            reqResAPICallBacks.updatePrimaryFriend(fragment.getActivity(), new ReqRespCallBack() {
                @Override
                public void onResponse(Object resObj, String resMessage, String resCode) {

                }

                @Override
                public void onResponse(String response, String resMessage, String resCode) {
                    if (resCode.equals(Constants.FAILURE)) {
                        Toast.makeText(fragment.getActivity(), resMessage, Toast.LENGTH_LONG).show();

                    }else {
                        Toast.makeText(fragment.getActivity(), resMessage, Toast.LENGTH_LONG).show();
                        ((MyFriendsFragment)fragment).getFriends(false);
                    }
                }
            }, user.get(SessionManager.USER_ID), friendId, true);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
