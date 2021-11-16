package cn.edu.bistu.mynote.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.edu.bistu.mynote.adapter.ListAdapter;
import cn.edu.bistu.mynote.Bean.Note;
import cn.edu.bistu.mynote.R;
import cn.edu.bistu.mynote.Utils.DBUtils;
import cn.edu.bistu.mynote.Utils.mySQLite;

public class MainActivity extends AppCompatActivity {
    private List<Note> noteList=new ArrayList<Note>();//用户信息列表
    private mySQLite mSQL;
    ListView listview;
    ListAdapter adapter;
    String author;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview = (ListView)findViewById(R.id.list);
        mSQL = new mySQLite(this, DBUtils.DATABASE_NAME,
                null,DBUtils.DATABASE_VERION);
        /**
         * 初始化用户数据
         */
        initMynote();
        /**
         * 写日记
         */
        insert();
        /**
         * 长按删除一条日记
         */
        delete();
        //退出登录
        myExit();
    }

    /**
     * 初始化用户数据
     */
    private void initMynote() {
        mSQL = new mySQLite(this,DBUtils.DATABASE_NAME,
                null,DBUtils.DATABASE_VERION);
        Intent intent = getIntent();
        author=intent.getStringExtra("author").toString();
        //显示所有记录
        showQueryData();

        //点击某一条记录时跳转编辑页面
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note nb = noteList.get(position);
                Intent intent = new Intent(MainActivity.this, RecordActivity.class);
                //将本条日记的全部信息传入下一个活动
                intent.putExtra("id",nb.getId());
                intent.putExtra("title",nb.getTitle());
                intent.putExtra("author",nb.getAuthor());
                intent.putExtra("date",nb.getDate());
                intent.putExtra("myContent",nb.getMyContent());
                intent.putExtra("myImage",nb.getMyImage());
                startActivityForResult(intent,1);
            }
        });
    }

    /**
     * 查询用户所有的记录，用listview显示
     */
    private void showQueryData() {
        if(noteList!=null){
            noteList.clear();
        }
        noteList = mSQL.query(author);
        adapter = new ListAdapter(this,R.layout.show_list,noteList);
        listview.setAdapter(adapter);
    }

    /**
     * 写日记
     */
    private void insert(){
        Button addData=findViewById(R.id.add_data);
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RecordActivity.class);
                //写日记时，需要初始化的信息有
                //intent.putExtra("id","");
                intent.putExtra("author",author);
                intent.putExtra("date",DBUtils.getTime());
                startActivityForResult(intent,1);
            }
        });
    }
    /**
     * 长按删除一条日记
     */
    private void delete(){
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final
                int position, long id) {
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setMessage("是否删除此记录")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Note nt = noteList.get(position);
                                if(mSQL.deleteData(nt.getId())){
                                    noteList.remove(position);
                                    String i=nt.getId();
                                    mSQL.deleteData(i);
                                    adapter.notifyDataSetChanged();//刷新界面
                                    Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();//取消对话框
                            }
                        });
                dialog = builder.create();
                dialog.show();
                return true;
            }
        });
    }

    /**
     * 退出登录
     */
    private void myExit(){
        Button btn_exit=findViewById(R.id.exit);
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            //刷新listview
            showQueryData();
        }
    }
}
//cd /data/data/cn.edu.bistu.mynote/databases