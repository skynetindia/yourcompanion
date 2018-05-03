package pkg.android.skynet.yourcompanion.adapter;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.fragment.AddNewFriendFragment;
import pkg.android.skynet.yourcompanion.models.ContactData;
import pkg.android.skynet.yourcompanion.models.FollowMeData;

public class FollowingMeAdapter extends RecyclerView.Adapter<FollowingMeAdapter.MyViewHolder> {

    private Fragment fragment;
    private List<FollowMeData> datas;

    private boolean[] checkedStates;

    class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userImg;
        TextView friendNameTxt;
        ImageView activeInactiveImg;

        MyViewHolder(final View view) {
            super(view);

            userImg = (CircleImageView)view.findViewById(R.id.img_user);
            friendNameTxt = (TextView)view.findViewById(R.id.txt_friend_name);
            activeInactiveImg = (ImageView)view.findViewById(R.id.img_active_inactive);
        }
    }


    public FollowingMeAdapter(Fragment fragment, List<FollowMeData> datas) {
        this.fragment = fragment;
        this.datas = datas;

        checkedStates = new boolean[datas.size()];
    }
 
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_following, parent, false);
 
        return new MyViewHolder(itemView);
    }
 
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final FollowMeData followMeData = datas.get(position);

        if (followMeData.getUserImg() != null && !followMeData.getUserImg().equals("")) {
            Picasso.with(fragment.getActivity())
                    .load(followMeData.getUserImg())
                    .placeholder(R.drawable.ic_profile_male)
                    .error(R.drawable.ic_profile_male)
                    .into(holder.userImg);
        }

        holder.friendNameTxt.setText(followMeData.getFname());

        if (followMeData.getStatus().equals("0")) {
            holder.activeInactiveImg.setImageResource(R.drawable.ic_inactive);
        }else {
            holder.activeInactiveImg.setImageResource(R.drawable.ic_active);
        }
    }
 
    @Override
    public int getItemCount() {
        return datas.size();
    }

}