package com.miklabs.music.Menu;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.miklabs.music.R;

public class SimpleItem extends DrawerItem<SimpleItem.ViewHolder> {

    private int selectedItemIconTint;
    private int selectedItemTextTint;
    private int selectedCardTint;

    private int normalItemIconTint;
    private int normalItemTextTint;
    private int normalCardTint;

    private final Drawable icon;
    private final String title;
    private final int card_color;

    public SimpleItem(Drawable icon, String title, int card_color) {
        this.icon = icon;
        this.title = title;
        this.card_color = card_color;
    }

    @Override
    public ViewHolder createViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_option, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void bindViewHolder(ViewHolder holder) {
        holder.title.setText(title);
        holder.icon.setImageDrawable(icon);
        holder.cardView.setCardBackgroundColor(card_color);

        holder.title.setTextColor(isChecked ? selectedItemTextTint : normalItemTextTint);
        holder.icon.setColorFilter(isChecked ? selectedItemIconTint : normalItemIconTint);
        holder.cardView.setCardBackgroundColor(isChecked ? selectedCardTint : normalCardTint);
        if (isChecked) {
            ViewGroup.MarginLayoutParams cardViewParams =
                    (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();
            cardViewParams.setMargins(10, 5, 5, 5);
            //cardViewParams.width = 500 ;
            holder.cardView.requestLayout();

            ViewGroup.MarginLayoutParams imageViewParams =
                    (ViewGroup.MarginLayoutParams) holder.icon.getLayoutParams();
            imageViewParams.setMargins(40, 0, 0, 0);
            holder.icon.requestLayout();

        } else {
            ViewGroup.MarginLayoutParams cardViewParams =
                    (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();
            cardViewParams.setMargins(10, 5, 50, 5);
            //cardViewParams.width = 440 ;
            holder.cardView.requestLayout();
            ViewGroup.MarginLayoutParams imageViewParams =
                    (ViewGroup.MarginLayoutParams) holder.icon.getLayoutParams();
            imageViewParams.setMargins(0, 0, 0, 0);
            holder.icon.requestLayout();
        }
    }

    public SimpleItem withSelectedIconTint(int selectedItemIconTint) {
        this.selectedItemIconTint = selectedItemIconTint;
        return this;
    }

    public SimpleItem withSelectedTextTint(int selectedItemTextTint) {
        this.selectedItemTextTint = selectedItemTextTint;
        return this;
    }

    public SimpleItem withSelectedCardTint(int selectedCardBackgroundTint) {
        this.selectedCardTint = selectedCardBackgroundTint;
        return this;
    }

    public SimpleItem withIconTint(int normalItemIconTint) {
        this.normalItemIconTint = normalItemIconTint;
        return this;
    }

    public SimpleItem withTextTint(int normalItemTextTint) {
        this.normalItemTextTint = normalItemTextTint;
        return this;
    }

    public SimpleItem withCardTint(int normalCardBackgroundTint) {
        this.normalCardTint = normalCardBackgroundTint;
        return this;
    }


    static class ViewHolder extends DrawerAdapter.ViewHolder {

        private final ImageView icon;
        private final TextView title;
        private final CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            title = (TextView) itemView.findViewById(R.id.title);
            cardView = (CardView) itemView.findViewById(R.id.menuItemCard);
        }
    }
}
