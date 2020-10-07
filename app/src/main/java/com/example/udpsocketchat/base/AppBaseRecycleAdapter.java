package com.example.udpsocketchat.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

/**
 * @author Manish Kumar
 * @since 18/8/17
 */


public abstract class AppBaseRecycleAdapter extends RecyclerView.Adapter<AppBaseRecycleAdapter.BaseViewHolder> {


    public static final int VIEW_TYPE_LOAD_MORE = 404;
    public static final int VIEW_TYPE_DATA = 1;
    protected boolean isForDesign = false;
    LayoutInflater layoutInflater;
    Context context;
    RecyclerView recyclerView;

    public Context getContext () {
        return context;
    }

    public RecyclerView getRecyclerView () {
        return recyclerView;
    }

    public LayoutInflater getLayoutInflater () {
        return layoutInflater;
    }

    public View inflateLayout (int layoutId) {
        return getLayoutInflater().inflate(layoutId, null);
    }

    @Override
    public int getItemViewType (int position) {
        return getViewType(position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        this.recyclerView = (RecyclerView) parent;
        context = parent.getContext();
        layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == 0) {
            return getViewHolder();
        }
        return getViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder (BaseViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount () {
        return isForDesign ? 5 : getDataCount();
    }

    public abstract BaseViewHolder getViewHolder ();

    public BaseViewHolder getViewHolder (ViewGroup parent, int viewType) {
        return null;
    }

    public int getViewType (int position) {
        return super.getItemViewType(position);
    }

    public abstract int getDataCount ();

    public abstract class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public BaseViewHolder (View itemView) {
            super(itemView);
        }

        public abstract void setData (int position);

        public void performItemClick (int position, View view) {
            ItemClickSupport itemClickSupport = ItemClickSupport.getFrom(itemView);
            if (itemClickSupport != null) {
                itemClickSupport.getmOnItemClickListener()
                        .onItemClicked(itemClickSupport.getmRecyclerView(), position, view);
            }
        }

        public void performChildItemClick (int parentPosition, int childPosition, View view) {
            ItemClickSupport itemClickSupport = ItemClickSupport.getFrom(itemView);
            if (itemClickSupport != null) {
                itemClickSupport.onChildItemClicked(itemClickSupport.getmRecyclerView(), parentPosition, childPosition, view);
            }
        }

        @Override
        public void onClick (View v) {

        }
    }

    public boolean isValidString (String data) {
        return data != null && !data.trim().isEmpty();
    }

    public boolean isValidObject (Object object) {
        return object != null;
    }

    public String getValidDecimalFormat (String value) {
        if (!isValidString(value)) {
            return "0.00";
        }
        double netValue = Double.parseDouble(value);
        return getValidDecimalFormat(netValue);
    }

    public String getValidDecimalFormat (double value) {
        return String.format(Locale.ENGLISH, "%.2f", value);
    }

    public void updateViewVisibitity (View myView, int visibility) {
        if (myView.getVisibility() != visibility) {
            myView.setVisibility(visibility);
        }

    }

    public class LoadMoreViewHolder extends BaseViewHolder {


        public LoadMoreViewHolder (View itemView) {
            super(itemView);
        }

        @Override
        public void setData (int position) {

        }
    }


}
