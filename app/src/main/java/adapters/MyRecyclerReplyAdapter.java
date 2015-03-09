package adapters;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import huka.com.repli.R;
import huka.com.repli.ReplyInfo;

public class MyRecyclerReplyAdapter extends RecyclerView.Adapter<MyRecyclerReplyAdapter.ViewHolder> {

    private ArrayList<ReplyInfo> mDataSet;
    private OnItemClickListener mItemClickListener;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private final ImageView thumbnailView;
        private final CircleImageView profilePictureView;
        private final ImageView inactiveIcon;
        private final TextView usernameText;
        private final TextView dateText;

        public ViewHolder(View v) {
            super(v);
            usernameText = (TextView) v.findViewById(R.id.usernameText);
            dateText = (TextView) v.findViewById(R.id.dateText);
            thumbnailView = (ImageView) v.findViewById(R.id.thumbnailView);
            profilePictureView = (CircleImageView) v.findViewById(R.id.profilePicture);
            inactiveIcon = (ImageView) v.findViewById(R.id.inactiveIcon);
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }

        public TextView getUsernameText() {
            return usernameText;
        }
        public TextView getDateTxt() {
            return dateText;
        }
        public ImageView getThumbnailView() {
            return thumbnailView;
        }
        public CircleImageView getProfilePictureView() {
            return profilePictureView;
        }
        public ImageView getInactiveIcon() {
            return inactiveIcon;
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mItemClickListener != null) {
                return mItemClickListener.onItemLongClicked(getPosition());
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view , int position);
        public boolean onItemLongClicked(int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public MyRecyclerReplyAdapter(ArrayList<ReplyInfo> dataSet) {
        super();
        mDataSet = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.reply_row_item, viewGroup, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        ReplyInfo replyInfo = mDataSet.get(position);

        viewHolder.getUsernameText().setText(replyInfo.getUsername());
        viewHolder.getDateTxt().setText(replyInfo.getDate());
        viewHolder.getThumbnailView().setBackground(new BitmapDrawable(replyInfo.getThumbnail()));
        viewHolder.getProfilePictureView().setImageBitmap(replyInfo.getProfilePicture());

        if(!replyInfo.isReplied()) {
            makeBlackAndWhite(replyInfo.getThumbnail());
            viewHolder.getInactiveIcon().setVisibility(View.VISIBLE);
        }
    }

    private Bitmap makeBlackAndWhite(Bitmap bitmap) {
        int width, height;
        height = bitmap.getHeight();
        width = bitmap.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bitmap, 0, 0, paint);
        return bmpGrayscale;


    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void removeItem(int position) {
        mDataSet.remove(position);
        notifyItemRemoved(position);
    }

    public void setDataSet(ArrayList<ReplyInfo> newDataSet){
        mDataSet = newDataSet;
        notifyDataSetChanged();
    }
}

