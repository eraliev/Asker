package com.moinut.asker.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ViewStubCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.moinut.asker.APP;
import com.moinut.asker.R;
import com.moinut.asker.config.Const;
import com.moinut.asker.model.bean.Student;
import com.moinut.asker.model.bean.Teacher;
import com.moinut.asker.model.bean.User;
import com.moinut.asker.presenter.UserInfoPresenter;
import com.moinut.asker.ui.vu.IUserInfoView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

@SuppressWarnings("WeakerAccess")
public class UserDetailActivity extends BaseActivity implements IUserInfoView {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.btn_exit)
    Button mBtnExit;
    @Bind(R.id.edit_name)
    EditText mEditName;
    @Bind(R.id.rb_sex_male)
    AppCompatRadioButton mRbSexMale;
    @Bind(R.id.rb_sex_female)
    AppCompatRadioButton mRbSexFemale;
    @Bind(R.id.rg_sex)
    RadioGroup mRgSex;
    @Bind(R.id.edit_tel)
    EditText mEditTel;
    @Bind(R.id.edit_email)
    EditText mEditEmail;
    @Bind(R.id.view_stub_student_info)
    ViewStubCompat mStudentViewStub;
    @Bind(R.id.view_stub_teacher_info)
    ViewStubCompat mTeacherViewStub;

    // Student
    private EditText mEditYear;
    private EditText mEditMajor;
    // Teacher
    private EditText mEditRealName;
    // Comment
    private EditText mEditCollege;
    private EditText mEditAcademy;

    private String mToken;
    private UserInfoPresenter mUserInfoPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        ButterKnife.bind(this);
        getType();
        initView();
        mUserInfoPresenter.get(mToken);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUserInfoPresenter.onRelieveView();
    }

    private void getType() {
        User currentUser;
        if ((currentUser = APP.getUser(this)) != null) {
            mUserInfoPresenter = new UserInfoPresenter(this, this, currentUser.getType());
            mToken = currentUser.getToken();
        } else {
            Toast.makeText(this, "未登录, 你怎么进来的!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initView() {
        initToolbar();
        mBtnExit.setOnClickListener(v -> {
            APP.exitUser(this);
            finish();
        });
        if (mUserInfoPresenter.getUserType().equals(Const.API_STUDENT)) {
            mStudentViewStub.inflate();
            mEditYear = (EditText) findViewById(R.id.edit_year);
            mEditMajor = (EditText) findViewById(R.id.edit_major);
        } else {
            mTeacherViewStub.inflate();
            mEditRealName = (EditText) findViewById(R.id.edit_real_name);
        }
        mEditCollege = (EditText) findViewById(R.id.edit_college);
        mEditAcademy = (EditText) findViewById(R.id.edit_academy);
    }

    private void initToolbar() {
        mToolbar.setTitle("Detail");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(v -> finish());
    }

    private void showStudent(Student student) {
        mEditName.setText(student.getNickName());
        mEditAcademy.setText(student.getAcademy());
        mEditCollege.setText(student.getCollege());
        mEditEmail.setText(student.getEmail());
        mEditTel.setText(student.getTel());
        mEditMajor.setText(student.getMajor());
        if (student.getYear() != 0) mEditYear.setText(student.getYear() + "");
        if (student.getSex().equals(Const.API_FEMALE)) {
            mRbSexFemale.setChecked(true);
        } else {
            mRbSexMale.setChecked(true);
        }
    }

    private void showTeacher(Teacher teacher) {
        mEditName.setText(teacher.getNickName());
        mEditAcademy.setText(teacher.getAcademy());
        mEditCollege.setText(teacher.getCollege());
        mEditEmail.setText(teacher.getEmail());
        mEditTel.setText(teacher.getTel());
        mEditRealName.setText(teacher.getRealName());
        if (teacher.getSex().equals(Const.API_FEMALE)) {
            mRbSexFemale.setChecked(true);
        } else {
            mRbSexMale.setChecked(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_done:
                send();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void send() {
        if (mUserInfoPresenter.getUserType().equals(Const.API_STUDENT) && mUserInfoPresenter.getUser() != null) {
            Student student;
            try {
                student = (Student) ((Student) mUserInfoPresenter.getUser()).clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                return;
            }

            String nickName = mEditName.getText().toString();
            if (!nickName.isEmpty()) student.setNickName(nickName);

            student.setSex(mRbSexFemale.isChecked() ? Const.API_FEMALE : Const.API_MALE);

            String tel = mEditTel.getText().toString();
            if (!tel.isEmpty()) student.setTel(tel);

            String email = mEditEmail.getText().toString();
            Pattern pattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$",Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                mEditEmail.setError("邮箱格式不对");
                return;
            }
            if (!email.isEmpty()) student.setEmail(email);

            String year = mEditYear.getText().toString();
            pattern = Pattern.compile("^\\d{4}$");
            matcher = pattern.matcher(year);
            if (!matcher.matches()) {
                mEditYear.setError("年级格式不对, 请输入4位数字的入学年份");
                return;
            }
            if (!year.isEmpty()) student.setYear(Integer.parseInt(year));

            String college = mEditCollege.getText().toString();
            if (!college.isEmpty()) student.setCollege(college);

            String academy = mEditAcademy.getText().toString();
            if (!academy.isEmpty()) student.setAcademy(academy);

            String major = mEditMajor.getText().toString();
            if (!major.isEmpty()) student.setMajor(major);

            mUserInfoPresenter.updateStudent(mToken, student);
            return;
        }
        if (mUserInfoPresenter.getUserType().equals(Const.API_TEACHER) && mUserInfoPresenter.getUser() != null) {
            Teacher teacher;
            try {
                teacher = (Teacher) ((Teacher) mUserInfoPresenter.getUser()).clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                return;
            }

            String nickName = mEditName.getText().toString();
            if (!nickName.isEmpty()) teacher.setNickName(nickName);

            teacher.setSex(mRbSexFemale.isChecked() ? Const.API_FEMALE : Const.API_MALE);

            String tel = mEditTel.getText().toString();
            if (!tel.isEmpty()) teacher.setTel(tel);

            String email = mEditEmail.getText().toString();
            Pattern pattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$",Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                mEditEmail.setError("邮箱格式不对");
                return;
            }
            if (!email.isEmpty()) teacher.setEmail(email);

            String college = mEditCollege.getText().toString();
            if (!college.isEmpty()) teacher.setCollege(college);

            String academy = mEditAcademy.getText().toString();
            if (!academy.isEmpty()) teacher.setAcademy(academy);

            String realName = mEditRealName.getText().toString();
            if (!realName.isEmpty()) teacher.setRealName(realName);

            mUserInfoPresenter.updateTeacher(mToken, teacher);
        }
    }

    @Override
    public void onGetSuccess(User user) {
        dismissProgress();
        if (user instanceof Student) showStudent((Student) user);
        else if (user instanceof Teacher) showTeacher((Teacher) user);
    }

    @Override
    public void onGetError(String info) {
        dismissProgress();
        showDialog("ERROR", info);
    }


    @Override
    public void onGetProgress() {
        showProgress("获取信息中");
    }

    @Override
    public void onUpdateSuccess(String info) {
        dismissProgress();
        showDialog("SUCCESS", info);
    }

    @Override
    public void onUpdateError(String info) {
        dismissProgress();
        showDialog("ERROR", info);
    }

    @Override
    public void onUpdateProgress() {
        showProgress("更新信息中");
    }

}
