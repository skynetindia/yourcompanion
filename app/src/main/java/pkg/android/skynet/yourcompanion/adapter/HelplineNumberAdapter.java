package pkg.android.skynet.yourcompanion.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.app.SessionManager;
import pkg.android.skynet.yourcompanion.app.YourCompanion;
import pkg.android.skynet.yourcompanion.models.HelplineNumbersData;

public class HelplineNumberAdapter extends RecyclerView.Adapter<HelplineNumberAdapter.MyViewHolder> {

    private Fragment fragment;
    private List<HelplineNumbersData> datas;

    private YourCompanion yourCompanion;
    private SessionManager sessionManager;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView helplineNumberTxt, helplineNameTxt;

        MyViewHolder(final View view) {
            super(view);
            helplineNumberTxt = (TextView)view.findViewById(R.id.txt_helpline_number);
            helplineNameTxt = (TextView)view.findViewById(R.id.txt_helpline_name);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+datas.get(getAdapterPosition()).getNumber()));
                    fragment.startActivity(callIntent);
                }
            });
        }
    }


    public HelplineNumberAdapter(Fragment fragment, List<HelplineNumbersData> datas) {
        this.fragment = fragment;
        this.datas = datas;

        yourCompanion = (YourCompanion)fragment.getActivity().getApplication();
        sessionManager = new SessionManager(fragment.getActivity());
    }
 
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_helpline, parent, false);
 
        return new MyViewHolder(itemView);
    }
 
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final HelplineNumbersData helplineNumbersData = datas.get(position);

        holder.helplineNumberTxt.setText(helplineNumbersData.getNumber());
        holder.helplineNameTxt.setText(helplineNumbersData.getName());
    }
 
    @Override
    public int getItemCount() {
        return datas.size();
    }


}