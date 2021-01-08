

package com.soulfriends.meditation.viewmodel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soulfriends.meditation.databinding.PsychologyColorTestItemBinding;
import com.soulfriends.meditation.util.ItemClickListener;

import java.util.List;

public class PsychologyColorTestAdapter extends RecyclerView.Adapter {

    private List<PsychologyColorTestViewModel> list;
    private Context context;
    private LayoutInflater inflater;
    private ItemClickListener listener;

    public PsychologyColorTestAdapter(List<PsychologyColorTestViewModel> list, Context context, ItemClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        PsychologyColorTestItemBinding psychologyColorTestItemBinding = PsychologyColorTestItemBinding.inflate(inflater, parent, false);
        return new MyViewHolder(psychologyColorTestItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyViewHolder myViewHolder = (MyViewHolder) holder;

        PsychologyColorTestViewModel psychologyColorTestViewModel = list.get(position);

        myViewHolder.bind(psychologyColorTestViewModel);

        FrameLayout frame = myViewHolder.getPsychologyColorTestItemBinding().frameLayout;
        frame.setOnClickListener(v -> {
            listener.onItemClick(null, position);
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static public class MyViewHolder extends RecyclerView.ViewHolder {
        private PsychologyColorTestItemBinding psychologyColorTestItemBinding;

        public MyViewHolder(PsychologyColorTestItemBinding psychologyColorTestItemBinding) {
            super(psychologyColorTestItemBinding.getRoot());
            this.psychologyColorTestItemBinding = psychologyColorTestItemBinding;
        }

        public void bind(PsychologyColorTestViewModel viewModel) {
            this.psychologyColorTestItemBinding.setViewModel(viewModel);
        }

        public PsychologyColorTestItemBinding getPsychologyColorTestItemBinding() {
            return psychologyColorTestItemBinding;
        }
    }
}
