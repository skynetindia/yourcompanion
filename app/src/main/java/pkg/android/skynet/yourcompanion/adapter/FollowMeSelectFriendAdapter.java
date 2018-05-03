package pkg.android.skynet.yourcompanion.adapter;

import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.app.SessionManager;
import pkg.android.skynet.yourcompanion.dialogfragment.FollowMeDialog;
import pkg.android.skynet.yourcompanion.models.FriendsData;

public class FollowMeSelectFriendAdapter extends RecyclerView.Adapter<FollowMeSelectFriendAdapter.MyViewHolder> {

    private DialogFragment fragment;
    private List<FriendsData> datas;

    private boolean[] checkedStates;

    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;

    class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView contactImg;
        TextView contactNameTxt, contactNumberTxt;
        CheckBox contactChk;

        MyViewHolder(final View view) {
            super(view);
            contactImg = (CircleImageView) view.findViewById(R.id.img_contact);

            contactNameTxt = (TextView)view.findViewById(R.id.txt_contact_name);
            contactNumberTxt = (TextView)view.findViewById(R.id.txt_contact_number);

            contactChk = (CheckBox)view.findViewById(R.id.chk_contacts);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (contactChk.isChecked())
                        contactChk.setChecked(false);
                    else contactChk.setChecked(true);
                }
            });
        }
    }


    public FollowMeSelectFriendAdapter(DialogFragment fragment, List<FriendsData> datas) {
        this.fragment = fragment;
        this.datas = datas;

        checkedStates = new boolean[datas.size()];
        sessionManager = new SessionManager(fragment.getActivity());
        userDetails = sessionManager.getUserDetails();
    }
 
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_friends, parent, false);
 
        return new MyViewHolder(itemView);
    }
 
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final FriendsData friendsData = datas.get(position);

        holder.contactNameTxt.setText(friendsData.getFirstName());
        holder.contactNumberTxt.setText(friendsData.getPhone());

        holder.contactChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                datas.get(position).setChecked(true);

                String userId = "";

                if (userDetails.get(SessionManager.USER_ID).equals(friendsData.getUserId()))
                    userId = friendsData.getFriendId();
                else userId = friendsData.getUserId();

                if (isChecked) {
                    ((FollowMeDialog)fragment).mSelectedContacts.add(userId);
                }else {
                    ((FollowMeDialog)fragment).mSelectedContacts.remove(userId);
                }

                System.out.println("SELECTED CONTACTS : " + ((FollowMeDialog)fragment).mSelectedContacts);
            }
        });

        holder.contactChk.setChecked(datas.get(position).isChecked());
    }
 
    @Override
    public int getItemCount() {
        return datas.size();
    }

}