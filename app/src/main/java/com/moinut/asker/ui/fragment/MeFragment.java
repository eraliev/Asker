package com.moinut.asker.ui.fragment;


import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.moinut.asker.APP;
import com.moinut.asker.R;

public class MeFragment extends BaseQuestionFragment {


    @Override
    protected View inflateLayout(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_me, container, false);
    }

    @Override
    public void onRefresh() {
        if (APP.getUser(getContext()) != null) {
            getQuestionPresenter().onMyQuestionsRefresh(APP.getUser(getContext()).getToken());
        } else {
            Toast.makeText(getContext(), "请登录", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoadMore() {
        if (APP.getUser(getContext()) != null) {
            getQuestionPresenter().onMyQuestionsLoadMore(APP.getUser(getContext()).getToken());
        } else {
            Toast.makeText(getContext(), "请登录", Toast.LENGTH_SHORT).show();
        }
    }
}
