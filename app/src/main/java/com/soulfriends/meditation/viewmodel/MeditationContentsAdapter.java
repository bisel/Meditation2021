package com.soulfriends.meditation.viewmodel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.soulfriends.meditation.databinding.MeditationContentsBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MeditationContentsAdapter extends RecyclerView.Adapter<MeditationContentsAdapter.MyViewHolder> {
    List<MeditationContentsViewModel> list;
    Context context;
    private LayoutInflater inflater;


    public MeditationContentsAdapter(List<MeditationContentsViewModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }

        MeditationContentsBinding meditationContentsBinding = MeditationContentsBinding.inflate(inflater, parent, false);



        return new MyViewHolder(meditationContentsBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MeditationContentsAdapter.MyViewHolder holder, int position) {

        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        private MeditationContentsBinding meditationContentsBinding;

        public MyViewHolder(MeditationContentsBinding meditationContentsBinding)
        {
            super(meditationContentsBinding.getRoot());
            this.meditationContentsBinding = meditationContentsBinding;
        }

        public void bind(MeditationContentsViewModel meditationContentsViewModel)
        {
            this.meditationContentsBinding.setViewModel(meditationContentsViewModel);
        }

        public MeditationContentsBinding getMeditationContentsBinding()
        {
            return meditationContentsBinding;
        }
    }
}
