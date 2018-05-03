package pkg.android.skynet.yourcompanion.adapter;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import pkg.android.skynet.yourcompanion.models.RequestSentData;

public class RequestSendListAdapter extends ArrayAdapter<RequestSentData> {

    private List<RequestSentData> mData;
    private Fragment fragment;

    private SessionManager sessionManager;
    private YourCompanion yourCompanion;

    public RequestSendListAdapter(Fragment fragment, List<RequestSentData> datas) {
        super(fragment.getActivity(), R.layout.row_request_send);
        this.mData = datas;
        this.fragment = fragment;

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
            view = layoutInflater.inflate(R.layout.row_request_send, null);
        }

        TextView friendNameTxt = (TextView) view.findViewById(R.id.txt_friends_name);
        TextView numberTxt = (TextView) view.findViewById(R.id.txt_number);
        CircleImageView friendsImg = (CircleImageView) view.findViewById(R.id.img_friends);
        ImageView cancelImg = (ImageView)view.findViewById(R.id.img_cancel);

        if (mData.get(position).getFirstName() != null && !mData.get(position).getFirstName().equals("")) {
            friendNameTxt.setText(mData.get(position).getFirstName() + " " + mData.get(position).getLastName());
            numberTxt.setText(mData.get(position).getPhone());

        }else {
            friendNameTxt.setText(mData.get(position).getInvReqNo());
            numberTxt.setText(mData.get(position).getInvReqNo());
        }

        /*Picasso.with(fragment.getActivity())
                .load(mData.get(position).getUserImg())
                .placeholder(R.drawable.ic_profile_male)
                .error(R.drawable.ic_profile_male)
                .into(friendsImg);*/

        cancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPopup(mData.get(position).getInvId(), position);
            }
        });

        return view;
    }


    /**
     * Used to confirm logout...
     */
    public void confirmPopup(final String reqInvId, final int position) {
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
        messageTxt.setText(R.string.msg_decline_request);

        positiveTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineRequest(reqInvId, position);
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
     * Used to declient friend request...
     * @param invId
     * @param position
     */
    private void declineRequest(String invId, final int position) {
        HashMap<String, String> user = sessionManager.getUserDetails();
        String userId = user.get(SessionManager.USER_ID);
        String phone = user.get(SessionManager.PHONE);

        try{
            ReqResAPICallBacks reqResAPICallBacks = new ReqResAPICallBacks();
            reqResAPICallBacks.declineRequest(fragment.getActivity(), new ReqRespCallBack() {
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
            }, userId, phone, invId, true, true);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
