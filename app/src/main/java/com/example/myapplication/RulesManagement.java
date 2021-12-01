package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.DB.DBAdapter;
import com.example.myapplication.DB.Properties;

public class RulesManagement extends AppCompatActivity {
    final static String TAG = "修改字段";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);
        String msg;
        long id;
        final EditText nameText = (EditText) findViewById(R.id.name);
        final EditText idEntry = (EditText) findViewById(R.id.id_entry);

        final TextView labelView = (TextView) findViewById(R.id.label);
        final TextView displayView = (TextView) findViewById(R.id.display);

        final RadioButton rb1 = (RadioButton) findViewById(R.id.RadioButton01);
        final RadioButton rb2 = (RadioButton) findViewById(R.id.RadioButton02);
        final RadioButton rb3 = (RadioButton) findViewById(R.id.RadioButton03);
        final RadioButton rb4 = (RadioButton) findViewById(R.id.RadioButton04);
        final RadioButton rb5 = (RadioButton) findViewById(R.id.RadioButton05);
        final RadioButton rb6 = (RadioButton) findViewById(R.id.RadioButton06);
        final RadioButton rb7 = (RadioButton) findViewById(R.id.RadioButton07);
        final RadioButton rb8 = (RadioButton) findViewById(R.id.RadioButton08);

        Button addButton = (Button) findViewById(R.id.add);
        Button queryAllButton = (Button) findViewById(R.id.query_all);
        Button clearButton = (Button) findViewById(R.id.clear);
        Button deleteAllButton = (Button) findViewById(R.id.delete_all);

        Button queryButton = (Button) findViewById(R.id.query);
        Button deleteButton = (Button) findViewById(R.id.delete);
        Button updateButton = (Button) findViewById(R.id.update);

        final DBAdapter dbAdepter = new DBAdapter(this);
        dbAdepter.open();


        Button.OnClickListener buttonListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.add:
                        Properties properties = new Properties();
                        properties.setName(nameText.getText().toString());
                        if (rb1.isChecked()) {
                            properties.setType(rb1.getText().toString());
                        } else if(rb2.isChecked()){
                            properties.setType(rb2.getText().toString());
                        }else if(rb3.isChecked()){
                            properties.setType(rb3.getText().toString());
                        }else if(rb4.isChecked()){
                            properties.setType(rb4.getText().toString());
                        }else if(rb5.isChecked()){
                            properties.setType(rb5.getText().toString());
                        }else if(rb6.isChecked()){
                            properties.setType(rb6.getText().toString());
                        }else if(rb7.isChecked()){
                            properties.setType(rb7.getText().toString());
                        }else if(rb8.isChecked()){
                            properties.setType(rb8.getText().toString());
                        }

                        long colunm = dbAdepter.insert(properties);
                        if (colunm == -1) {
                            labelView.setText("添加过程错误！");
                        } else {
                            labelView.setText("成功添加数据，ID：" + String.valueOf(colunm));
                        }
                        return;
                    case R.id.query_all:
                        Properties[] properties1 = dbAdepter.queryAllData();
                        if (properties1 == null) {
                            labelView.setText("数据库中没有数据");
                            return;
                        }
                        labelView.setText("数据库：");
                        String msg = "";
                        for (int i = 0; i < properties1.length; i++) {
                            msg += properties1[i].toString() + "\n";
                        }
                        displayView.setText(msg);
                        return;
                    case R.id.clear:
                        displayView.setText("");
                        return;
                    case R.id.delete_all:
                        dbAdepter.deleteAllData();
                        msg = "数据全部删除";
                        labelView.setText(msg);
                        return;
                    case R.id.query:
                        int id = Integer.parseInt(idEntry.getText().toString());
                        Properties[] users1 = dbAdepter.queryOneData(id);
                        if (users1 == null) {
                            labelView.setText("数据库中没有ID为" + String.valueOf(id) + "的数据");
                            return;
                        }
                        labelView.setText("数据库：");
                        displayView.setText(users1[0].toString());
                        return;
                    case R.id.delete:
                        id = Integer.parseInt(idEntry.getText().toString());
                        long result = dbAdepter.deleteOneData(id);
                        msg = "删除ID为" + idEntry.getText().toString() + "的数据" + (result > 0 ? "成功" : "失败");
                        labelView.setText(msg);
                        return;
                    case R.id.update:
                        Properties properties2 = new Properties();
                        properties2.setName(nameText.getText().toString());
                        if (rb1.isChecked()) {
                            properties2.setType(rb1.getText().toString());
                        } else if(rb2.isChecked()){
                            properties2.setType(rb2.getText().toString());
                        }else if(rb3.isChecked()){
                            properties2.setType(rb3.getText().toString());
                        }else if(rb4.isChecked()){
                            properties2.setType(rb4.getText().toString());
                        }else if(rb5.isChecked()){
                            properties2.setType(rb5.getText().toString());
                        }else if(rb6.isChecked()){
                            properties2.setType(rb6.getText().toString());
                        }else if(rb7.isChecked()){
                            properties2.setType(rb7.getText().toString());
                        }else if(rb8.isChecked()){
                            properties2.setType(rb8.getText().toString());
                        }

                        id = Integer.parseInt(idEntry.getText().toString());
                        long count = dbAdepter.updateOneData(id, properties2);
                        if (count == -1) {
                            labelView.setText("更新错误！");
                        } else {
                            labelView.setText("更新成功，更新数据" + String.valueOf(count) + "条");
                        }
                        return;
                }
            }};
        addButton.setOnClickListener(buttonListener);
        queryAllButton.setOnClickListener(buttonListener);
        clearButton.setOnClickListener(buttonListener);
        deleteAllButton.setOnClickListener(buttonListener);
        queryButton.setOnClickListener(buttonListener);
        deleteButton.setOnClickListener(buttonListener);
        updateButton.setOnClickListener(buttonListener);
    }


    //    Button encryptlist,signlist,reenc,resign,formenc,formsign,formreenc,formresign;
//    int modecode=0;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_rules);
//
//        encryptlist=findViewById(R.id.encryptlist_btn);
//        signlist=findViewById(R.id.signlist_btn);
//        reenc=findViewById(R.id.reenc_btn);
//        resign=findViewById(R.id.resign_btn);
//        formenc=findViewById(R.id.formenc_btn);
//        formsign=findViewById(R.id.formsign_btn);
//        formreenc=findViewById(R.id.formreenc_btn);
//        formresign=findViewById(R.id.formresign_btn);
//
//        encryptlist.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(), EncryptlistModify.class);
//                startActivity(i);
//            }
//        });
//
//        signlist.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(), SignlistModify.class);
//                startActivity(i);
//            }
//        });
//
//        reenc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(), ReencModify.class);
//                startActivity(i);
//            }
//        });
//
//        resign.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(), ResignModify.class);
//                startActivity(i);
//            }
//        });
//
//        formenc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(), FormencModify.class);
//                startActivity(i);
//            }
//        });
//
//        formsign.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(), FormsignModify.class);
//                startActivity(i);
//            }
//        });
//
//        formreenc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(), FormreencModify.class);
//                startActivity(i);
//            }
//        });
//
//        formresign.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(), FormresignModify.class);
//                startActivity(i);
//            }
//        });
//
//
//    }
}
