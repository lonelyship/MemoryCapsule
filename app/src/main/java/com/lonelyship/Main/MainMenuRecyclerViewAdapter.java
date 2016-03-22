package com.lonelyship.Main;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lonelyship.CustomViews.ThreeBarView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 側拉式選單的Adapter
 */
public class MainMenuRecyclerViewAdapter extends RecyclerView.Adapter<MainMenuRecyclerViewAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    // IF the view under inflation and population is header or Item
    private static final int TYPE_ITEM = 1;

    private String mNavTitles[]; // String Array to store the passed titles Value from MainActivity.java
    private int mIcons[];       // Int Array to store the passed icons resource value from MainActivity.java

    private String name;        //String Resource for header View Name
    //private int profile;        //int Resource for header view profile picture
    private Bitmap profile;        //bitmap Resource for header view profile picture
    private String email;       //String Resource for header view email 

    private double m_dFreeSpaceWeight;
    private double m_dUsedSpaceWeight;

    private String m_sFreeSpaceText;
    private String m_sUsedSpaceText;

    private Context m_context;

    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them

    public static class ViewHolder extends RecyclerView.ViewHolder {
        int Holderid;

        TextView textView;
        ImageView imageView;
        CircleImageView profile;
        TextView Name;
        TextView email;
        ThreeBarView threeBarView;


        public ViewHolder(Context context, View itemView,int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);

            // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created

            if(ViewType == TYPE_ITEM) {
                textView = (TextView) itemView.findViewById(R.id.main_menu_row_item_text); // Creating TextView object with the id of textView from menu_item_rowm_row.xml
                imageView = (ImageView) itemView.findViewById(R.id.main_menu_row_item_Icon);// Creating ImageView object with the id of ImageView from menu_item_row.xmlw.xml
                Holderid = 1;                                               // setting holder id as 1 as the object being populated are of type item row
            }
            else{

                Name = (TextView) itemView.findViewById(R.id.menu_header_name);         // Creating Text View object from header.xml for name
                email = (TextView) itemView.findViewById(R.id.menu_header_email);       // Creating Text View object from header.xml for email
                profile = (CircleImageView) itemView.findViewById(R.id.menu_header_circle_image_view);// Creating Image view object from header.xml for profile pic
                threeBarView = (ThreeBarView) itemView.findViewById(R.id.menu_header_three_bar_view);
                Holderid = 0;                                                // Setting holder id = 0 as the object being populated are of type header view
            }
        }
    }



//    MainMenuRecyclerViewAdapter(Context context, String Titles[], int Icons[], String Name, String Email, int Profile,
//                                double dFree, double dUsed, String sFree, String sUsed){ // MyAdapter Constructor with titles and icons parameter
//        // titles, icons, name, email, profile pic are passed from the main activity as we
//        m_context = context;
//        mNavTitles = Titles;                //have seen earlier
//        mIcons = Icons;
//        name = Name;
//        email = Email;
//        profile = Profile;                     //here we assign those passed values to the values we declared here
//        this.m_dFreeSpaceWeight = dFree;
//        this.m_dUsedSpaceWeight = dUsed;
//        this.m_sFreeSpaceText = sFree;
//        this.m_sUsedSpaceText = sUsed;
//        //in adapter
//    }

    MainMenuRecyclerViewAdapter(Context context, String Titles[], int Icons[], String Name, String Email, Bitmap Profile,
                                double dFree, double dUsed, String sFree, String sUsed){ // MyAdapter Constructor with titles and icons parameter
        // titles, icons, name, email, profile pic are passed from the main activity as we
        m_context = context;
        mNavTitles = Titles;                //have seen earlier
        mIcons = Icons;
        name = Name;
        email = Email;
        profile = Profile;                     //here we assign those passed values to the values we declared here
        this.m_dFreeSpaceWeight = dFree;
        this.m_dUsedSpaceWeight = dUsed;
        this.m_sFreeSpaceText = sFree;
        this.m_sUsedSpaceText = sUsed;
        //in adapter

    }



    //Below first we ovverride the method onCreateViewHolder which is called when the ViewHolder is
    //Created, In this method we inflate the menu_item_row.xmlw.xml layout if the viewType is Type_ITEM or else we inflate header.xml
    // if the viewType is TYPE_HEADER
    // and pass it to the view holder

    @Override
    public MainMenuRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item_row,parent,false); //Inflating the layout
            ViewHolder vhItem = new ViewHolder(m_context,v,viewType); //Creating ViewHolder and passing the object of type view

            return vhItem; // Returning the created object

            //inflate your layout and pass it to view holder

        } else if (viewType == TYPE_HEADER) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_header_view,parent,false); //Inflating the layout

            ViewHolder vhHeader = new ViewHolder(m_context,v,viewType); //Creating ViewHolder and passing the object of type view

            return vhHeader; //returning the object created

        }

        return null;

    }

    //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
    // which view type is being created 1 for item row
    @Override
    public void onBindViewHolder(MainMenuRecyclerViewAdapter.ViewHolder holder, int position) {
        if(holder.Holderid ==1) {                              // as the list view is going to be called after the header view so we decrement the
            // position by 1 and pass it to the holder while setting the text and image
            holder.textView.setText(mNavTitles[position - 1]); // Setting the Text with the array of our Titles
            holder.imageView.setImageResource(mIcons[position -1]);// Settimg the image with array of our icons
        }
        else{

            //holder.profile.setImageResource(profile);           // Similarly we set the resources for header view
            holder.profile.setImageBitmap(profile);           // Similarly we set the resources for header view
            holder.Name.setText(name);
            holder.email.setText(email);
            holder.threeBarView.setWeight(m_dFreeSpaceWeight,m_dUsedSpaceWeight);
            holder.threeBarView.setSpaceString(m_sFreeSpaceText,m_sUsedSpaceText);
        }
    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return mNavTitles.length+1; // the number of items in the list will be +1 the titles including the header view.
    }


    // Witht the following method we check what type of view is being passed
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }
}