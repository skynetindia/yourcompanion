package pkg.android.skynet.yourcompanion.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.HashMap;
import java.util.List;

import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.app.SessionManager;
import pkg.android.skynet.yourcompanion.app.YourCompanion;
import pkg.android.skynet.yourcompanion.models.HelplineNumbersData;
import pkg.android.skynet.yourcompanion.models.HistoryData;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private Fragment fragment;
    private List<HistoryData> datas;

    private YourCompanion yourCompanion;
    private SessionManager sessionManager;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mVideoHeadingTxt, mVideoDetailTxt;
        ImageView mPreviewVdo;

        MyViewHolder(final View view) {
            super(view);
            mVideoHeadingTxt = (TextView)view.findViewById(R.id.txt_video_heading);
            mVideoDetailTxt = (TextView)view.findViewById(R.id.txt_video_detail);

            mPreviewVdo = (ImageView)view.findViewById(R.id.vdo_preview);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent playVideo = new Intent(Intent.ACTION_VIEW);
                    playVideo.setDataAndType(Uri.parse(datas.get(getAdapterPosition()).getVideo()), "video/mp4");
                    fragment.startActivity(playVideo);

                }
            });
        }
    }


    public HistoryAdapter(Fragment fragment, List<HistoryData> datas) {
        this.fragment = fragment;
        this.datas = datas;

        yourCompanion = (YourCompanion)fragment.getActivity().getApplication();
        sessionManager = new SessionManager(fragment.getActivity());
    }
 
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_history, parent, false);
 
        return new MyViewHolder(itemView);
    }
 
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final HistoryData historyData = datas.get(position);

        holder.mVideoHeadingTxt.setText(historyData.getFollowName());
        holder.mVideoDetailTxt.setText(historyData.getDate());

    }
 
    @Override
    public int getItemCount() {
        return datas.size();
    }


}