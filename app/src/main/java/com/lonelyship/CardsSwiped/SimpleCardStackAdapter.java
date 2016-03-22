package com.lonelyship.CardsSwiped;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lonelyship.Main.R;


public final class SimpleCardStackAdapter extends CardStackAdapter {

	public SimpleCardStackAdapter(Context mContext) {
		super(mContext);
	}

	@Override
	public View getCardView(int position, CardModel model, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.card_swiped_std_card_inner, parent, false);
			assert convertView != null;
		}



		((ImageView) convertView.findViewById(R.id.image)).setImageDrawable(model.getCardImageDrawable());
		((TextView) convertView.findViewById(R.id.title)).setText(model.getTitle());
		((TextView) convertView.findViewById(R.id.description)).setText(model.getDescription());


        ImageView iv_1 = ((ImageView) convertView.findViewById(R.id.image_1));
        iv_1.setTag(position);
        iv_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // Toast.makeText(getContext(), "點第" + v.getTag() + "張相機", Toast.LENGTH_SHORT).show();

            }
        });

        ImageView iv_2 = ((ImageView) convertView.findViewById(R.id.image_2));
        iv_2.setTag(position);
        iv_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // Toast.makeText(getContext(), "點第" + v.getTag() + "張人頭", Toast.LENGTH_SHORT).show();

            }
        });

		return convertView;
	}
}
