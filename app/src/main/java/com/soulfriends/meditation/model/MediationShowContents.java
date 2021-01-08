package com.soulfriends.meditation.model;

import java.util.Iterator;
import java.util.List;

public class MediationShowContents {
    public MeditationContents entity;
    public List<MeditationContents> childcontents;

    public void allClear()
    {
        // delete showcontents
        for (Iterator<MeditationContents> iter = childcontents.iterator(); iter.hasNext(); ) {
            MeditationContents letter = iter.next();
            iter.remove();
        }
    }
}
