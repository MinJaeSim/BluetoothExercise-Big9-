package cc.foxtail.funkey.alarm;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import cc.foxtail.funkey.data.Child;

class AlarmViewPageAdapter extends FragmentPagerAdapter {

    private List<Child> list;
    private AlarmPresenter alarmPresenter;

    public AlarmViewPageAdapter(FragmentManager fm, List<Child> list) {
        super(fm);
        this.list = list;
        System.out.println("list = " + list.size());
    }

    @Override
    public Fragment getItem(int position) {
        System.out.println(position);
        AlarmViewPageFragment fragment;
        if (position < 0)
            return null;

        fragment = AlarmViewPageFragment.newInstance(list.get(position));
        fragment.setPresenter(alarmPresenter);

        return fragment;
    }


    public void setAlarmPresenter(AlarmPresenter alarmPresenter) {
        this.alarmPresenter = alarmPresenter;
    }

    @Override
    public int getCount() {
        return list.size();
    }

}
