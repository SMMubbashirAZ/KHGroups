package com.blazeminds.pos;

/*
  Created by Saad Kalim on 06-Apr-15.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import static com.blazeminds.pos.Constant.FIRST_COLUMN;
import static com.blazeminds.pos.Constant.FOURTH_COLUMN;
import static com.blazeminds.pos.Constant.SECOND_COLUMN;
import static com.blazeminds.pos.Constant.THIRD_COLUMN;

//import static com.blazeminds.pos.Constant.CODE_PICTXT;


public class ListViewAdapter extends BaseAdapter {
    public ArrayList<HashMap<String, String>> list;
    public ArrayList<String> list1;
    Activity activity;
    
    
    public ListViewAdapter(Activity activity, ArrayList<HashMap<String, String>>/*ArrayList<String>*/ list) {
        super();
        this.activity = activity;
        
        
        this.list = list;
    }
    
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }
    
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

/*
    TextView txtCode;
    TextView txtFirst;
    TextView txtSecond;
    TextView txtThird;
    TextView txtFourth;
    TextView txtFifth;
*/
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        
        // TODO Auto-generated method stub
        ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            //  holder.txtCode = (TextView) convertView.findViewById(R.id.Code);
            //  holder.txtPic = (ImageView) convertView.findViewById(R.id.Pic);
            holder.txtFirst = convertView.findViewById(R.id.DBInventNameValues);
            holder.txtSecond = convertView.findViewById(R.id.DBQtyValues);
            holder.txtThird = convertView.findViewById(R.id.DBPriceValues);
            holder.txtFourth = convertView.findViewById(R.id.DBBrandNameValues);

/*
            txtCode = (TextView) convertView.findViewById(R.id.Code);
            txtFirst = (TextView) convertView.findViewById(R.id.DBInventNameValues);
            txtSecond = (TextView) convertView.findViewById(R.id.DBQtyValues);
            txtThird = (TextView) convertView.findViewById(R.id.DBPriceValues);
            txtFourth = (TextView) convertView.findViewById(R.id.DBBrandNameValues);
            txtFifth = (TextView) convertView.findViewById(R.id.DBTypeNameValues);
*/
            
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        HashMap map = list.get(position);
        
        
        try {
            
            
            //  if( !(map.get(CODE_PICTXT).toString().equalsIgnoreCase("0.jpg")) ){




/*
                Log.d("ListAdapter : ",map.get(CODE_PICTXT).toString());

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString()+"/bms_pos_images/"+map.get(CODE_PICTXT).toString(), options);
*/
            
            //        holder.txtCode.setText(map.get(CODE_COLUMN).toString());
            
            //    holder.txtPic.setImageBitmap((Bitmap.createScaledBitmap(bitmap, 100, 100, false)));
            
            
            holder.txtFirst.setText(map.get(FIRST_COLUMN).toString());
            holder.txtSecond.setText(map.get(SECOND_COLUMN).toString());
            holder.txtThird.setText(map.get(THIRD_COLUMN).toString());
            holder.txtFourth.setText(map.get(FOURTH_COLUMN).toString());
            //       }
            //  else{

//               ImgName = "0.jpg";

                /*    BitmapFactory.Options options1 = new BitmapFactory.Options();
                    options1.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap1 = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString()+"/bms_pos_images/"+map.get(CODE_PICTXT).toString(), options1);

                    holder.txtCode.setText(map.get(CODE_COLUMN).toString());

                    holder.txtPic.setImageBitmap((Bitmap.createScaledBitmap(bitmap1, 100, 100, false)) );

                    holder.txtFirst.setText(map.get(FIRST_COLUMN).toString());
                    holder.txtSecond.setText(map.get(SECOND_COLUMN).toString());
                    holder.txtThird.setText(map.get(THIRD_COLUMN).toString());
                    holder.txtFourth.setText(map.get(FOURTH_COLUMN).toString());

*/
            
            
            //    }
            
        } catch (Exception e) {
            System.out.println("Error IMG: " + e.toString());
        }
        
        
        return convertView;
    }
    
    private class ViewHolder {
        //TextView txtCode;
        //ImageView txtPic;
        TextView txtFirst;
        TextView txtSecond;
        TextView txtThird;
        TextView txtFourth;
        
    }
    
}