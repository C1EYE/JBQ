package com.example.jbq.bean;


import android.os.Parcelable;

public class PedometerChartBean implements Parcelable {
    //当前记录的索引值
    private int index = 0;

    public PedometerChartBean() {
        index = 0;
    }

    protected PedometerChartBean(android.os.Parcel in) {
        index = in.readInt();
        dataArray = in.createIntArray();
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        //写入索引和数据
        dest.writeInt(index);
        dest.writeIntArray(dataArray);
    }


    public static final Creator<PedometerChartBean> CREATOR = new Creator<PedometerChartBean>() {
        public PedometerChartBean createFromParcel(android.os.Parcel in) {
            return new PedometerChartBean(in);
        }

        public PedometerChartBean[] newArray(int size) {
            return new PedometerChartBean[size];
        }
    };

    public void setIndex(int pIndex) {
        index = pIndex;
    }

    public int getIndex() {
        return index;
    }

    //记录全天的运动数据,用来生成曲线
    private int[] dataArray = new int[1440];

    public void reset() {
        for (int i : dataArray) {
            i = 0;
        }
        index = 0;
    }

    public void setDataArray(int[] pDataArray) {
        dataArray = pDataArray;
    }

    public int[] getDataArray() {
        return dataArray;
    }


    public int describeContents() {
        return 0;
    }


}
