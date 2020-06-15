package com.notebook.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.notebook.bean.NoteBean;
import com.notebook.bean.User;

import java.util.ArrayList;

/**
 * @name: DBHelper
 * @date: 2020-05-18 10:19
 * @comment: 处理数据库操作类
 */
public class DBHelper extends SQLiteOpenHelper {

    private String TAG = "DBHelper";

    //用户表
    /*表名*/
    private final String TABLE_NAME_USER = "_user";
    /*id字段*/
    private final String VALUE_ID = "_id";
    private final String VALUE_NAME = "subject";
    private final String VALUE_PWD = "body";
    /*创建表语句 语句对大小写不敏感 create table 表名(字段名 类型，字段名 类型，…)*/
    private final String CREATE_USER = "create table " + TABLE_NAME_USER + "(" +
            VALUE_ID + " integer primary key," +
            VALUE_NAME + " text ," +
            VALUE_PWD + " text" +
            ")";

    //日记表
    /*表名*/
    private final String TABLE_NAME_NOTEBOOK = "_notebook";
    /*id字段*/
    public final String VALUE_NB_ID = "nb_id";
    private final String VALUE_NB_TITLE = "nb_title";
    private final String VALUE_NB_BODY = "nb_body";
    private final String VALUE_NB_TIME = "nb_time";
    private final String VALUE_USER_ID = "user_id";
    /*创建表语句 语句对大小写不敏感 create table 表名(字段名 类型，字段名 类型，…)*/
    private final String CREATE_NOTEBOOK = "create table " + TABLE_NAME_NOTEBOOK + "(" +
            VALUE_NB_ID + " integer primary key," +
            VALUE_NB_TITLE + " text ," +
            VALUE_USER_ID + " integer ," +
            VALUE_NB_BODY + " text ," +
            VALUE_NB_TIME + " integer" +
            ")";


    public DBHelper(Context context) {
        this(context, "notebook.db", null, 1);

        Log.e(TAG, "-------> MySqliteHelper");
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

        Log.e(TAG, "-------> MySqliteHelper");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //创建表
        db.execSQL(CREATE_USER);
        db.execSQL(CREATE_NOTEBOOK);

        Log.e(TAG, "-------> onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.e(TAG, "-------> onUpgrade" + "  oldVersion = " + oldVersion + "   newVersion = " + newVersion);

    }

    /**
     * 注册
     *
     * @param pwd
     * @param name
     * @return
     */
    public boolean register(String pwd, String name) {
        Cursor cursor = getWritableDatabase().query(TABLE_NAME_USER,
                null, VALUE_NAME + "=?" + " and " + VALUE_PWD + "=?",
                new String[]{name, pwd}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        ContentValues values = new ContentValues();
        values.put(VALUE_PWD, pwd);
        values.put(VALUE_NAME, name);
        //添加数据到数据库
        long index = getWritableDatabase().insert(TABLE_NAME_USER, null, values);
        getWritableDatabase().close();
        if (index != -1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 登录
     *
     * @param name
     * @param pwd
     * @return
     */

    public User login(String name, String pwd) {
        Cursor cursor = getWritableDatabase().query(TABLE_NAME_USER,
                null, VALUE_NAME + "=?" + " and " + VALUE_PWD + "=?",
                new String[]{name, pwd}, null, null, null);
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        User user = new User();
        user.id = cursor.getInt(cursor.getColumnIndex(VALUE_ID));
        user.name = cursor.getString(cursor.getColumnIndex(VALUE_NAME));
        user.pwd = cursor.getString(cursor.getColumnIndex(VALUE_PWD));
        cursor.close();
        getWritableDatabase().close();
        return user;
    }

    public boolean updateUser(String name, String pwd, int id) {
        ContentValues values = new ContentValues();
        values.put(VALUE_NAME, name);
        values.put(VALUE_PWD, pwd);

        //修改model的数据
        long index = getWritableDatabase().update(TABLE_NAME_USER, values, VALUE_ID + "=?", new String[]{"" + id});
        getWritableDatabase().close();
        if (index != -1) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 添加日记
     *
     * @param title
     * @param body
     * @return
     */
    public boolean addNote(String title, String body,int userId) {
        ContentValues values = new ContentValues();
        values.put(VALUE_NB_TITLE, title);
        values.put(VALUE_NB_BODY, body);
        values.put(VALUE_USER_ID, userId);
//        String currentDateTimeString =
//                new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US).format(new Date());
        java.util.Date writeTime = new java.util.Date();
        values.put(VALUE_NB_TIME, writeTime.getTime());
        //添加数据到数据库
        long index = getWritableDatabase().insert(TABLE_NAME_NOTEBOOK, null, values);
        getWritableDatabase().close();
        if (index != -1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除日记
     *
     * @param noteBean
     * @return
     */
    public boolean deleteNote(NoteBean noteBean) {
        long index = getWritableDatabase().delete(TABLE_NAME_NOTEBOOK, VALUE_NB_ID + "=?", new String[]{"" + noteBean.id});
        getWritableDatabase().close();
        if (index != -1) {
            return true;
        } else {
            return false;
        }
    }

    public boolean updateNote(NoteBean noteBean) {
        ContentValues values = new ContentValues();
        values.put(VALUE_NB_TITLE, noteBean.title);
        values.put(VALUE_NB_BODY, noteBean.body);
//        String currentDateTimeString =
//                new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US).format(new Date());
        java.util.Date writeTime = new java.util.Date();
        values.put(VALUE_NB_TIME, writeTime.getTime());

        //修改model的数据
        long index = getWritableDatabase().update(TABLE_NAME_NOTEBOOK, values, VALUE_NB_ID + "=?", new String[]{"" + noteBean.id});
        getWritableDatabase().close();
        if (index != -1) {
            return true;
        } else {
            return false;
        }
    }


    public ArrayList<NoteBean> getAllNotesByUserId(int userId) {
        //查询全部数据
        Cursor cursor = getWritableDatabase().query(TABLE_NAME_NOTEBOOK, null,
                VALUE_USER_ID + "=?", new String[]{"" + userId}, null, null, null);
        ArrayList<NoteBean> list = new ArrayList<>();
        if (cursor.getCount() > 0) {
            //移动到首位
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                NoteBean noteBean = new NoteBean();
                noteBean.id = cursor.getInt(cursor.getColumnIndex(VALUE_NB_ID));
                noteBean.title = cursor.getString(cursor.getColumnIndex(VALUE_NB_TITLE));
                noteBean.body = cursor.getString(cursor.getColumnIndex(VALUE_NB_BODY));
                noteBean.time = cursor.getLong(cursor.getColumnIndex(VALUE_NB_TIME));
                noteBean.userId = cursor.getInt(cursor.getColumnIndex(VALUE_USER_ID));
                list.add(noteBean);
                //移动到下一位
                cursor.moveToNext();
            }
        }
        cursor.close();
        getWritableDatabase().close();
        return list;
    }

    public NoteBean
    getNoteById(int id) {
        //查询全部数据
        Cursor cursor = getWritableDatabase().query(TABLE_NAME_NOTEBOOK, null,
                VALUE_NB_ID + "=?", new String[]{"" + id}, null, null, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            NoteBean noteBean = new NoteBean();
            noteBean.id = cursor.getInt(cursor.getColumnIndex(VALUE_NB_ID));
            noteBean.title = cursor.getString(cursor.getColumnIndex(VALUE_NB_TITLE));
            noteBean.body = cursor.getString(cursor.getColumnIndex(VALUE_NB_BODY));
            noteBean.time = cursor.getLong(cursor.getColumnIndex(VALUE_NB_TIME));
            noteBean.userId = cursor.getInt(cursor.getColumnIndex(VALUE_USER_ID));
            cursor.close();
            getWritableDatabase().close();
            return noteBean;
        } else {
            cursor.close();
            getWritableDatabase().close();
            return null;
        }
    }

}
