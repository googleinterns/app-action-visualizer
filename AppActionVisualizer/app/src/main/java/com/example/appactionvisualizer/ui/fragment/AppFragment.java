package com.example.appactionvisualizer.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.databean.ActionType;
import com.example.appactionvisualizer.ui.adapter.AppRecyclerViewAdapter;

/**
 * A fragment of MainActivity
 * Displays apps with the same action type using recyclerview
 */
public class AppFragment extends Fragment {
  private final static String TAG = "AppFragment";

  private ActionType actionType;

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the
   * fragment (e.g. upon screen orientation changes).
   */
  public AppFragment(ActionType actionType) {
    this.actionType = actionType;
  }

  /**
   * @param pos create a specific type fragment instance
   * @return
   */
  public static AppFragment newInstance(int pos) {
    ActionType actionType = null;
    if(pos != 0) {
      actionType = ActionType.getActionTypeValue(pos - 1);
    }
    AppFragment fragment = new AppFragment(actionType);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_category_app_list, container, false);

    // Set the adapter
    if (view instanceof RecyclerView) {
      Context context = view.getContext();
      RecyclerView recyclerView = (RecyclerView) view;
      recyclerView.setLayoutManager(new LinearLayoutManager(context));
      recyclerView.setAdapter(new AppRecyclerViewAdapter(actionType, getContext()));
    }
    return view;
  }
}