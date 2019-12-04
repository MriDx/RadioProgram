package com.mridx.radioprogram.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mridx.radioprogram.R;
import com.mridx.radioprogram.activity.DetailUI;

import java.util.ArrayList;

import static com.mridx.radioprogram.activity.HomeUI.AllSongsUri;
import static com.mridx.radioprogram.activity.HomeUI.mediaHelper;

public class ProgramsFrag extends Fragment {

    private RecyclerView programHolder;

    private ArrayList<String> some = new ArrayList<>();



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.program_frag, null);
        setupView(view);
        return view;
    }

    private void setupView(View view) {
        programHolder = view.findViewById(R.id.programHolder);

        PopulateView();
    }

    private void PopulateView() {
        if (getData()) {
            ProgramAdapter programAdapter = new ProgramAdapter(some);
            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
            programHolder.setLayoutManager(layoutManager);
            programHolder.setAdapter(programAdapter);
        }
    }

    private boolean getData() {
        for (int i = 0; i < 20; i++) {
            String a = "a" + i;
            some.add(a);
        }
        return true;
    }

    public void showToast() {
        Toast.makeText(getActivity(), "Haven't added any callback.", Toast.LENGTH_SHORT).show();
    }

    class ProgramAdapter extends RecyclerView.Adapter<ProgramAdapter.MyViewHolder> {


        private ArrayList<String> some = new ArrayList<>();

        public ProgramAdapter(ArrayList<String> some) {
            this.some = some;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.program_view, null);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProgramAdapter.MyViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return AllSongsUri.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private AppCompatImageView playProgram;
            private AppCompatTextView playText;
            private ProgressBar nestedProg;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                playProgram = itemView.findViewById(R.id.playProgram);
                playText = itemView.findViewById(R.id.playText);
                nestedProg = itemView.findViewById(R.id.nestedProg);
                playProgram.setOnClickListener(v -> startPlay(AllSongsUri.get(getAdapterPosition()), playProgram, nestedProg));
                playText.setOnClickListener(v -> startPlay(AllSongsUri.get(getAdapterPosition()), playProgram, nestedProg));
                itemView.setOnClickListener(v -> startActivity(new Intent(getActivity(), DetailUI.class)));
            }
        }

    }

    public void startPlay(String s, AppCompatImageView playProgram, ProgressBar nestedProg) {
        /*mediaHelper.init(s);
        mediaHelper.prepare();*/

        if (mediaHelper.isPlaying()) {
            mediaHelper.pause();
            mediaHelper.reset();
            //startPlay(s, playProgram, nestedProg);
        } else if (mediaHelper.canResume()) {
            mediaHelper.resume();
            //startHandler();
        } else {
            mediaHelper.init(s);
            mediaHelper.prepare();
            //setLoading();
        }
    }


}
