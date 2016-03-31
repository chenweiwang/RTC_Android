package com.ibm.rtc.rtc.core;

import com.ibm.rtc.rtc.model.Workitem;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Pampas Eagle on 2016/1/14.
 */
public class WorkitemSorter {

    public static void sort(List<Workitem> workitems, int which) {
        switch (which) {
            case 0:
                Collections.sort(workitems, new Comparator<Workitem>() {
                    @Override
                    public int compare(Workitem lhs, Workitem rhs) {
                        return lhs.getId() < rhs.getId() ? -1 :
                                lhs.getId() > rhs.getId() ? 1 : 0;
                    }
                });
                break;
            case 1:
                Collections.sort(workitems, new Comparator<Workitem>() {
                    @Override
                    public int compare(Workitem lhs, Workitem rhs) {
                        return lhs.getId() < rhs.getId() ? 1 :
                                lhs.getId() > rhs.getId() ? -1 : 0;
                    }
                });
                break;
            case 2:
                Collections.sort(workitems, new Comparator<Workitem>() {
                    @Override
                    public int compare(Workitem lhs, Workitem rhs) {
                        return lhs.getTitle().compareTo(rhs.getTitle());
                    }
                });
                break;
            case 3:
                Collections.sort(workitems, new Comparator<Workitem>() {
                    @Override
                    public int compare(Workitem lhs, Workitem rhs) {
                        return lhs.getCreatedTime().compareTo(rhs.getCreatedTime());
                    }
                });
                break;
            case 4:
                Collections.sort(workitems, new Comparator<Workitem>() {
                    @Override
                    public int compare(Workitem lhs, Workitem rhs) {
                        return lhs.getLastModifiedTime().compareTo(rhs.getLastModifiedTime());
                    }
                });
                break;
        }
    }
}
