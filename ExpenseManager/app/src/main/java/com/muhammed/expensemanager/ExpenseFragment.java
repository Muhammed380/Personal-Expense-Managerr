package com.muhammed.expensemanager;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muhammed.expensemanager.Model.Data;

import java.text.DateFormat;
import java.util.Date;


public class ExpenseFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;

    private RecyclerView recyclerView;
    private TextView expenseSumResult;

    private EditText edtAmmount;
    private EditText edtType;
    private EditText edtNote;

    private Button  btnUpdate;
    private Button btnDelete;
    
    private String type;
    private String note;
    private int ammount;

    private String post_key;


    public ExpenseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview= inflater.inflate(R.layout.fragment_expense, container, false);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser muser = mAuth.getCurrentUser();
        String uid = muser.getUid();
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
        expenseSumResult = myview.findViewById(R.id.expense_txt_result);

        recyclerView = myview.findViewById(R.id.recyler_id_espense);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int expesnSum = 0;
                for (DataSnapshot mysanapshot:snapshot.getChildren()) {
                    Data data = mysanapshot.getValue(Data.class);
                    expesnSum+=data.getAmount();

                    String strExpenseSum = String.valueOf(expesnSum);
                    expenseSumResult.setText(strExpenseSum+".00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return myview;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mExpenseDatabase,Data.class)
                .setLifecycleOwner(this)
                .build();
        FirebaseRecyclerAdapter<Data,MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {
                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());
                holder.setAmount(model.getAmount());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key = getRef(position).getKey();
                        type = model.getType();
                        note = model.getNote();
                        ammount = model.getAmount();

                        updateDataItem();
                    }
                });

            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_recyler_data,parent,false));

            }
        };
        recyclerView.setAdapter(adapter);



            }



    private static class MyViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        private void setDate(String date) {
            TextView mDate =mView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }
        private void setType(String type) {
            TextView mType = mView.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }
        private void setNote (String note) {
            TextView mNote = mView.findViewById(R.id.note_txt_expense);
            mNote.setText(note);
        }
        private void setAmount (int amount) {
            TextView mAAmount = mView.findViewById(R.id.amount_txt_expense);
            String stammount = String.valueOf(amount);
            mAAmount.setText(stammount);
        }
    }

    private void updateDataItem () {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.update_data_item,null);
        myDialog.setView(myview);

        edtAmmount = myview.findViewById(R.id.amount_edt);
        edtNote = myview.findViewById(R.id.note_edt);
        edtType = myview.findViewById(R.id.type_edt);

        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmmount.setText(String.valueOf(ammount));
        edtAmmount.setSelection(String.valueOf(ammount).length());

        btnUpdate  = myview.findViewById(R.id.btnuPD_Delete);
        btnDelete = myview.findViewById(R.id.btnuPD_Delete);


        AlertDialog dialog = myDialog.create();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type=edtType.getText().toString().trim();
                note= edtNote.getText().toString().trim();

                String stammount = String.valueOf(ammount);
                stammount = edtAmmount.getText().toString().trim();

                int intamount = Integer.parseInt(stammount);
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(intamount,type,note,post_key,mDate);
                mExpenseDatabase.child(post_key).setValue(data);
                dialog.dismiss();


            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mExpenseDatabase.child(post_key).removeValue();
                dialog.dismiss();
            }
        });
        dialog.show();



    }

}