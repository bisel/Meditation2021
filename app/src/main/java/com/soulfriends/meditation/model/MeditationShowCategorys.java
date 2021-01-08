package com.soulfriends.meditation.model;

import java.util.Iterator;
import java.util.List;

public class MeditationShowCategorys {
    public List<MeditationCategory> showcategorys;

    // 모든 showcategorys 정보 지움
    public void allClear()
    {
        for (Iterator<MeditationCategory> iter = showcategorys.iterator(); iter.hasNext(); ) {
            MeditationCategory letter = iter.next();
            iter.remove();
        }
    }
}
