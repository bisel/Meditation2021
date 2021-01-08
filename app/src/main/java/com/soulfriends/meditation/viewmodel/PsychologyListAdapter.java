package com.soulfriends.meditation.viewmodel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soulfriends.meditation.databinding.PsychologyListItemBinding;
import com.soulfriends.meditation.util.ItemClickListener;

import java.util.List;

public class PsychologyListAdapter extends RecyclerView.Adapter {

    private List<PsychologyListViewModel> list;
    private Context context;
    private LayoutInflater inflater;
    private ItemClickListener listener;

    public PsychologyListAdapter(List<PsychologyListViewModel> list, Context context, ItemClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        PsychologyListItemBinding psychologyListItemBinding = PsychologyListItemBinding.inflate(inflater, parent, false);
        return new MyViewHolder(psychologyListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyViewHolder myViewHolder = (MyViewHolder)holder;
        myViewHolder.bind(list.get(position));

        FrameLayout frame = myViewHolder.getPsychologyListItemBinding().frameLayout;
        frame.setOnClickListener(v -> {
            listener.onItemClick(null, position);
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static public class MyViewHolder extends RecyclerView.ViewHolder
    {
        private PsychologyListItemBinding psychologyListItemBinding;

        public MyViewHolder(PsychologyListItemBinding psychologyListItemBinding) {
            super(psychologyListItemBinding.getRoot());
            this.psychologyListItemBinding = psychologyListItemBinding;
        }

        public void bind(PsychologyListViewModel viewModel)
        {
            this.psychologyListItemBinding.setViewModel(viewModel);
        }

        public PsychologyListItemBinding getPsychologyListItemBinding()
        {
            return psychologyListItemBinding;
        }
    }
}
