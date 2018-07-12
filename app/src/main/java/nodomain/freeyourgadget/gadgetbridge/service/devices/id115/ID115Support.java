package nodomain.freeyourgadget.gadgetbridge.service.devices.id115;

import android.bluetooth.BluetoothGattCharacteristic;
import android.net.Uri;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

import nodomain.freeyourgadget.gadgetbridge.devices.id115.ID115Constants;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.Alarm;
import nodomain.freeyourgadget.gadgetbridge.model.CalendarEventSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CallSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CannedMessagesSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicStateSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattService;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.SetDeviceStateAction;
import nodomain.freeyourgadget.gadgetbridge.service.devices.no1f1.No1F1Support;

public class ID115Support extends AbstractBTLEDeviceSupport {
    private static final Logger LOG = LoggerFactory.getLogger(No1F1Support.class);

    public BluetoothGattCharacteristic normalWriteCharacteristic = null;
    public BluetoothGattCharacteristic healthWriteCharacteristic = null;

    public ID115Support() {
        super(LOG);
        addSupportedService(GattService.UUID_SERVICE_GENERIC_ACCESS);
        addSupportedService(GattService.UUID_SERVICE_GENERIC_ATTRIBUTE);
        addSupportedService(ID115Constants.UUID_SERVICE_ID115);
    }

    @Override
    protected TransactionBuilder initializeDevice(TransactionBuilder builder) {
        normalWriteCharacteristic = getCharacteristic(ID115Constants.UUID_CHARACTERISTIC_WRITE_NORMAL);
        healthWriteCharacteristic = getCharacteristic(ID115Constants.UUID_CHARACTERISTIC_WRITE_HEALTH);

        builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZING, getContext()));

        setTime(builder)
                .setDisplayMode(builder, false)
                .setInitialized(builder);

        return builder;
    }

    @Override
    public boolean useAutoConnect() {
        return true;
    }

    @Override
    public void onNotification(NotificationSpec notificationSpec) {

    }

    @Override
    public void onDeleteNotification(int id) {

    }

    @Override
    public void onSetTime() {
        try {
            TransactionBuilder builder = performInitialized("time");
            setTime(builder);
            performConnected(builder.getTransaction());
        } catch(IOException e) {
        }
    }

    @Override
    public void onSetAlarms(ArrayList<? extends Alarm> alarms) {

    }

    @Override
    public void onSetCallState(CallSpec callSpec) {

    }

    @Override
    public void onSetCannedMessages(CannedMessagesSpec cannedMessagesSpec) {

    }

    @Override
    public void onSetMusicState(MusicStateSpec stateSpec) {

    }

    @Override
    public void onSetMusicInfo(MusicSpec musicSpec) {

    }

    @Override
    public void onEnableRealtimeSteps(boolean enable) {

    }

    @Override
    public void onInstallApp(Uri uri) {

    }

    @Override
    public void onAppInfoReq() {

    }

    @Override
    public void onAppStart(UUID uuid, boolean start) {

    }

    @Override
    public void onAppDelete(UUID uuid) {

    }

    @Override
    public void onAppConfiguration(UUID appUuid, String config, Integer id) {

    }

    @Override
    public void onAppReorder(UUID[] uuids) {

    }

    @Override
    public void onFetchRecordedData(int dataTypes) {

    }

    @Override
    public void onReboot() {
        try {
            getQueue().clear();

            TransactionBuilder builder = performInitialized("reboot");
            builder.write(normalWriteCharacteristic, new byte[] {
                    ID115Constants.CMD_ID_DEVICE_RESTART, ID115Constants.CMD_KEY_REBOOT
            });
            performConnected(builder.getTransaction());
        } catch(Exception e) {
        }
    }

    @Override
    public void onHeartRateTest() {

    }

    @Override
    public void onEnableRealtimeHeartRateMeasurement(boolean enable) {

    }

    @Override
    public void onFindDevice(boolean start) {

    }

    @Override
    public void onSetConstantVibration(int integer) {

    }

    @Override
    public void onScreenshotReq() {

    }

    @Override
    public void onEnableHeartRateSleepSupport(boolean enable) {

    }

    @Override
    public void onSetHeartRateMeasurementInterval(int seconds) {

    }

    @Override
    public void onAddCalendarEvent(CalendarEventSpec calendarEventSpec) {

    }

    @Override
    public void onDeleteCalendarEvent(byte type, long id) {

    }

    @Override
    public void onSendConfiguration(String config) {

    }

    @Override
    public void onTestNewFunction() {

    }

    @Override
    public void onSendWeather(WeatherSpec weatherSpec) {

    }

    private void setInitialized(TransactionBuilder builder) {
        builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZED, getContext()));
    }

    ID115Support setTime(TransactionBuilder builder) {
        Calendar c = GregorianCalendar.getInstance();

        int day = c.get(Calendar.DAY_OF_WEEK);

        byte dayOfWeek;
        if (day == Calendar.SUNDAY) {
            dayOfWeek = 6;
        } else {
            dayOfWeek = (byte)(day - 2);
        }

        // TODO: set year
        builder.write(normalWriteCharacteristic, new byte[] {
                ID115Constants.CMD_ID_SETTINGS, ID115Constants.CMD_KEY_SET_TIME,
                0, 0,
                (byte)c.get(Calendar.MONTH),
                (byte)c.get(Calendar.DAY_OF_MONTH),
                (byte)c.get(Calendar.HOUR_OF_DAY),
                (byte)c.get(Calendar.MINUTE),
                (byte)c.get(Calendar.SECOND),
                dayOfWeek
        });
        return this;
    }

    ID115Support setDisplayMode(TransactionBuilder builder, boolean vertical) {
        byte mode = (byte)(vertical? 2 : 0);
        builder.write(normalWriteCharacteristic, new byte[] {
                ID115Constants.CMD_ID_SETTINGS, ID115Constants.CMD_KEY_SET_DISPLAY_MODE,
                mode
        });
        return this;
    }
}
