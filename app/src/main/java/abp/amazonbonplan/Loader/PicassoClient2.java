package abp.amazonbonplan.Loader;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import abp.amazonbonplan.R;

/**
 * Created by Tiger on 7/11/2017.
 */


public class PicassoClient2 {

    public static void downloadImage(Context c, String imageUrl, ImageView img)
    {
        if(imageUrl!=null && imageUrl.length()>0)

        {
            Picasso.with(c).load(imageUrl).placeholder(R.drawable.back_video).into(img);

        }else
        {
            Picasso.with(c).load(R.drawable.back_video).into(img);
        }

    }
}
