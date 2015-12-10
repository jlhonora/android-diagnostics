package org.honorato.diagnostics.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.honorato.diagnostics.R;
import org.honorato.diagnostics.databinding.CheckBinding;
import org.honorato.diagnostics.models.Check;

/**
 * Created by jlh on 11/26/15.
 */
public class ChecksAdapter extends ArrayAdapter<Check> {

    public ChecksAdapter(Context context, ObservableList<Check> checks) {
        super(context, R.layout.check, checks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CheckBinding binding;
        if (convertView != null) {
            binding = DataBindingUtil.getBinding(convertView);
        } else {
            binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                    R.layout.check, parent, false);
        }
        binding.setCheck(this.getItem(position));
        return binding.getRoot();
    }

}
