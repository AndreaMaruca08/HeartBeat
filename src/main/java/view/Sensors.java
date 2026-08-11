package view;

import data.Monitoring;
import nv.core.NvContext;
import nv.core.graphic.NvGraphic;
import nv.utils.NvTimer;

public class Sensors extends Option {
    private final float W = NvContext.getInstance().getRenderWidth();
    private final float H = NvContext.getInstance().getRenderHeight();

    private final int detailY = (int) (H * 0.4f);
    private final int space = (int) (H * 0.05f);

    private double cpuTemp = Monitoring.getCpuTemperature();
    private double cpuVoltage = Monitoring.getCpuVoltage();
    private int[] fanSpeeds = Monitoring.getFanSpeeds();
    private double batteryPercent = Monitoring.getBatteryPercent();
    private boolean charging = Monitoring.isCharging();
    private double batteryTimeRemaining = Monitoring.getBatteryTimeRemainingMinutes();

    private final VerticalBar[] fanBars;
    private final VerticalBar batteryBar;

    public Sensors() {
        super();

        int x = (int) (W * 0.05f) + getX();
        int y = (int) (H * 0.15f) + getY();
        int barW = (int) (W * 0.03f);
        int barH = (int) (H * 0.08f);

        x += (int) (barW + W * 0.015f);
        this.batteryBar = new VerticalBar((int) (x*0.9f), (int) (y*0.9f), (int) (barW*1.3f), (int) (barH*1.3f), 100, 0);
        batteryBar.setFillColor(0,1,0);

        x += (int) (barW + W * 0.015f);
        this.fanBars = new VerticalBar[fanSpeeds.length];
        for (int i = 0; i < fanBars.length; i++) {
            fanBars[i] = new VerticalBar(x, y, barW, barH, 6000, 0);
            fanBars[i].setFillColor(0.1f,0.1f,0.9f);
            x += (int) (barW + W * 0.015f);
            if (x > W * 0.9f) {
                x = (int) (W * 0.05f) + getX();
                y += (int) (H * 0.05f) + barH + getY();
            }
        }

        NvTimer updTimer = new NvTimer(1000);
        updTimer.setIsLoop(true);
        updTimer.setOnFinished(() -> {
            if (!isActive())
                return;

            double newTemp = Monitoring.getCpuTemperature();
            if (cpuTemp != newTemp) {
                cpuTemp = newTemp;
                markDirty();
            }

            cpuVoltage = Monitoring.getCpuVoltage();

            int[] newFans = Monitoring.getFanSpeeds();
            for (int i = 0; i < fanBars.length && i < newFans.length; i++) {
                float newValue = (float) newFans[i];
                if (fanBars[i].getValue() != newValue) {
                    fanBars[i].setValue(newValue);
                    markDirty();
                }
            }
            fanSpeeds = newFans;

            double newBattery = Monitoring.getBatteryPercent();
            batteryPercent = newBattery;
            batteryBar.setValue((float) batteryPercent);

            charging = Monitoring.isCharging();
            batteryTimeRemaining = Monitoring.getBatteryTimeRemainingMinutes();
        });
        updTimer.start();
        NvContext.getInstance().addUpdatable(updTimer);
    }

    @Override
    String getName() {
        return "Sensors";
    }

    @Override
    public void drawIntern(NvGraphic g) {
        if (!isActive())
            return;

        g.drawText("SENSORS", 50, 50);

        int line = 0;
        if (cpuTemp > 0) {
            g.drawText(String.format("CPU Temperature: %.2f C", cpuTemp), 50, detailY + space * line++);
        } else {
            g.drawText("CPU Temperature: N/A", 50, detailY + space * line++);
        }

        if (cpuVoltage > 0) {
            g.drawText(String.format("CPU Voltage: %.2f V", cpuVoltage), 50, detailY + space * line++);
        }

        if (fanSpeeds.length == 0) {
            g.drawText("Fans: N/A", 50, detailY + space * line++);
        } else {
            for (int i = 0; i < fanSpeeds.length; i++) {
                g.drawText(String.format("Fan %d: %d RPM", i + 1, fanSpeeds[i]), 50, detailY + space * line++);
            }
        }

        g.drawText(String.format("Battery: %.1f%%", batteryPercent), 50, detailY + space * line++);
        g.drawText("Status: " + (charging ? "Charging" : "Discharging"), 50, detailY + space * line++);
        if (!charging && batteryTimeRemaining > 0) {
            g.drawText(String.format("Time remaining: %.0f min", batteryTimeRemaining), 50, detailY + space * line++);
        }

        batteryBar.draw(g);
        for (VerticalBar bar : fanBars) {
            bar.draw(g);
        }
    }

    @Override
    public void update(float dt) {
        if (!isActive()) {
            return;
        }
    }
}