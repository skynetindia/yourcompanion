package pkg.android.skynet.yourcompanion.adapter;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.app.Constants;
import pkg.android.skynet.yourcompanion.app.SessionManager;
import pkg.android.skynet.yourcompanion.app.YourCompanion;
import pkg.android.skynet.yourcompanion.models.FriendsData;

public class MyFriendsAdapter extends RecyclerView.Adapter<MyFriendsAdapter.MyViewHolder> {

    private Fragment fragment;
    private List<FriendsData> datas;

    private YourCompanion yourCompanion;
    private SessionManager sessionManager;

    class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView friendsImg;
        TextView friendNameTxt, numberTxt;
        ImageView trashImg;

        MyViewHolder(final View view) {
            super(view);
            friendsImg = (CircleImageView) view.findViewById(R.id.img_friends);

            friendNameTxt = (TextView)view.findViewById(R.id.txt_friends_name);
            numberTxt = (TextView)view.findViewById(R.id.txt_number);
            trashImg = (ImageView)view.findViewById(R.id.img_trash);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }


    public MyFriendsAdapter(Fragment fragment, List<FriendsData> datas) {
        this.fragment = fragment;
        this.datas = datas;

        yourCompanion = (YourCompanion)fragment.getActivity().getApplication();
        sessionManager = new SessionManager(fragment.getActivity());
    }
 
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_friends_list, parent, false);
 
        return new MyViewHolder(itemView);
    }
 
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final FriendsData friendsData = datas.get(position);

        holder.friendNameTxt.setText(friendsData.getFirstName() + " " + friendsData.getLastName());
        holder.numberTxt.setText(friendsData.getPhone());

        Picasso.with(fragment.getActivity())
                .load(friendsData.getUserImg())
                .placeholder(R.drawable.ic_profile_male)
                .error(R.drawable.ic_profile_male)
                .into(holder.friendsImg);

        holder.trashImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete(friendsData.getId(), position);
            }
        });

    }
 
    @Override
    public int getItemCount() {
        return datas.size();
    }


    /**
     * Used to confirm logout...
     */
    public void confirmDelete(final String id, int position) {
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
        positiveTxt.setText(fragment.getString(R.string.txt_i_am_sure));
        messageTxt.setText(R.string.msg_remove_friend);

        positiveTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        negativeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }



    private void removeFriends(String id, int position) {
        HashMap<String, String> user = sessionManager.getUserDetails();
        String userId = user.get(SessionManager.USER_ID);

        try{

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}