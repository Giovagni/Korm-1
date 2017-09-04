package univpm.korm.Connessione;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import java.util.List;
import univpm.korm.TabPunti;
import univpm.korm.R;




public class Mappa  extends ImageView{

    public Mappa(Context context, AttributeSet attrs) {
        super(context, attrs);

    }


    public void init(Toolbar toolbar,int x,int y,int quota){
        Bitmap bMap=null;
        if(quota==145)
            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.q145_misure);
        if(quota==150)
            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.q150_misure);
        if(quota==155)
            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.q155_misure);
        Bitmap mutableBitmap = bMap.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap bMap1 = BitmapFactory.decodeResource(getResources(),R.drawable.pin);
        Bitmap mutableBitmap1 = bMap1.copy(Bitmap.Config.ARGB_8888, true);
        mutableBitmap=overlay(mutableBitmap,mutableBitmap1,x,y,quota);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        params.addRule(RelativeLayout.BELOW, toolbar.getId());
        int dpValue = 43; // margin in dips
        float d = this.getResources().getDisplayMetrics().density;
        int margin = (int)(dpValue * d); // margin in pixels
        params.setMargins(0,margin,0,0);
        this.setLayoutParams(params);
        this.setImageBitmap(mutableBitmap);

    }

    public void init(Toolbar toolbar, int x, int y, int quota, List<TabPunti> pericolo){
        Bitmap bMap=null;
        if(quota==145)
            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.q145_misure);
        if(quota==150)
            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.q150_misure);
        if(quota==155)
            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.q155_misure);
        Bitmap mutableBitmap = bMap.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap bMap1 = BitmapFactory.decodeResource(getResources(),R.drawable.pin);
        Bitmap mutableBitmap1 = bMap1.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap bMap3 = BitmapFactory.decodeResource(getResources(),R.drawable.fire);
        Bitmap mutableBitmap3 = bMap3.copy(Bitmap.Config.ARGB_8888, true);
        mutableBitmap=overlay(mutableBitmap,mutableBitmap1,x,y,quota,pericolo,mutableBitmap3);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        params.addRule(RelativeLayout.BELOW, toolbar.getId());
        int dpValue = 43; // margin in dips
        float d = this.getResources().getDisplayMetrics().density;
        int margin = (int)(dpValue * d); // margin in pixels
        params.setMargins(0,margin,0,0);
        this.setLayoutParams(params);
        this.setImageBitmap(mutableBitmap);

    }

    private Bitmap overlay(Bitmap bmp1, Bitmap bmp2,int x, int y,int quota) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        Rect rect=null;
        if(quota==145)
            rect = new Rect((int) ((y - 419.125) * 8 - 55), (int) ((x - 42.375) * 7.8889 + 40), (int) ((y - 419.125) * 8 - 5), (int) ((x - 42.375) * 7.8889 + 100));
        if(quota==150)
            rect = new Rect((int) ((y - 419.125) * 8 - 35), (int) ((x - 42.375) * 7.8889 -5), (int) ((y - 419.125) * 8 + 15), (int) ((x - 42.375) * 7.8889 + 55));
        if(quota==155)
            rect = new Rect((int) ((y - 419.125) * 8 - 25), (int) ((x - 42.375) * 7.8889 - 30), (int) ((y - 419.125) * 8 + 25), (int) ((x - 42.375) * 7.8889 + 35));
        canvas.drawBitmap(bmp2,null,rect,null);
        return bmOverlay;
    }

    private Bitmap overlay(Bitmap bmp1, Bitmap bmp2,int x, int y,int quota,List<TabPunti> pericolo,Bitmap bmp3) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        Rect rect = null;
        if (quota == 145) {
            rect = new Rect((int) ((y - 419.125) * 8 - 55), (int) ((x - 42.375) * 7.8889 + 40), (int) ((y - 419.125) * 8 - 5), (int) ((x - 42.375) * 7.8889 + 100));
            TabPunti punti;
            for(int i=0;i<pericolo.size();i++){
                punti=pericolo.get(i);

                int y1= Integer.parseInt(punti.y);
                int x1= Integer.parseInt(punti.x);
                Rect rect1= new Rect((int) ((y1 - 419.125) * 8 - 65), (int) ((x1 - 42.375) * 7.8889 + 25), (int) ((y1 - 419.125) * 8 +5), (int) ((x1 - 42.375) * 7.8889 + 110));
                canvas.drawBitmap(bmp3,null,rect1,null);
            }
        }
        if (quota == 150){
            rect = new Rect((int) ((y - 419.125) * 8 - 35), (int) ((x - 42.375) * 7.8889 -5), (int) ((y - 419.125) * 8 + 15), (int) ((x - 42.375) * 7.8889 + 55));
            TabPunti punti;
            for(int i=0;i<pericolo.size();i++){
                punti=pericolo.get(i);
                int y1= Integer.parseInt(punti.y);
                int x1= Integer.parseInt(punti.x);
                Rect rect1= new Rect((int) ((y1 - 419.125) * 8 - 45), (int) ((x1 - 42.375) * 7.8889 -30), (int) ((y1 - 419.125) * 8 + 25), (int) ((x1 - 42.375) * 7.8889 + 65));
                canvas.drawBitmap(bmp3,null,rect1,null);
            }
        }
        if(quota==155) {
            rect = new Rect((int) ((y - 419.125) * 8 - 25), (int) ((x - 42.375) * 7.8889 - 30), (int) ((y - 419.125) * 8 + 25), (int) ((x - 42.375) * 7.8889 + 35));
            TabPunti punti;
            for(int i=0;i<pericolo.size();i++){
                punti=pericolo.get(i);
                int y1= Integer.parseInt(punti.y);
                int x1= Integer.parseInt(punti.x);
                Rect rect1 = new Rect((int) ((y1 - 419.125) * 8 - 35), (int) ((x1 - 42.375) * 7.8889 - 45), (int) ((y1 - 419.125) * 8 + 35), (int) ((x1 - 42.375) * 7.8889 + 45));
                canvas.drawBitmap(bmp3,null,rect1,null);
            }
        }
        canvas.drawBitmap(bmp2,null,rect,null);
        return bmOverlay;
    }


}
