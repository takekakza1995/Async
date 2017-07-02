package com.example.take.async;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Take on 19/6/2560.
 */
                            //Adap type Book
public class ListBookAdap extends ArrayAdapter<Book>
{

    ArrayList<Book> books;
    Activity context;
    int ressource;


                                         //layout
    public ListBookAdap(Activity context, int resource, ArrayList<Book> objects)
    {
        super(context, resource, objects);
        this.context =  context;
        this.books = objects;
        this.ressource = resource;
    }

    static class ViewHolder
    {
        public TextView textBook;
        public TextView textAuthor;
        public TextView textPrice;
    }

                                        //View ที่อ่านมา
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View rowView = convertView;
        if (rowView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.edit_listbook,null);
            //ผูก layout

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.textBook = (TextView)rowView.findViewById(R.id.textBook);
            viewHolder.textAuthor= (TextView)rowView.findViewById(R.id.textAuthors);
            viewHolder.textPrice = (TextView)rowView.findViewById(R.id.textPrice);
            rowView.setTag(viewHolder);

        }


            ViewHolder holder = (ViewHolder) rowView.getTag();
            Book book = books.get(position);

            holder.textBook.setText(book.getTitle());
            holder.textAuthor.setText(book.getAuthors());
            holder.textPrice.setText(Double.toString(book.getPrice()));



        return rowView;
    }
}
