package pkg.android.skynet.yourcompanion.adapter;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.fragment.AddNewFriendFragment;
import pkg.android.skynet.yourcompanion.models.ContactData;
import pkg.android.skynet.yourcompanion.models.NotificationData;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder> {

    private Fragment fragment;
    private List<ContactData> datas;
    private List<ContactData> mAllData;

    private boolean[] checkedStates;

    class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView contactImg;
        TextView contactNameTxt, contactNumberTxt;
        CheckBox contactChk;
        RelativeLayout rootLayout;

        MyViewHolder(final View view) {
            super(view);
            contactImg = (CircleImageView) view.findViewById(R.id.img_contact);

            contactNameTxt = (TextView)view.findViewById(R.id.txt_contact_name);
            contactNumberTxt = (TextView)view.findViewById(R.id.txt_contact_number);

            contactChk = (CheckBox)view.findViewById(R.id.chk_contacts);

            rootLayout = (RelativeLayout)view.findViewById(R.id.rl_root);

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


    /**
     * Used to filter the list as per given text..
     * @param charText
     */
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());

        datas.clear();
        if (charText.length() == 0) {
            datas.addAll(mAllData);
        } else {
            System.out.println("---> " + mAllData.size());
            for(ContactData data : mAllData) {
                if (data.getName().toLowerCase(Locale.getDefault()).contains(charText) || data.getNumber().toLowerCase(Locale.getDefault()).contains(charText)) {
                    datas.add(data);
                }
            }
        }
        notifyDataSetChanged();
    }


    public ContactListAdapter(Fragment fragment, List<ContactData> datas) {
        this.fragment = fragment;
        this.mAllData = datas;

        this.datas = new ArrayList<>();
        this.datas.addAll(mAllData);

        checkedStates = new boolean[datas.size()];
    }
 
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_contacts, parent, false);
 
        return new MyViewHolder(itemView);
    }
 
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ContactData contactData = datas.get(position);

        holder.contactNameTxt.setText(contactData.getName());
        holder.contactNumberTxt.setText(contactData.getNumber());

        holder.contactChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                datas.get(position).setChecked(true);

                if (isChecked) {
                    ((AddNewFriendFragment)fragment).mSelectedContacts.add(contactData.getNumber());
                }else {
                    ((AddNewFriendFragment)fragment).mSelectedContacts.remove(contactData.getNumber());
                }

                System.out.println("SELECTED CONTACTS : " + ((AddNewFriendFragment)fragment).mSelectedContacts);
            }
        });

        holder.contactChk.setChecked(datas.get(position).isChecked());

        /*holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.contactChk.isChecked())
                    holder.contactChk.setChecked(false);
                else holder.contactChk.setChecked(true);
            }
        });*/
    }
 
    @Override
    public int getItemCount() {
        return datas.size();
    }

}