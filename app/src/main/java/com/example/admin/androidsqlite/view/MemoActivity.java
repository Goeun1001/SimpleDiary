package com.example.admin.androidsqlite.view;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.androidsqlite.R;
import com.example.admin.androidsqlite.database.DatabaseHelper;
import com.example.admin.androidsqlite.model.Memo;
import com.example.admin.androidsqlite.utils.MyDividerItemDecoration;
import com.example.admin.androidsqlite.utils.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MemoActivity extends AppCompatActivity {

    private MemoAdapter memoAdapter;
    private RecyclerView recyclerView;
    private List<Memo> memoList = new ArrayList<>();
    private DatabaseHelper dbh;
    private TextView textmemo;

    EditText editMemo;
    TextView textDate;
    int year;
    int month;
    int day;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date1;
    String date;
    Memo memo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textmemo = findViewById(R.id.empty_memo);
        recyclerView = findViewById(R.id.recycler_view);

        memoAdapter = new MemoAdapter(this, memoList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(memoAdapter);

        dbh = new DatabaseHelper(getApplicationContext());

        setData();
        showEmptyMemo();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {

            @Override
            public void onClick(View view, int position) {
               showActionsDialog(position);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMemoDialog();
            }
        });
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    //추가된 소스, ToolBar에 추가된 항목의 select 이벤트를 처리하는 함수

    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_search:
                // User chose the "Settings" item, show the app settings UI...
                Intent intent = new Intent(getApplicationContext(), StaticsActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "환경설정 버튼 클릭됨", Toast.LENGTH_LONG).show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Toast.makeText(getApplicationContext(), "나머지 버튼 클릭됨", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);

        }
    }

    private void showMemoDialog() {

        myCalendar = Calendar.getInstance();
        year = myCalendar.get(Calendar.YEAR);
        month = myCalendar.get(Calendar.MONTH);
        day = myCalendar.get(Calendar.DAY_OF_MONTH);

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.memo_dialog, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MemoActivity.this);
        alertDialogBuilderUserInput.setView(view);
        editMemo = view.findViewById(R.id.editmemo);
        textDate = view.findViewById(R.id.txtdate);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        ImageButton datebtn = view.findViewById(R.id.imgdate);

        dialogTitle.setText("Add Memo");
        if (month + 1 < 10) {
            textDate.setText(new StringBuilder()
                    .append(year).append("/0").append(month + 1).append("/").append(day));
        } else {
            textDate.setText(new StringBuilder()
                    .append(year).append("/").append(month + 1).append("/").append(day));
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        datebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MemoActivity.this, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

       updateDisplay();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(editMemo.getText().toString())) {
                    Toast.makeText(MemoActivity.this, "Enter Memo!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    date=textDate.getText().toString().trim();
                    createMemo();
                    setData();
                    showEmptyMemo();
                    alertDialog.dismiss();
                }

            }
        });
    }


    private void showActionsDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MemoActivity.this);
        builder
                .setCancelable(false).setTitle("Choose Option").setMessage("Delete or Update Memo?")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        updateData(position);
                    }
                })
                .setNegativeButton("Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                deleteData(position);
                                memoList.remove(position);
                                recyclerView.removeViewAt(position);
                                memoAdapter.notifyItemRemoved(position);
                                memoAdapter.notifyItemRangeChanged(position, memoList.size());
                                memoAdapter.notifyDataSetChanged();
                                showEmptyMemo();
                            }
                        })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void updateData(final int position) {

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.memo_dialog, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MemoActivity.this);
        alertDialogBuilderUserInput.setView(view);
        editMemo = view.findViewById(R.id.editmemo);
        textDate = view.findViewById(R.id.txtdate);
        ImageButton datebtn = view.findViewById(R.id.imgdate);

        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText("Update Memo");

        memo = memoList.get(position);
        if (memo != null) {

            editMemo.setText(memo.getEvent());
            textDate.setText(memo.getDate());
        }

        myCalendar = Calendar.getInstance();
        year = myCalendar.get(Calendar.YEAR);
        month = myCalendar.get(Calendar.MONTH);
        day = myCalendar.get(Calendar.DAY_OF_MONTH);

        datebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(MemoActivity.this, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

       updateDisplay();

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });
        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editMemo.getText().toString())) {
                    Toast.makeText(MemoActivity.this, "Enter Memo!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    date=textDate.getText().toString().trim();
                    updateMemo(editMemo.getText().toString(), position);
                    setData();
                    showEmptyMemo();
                    alertDialog.dismiss();

                }
            }
        });
    }

    private void updateDisplay() {
        date1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year1, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                year = year1;
                month = monthOfYear;
                day = dayOfMonth;
                if (month + 1 < 10) {
                    textDate.setText(new StringBuilder()
                            .append(year).append("/0").append(month + 1).append("/").append(day));
                } else {
                    textDate.setText(new StringBuilder()
                            .append(year).append("/").append(month + 1).append("/").append(day));
                }
            }
        };
    }


    private void showEmptyMemo() {
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.rawQuery("Select * from memotable", null);
        int count = c.getCount();
        c.close();

        if (count == 0) {
            textmemo.setVisibility(View.VISIBLE);
        } else {
            textmemo.setVisibility(View.GONE);
        }
    }

    private void createMemo() {
        SQLiteDatabase db = dbh.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("event", editMemo.getText().toString());
        db.insert("memotable", null, values);

        Toast.makeText(getApplicationContext(), "Memo Created", Toast.LENGTH_LONG).show();
    }

    private void setData() {
        memoList.clear();
        SQLiteDatabase db = dbh.getWritableDatabase();
        Cursor c = db.rawQuery("Select * from memotable", null);
        if (c.moveToFirst()) {

            do {

           memo = new Memo(c.getInt(0), c.getString(1), c.getString(2));
           memoList.add(memo);

            } while (c.moveToNext());
        }
    }

    private void deleteData(int position) {
        memo = memoList.get(position);
        SQLiteDatabase db = dbh.getWritableDatabase();
        db.delete("memotable", "id=" + memo.getId(), null);
        db.close();

    }

    private void updateMemo(String memoedit, int position) {
        memo = memoList.get(position);
        memo.setEvent(memoedit);
        memo.setDate(date);

        SQLiteDatabase db = dbh.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("event", memo.getEvent());
        db.update("memotable", values, "id=" + memo.getId(), null);

        memoList.set(position,memo);
        memoAdapter.notifyItemChanged(position);

        showEmptyMemo();

    }
}

