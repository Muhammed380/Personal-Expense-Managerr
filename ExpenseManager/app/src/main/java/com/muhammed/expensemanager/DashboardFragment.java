package com.muhammed.expensemanager;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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


public class DashboardFragment extends Fragment {

    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    private TextView fab_income_txt;
    private TextView  fab_expense_txt;

    private Boolean isOpen=false;

    private TextView totalIncomeResult;
    private TextView totalExpenseResult;
    private Animation FadOpen,FadClose;

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    private RecyclerView mRecylerIncome;
    private RecyclerView mRecylerExpense;

    private AdView mAdView;

    public DashboardFragment() {
        // Required empty public constructor
    }


    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
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
        View myView=  inflater.inflate(R.layout.fragment_dashboard, container, false);

        MobileAds.initialize(this.getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }

        });
/*
        mAdView = myView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

 */

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser =mAuth.getCurrentUser();
        String uid  = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);

        fab_main_btn = myView.findViewById(R.id.fb_main_plus_btn);
        fab_expense_btn = myView.findViewById(R.id.expense_ft_btn);
        fab_income_btn = myView.findViewById(R.id.income_ft_btn);

        FadOpen = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        FadClose = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);

        fab_income_txt = myView.findViewById(R.id.income_ft_text);
        fab_expense_txt = myView.findViewById(R.id.expense_ft_text);

        totalIncomeResult = myView.findViewById(R.id.income_set_result);
        totalExpenseResult = myView.findViewById(R.id.expense_set_result);

        mRecylerIncome = myView.findViewById(R.id.recyler_income);
        mRecylerExpense = myView.findViewById(R.id.recyler_epense);

        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isOpen) {
                    fab_income_btn.startAnimation(FadClose);
                    fab_expense_btn.startAnimation(FadClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(FadClose);
                    fab_expense_txt.startAnimation(FadClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen = false;
                }else  {
                    fab_income_btn.startAnimation(FadOpen);
                    fab_expense_btn.startAnimation(FadOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(FadOpen);
                    fab_expense_txt.startAnimation(FadOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen=true;
                }

                addData();

            }
        });

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalsum = 0;

                for (DataSnapshot mysnap : snapshot.getChildren()) {

                    Data data = mysnap.getValue(Data.class);
                    totalsum+=data.getAmount();

                    String stResult =String.valueOf(totalsum);
                    totalIncomeResult.setText(stResult+".00");
                }
             }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalsum = 0;

                for (DataSnapshot mysnapshot : snapshot.getChildren()) {

                    Data data = mysnapshot.getValue(Data.class);
                    totalsum+=data.getAmount();

                    String strTotalsum = String.valueOf(totalsum);

                    totalExpenseResult.setText(strTotalsum+".00");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        LinearLayoutManager layoutManagerIncoem = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        layoutManagerIncoem.setStackFromEnd(true);
        layoutManagerIncoem.setReverseLayout(true);
        mRecylerIncome.setHasFixedSize(true);
        mRecylerIncome.setLayoutManager(layoutManagerIncoem);

        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        layoutManagerExpense.setReverseLayout(true);
        layoutManagerExpense.setStackFromEnd(true);
        mRecylerExpense.setHasFixedSize(true);
        mRecylerExpense.setLayoutManager(layoutManagerExpense);

        return myView;
    }

    private void ftAnimation () {

        if (isOpen) {
            fab_income_btn.startAnimation(FadClose);
            fab_expense_btn.startAnimation(FadClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(FadClose);
            fab_expense_txt.startAnimation(FadClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen = false;
        }else  {
            fab_income_btn.startAnimation(FadOpen);
            fab_expense_btn.startAnimation(FadOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(FadOpen);
            fab_expense_txt.startAnimation(FadOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen=true;
        }

    }

    private void addData () {

        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incomeDataInsert();
            }
        });
        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                expenseDataInsert();
            }
        });
    }
    public  void incomeDataInsert() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myViewm = inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        myDialog.setView(myViewm);

    final  AlertDialog dialog = myDialog.create();

    dialog.setCancelable(false);

        EditText editAmount = myViewm.findViewById(R.id.amount_edt);
        EditText edtType = myViewm.findViewById(R.id.type_edt);
        EditText edtNote  = myViewm.findViewById(R.id.note_edt);

        Button btnSave = myViewm.findViewById(R.id.btnSave);
        Button btnCansel = myViewm.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String type= edtType.getText().toString().trim();
                String ammount = editAmount.getText().toString();
                String note = edtNote.getText().toString().trim();

                if (TextUtils.isEmpty(type)) {

                    edtType.setError("Required Field...");
                    return;
                }

                if (TextUtils.isEmpty(ammount)) {
                    editAmount.setError("Required Field...");
                    return;
                }


              //  int ourammontint = Integer.parseInt(ammount);

                int ourammontint =Integer.parseInt(ammount);


                if (TextUtils.isEmpty(note)) {

                    edtNote.setError("Required Field...");
                    return;
                }

                String id  = mIncomeDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data( ourammontint,type,note,id,mDate);
                mIncomeDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(),"Data Added", Toast.LENGTH_SHORT).show();

                ftAnimation();
                dialog.dismiss();
            }
        });

        btnCansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void expenseDataInsert () {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myview = inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        myDialog.setView(myview);

    final     AlertDialog dialog =  myDialog.create();

    dialog.setCancelable(false);

        EditText ammount = myview.findViewById(R.id.amount_edt);
        EditText type = myview.findViewById(R.id.type_edt);
        EditText note = myview.findViewById(R.id.note_edt);

        Button btnSave =  myview.findViewById(R.id.btnSave);
        Button btnCansel = myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tmAmmount = ammount.getText().toString().trim();
                String tmtype = type.getText().toString().trim();
                String tmnote = note.getText().toString().trim();

                if  (TextUtils.isEmpty(tmAmmount)) {
                    ammount.setError("Required Field...");
                    return;
                }

                int inamount  =Integer.parseInt(tmAmmount);

                if (TextUtils.isEmpty(tmtype)) {
                    type.setError("Required Field...");
                    return;
                }
                if (TextUtils.isEmpty(tmnote)) {
                    note.setError("Required Field...");
                    return;
                }

                String id =     mExpenseDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(inamount,tmtype,tmnote,id,mDate);
                mExpenseDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(),"Data added",Toast.LENGTH_SHORT).show();

                ftAnimation();
                dialog.dismiss();
            }
        });

        btnCansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mIncomeDatabase,Data.class)
                .setLifecycleOwner(this)
                .build();
        FirebaseRecyclerAdapter<Data,IncomeViewHolder> adapter = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull IncomeViewHolder holder, int position, @NonNull Data model) {

                holder.setIncomeAmmoutn(model.getAmount());
                holder.setIncomeType(model.getType());
                holder.setIncomeDate(model.getDate());

            }

            @NonNull
            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new IncomeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_income,parent,false));
            }
        };

        mRecylerIncome.setAdapter(adapter);

        FirebaseRecyclerOptions<Data> options1 = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mExpenseDatabase,Data.class)
                .setLifecycleOwner(this)
                .build();
        FirebaseRecyclerAdapter<Data, ExpenseViewHolder> adapter1 = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(options1) {
            @Override
            protected void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position, @NonNull Data model) {

                holder.setExpenseAmmount(model.getAmount());
                holder.setExpenseType(model.getType());
                holder.setExpenseDate(model.getDate());
            }

            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ExpenseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboart_expense,parent,false));
            }
        };

        mRecylerExpense.setAdapter(adapter1);
    }

    public static class IncomeViewHolder extends RecyclerView.ViewHolder {

        View mIncomeView;
        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            mIncomeView = itemView;
        }

        public void setIncomeType(String  type) {
            TextView mtype= mIncomeView.findViewById(R.id.type_Income_ds);
            mtype.setText(type);

        }

        public void setIncomeAmmoutn (int ammoutn) {
            TextView mAmount = mIncomeView.findViewById(R.id.ammoun_income_ds);
            String  strAmount = String.valueOf(ammoutn);
            mAmount.setText(strAmount);
        }

        public void setIncomeDate (String  date ) {
            TextView mDate =mIncomeView.findViewById(R.id.date_income_ds);
            mDate.setText(date);
        }


    }


    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {

        View mExpenseView;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            mExpenseView = itemView;

        }
        public void setExpenseType (String type) {
            TextView mtype = mExpenseView.findViewById(R.id.type_expense_ds);
            mtype.setText(type);

        }
        public void setExpenseAmmount (int ammount) {
            TextView mAmmount = mExpenseView.findViewById(R.id.ammoun_expense_ds);
            String strAmmount =String.valueOf(ammount);
            mAmmount.setText(strAmmount);
        }
        public void setExpenseDate (String date) {
            TextView mDate =mExpenseView.findViewById(R.id.date_expense_ds);
            mDate.setText(date);
        }
    }
}