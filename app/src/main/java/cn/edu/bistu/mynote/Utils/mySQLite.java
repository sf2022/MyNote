package cn.edu.bistu.mynote.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.edu.bistu.mynote.Bean.Note;

public class mySQLite extends SQLiteOpenHelper {
    //建库语句
    public static final String CREATE_NOTE = "create table Note ("
            + "id integer primary key autoincrement, title text, author text, "
            + "date text, myContent text,myImage text)";
    private Context mContext;

    public mySQLite(Context context, String name,
                          SQLiteDatabase.CursorFactory
                                  factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NOTE);
        Toast.makeText(mContext,
                "数据库创建成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Note");
        //db.execSQL(CREATE_NOTE);
        onCreate(db);
    }

    /**
     * 删除日记
     * @param id
     * @return
     */
    public boolean deleteData(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (db.delete(DBUtils.DATABASE_TABLE, "id=?", new String[]{id})>0){
            return true;
        }
        return false;
    }

    /**
     * 查询当前用户的所有日记
     */
    public List<Note> query(String author){
        List<Note> noteList=new ArrayList<Note>();
        SQLiteDatabase db = this.getWritableDatabase();
        // 查询Book表中所有的数据
        Cursor cursor = db.query("Note", null, "author=?",
                new String[]{author}, null, null, null);
        //移至第一条记录
        if (cursor.moveToFirst()) {
            do {
                Note nb = new Note();
                // 遍历Cursor对象，取出数据,放到noteList
                //获取id
                int i=cursor.getColumnIndex("id");
                String id = cursor.getString(i);
                nb.setId(id);
                //获取标题
                i=cursor.getColumnIndex("title");
                String title = cursor.getString(i);
                nb.setTitle(title);
                //获取作者
                i=cursor.getColumnIndex("author");
                //String author = cursor.getString(i);
                nb.setAuthor(author);
                //获取日期
                i=cursor.getColumnIndex("date");
                String date=cursor.getString(i);
                nb.setDate(date);
                //获取内容
                i=cursor.getColumnIndex("myContent");
                String myContent=cursor.getString(i);
                nb.setMyContent(myContent);
                //获取图片
                i=cursor.getColumnIndex("myImage");
                String myImage=cursor.getString(i);
                nb.setMyImage(myImage);
                noteList.add(nb);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return noteList;
    }

    /**
     * 更新记录
     */
    public boolean updateData(Note nt){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title",nt.getTitle());
        values.put("date", nt.getDate());
        values.put("myContent", nt.getMyContent());
        values.put("myImage", nt.getMyImage());
        if(db.update(DBUtils.DATABASE_TABLE, values, "id = ?",
                new String[]{nt.getId()})>0){
            return true;
        }
        return false;
    }

    /**
     *插入一条日记
     */
    public boolean insertData(Note nt){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        // 开始组装数据
        values.put("title",nt.getTitle());
        values.put("author", nt.getAuthor());
        values.put("date", nt.getDate());
        values.put("myContent", nt.getMyContent());
        values.put("myImage", nt.getMyImage());
        if (db.insert(DBUtils.DATABASE_TABLE, null, values)>0){
            return true;
        }
        return false;
    }
}
