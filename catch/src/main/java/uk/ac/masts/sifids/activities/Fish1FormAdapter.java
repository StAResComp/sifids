package uk.ac.masts.sifids.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import uk.ac.masts.sifids.R;
import uk.ac.masts.sifids.database.CatchDatabase;
import uk.ac.masts.sifids.entities.Fish1Form;

/**
 * Created by pgm5 on 21/02/2018.
 */

public class Fish1FormAdapter extends RecyclerView.Adapter<Fish1FormAdapter.ViewHolder> {

    private List<Fish1Form> forms;
    CatchDatabase db;

    public Fish1FormAdapter(List<Fish1Form> forms) {
        this.forms = forms;
    }

    @Override
    public Fish1FormAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_recycler_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Fish1FormAdapter.ViewHolder holder, int position) {
        final Fish1Form form = forms.get(position);
        db = CatchDatabase.getInstance(holder.itemView.getContext());
        final Calendar lowerCal = Calendar.getInstance();
        final Calendar upperCal = Calendar.getInstance();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Date lowerDate = db.catchDao().getDateOfEarliestRow(form.getId());
                if (lowerDate != null) lowerCal.setTime(lowerDate);
                Date upperDate = db.catchDao().getDateOfLatestRow(form.getId());
                if (upperDate != null) upperCal.setTime(upperDate);
            }
        };
        Thread newThread= new Thread(r);
        newThread.start();
        try {
            newThread.join();
        }
        catch (InterruptedException ie) {

        }
        holder.createdAt.setText(form.getPln() + "\n" + new SimpleDateFormat("dd MMM yyyy").format(lowerCal.getTime()) + " -\n" + new SimpleDateFormat("dd MMM yyyy").format(upperCal.getTime()));
        holder.editButton.setTag(R.id.form_in_question, Integer.valueOf(forms.get(position).getId()));
        holder.deleteButton.setTag(R.id.form_in_question, Integer.valueOf(forms.get(position).getId()));
    }

    @Override
    public int getItemCount() {
        return forms.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView createdAt;
        public Button editButton;
        public Button deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            createdAt = itemView.findViewById(R.id.label);
            editButton = (Button) itemView.findViewById(R.id.btn_edit_form);
            editButton.setOnClickListener(this);
            deleteButton = (Button) itemView.findViewById(R.id.btn_delete_form);
            deleteButton.setOnClickListener(this);
        }

        public void onClick(View view) {
            if (view.getId() == R.id.btn_edit_form) {
                Intent i = new Intent(view.getContext(), EditFish1FormActivity.class);
                int id = (Integer) view.getTag(R.id.form_in_question);
                i.putExtra("id", id);
                view.getContext().startActivity(i);
            }
            else if (view.getId() == R.id.btn_delete_form) {
                this.confirmDialog(view);
            }
        }

        private void confirmDialog(final View view) {
            final int formId = (Integer) view.getTag(R.id.form_in_question);
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder
                    .setMessage("Are you sure you want to delete this form?")
                    .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Runnable r = new Runnable() {
                                @Override
                                public void run() {
                                    db.catchDao().deleteFish1Form(formId);
                                }
                            };
                            Thread newThread= new Thread(r);
                            newThread.start();
                            try {
                                newThread.join();
                            }
                            catch (InterruptedException ie) {

                            }
                            Activity activity = (Activity) view.getContext();
                            activity.finish();
                            activity.startActivity(activity.getIntent());
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .show();
        }
    }
}
