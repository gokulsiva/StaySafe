package com.example.staysafe.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.staysafe.R;
import com.example.staysafe.data.model.User;

import java.util.ArrayList;

public class DependentsViewAdapter extends ArrayAdapter<User>{

    private ArrayList<User> dataSet;
    private Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView name;
        TextView email;
        TextView contact;
    }


    public DependentsViewAdapter(ArrayList<User> data, Context context) {
        super(context, R.layout.dependents_view_template, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        User user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.dependents_view_template, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.depTemplateName);
            viewHolder.email = (TextView) convertView.findViewById(R.id.depTemplateEmail);
            viewHolder.contact = (TextView) convertView.findViewById(R.id.depTemplateContact);


            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.name.setText(user.getName());
        viewHolder.email.setText(user.getEmail());
        viewHolder.contact.setText(user.getContactNo().toString());

        // Return the completed view to render on screen
        return convertView;
    }
}
