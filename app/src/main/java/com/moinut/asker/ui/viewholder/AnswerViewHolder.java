package com.moinut.asker.ui.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.moinut.asker.APP;
import com.moinut.asker.R;
import com.moinut.asker.config.Const;
import com.moinut.asker.model.bean.Answer;
import com.moinut.asker.model.network.RequestManager;
import com.moinut.asker.model.subscriber.SimpleSubscriber;
import com.moinut.asker.model.subscriber.SubscriberListener;

import java.util.Locale;

import retrofit2.adapter.rxjava.HttpException;

public class AnswerViewHolder extends BaseViewHolder<Answer> {

    private TextView author;
    private TextView content;
    private TextView date;
    private TextView likeNumber;
    private ImageView like;
    private ImageView dislike;
    private TextView authorType;

    public AnswerViewHolder(View itemView) {
        super(itemView);
        author = $(R.id.tv_author);
        content = $(R.id.tv_content);
        date = $(R.id.tv_date);
        likeNumber = $(R.id.tv_like_number);
        like = $(R.id.iv_like);
        dislike = $(R.id.iv_dislike);
        authorType = $(R.id.tv_type);
    }

    @Override
    public void setData(Answer data) {
        super.setData(data);
        String name = data.getAuthorName();
        if (name == null) {
            author.setText(R.string.no_name_user);
        } else {
            author.setText(data.getAuthorName());
        }
        content.setText(data.getContent());
        date.setText(data.getDateFormat());
        likeNumber.setText(String.format(Locale.getDefault(), "%d", data.getLikeNumber() - data.getDislikeNumber()));
        switch (data.getAuthorType()) {
            case Const.API_STUDENT:
                authorType.setText(R.string.student);
                authorType.setBackgroundResource(R.drawable.bg_student_type);
                break;
            case Const.API_TEACHER:
                authorType.setText(R.string.teacher);
                authorType.setBackgroundResource(R.drawable.bg_teacher_type);
                break;
            default:
                authorType.setText(R.string.wrong_user);
                authorType.setBackgroundResource(R.drawable.bg_un_know_type);
                break;
        }
        like.setOnClickListener(v -> {
            if (APP.getUser(getContext()) != null) {
                RequestManager.getInstance().likeAnswer(new SimpleSubscriber<>(getContext(), new SubscriberListener<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        likeNumber.setText(String.format(Locale.getDefault(), "%d", integer));
                    }

                    @Override
                    public void onError(Throwable e) {
                        onLikeError(e);
                    }
                }), APP.getUser(getContext()).getToken(), data.getId(), Const.API_LIKE);
            } else {
                Toast.makeText(getContext(), R.string.please_login, Toast.LENGTH_SHORT).show();
            }
        });
        dislike.setOnClickListener(v -> {
            if (APP.getUser(getContext()) != null) {
                RequestManager.getInstance().likeAnswer(new SimpleSubscriber<>(getContext(), new SubscriberListener<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        likeNumber.setText(String.format(Locale.getDefault(), "%d", integer));
                    }

                    @Override
                    public void onError(Throwable e) {
                        onLikeError(e);
                    }
                }), APP.getUser(getContext()).getToken(), data.getId(), Const.API_DISLIKE);
            } else {
                Toast.makeText(getContext(), R.string.please_login, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onLikeError(Throwable e) {
        e.printStackTrace();
        if (e instanceof HttpException) {
            if (((HttpException) e).code() == 401) {
                Toast.makeText(getContext(), R.string.token_out_date_login_again, Toast.LENGTH_SHORT).show();
            }
        }
        // 其他不想处理……
    }
}
