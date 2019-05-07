package com.paul.simpletools.classbox.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.paul.simpletools.R;
import com.paul.simpletools.classbox.SuperBoxRequest;
import com.paul.simpletools.classbox.model.SuperLesson;
import com.paul.simpletools.classbox.model.SuperResult;
import com.paul.simpletools.classbox.model.SuperTerm;
import com.paul.simpletools.classbox.utils.SuperUtils;
import com.paul.simpletools.classbox.api.SuperEncrypt;
import com.paul.simpletools.classbox.model.SuperProfile;
import com.paul.simpletools.classbox.utils.SuperParser;
import com.paul.simpletools.dataBase.TermData;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.litepal.LitePal;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends AppCompatActivity {

    private static final String TAG = "AuthActivity";

    //本地数据存储
    public static final String SAVE_SHARED_NAME = "save_shared_name";
    public static final String SAVE_NUMBER = "save_number";
    public static final String SAVE_PASSWORD = "save_password";
    public static final String SAVE_NAME = "save_username";
    public static final String SAVE_SCHOOL = "save_school";

    /**
     * 类型
     */
    public static final String FLAG_TYPE = "key_type";

    /**
     * 是否保存用户信息，下次自动登录
     */
    public static final String FLAG_IS_SAVE_WHEN_SCAN = "key_is_save_when_scan";

    /**
     * 是否清空本地用户信息
     */
    public static final String FLAG_IS_CLEAR = "key_is_clear";

    /**
     * 传递的参数：超表课程码表示的内容
     */
    public static final String PARAMS_SCAN_URL = "params_scan_url";

    /**
     * 生成二维码时需要该参数
     */
    public static final String PARAMS_SCHEDULE_OBJS = "params_schedule_objs";

    /**
     * 返回的状态，用于标记从本页面返回
     */
    public static final int RESULT_STATUS = 200;

    /**
     * 返回的对象：SuperResult
     */
    public static final String RESULT_OBJ = "result_obj";

    private SuperProfile profile;

    //类型：导入、扫码
    public static final int TYPE_IMPORT = 0;
    public static final int TYPE_SCAN = 1;
    public static final int TYPE_MAKE_CODEIMAGE = 1;

    private int type = TYPE_SCAN;

    private EditText numberEdit;
    private EditText passwordEdit;

    private boolean isSaveInfo;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private String url;

    private LinearLayout loadLayout;
    private LinearLayout hasLocalLayout;
    private TextView localAccountText;
    private ListView chooseTermListView;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> list;
    private int term_id=100;
    private String termName="@";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)//透明状态栏
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        // 创建状态栏的管理实例
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // 激活状态栏设置
        tintManager.setStatusBarTintEnabled(true);
        // 激活导航栏设置
        tintManager.setNavigationBarTintEnabled(true);
        // 设置一个颜色给系统栏
        tintManager.setTintColor(Color.parseColor("#FFFFFF"));
        setContentView(R.layout.activity_auth);
        initView();
        getExtras();
        loadLocalData();
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK ) {
            //do something.
            return true;
        }else {
            return super.dispatchKeyEvent(event);
        }
    }

    public void showDialog() {
        loadLayout.setVisibility(View.VISIBLE);
    }

    public void hideDialog() {
        loadLayout.setVisibility(View.GONE);
    }

    /**
     * TYPE_IMPORT模式下不加载本地数据
     */
    private void loadLocalData() {
        if (type != TYPE_IMPORT) {
            String numberEncrypt = sp.getString(SAVE_NUMBER, null);
            String passwordEncrypt = sp.getString(SAVE_PASSWORD, null);
            String userName = sp.getString(SAVE_NAME, "超表账户");
            String schoolName = sp.getString(SAVE_SCHOOL, "未知学校");
            if (numberEncrypt != null && passwordEncrypt != null) {
                localAccountText.setText(userName + "(" + schoolName + ")");
                hasLocalLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initView() {
        sp = getSharedPreferences(SAVE_SHARED_NAME, MODE_PRIVATE);
        editor = sp.edit();

        numberEdit = findViewById(R.id.id_auth_number);
        passwordEdit = findViewById(R.id.id_auth_password);

        loadLayout = findViewById(R.id.id_auth_loadlayout);
        hasLocalLayout = findViewById(R.id.id_auth_haslocal_layout);
        localAccountText = findViewById(R.id.id_auth_tel);
        chooseTermListView=findViewById(R.id.id_auth_chooseterm_listview);
        list=new ArrayList<>();
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        chooseTermListView.setAdapter(arrayAdapter);

        findViewById(R.id.id_auth_parsebtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numberEncrypt = sp.getString(SAVE_NUMBER, null);
                String passwordEncrypt = sp.getString(SAVE_PASSWORD, null);
                hasLocalLayout.setVisibility(View.GONE);
                onLogin(numberEncrypt, passwordEncrypt);
            }
        });

        findViewById(R.id.id_auth_clearbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasLocalLayout.setVisibility(View.GONE);
                clearLocalData();
            }
        });

        findViewById(R.id.id_auth_loginbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prereLogin();
            }
        });

        /*findViewById(R.id.id_auth_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/

        chooseTermListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(profile!=null){
                    List<SuperTerm> terms=profile.getTermList();
                    if(terms!=null){
                        SuperTerm superTerm=terms.get(position);
                        if(superTerm!=null){
                            term_id=position;
                            Log.d("获取点击位置",term_id+"");
                            getCourseForSuper(superTerm.getBeginYear()+"",superTerm.getTerm()+"");
                        }
                    }
                }
            }
        });
    }

    public void getExtras() {
        Intent intent = getIntent();
        type = intent.getIntExtra(FLAG_TYPE, TYPE_SCAN);
        url = intent.getStringExtra(PARAMS_SCAN_URL);
        isSaveInfo = intent.getBooleanExtra(FLAG_IS_SAVE_WHEN_SCAN, true);
        if (type == TYPE_IMPORT) isSaveInfo = false;
        boolean isClear = intent.getBooleanExtra(FLAG_IS_CLEAR, false);
        if (isClear) clearLocalData();
    }

    public void prereLogin() {
        final String number = numberEdit.getText().toString();
        final String password = passwordEdit.getText().toString();

        if (TextUtils.isEmpty(number) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入账号和密码!", Toast.LENGTH_SHORT).show();
            return;
        }

        String numberEncrypt = SuperEncrypt.encrypt(number);
        String passwordEncrypt = SuperEncrypt.encrypt(password);

        onLogin(numberEncrypt, passwordEncrypt);
    }

    public void clearLocalData() {
        editor.clear();
        editor.commit();
        getSharedPreferences("cookies_app", Context.MODE_PRIVATE).edit().clear().commit();
    }

    public void onLogin(final String numberEncrypt, final String passwordEncrypt) {
        showDialog();
        SuperBoxRequest.loginForSuper(this, numberEncrypt, passwordEncrypt,
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        ResponseBody body = response.body();
                        hideDialog();
                        try {
                            String result = body.string();
                            profile = SuperParser.parseLoginResult(result);
                            if (profile == null) {
                                Toast.makeText(AuthActivity.this, "解析异常:" + result, Toast.LENGTH_SHORT).show();
                                clearLocalData();
                            } else if (!profile.isSuccess()) {
                                Toast.makeText(AuthActivity.this, "Error:" + profile.getErrMsg(), Toast.LENGTH_SHORT).show();
                                clearLocalData();
                            } else {

                                String school = profile.getSchoolName();
                                String userName = profile.getNickName();
                                if (isSaveInfo) {
                                    editor.putString(SAVE_NUMBER, numberEncrypt);
                                    editor.putString(SAVE_PASSWORD, passwordEncrypt);
                                    editor.putString(SAVE_SCHOOL, school);
                                    editor.putString(SAVE_NAME, userName);
                                    editor.commit();
                                }

                                if (type == TYPE_SCAN) {
                                    scanCode();
                                } else {
                                    chooseImportTerm(profile);
                                }
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        hideDialog();
                    }
                });
    }

    private void chooseImportTerm(SuperProfile profile) {
        if (profile==null) return;
        List<SuperTerm> terms=profile.getTermList();
        if(terms!=null){
            chooseTermListView.setVisibility(View.VISIBLE);
            list.clear();
            List<String> data=new ArrayList<>();
            for(SuperTerm term:terms){
                if(term!=null){
                    String t="未知学期";
                    if(term.getTerm()==1) t="秋季学期（上学期）";
                    if(term.getTerm()==2) t="春季学期（下学期）";

                    data.add(term.getBeginYear()+""+t);
                }
            }
            list.addAll(data);

            arrayAdapter.notifyDataSetChanged();
            if(terms.size()==0){
                Toast.makeText(this,"该用户未创建学期",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this,"terms is null",Toast.LENGTH_SHORT).show();
        }
    }

    public void getCourseForSuper(String beginYear, final String term) {
        showDialog();
        SuperBoxRequest.getCourseForSuper(this, beginYear,
                term, new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {

                            String result = response.body().string();
                            Log.d(TAG, "onResponse: " + result);
                            SuperResult superResult = SuperParser.parseLessonResult(result);
                            superResult.setProfile(profile);
                            hideDialog();
                            //superresult
                            if(term_id>=0&&term_id<list.size())
                                termName=list.get(term_id);
                            Log.d("获取点击位置","字符串："+termName);
                            LitePal.initialize(AuthActivity.this);
                            if(!termName.equals("@")) {
                                for (SuperLesson item : superResult.getLessons()) {
                                    item.setSemester(termName);
                                }

                                TermData termData=new TermData();
                                termData.setTermName(termName);
                                List<TermData> termDatas=LitePal.where("termName=?",termName).find(TermData.class);
                                Log.d("Auth",termDatas.size()+"");
                                if(termDatas.size()!=0)
                                {
                                     for(TermData item:termDatas)
                                     {
                                         item.delete();
                                     }
                                     Toast.makeText(AuthActivity.this,"重复添加！已覆盖原表！",Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                termData.save();

                            }
                            else
                            {
                                TermData termData=new TermData();
                                termData.setTermName("默认"+superResult.getLessons().get(0).getSemester());
                                termData.save();

                            }
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(RESULT_OBJ, (Serializable) superResult);
                            intent.putExtras(bundle);
                            AuthActivity.this.setResult(RESULT_STATUS, intent);
                            finish();

                        } catch (IOException e) {
                            hideDialog();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                        hideDialog();
                    }
                });
    }

    public void scanCode() {
        if (TextUtils.isEmpty(url)) return;
        String[] params = SuperUtils.parseUrl(url);
        if (params == null || params.length < 3) return;

        showDialog();
        SuperBoxRequest.scanCodeForSuper(this, params[0], params[1],
                params[2], new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String result = response.body().string();
                            SuperResult superResult = SuperParser.parseScanResult(result);
                            superResult.setProfile(profile);
                            hideDialog();
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(RESULT_OBJ, (Serializable) superResult);
                            intent.putExtras(bundle);
                            AuthActivity.this.setResult(RESULT_STATUS, intent);
                            finish();
                        } catch (IOException e) {
                            hideDialog();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        hideDialog();
                        t.printStackTrace();
                    }
                });
    }
}
