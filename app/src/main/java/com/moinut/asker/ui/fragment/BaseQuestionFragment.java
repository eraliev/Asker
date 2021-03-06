package com.moinut.asker.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.moinut.asker.R;
import com.moinut.asker.config.Const;
import com.moinut.asker.event.AnswerEvent;
import com.moinut.asker.event.AskEvent;
import com.moinut.asker.event.ExitEvent;
import com.moinut.asker.event.LoginEvent;
import com.moinut.asker.model.bean.Question;
import com.moinut.asker.presenter.QuestionPresenter;
import com.moinut.asker.ui.activity.AnswerActivity;
import com.moinut.asker.ui.activity.MainActivity;
import com.moinut.asker.ui.adapter.QuestionAdapter;
import com.moinut.asker.ui.vu.IQuestionView;
import com.moinut.asker.utils.AnimationUtils;
import com.moinut.asker.utils.ScreenUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class BaseQuestionFragment extends BaseFragment implements
        RecyclerArrayAdapter.OnLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener,
        IQuestionView {

    public static final String TAG = BaseQuestionFragment.class.getSimpleName();

    @Bind(R.id.rv_questions)
    EasyRecyclerView mRecyclerView;

    protected float mFabBottom;
    protected QuestionAdapter mAdapter;
    protected QuestionPresenter mQuestionPresenter;

    public BaseQuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new QuestionAdapter(getContext());
        mQuestionPresenter = new QuestionPresenter(getContext(), this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflateLayout(inflater, container);
        ButterKnife.bind(this, view);
        return view;
    }

    protected abstract View inflateLayout(LayoutInflater inflater, @Nullable ViewGroup container);

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ScreenUtils.paddingToToolbarAndNavigationBar(mRecyclerView.getRecyclerView());
        mRecyclerView.getSwipeToRefresh().setProgressViewOffset(true, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 72, getResources().getDisplayMetrics()));

        mRecyclerView.setAdapterWithProgress(mAdapter);
        mRecyclerView.setRefreshListener(this);
        mRecyclerView.setRefreshingColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

        if (getActivity() instanceof MainActivity) {
            AnimationUtils.hideFabAndAppBarInRecyclerView(mRecyclerView.getRecyclerView(),
                    ((MainActivity) getActivity()).getFab(),
                    ((MainActivity)getActivity()).getAppBar());
        }

        mAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(getContext(), AnswerActivity.class);
            intent.putExtra(Const.INTENT_QUESTION, mAdapter.getItem(position));
            startActivity(intent);
        });
        mAdapter.setMore(R.layout.view_question_more, this);
        mAdapter.setNoMore(R.layout.view_question_nomore);
        mAdapter.setError(R.layout.view_question_empty).setOnClickListener(v -> mAdapter.resumeMore());

        onRefresh();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
        ButterKnife.unbind(this);
        mQuestionPresenter.onRelieveView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginEvent(LoginEvent event){
        Log.d(TAG, "onLoginEvent: ");
        onRefresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExitEvent(ExitEvent event){
        Log.d(TAG, "onExitEvent: ");
        onRefresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAskEvent(AskEvent event){
        Log.d(TAG, "onAskEvent: ");
        onRefresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAnswerEvent(AnswerEvent event){
        Log.d(TAG, "onAnswerEvent: ");
        onRefresh();
    }

    @Override
    public abstract void onRefresh();

    @Override
    public abstract void onLoadMore();

    @Override
    public void onRefreshEnd(List<Question> questions) {
        mAdapter.clear();
        mAdapter.addAll(questions);
    }

    @Override
    public void onLoadMoreEnd(List<Question> questions) {
        mAdapter.addAll(questions);
    }

    protected QuestionPresenter getQuestionPresenter() {
        return mQuestionPresenter;
    }

    protected QuestionAdapter getAdapter() {
        return mAdapter;
    }
}
