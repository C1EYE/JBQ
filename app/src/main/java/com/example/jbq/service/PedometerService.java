package com.example.jbq.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class PedometerService extends Service {
    private IPedometerService.Stub iPedometerService = new IPedometerService.Stub() {
        @Override
        public int getStepsCount() throws RemoteException {
            return 0;
        }

        @Override
        public void resetCount() throws RemoteException {

        }

        @Override
        public void startSetpsCount() throws RemoteException {

        }

        @Override
        public void stopSetpsCount() throws RemoteException {

        }

        @Override
        public double getCalorie() throws RemoteException {
            return 0;
        }

        @Override
        public double getDistance() throws RemoteException {
            return 0;
        }

        @Override
        public void saveData() throws RemoteException {

        }

        @Override
        public void setSensitivity(float sensitivity) throws RemoteException {

        }

        @Override
        public double getSensitivity() throws RemoteException {
            return 0;
        }

        @Override
        public int getInterval() throws RemoteException {
            return 0;
        }

        @Override
        public void setInterval(int interval) throws RemoteException {

        }

        @Override
        public long getStartTimestmp() throws RemoteException {
            return 0;
        }

        @Override
        public int getServiceRunningStatus() throws RemoteException {
            return 0;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
