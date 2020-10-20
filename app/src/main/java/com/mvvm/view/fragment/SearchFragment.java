package com.mvvm.view.fragment;


import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;

import com.haerul.swipeviewpager.R;
import com.mvvm.adapter.SearchAdapter;
import com.mvvm.model.User;
import com.mvvm.retrofit.RetrofitClient;
import com.mvvm.view_model.SearchViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.text.Editable;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    View view;
    Context appContext;
    SearchViewModel searchViewModel;
    //widgets
    private EditText mSearchParam;
    private RecyclerView mListView;

    //vars
    private List<User> mUserList;
    private SearchAdapter mAdapter;

    public SearchFragment(Context contx) {
        // Required empty public constructor
        this.appContext = contx;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);

        mSearchParam = (EditText) view.findViewById(R.id.search);
        mListView = (RecyclerView) view.findViewById(R.id.search_listView);
        mListView.setLayoutManager(new LinearLayoutManager(appContext));

        hideSoftKeyboard();
        initTextListener();

        // init ViewModel
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);

        return view;
    }


    private void searchForMatch(String keyword){
        mUserList.clear();
        //update the users list view
        if(keyword.length() <= 2){
            mUserList.clear();
            updateUsersList();
        }else {
            mUserList = searchViewModel.getUserList(keyword);
            updateUsersList();

        }
    }

    private void updateUsersList(){

        mAdapter = new SearchAdapter(mUserList, appContext);

        mListView.setAdapter(mAdapter);


    }

    private void hideSoftKeyboard(){
        if(getActivity().getCurrentFocus() != null){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void initTextListener(){

        mUserList = new ArrayList<>();

        mSearchParam.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = mSearchParam.getText().toString().toLowerCase(Locale.getDefault());
                searchForMatch(text);
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
    }

}
