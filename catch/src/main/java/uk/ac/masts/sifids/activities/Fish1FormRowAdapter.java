package uk.ac.masts.sifids.activities;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import uk.ac.masts.sifids.R;
import uk.ac.masts.sifids.entities.Fish1FormRow;

/**
 * Created by pgm5 on 21/02/2018.
 */

public class Fish1FormRowAdapter extends RecyclerView.Adapter<Fish1FormRowAdapter.ViewHolder> {

    private List<Fish1FormRow> formRows;

    public Fish1FormRowAdapter(List<Fish1FormRow> formRows) {
        this.formRows = formRows;
    }

    @Override
    public Fish1FormRowAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_recycler_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Fish1FormRowAdapter.ViewHolder holder, int position) {
        holder.createdAt.setText(formRows.get(position).getCreatedAt().toString());
        holder.button.setTag(R.id.parent_form, Integer.valueOf(formRows.get(position).getFormId()));
        holder.button.setTag(R.id.form_row_to_edit, Integer.valueOf(formRows.get(position).getId()));
    }

    @Override
    public int getItemCount() {
        return formRows.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView createdAt;
        public Button button;

        public ViewHolder(View itemView) {
            super(itemView);
            createdAt = itemView.findViewById(R.id.created_at);
            button = (Button) itemView.findViewById(R.id.btn_edit_form_row);
            button.setOnClickListener(this);
        }

        public void onClick(View view) {
            Intent i = new Intent(view.getContext(), EditFish1FormActivity.class);
            int id = (Integer) view.getTag(R.id.form_row_to_edit);
            int form_id = (Integer) view.getTag(R.id.parent_form);
            i.putExtra("id", id);
            i.putExtra("form_id", form_id);
            view.getContext().startActivity(i);
        }
    }
}
