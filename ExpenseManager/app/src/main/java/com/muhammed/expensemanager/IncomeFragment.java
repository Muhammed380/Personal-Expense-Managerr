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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.muhammed.expensemanager.Model.Data;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;


public class IncomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private RecyclerView recyclerView;
    private TextView incomeTotalsum;
    private EditText edtAmount;
    private EditText edtType;
    private EditText edtNote;

    private Button btnUpdate;
    private Button btnDelete;

    private String type ;
    private String note;
    private int amount;
    private String post_key;


    public IncomeFragment() {
        // Required empty public constructor
    }

    
    public static IncomeFragment newInstance(String param1, String param2) {
        IncomeFragment fragment = new IncomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview =  inflater.inflate(R.layout.fragment_income, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid  = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        incomeTotalsum = myview.findViewById(R.id.income_txt_result);
        recyclerView = myview.findViewById(R.id.recyler_id_income);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totlatvalue = 0;
                for (DataSnapshot mysnapshot : snapshot.getChildren()) {
                    Data data = mysnapshot.getValue(Data.class);
                    totlatvalue+=data.getAmount();
                    String stTotalvale = String.valueOf(totlatvalue);
                    incomeTotalsum.setText(stTotalvale+".00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return  myview;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mIncomeDatabase,Data.class)
                .setLifecycleOwner(this)
                .build();
        FirebaseRecyclerAdapter<Data,myViewHolder> adapter = new FirebaseRecyclerAdapter<Data, myViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull myViewHolder holder, int    position, @NonNull Data model) {

                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());
                holder.setAmount(model.getAmount());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key  = getRef(position).getKey();
                        type = model .getType();
                        note = model.getNote();
                        amount = model.getAmount();
                        updateDataItem();
                    }


                });

            }

            @NonNull
            @Override
            public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new myViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.income_recyler_data,parent,false));
            }
        };
        recyclerView.setAdapter(adapter);
    }


    public static class myViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        private void setType (String type) {
            TextView mType  = mView.findViewById(R.id.type_txt_income);
            mType.setText(type);
        }

        private void setNote (String note) {
            TextView mNote = mView.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }

        private void setDate (String date) {

            TextView mDate  = mView.findViewById(R.id.date_txt_income);
            mDate.setText(date);

        }
        private void  setAmount (int amount) {
            TextView mAmount = mView.findViewById(R.id.amount_txt_income);
            String stammount = String.valueOf(amount);
            mAmount.setText(stammount);
        }

    }

    private void updateDataItem() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.update_data_item,null);
        myDialog.setView(myview);

        edtAmount = myview.findViewById(R.id.amount_edt);
        edtType = myview.findViewById(R.id.type_edt);
        edtNote = myview.findViewById(R.id.note_edt);

        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());

        btnUpdate = myview.findViewById(R.id.btn_upd_Update);
        btnDelete = myview.findViewById(R.id.btnuPD_Delete);

        AlertDialog dialog = myDialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type = edtType.getText().toString().trim();
                note = edtNote.getText().toString().trim();

                String mdammount  = String.valueOf(amount);
                mdammount = edtAmount.getText().toString().trim();

                int myAmmount  = Integer.parseInt(mdammount);

                String  mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data( myAmmount,type,note,post_key,mDate);

                mIncomeDatabase.child(post_key).setValue(data);
                dialog.dismiss();

            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mIncomeDatabase.child(post_key).removeValue();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
 }