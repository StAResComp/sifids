package uk.ac.masts.sifids.activities;

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
                lowerCal.setTime(lowerDate);
                Date upperDate = db.catchDao().getDateOfLatestRow(form.getId());
                upperCal.setTime(upperDate);
            }
        };
        Thread newThread= new Thread(r);
        newThread.start();
        try {
            newThread.join();
        }
        catch (InterruptedException ie) {

        }
        holder.createdAt.setText(form.getPln() + " " + new SimpleDateFormat("dd MMM yyyy").format(lowerCal.getTime()) + " - " + new SimpleDateFormat("dd MMM yyyy").format(upperCal.getTime()));
        holder.button.setTag(R.id.form_to_edit, Integer.valueOf(forms.get(position).getId()));
    }

    @Override
    public int getItemCount() {
        return forms.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView createdAt;
        public Button button;

        public ViewHolder(View itemView) {
            super(itemView);
            createdAt = itemView.findViewById(R.id.label);
            button = (Button) itemView.findViewById(R.id.btn_edit_form);
            button.setOnClickListener(this);
        }

        public void onClick(View view) {
            Intent i = new Intent(view.getContext(), EditFish1FormActivity.class);
            int id = (Integer) view.getTag(R.id.form_to_edit);
            i.putExtra("id", id);
            view.getContext().startActivity(i);
        }
    }
}
