package com.wasn.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wasn.R;
import com.wasn.pojos.Transaction;

import java.util.ArrayList;

/**
 * List adapter class to display transaction list
 *
 * @author erangaeb@gmail.com (eranga bandara)
 */
public class TransactionListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Transaction> transactionList;

    private Typeface typeface;

    /**
     * Set context and attribute list
     *
     * @param context
     * @param transactionList
     */
    public TransactionListAdapter(Context context, ArrayList<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;

        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/GeosansLight.ttf");
    }

    /**
     * Add filtered client to list
     */
    public void reloadAdapter(ArrayList<Transaction> transactionList) {
        this.transactionList = transactionList;
        notifyDataSetChanged();
    }

    /**
     * Get size of attribute list
     */
    public int getCount() {
        return transactionList.size();
    }

    /**
     * Get specific item from attribute list
     */
    public Object getItem(int i) {
        return transactionList.get(i);
    }

    /**
     * Get attribute list item id
     */
    public long getItemId(int i) {
        return i;
    }

    /**
     * Create list row view
     */
    public View getView(int i, View view, ViewGroup viewGroup) {
        // A ViewHolder keeps references to children views to avoid unnecessary calls
        // to findViewById() on each row
        final ViewHolder holder;

        Transaction transaction = (Transaction) getItem(i);

        if (view == null) {
            //inflate print_list_row layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.transaction_row_layout, viewGroup, false);

            //create view holder to store reference to child views
            holder = new ViewHolder();
            holder.iconText = (TextView) view.findViewById(R.id.icon_text);
            holder.account = (TextView) view.findViewById(R.id.account_no);
            holder.amount = (TextView) view.findViewById(R.id.amount);
            holder.iconText.setTypeface(typeface, Typeface.BOLD);
            holder.iconText.setTextColor(context.getResources().getColor(R.color.white));
            holder.account.setTypeface(typeface, Typeface.BOLD);
            holder.amount.setTypeface(typeface, Typeface.NORMAL);

            view.setTag(holder);
        } else {
            // get view holder back
            holder = (ViewHolder) view.getTag();
        }

        // bind text with view holder content view for efficient use
        holder.iconText.setText("$");
        holder.account.setText(transaction.getClientAccountNo());
        holder.amount.setText(transaction.getTransactionAmount()+"");
        view.setBackgroundResource(R.drawable.more_layout_selector_normal);

        return view;
    }

    /**
     * Keep reference to children view to avoid unnecessary calls
     */
    static class ViewHolder {
        TextView iconText;
        TextView account;
        TextView amount;
    }

}
