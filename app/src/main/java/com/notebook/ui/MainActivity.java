package com.notebook.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.notebook.R;
import com.notebook.UserManager;
import com.notebook.bean.NoteBean;
import com.notebook.bean.User;
import com.notebook.db.DBHelper;
import com.notebook.utils.SPUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    DBHelper dbHelper;
    private DrawerLayout drawer;
    private ImageView iv_menu;
    private ListView all_note;
    private FloatingActionButton fab_add;
    private NavigationView nav_view;
    private User user;
    private NoteAdapter mAdapter;
    private ArrayList<NoteBean> mNoteList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawer = findViewById(R.id.drawer);
        iv_menu = findViewById(R.id.iv_menu);
        all_note = findViewById(R.id.all_note);
        fab_add = findViewById(R.id.fab_add);
        nav_view = findViewById(R.id.nav_view);
        user = UserManager.instance.getUser();
        dbHelper = new DBHelper(this);
        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer();
            }
        });
        TextView textView = nav_view.getHeaderView(0).findViewById(R.id.tv_app_name);
        textView.setText(user.name);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.nav_exit) {
                    cloaseDrawer();
                    AlertDialog.Builder bulder = new AlertDialog.Builder(MainActivity.this);
                    bulder.setTitle("提示");
                    bulder.setMessage("是否退出登录?");
                    bulder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    bulder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SPUtil.put(MainActivity.this, "name", "");
                            SPUtil.put(MainActivity.this, "pwd", "");
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                            dialog.dismiss();
                        }
                    });
                    bulder.create().show();

                } else if (menuItem.getItemId() == R.id.nav_update) {
                    cloaseDrawer();
                    Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, EditNoteActivity.class);
                startActivity(i);
            }
        });
        mAdapter = new NoteAdapter(mNoteList, this);
        all_note.setAdapter(mAdapter);
        all_note.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NoteBean noteBean = mAdapter.getItem(position);
                Intent i = new Intent(MainActivity.this, EditNoteActivity.class);
                i.putExtra(dbHelper.VALUE_NB_ID, noteBean.id);
                startActivity(i);
            }
        });
        all_note.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final  NoteBean noteBean = mAdapter.getItem(position);
                AlertDialog.Builder bulder = new AlertDialog.Builder(MainActivity.this);
                bulder.setTitle("提示");
                bulder.setMessage("是否删除?");
                bulder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                bulder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean isDel = dbHelper.deleteNote(noteBean);
                        if(isDel){
                            Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            mNoteList.clear();
                            mNoteList.addAll(dbHelper.getAllNotesByUserId(user.id));
                            mAdapter.notifyDataSetChanged();
                        }else {
                            Toast.makeText(MainActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
                bulder.create().show();
                return true;
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        mNoteList.clear();
        mNoteList.addAll(dbHelper.getAllNotesByUserId(user.id));
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 打开侧滑
     */
    private void openDrawer() {
        drawer.openDrawer(nav_view);
    }

    /**
     * 关闭侧滑
     */
    private void cloaseDrawer() {
        drawer.closeDrawers();
    }


    private class NoteAdapter extends BaseAdapter {

        private ArrayList<NoteBean> noteList = new ArrayList<>();
        private LayoutInflater mInflater;

        public NoteAdapter(ArrayList<NoteBean> noteList, Context context) {
            this.noteList = noteList;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return noteList.size();
        }

        @Override
        public NoteBean getItem(int position) {
            return noteList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.item_note, null);
            TextView title = view.findViewById(R.id.tv_title);
            NoteBean note = getItem(position);
            title.setText(note.title);
            TextView time = view.findViewById(R.id.tv_date);
            String currentDateTimeString =
                    new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US).format(new Date(note.time));
            time.setText("创建于 " + currentDateTimeString);
            return view;
        }
    }
}