package com.wasn.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wasn.R;
import com.wasn.pojos.Account;

import java.util.ArrayList;

/**
 * List adapter class to display transaction list
 *
 * @author erangaeb@gmail.com (eranga bandara)
 */
public class AccountListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Account> accounts;

    private Typeface typeface;

    /**
     * Set context and attribute list
     *
     * @param context
     * @param accounts
     */
    public AccountListAdapter(Context context, ArrayList<Account> accounts) {
        this.context = context;
        this.accounts = accounts;

        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/GeosansLight.ttf");
    }

    /**
     * Get size of attribute list
     */
    public int getCount() {
        return accounts.size();
    }

    /**
     * Get specific item from attribute list
     */
    public Object getItem(int i) {
        return accounts.get(i);
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

        Account account = (Account) getItem(i);

        if (view == null) {
            //inflate print_list_row layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.account_list_row_layout, viewGroup, false);

            //create view holder to store reference to child views
            holder = new ViewHolder();
            holder.account = (TextView) view.findViewById(R.id.account_no);
            holder.icon = (TextView) view.findViewById(R.id.icon_text);
            holder.icon.setTypeface(typeface, Typeface.BOLD);
            holder.account.setTypeface(typeface, Typeface.BOLD);

            view.setTag(holder);
        } else {
            // get view holder back
            holder = (ViewHolder) view.getTag();
        }

        // bind text with view holder content view for efficient use
        holder.account.setText(account.getAccNo());
        view.setBackgroundResource(R.drawable.more_layout_selector_normal);

        return view;
    }

    /**
     * Keep reference to children view to avoid unnecessary calls
     */
    static class ViewHolder {
        TextView icon;
        TextView account;
    }

}
