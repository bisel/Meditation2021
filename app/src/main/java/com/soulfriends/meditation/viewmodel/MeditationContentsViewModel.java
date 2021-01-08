package com.soulfriends.meditation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.model.MeditationContents;

public class MeditationContentsViewModel extends ViewModel {

    public MutableLiveData<String> thumbnail = new MutableLiveData<>();
    public MutableLiveData<String> title = new MutableLiveData<>();
//    public String instroduction; // 소개글
//    public String bgimgurl;
//    public String writer;
//    public int playtime;  // s
//    public int favoritecnt;
//    public String contentsurl;


    public MutableLiveData<MeditationContents> listMeditationContents = new MutableLiveData<>();

    public MeditationContentsViewModel() {
        super();
    }


    public void Set(MeditationContents data)
    {
        listMeditationContents.setValue(data);
    }

    public MeditationContentsViewModel(String aa, String bb)
    {

        this.thumbnail.postValue(aa);
        this.title.postValue(bb);
        //MeditationContents m = new MeditationContents(aa, bb);

        //listMeditationContents.setValue(m);
    }

    public LiveData<MeditationContents> getMeditationContents(){
        return listMeditationContents;
    }
}
