package cn.edu.bistu.mynote.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import cn.edu.bistu.mynote.R;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences pref;//定义一个SharedPreferences对象
    private SharedPreferences.Editor editor;
    private Button login;//登录按钮
    private EditText adminEdit;//用户名输入框
    private EditText passwordEdit;//密码输入框
    private RadioGroup rem_password;//是否保存密码复选框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //获取各组件或对象的实例
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        login=(Button)findViewById(R.id.login);
        adminEdit=(EditText)findViewById(R.id.adminEdit);
        passwordEdit=(EditText)findViewById(R.id.passwordEdit);
        rem_password=(RadioGroup) findViewById(R.id.rem_password);
        adminEdit.setText("wkp");
        //获取当前“是否保存密码”的状态
        final boolean isSave=pref.getBoolean("rem_password",false);
        //当“是否保存密码”勾选时，从SharedPreferences对象中读出保存的内容，并显示出来
        if(isSave){
            String account=pref.getString("account","");
            String password=pref.getString("password","");
            adminEdit.setText(account);
            passwordEdit.setText(password);
            //把光标移到文本末尾处
            adminEdit.setSelection(adminEdit.getText().length());
            passwordEdit.setSelection(passwordEdit.getText().length());
            rem_password.check(R.id.SavePassword);
        }
        //用户点击登录时的处理事件
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account=adminEdit.getText().toString();
                String password=passwordEdit.getText().toString();
                //用户名和密码正确
                editor=pref.edit();
                //“是否保存密码”勾选
                if(rem_password.getCheckedRadioButtonId()==R.id.SavePassword){
                    editor.putBoolean("rem_password",true);
                    editor.putString("account",account);
                    editor.putString("password",password);
                }
                else{
                    editor.clear();
                }
                //提交完成数据存储
                editor.apply();
                //显示登录成功并跳转到主界面活动
                Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("author",adminEdit.getText().toString().trim());
                startActivity(intent);
            }
        });
    }
}