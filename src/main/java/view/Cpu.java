package view;

import data.Monitoring;
import nv.core.NvContext;
import nv.core.graphic.NvGraphic;
import nv.utils.NvTimer;

public class Cpu extends Option{
    private final String CPU_NAME = Monitoring.getCpuName();
    private final String CPU_NAME_DISPLAY = "CPU - " + CPU_NAME;
    private final float W = NvContext.getInstance().getRenderWidth();
    private final float H = NvContext.getInstance().getRenderHeight();

    private final int detailY = (int) (H*0.4f);

    private final int space = (int) (H*0.05f);

    private double load = Monitoring.getCpuLoad();
    private int cores = Monitoring.getPhysicalCoreCount();
    private double temp = Monitoring.getCpuTemperature();
    private double freq = Monitoring.getMaxFreqGHz();
    private int threads = Monitoring.getThreadCount();
    private final String vendor = Monitoring.getCpuVendor();
    private double voltage = Monitoring.getCpuVoltage();

    private final VerticalBar[] bars;

    public Cpu() {
        super();

        this.bars = new VerticalBar[Monitoring.getCpuLoadPerCore().length];
        int x = (int) (W*0.05f) + getX();
        int y = (int) (H*0.15f) + getY();
        int barW = (int) (W*0.03f);
        int barH = (int) (H*0.08f);
        for(int i = 0; i < bars.length; i++){
            bars[i] = new VerticalBar(x, y, barW, barH, 100, 0);
            x += (int) (barW + W*0.015f);
            if(x > W*0.9f){
                x = (int) (W*0.05f) + getX();
                y += (int) (H*0.05f) + barH + getY();
            }
        }

        NvTimer updTimer = new NvTimer(1000);
        updTimer.setIsLoop(true);
        updTimer.setOnFinished(() -> {
            if(!isActive())
                return;
            for(int i = 0; i < bars.length; i++){
                var newValue = (float) Monitoring.getCpuLoadPerCore()[i];
                if(bars[i].getValue() != newValue) {
                    bars[i].setValue(newValue);
                    markDirty();
                }
            }
            load = Monitoring.getCpuLoad();
            temp = Monitoring.getCpuTemperature();
            freq = Monitoring.getMaxFreqGHz();
            threads = Monitoring.getThreadCount();
            voltage = Monitoring.getCpuVoltage();
            cores = Monitoring.getPhysicalCoreCount();
        });
        updTimer.start();
        NvContext.getInstance().addUpdatable(updTimer);
    }

    @Override
    public String getName() {
        return "Cpu";
    }

    @Override
    public void drawIntern(NvGraphic g) {
        if(!isActive())
            return;

        g.drawText(CPU_NAME_DISPLAY, 50,50);

        g.drawText("Vendor: " + vendor, 50, detailY);
        g.drawText("Cores: " + cores, 50, detailY + space);
        g.drawText(String.format("Load: %.2f%%", load), 50, detailY + space*2);
        g.drawText(String.format("Temperature: %.2f C", temp), 50, detailY + space*3);
        g.drawText(String.format("MAX Frequency: %.2f GHz", freq), 50, detailY + space*4);
        g.drawText(String.format("Threads: %d", threads), 50, detailY + space*5);
        if(voltage > 0){
            g.drawText(String.format("Voltage: %.2f", voltage), 50, detailY + space*7);
        }
        for(VerticalBar bar : bars){
            bar.draw(g);
        }
    }

    @Override
    public void update(float dt) {
        if(!isActive()){
            return;
        }
    }
}
