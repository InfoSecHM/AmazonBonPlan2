package abp.amazonbonplan.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import abp.amazonbonplan.Loader.PicassoClient;
import abp.amazonbonplan.R;
import abp.amazonbonplan.models.amazon_data;

/**
 * Created by Admin on 28/09/2017.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> implements Filterable {

    public Context mContext;
    public List<amazon_data> original_items = new ArrayList<>();
    public List<amazon_data> filtered_items = new ArrayList<>();
    private final int VIEW_ITEM_HEADER = 1;  // type: name/value
    private final int VIEW_ITEM_FOOTER = 0;
      public ItemFilter mFilters = new ItemFilter();


    public PostAdapter(Context mContext, List<amazon_data> postList) {
        this.mContext = mContext;
        this.original_items = postList;
        this.filtered_items = postList;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_amazon, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        try {

            final amazon_data post = filtered_items.get(position);
            // holder.frameLayout.setBackgroundColor((mContext.getResources().getColor(R.color.newgreen)));
            String[] separated = post.getItem_title().split(":");
            String real_title=separated[1];

            holder.txt_title.setText(real_title);
            holder.txt_date.setText(post.getItem_date());
            PicassoClient.downloadImage(mContext,post.getItem_image(),holder.imageview);
            holder.bt_deal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Uri uri = Uri.parse(post.getItem_url()); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        mContext.startActivity(intent);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            });
            final int min = 1;
            final int max = 150;
            Random r = new Random();
            String i1=String.valueOf(r.nextInt(150 - 1) + 40);
            holder.txt_overimage.setText(i1+"°");
            holder.txt_price.setText(post.getItem_price()+"€");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return filtered_items.size();
    }

    @Override
    public Filter getFilter() {
        return mFilters;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemClickListener itemClickListener;
        private TextView txt_title,txt_date,txt_overimage,txt_price;
        private ImageView imageview;
        private Button bt_deal;

        public ViewHolder(View itemView) {
            super(itemView);

            txt_title = (TextView) itemView.findViewById(R.id.txt_amazon_title);
            txt_date = (TextView) itemView.findViewById(R.id.txt_datetimevideo);
            imageview = (ImageView) itemView.findViewById(R.id.imageview_video);
            bt_deal = (Button) itemView.findViewById(R.id.bt_deal);
            txt_overimage=(TextView)itemView.findViewById(R.id.txt_overimage);
            txt_price=(TextView)itemView.findViewById(R.id.txt_price);

            // frameLayout=(FrameLayout)itemView.findViewById(R.id.frame_color);
            itemView.setOnClickListener(this);
        }



        @Override
        public void onClick(View v) {
            try {
                this.itemClickListener.onItemClick();

            }catch (Exception e)
            {
                e.printStackTrace();
            }


        }
        public void setItemClickListener(ItemClickListener itemClickListener)
        {
            this.itemClickListener=itemClickListener;
        }

    }
    private class  ItemFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            String query=charSequence.toString().toLowerCase();
            FilterResults results=new FilterResults();
            final List<amazon_data> list=original_items;
            final List<amazon_data> result_list= new ArrayList<>(list.size());
            for(int i=0 ; i<list.size() ; i++)
            {
                String str_title=list.get(i).getItem_title();
                if(str_title.toLowerCase().contains(query))
                {
                    result_list.add(list.get(i));
                }
            }
            results.values=result_list;
            results.count=result_list.size();
            return results;
        }



        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            filtered_items=(List<amazon_data>) filterResults.values;
            notifyDataSetChanged();

        }
    }
}



