package com.notebook.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.notebook.R;
import com.notebook.UserManager;
import com.notebook.bean.NoteBean;
import com.notebook.bean.User;
import com.notebook.db.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @name: EditNoteActivity
 * @date: 2020-05-18 14:03
 * @comment: 日记编辑以及添加页面
 */
public class EditNoteActivity extends AppCompatActivity {
    DBHelper dbHelper;
    private User user;
    private NoteBean oldNote;
    private ImageView iv_back;
    private ImageView iv_save;
    private TextView tv_title;
    private TextView tv_time;
    private EditText ed_title;
    private EditText ed_body;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_edit_note);
        dbHelper = new DBHelper(this);
        user = UserManager.instance.getUser();
        iv_back = findViewById(R.id.iv_back);
        iv_save = findViewById(R.id.iv_save);
        tv_title = findViewById(R.id.tv_title);
        tv_time = findViewById(R.id.tv_time);
        ed_title = findViewById(R.id.ed_title);
        ed_body = findViewById(R.id.ed_body);
        Intent intent = getIntent();
        if (intent != null) {
            int id = intent.getIntExtra(dbHelper.VALUE_NB_ID, -1);
            if (id > 0) {
                oldNote = dbHelper.getNoteById(id);
            }
        }
        if (oldNote != null) {
            String currentDateTimeString =
                    new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US).format(new Date(oldNote.time));
            tv_time.setText("创建于 " + currentDateTimeString);
            tv_title.setText("编辑日记");
            ed_title.setText(oldNote.title);
            ed_body.setText(oldNote.body);
        } else {
            tv_title.setText("添加日记");
            String currentDateTimeString =
                    new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US).format(new Date());
            tv_time.setText(currentDateTimeString);
        }
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = ed_title.getText().toString().trim();
                String body = ed_body.getText().toString().trim();
                if(TextUtils.isEmpty(title)){
                    Toast.makeText(EditNoteActivity.this, "标题不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(oldNote==null){
                    //新建
                    boolean isAdd= dbHelper.addNote(title,body,user.id);
                    if(isAdd){
                        Toast.makeText(EditNoteActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        Toast.makeText(EditNoteActivity.this, "保存失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    //编辑
                    oldNote.title=title;
                    oldNote.body=body;
                    boolean isUpdate= dbHelper.updateNote(oldNote);
                    if(isUpdate){
                        Toast.makeText(EditNoteActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        Toast.makeText(EditNoteActivity.this, "更新失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
