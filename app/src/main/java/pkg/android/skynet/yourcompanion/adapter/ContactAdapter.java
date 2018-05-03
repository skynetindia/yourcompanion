package pkg.android.skynet.yourcompanion.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import libs.indexablelistview.StringMatcher;
import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.app.YourCompanion;
import pkg.android.skynet.yourcompanion.fragment.AddNewFriendFragment;
import pkg.android.skynet.yourcompanion.models.ContactData;

public class ContactAdapter extends BaseAdapter implements SectionIndexer {

    private LayoutInflater inflater;
    private List<ContactData> datas;
    private Fragment fragment;
    private List<ContactData> mAllData;
    private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private YourCompanion mAppContext;

    public ContactAdapter(Fragment fragment, List<ContactData> datas) {
        inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.fragment = fragment;

        this.mAllData = datas;

        this.datas = new ArrayList<>();
        this.datas.addAll(mAllData);

        mAppContext = (YourCompanion)fragment.getActivity().getApplication();
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


        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ContactHolder holder = new ContactHolder();

            if (convertView == null) {

                convertView = inflater.inflate(R.layout.row_contacts, parent, false);

                holder.img_contact_pic = (CircleImageView) convertView.findViewById(R.id.img_contact);
                holder.txt_name = (TextView) convertView.findViewById(R.id.txt_contact_name);
                holder.txt_number = (TextView) convertView.findViewById(R.id.txt_contact_number);
                holder.contactChk = (CheckBox)convertView.findViewById(R.id.chk_contacts);

                convertView.setTag(holder);

            } else {
                holder = (ContactHolder) convertView.getTag();
            }

            holder.txt_name.setText(datas.get(position).getName());
            holder.txt_number.setText(datas.get(position).getNumber());

            if (datas.get(position).getPhoto().equals("")) {
                holder.img_contact_pic.setImageResource(R.drawable.ic_profile_male);
            } else {
                Picasso.with(fragment.getActivity())
                        .load(datas.get(position).getPhoto())
                        .error(R.drawable.ic_profile_male)
                        .placeholder(R.drawable.ic_profile_male)
                        .into(holder.img_contact_pic);
            }

            holder.contactChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    datas.get(position).setChecked(true);

                    System.out.println("______ " + mAppContext.getCountryPhoneCode());

                    if (isChecked) {
                        if (datas.get(position).getNumber().contains("+"+mAppContext.getCountryPhoneCode()) || mAppContext.getCountryPhoneCode().equals("")) {
                            ((AddNewFriendFragment) fragment).mSelectedContacts.add(datas.get(position).getNumber());

                        }else {
                            if (!mAppContext.getCountryPhoneCode().equals(""))
                                ((AddNewFriendFragment) fragment).mSelectedContacts.add("+"+mAppContext.getCountryPhoneCode() + datas.get(position).getNumber());
                        }
                    }else {
                        ((AddNewFriendFragment)fragment).mSelectedContacts.remove(datas.get(position).getNumber());
                    }

                    System.out.println("SELECTED CONTACTS : " + ((AddNewFriendFragment)fragment).mSelectedContacts);
                }
            });

            holder.contactChk.setChecked(datas.get(position).isChecked());

            final ContactHolder finalHolder = holder;
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (finalHolder.contactChk.isChecked())
                        finalHolder.contactChk.setChecked(false);
                    else   finalHolder.contactChk.setChecked(true);
                }
            });

            return convertView;
        }

        @Override
        public Object[] getSections() {
            String[] sections = new String[mSections.length()];
            for (int i = 0; i < mSections.length(); i++)
                sections[i] = String.valueOf(mSections.charAt(i));
            return sections;
        }

        @Override
        public int getPositionForSection(int section) {
            for (int i = section; i >= 0; i--) {
                for (int j = 0; j < getCount(); j++) {
                    if (i == 0) {
                        // For numeric section
                        for (int k = 0; k <= 9; k++) {
                            if (StringMatcher.match(String.valueOf(datas.get(j).getName().charAt(0)), String.valueOf(k)))
                                return j;
                        }
                    } else {
                        if (StringMatcher.match(String.valueOf(datas.get(j).getName().charAt(0)), String.valueOf(mSections.charAt(i))))
                            return j;
                    }
                }
            }
            return 0;
        }

        @Override
        public int getSectionForPosition(int i) {
            return 0;
        }

        class ContactHolder {
            CircleImageView img_contact_pic;
            TextView txt_name, txt_number;
            CheckBox contactChk;
        }
    }