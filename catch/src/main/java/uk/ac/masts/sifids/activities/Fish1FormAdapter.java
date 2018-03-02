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
import uk.ac.masts.sifids.entities.Fish1Form;

/**
 * Created by pgm5 on 21/02/2018.
 */

public class Fish1FormAdapter extends RecyclerView.Adapter<Fish1FormAdapter.ViewHolder> {

    private List<Fish1Form> forms;

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
        holder.createdAt.setText(forms.get(position).getCreatedAt().toString());
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
            createdAt = itemView.findViewById(R.id.created_at);
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
