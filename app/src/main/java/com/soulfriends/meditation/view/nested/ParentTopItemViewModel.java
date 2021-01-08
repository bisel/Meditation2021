package com.soulfriends.meditation.view.nested;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.model.UserProfile;

public class ParentTopItemViewModel extends ViewModel {

    public MutableLiveData<String> name = new MutableLiveData<>();
    public MutableLiveData<String> feelstate = new MutableLiveData<>();
    private UserProfile userProfile;

    public ParentTopItemViewModel(String name, UserProfile userProfile) {

        this.userProfile = userProfile;
        this.name.setValue(name);
    }

    public MutableLiveData<String> getFeelstate() {
        return feelstate;
    }

    public void setFeelstate(String feelstate) {
        this.feelstate.setValue(feelstate);
    }

    public MutableLiveData<String> getName() {
        return name;
    }
    public void setName(MutableLiveData<String> name) {
        this.name = name;
    }
}
