package com.ithomaslin.caffeinista;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.webianks.library.PopupBubble;

import java.util.List;

import top.wefor.circularanim.CircularAnim;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private PopupBubble mPopupBubble;
    private OnFragmentInteractionListener mListener;
    private DatabaseReference mFirebaseDatabaseReference;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<Barista, BaristaViewHolder> mFirebaseAdapter;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPopupBubble = (PopupBubble) view.findViewById(R.id.popup_bubble);
        mPopupBubble.setPopupBubbleListener(new PopupBubble.PopupBubbleClickListener() {
            @Override
            public void bubbleClicked(Context context) {

            }
        });

        mLinearLayoutManager = new LinearLayoutManager(view.getContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Barista, BaristaViewHolder>(
                Barista.class,
                R.layout.card_item,
                BaristaViewHolder.class,
                mFirebaseDatabaseReference.child("users").orderByKey()) {
            @Override
            protected void populateViewHolder(BaristaViewHolder viewHolder, Barista model, int position) {
                viewHolder.mTextView.setText(model.getName());
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }

            @Override
            public void onAttachedToRecyclerView(RecyclerView recyclerView) {
                super.onAttachedToRecyclerView(recyclerView);
            }

            @Override
            public void onBindViewHolder(BaristaViewHolder viewHolder, int position) {
                super.onBindViewHolder(viewHolder, position);
            }

        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int baristaCount = mFirebaseAdapter.getItemCount();
                int firstVisiblePosition = mLinearLayoutManager.findFirstVisibleItemPosition();

                if (firstVisiblePosition == 1 ||
                        (positionStart >= baristaCount && firstVisiblePosition == positionStart)) {
                    mRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Log.d(TAG, String.valueOf(position));
                        final Intent intent = new Intent(getContext(), BaristaDetailActivity.class);
                        CircularAnim.fullActivity(getActivity(), view)
                                .colorOrImageRes(R.color.primary)
                                .go(new CircularAnim.OnAnimationEndListener() {
                                    @Override
                                    public void onAnimationEnd() {
                                        startActivity(intent);
                                    }
                                });
                    }
                })
        );

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mFirebaseAdapter);

        mPopupBubble.setRecyclerView(mRecyclerView);
    }

//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(int position);
    }

    public static class BaristaViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;
        private Context mContext;

        public BaristaViewHolder(final View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.name);
            mContext = v.getContext();
        }
    }

}
