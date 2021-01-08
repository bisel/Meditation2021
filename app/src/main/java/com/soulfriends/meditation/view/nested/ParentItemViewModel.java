package com.soulfriends.meditation.view.nested;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.model.MeditationCategory;

public class ParentItemViewModel extends ViewModel {

    public MutableLiveData<String> name = new MutableLiveData<>();

    private MeditationCategory meditationCategory;

    public ParentItemViewModel(MeditationCategory meditationCategory) {

        this.name.setValue(meditationCategory.name);

        this.meditationCategory = meditationCategory;
    }

    public MutableLiveData<String> getName() {
        return name;
    }

    public void setName(MutableLiveData<String> name) {
        this.name = name;
    }

    public MeditationCategory getMeditationCategory() {
        return meditationCategory;
    }

    public int getSizeContest() {
        return meditationCategory.contests.size();
    }

    public void setMeditationCategory(MeditationCategory meditationCategory) {
        this.meditationCategory = meditationCategory;
    }
}