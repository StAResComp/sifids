package uk.ac.masts.sifids.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import uk.ac.masts.sifids.R;
import uk.ac.masts.sifids.database.CatchDatabase;
import uk.ac.masts.sifids.entities.Fish1Form;
import uk.ac.masts.sifids.entities.Fish1FormRow;

/**
 * Created by pgm5 on 21/02/2018.
 */

public class Fish1FormRowAdapter extends RecyclerView.Adapter<Fish1FormRowAdapter.ViewHolder> {

    private List<Fish1FormRow> formRows;
    CatchDatabase db;
    Context context;

    public Fish1FormRowAdapter(List<Fish1FormRow> formRows, Context context) {
        this.formRows = formRows;
        this.context = context;
    }

    @Override
    public Fish1FormRowAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_row_recycler_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Fish1FormRowAdapter.ViewHolder holder, int position) {
        db = CatchDatabase.getInstance(holder.itemView.getContext());
        holder.label.setText(formRows.get(position).toString());
        holder.editButton.setTag(R.id.parent_form, formRows.get(position).getFormId());
        holder.editButton.setTag(R.id.form_row_in_question, formRows.get(position).getId());
        holder.deleteButton.setTag(R.id.parent_form, formRows.get(position).getFormId());
        holder.deleteButton.setTag(R.id.form_row_in_question, formRows.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return formRows.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView label;
        public Button editButton;
        public Button deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.label);
            editButton = (Button) itemView.findViewById(R.id.btn_edit_form_row);
            editButton.setOnClickListener(this);
            deleteButton = (Button) itemView.findViewById(R.id.btn_delete_form_row);
            deleteButton.setOnClickListener(this);
        }

        public void onClick(View view) {
            if (view.getId() == R.id.btn_edit_form_row) {
                Intent i = new Intent(view.getContext(), EditFish1FormRowActivity.class);
                int id = (Integer) view.getTag(R.id.form_row_in_question);
                int form_id = (Integer) view.getTag(R.id.parent_form);
                i.putExtra(Fish1FormRow.ID, id);
                i.putExtra(Fish1FormRow.FORM_ID, form_id);
                view.getContext().startActivity(i);
            }
            else if (view.getId() == R.id.btn_delete_form_row) {
                this.confirmDialog(view);
            }
        }

        private void confirmDialog(final View view) {
            final int formRowId = (Integer) view.getTag(R.id.form_row_in_question);
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder
                    .setMessage(context.getString(R.string.fish_1_form_row_deletion_confirmation_message))
                    .setPositiveButton(context.getString(R.string.yes),  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Runnable r = new Runnable() {
                                @Override
                                public void run() {
                                    db.catchDao().deleteFish1FormRow(formRowId);
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
                            Intent i = new Intent(activity, EditFish1FormActivity.class);
                            int formId = (Integer) view.getTag(R.id.parent_form);
                            i.putExtra(Fish1Form.ID, formId);
                            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            activity.finish();
                            activity.startActivity(i);
                        }
                    })
                    .setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .show();
        }
    }
}
