

package com.soulfriends.meditation.viewmodel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soulfriends.meditation.databinding.PsychologyFeelingTestItemBinding;
import com.soulfriends.meditation.util.ItemClickListener;

import java.util.List;

public class PsychologyFeelingTestAdapter extends RecyclerView.Adapter {

    private List<PsychologyFeelingTestViewModel> list;
    private Context context;
    private LayoutInflater inflater;
    private ItemClickListener listener;

    public PsychologyFeelingTestAdapter(List<PsychologyFeelingTestViewModel> list, Context context, ItemClickListener listener) {
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
        PsychologyFeelingTestItemBinding psychologyFeelingTestItemBinding = PsychologyFeelingTestItemBinding.inflate(inflater, parent, false);
        return new MyViewHolder(psychologyFeelingTestItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.bind(list.get(position));

        FrameLayout frame = myViewHolder.getPsychologyFeelingTestItemBinding().frameLayout;
        frame.setOnClickListener(v -> {
            listener.onItemClick(null, position);
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static public class MyViewHolder extends RecyclerView.ViewHolder {
        private PsychologyFeelingTestItemBinding psychologyFeelingTestItemBinding;

        public MyViewHolder(PsychologyFeelingTestItemBinding psychologyFeelingTestItemBinding) {
            super(psychologyFeelingTestItemBinding.getRoot());
            this.psychologyFeelingTestItemBinding = psychologyFeelingTestItemBinding;
        }

        public void bind(PsychologyFeelingTestViewModel viewModel) {
            this.psychologyFeelingTestItemBinding.setViewModel(viewModel);
        }

        public PsychologyFeelingTestItemBinding getPsychologyFeelingTestItemBinding() {
            return psychologyFeelingTestItemBinding;
        }
    }
}
