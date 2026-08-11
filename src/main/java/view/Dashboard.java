package view;

import data.Monitoring;
import nv.core.NvContext;
import nv.core.graphic.NvGraphic;
import nv.core.io.AudioManager;
import nv.utils.NvTimer;
import nv.utils.shapes.dynamic.NvLabel;

import java.awt.*;

public class Dashboard extends Option {
    private final Heart heart;
    private final VerticalBar cpuBar;
    private final NvLabel cpuBarLabel;
    private final NvLabel cpuBarTitle;
    private final VerticalBar ramBar;
    private final NvLabel ramBarLabel;
    private final NvLabel ramBarTitle;
    private final VerticalBar diskBar;
    private final NvLabel diskBarLabel;
    private final NvLabel diskBarTitle;
    private final NvLabel uptimeLabel;
    private final NvLabel tempLabel;

    private final float baseSize;

    private float time;
    private float lastPhase;

    private double cpuUsage;
    private double cpuTemp;
    private double ramUsage;
    private double diskUsage;

    public Dashboard() {
        super();

        float size = getW() / 3.5f;

        float centerX = getW() / 3.0f;
        float centerY = getH() * 0.03f + size / 2.0f;

        this.heart = new Heart(
                (int) (centerX - size / 2.0f),
                (int) (centerY - size / 3.0f),
                (int) size,
                (int) size
        );

        int startX = (int) (heart.getX() * 4f);
        int space = (int) size / 2;

        this.cpuBar = new VerticalBar(
                startX,
                heart.getY(),
                (int) size / 5,
                (int) size / 2,
                100,
                0
        );

        this.ramBar = new VerticalBar(
                startX + space,
                heart.getY(),
                (int) size / 5,
                (int) size / 2,
                100,
                0
        );

        this.diskBar = new VerticalBar(
                startX + space * 2,
                heart.getY(),
                (int) size / 5,
                (int) size / 2,
                100,
                0
        );

        this.cpuBarLabel = new NvLabel(
                cpuBar.getX(),
                (int) (cpuBar.getY() * 0.4f)
        );

        this.cpuBarTitle = new NvLabel(
                cpuBar.getX(),
                heart.getY() + ramBar.getH()
        );

        this.ramBarLabel = new NvLabel(
                ramBar.getX(),
                (int) (ramBar.getY() * 0.4f)
        );

        this.ramBarTitle = new NvLabel(
                ramBar.getX(),
                heart.getY() + ramBar.getH()
        );

        this.diskBarLabel = new NvLabel(
                diskBar.getX(),
                (int) (diskBar.getY() * 0.4f)
        );

        this.diskBarTitle = new NvLabel(
                diskBar.getX(),
                heart.getY() + diskBar.getH()
        );

        this.uptimeLabel = new NvLabel(
                (int) (getW() * 0.25f),
                (int) (getH() * 0.9f)
        );

        this.tempLabel = new NvLabel(
                (int) (heart.getX() * 1.2f),
                (int) (heart.getY() * 0.7f)
        );

        cpuBarLabel.changeText("");
        cpuBarTitle.changeText("CPU");
        cpuBarLabel.setRgb(1, 1, 1);
        cpuBarTitle.setRgb(1, 1, 1);

        ramBarLabel.changeText("");
        ramBarTitle.changeText("RAM");
        ramBarLabel.setRgb(1, 1, 1);
        ramBarTitle.setRgb(1, 1, 1);

        diskBarLabel.changeText("");
        diskBarTitle.changeText("DISK");
        diskBarLabel.setRgb(1, 1, 1);
        diskBarTitle.setRgb(1, 1, 1);

        uptimeLabel.changeText("");
        uptimeLabel.setRgb(0.8f, 0.8f, 0.8f);

        tempLabel.changeText("");
        tempLabel.setRgb(1, 1, 1);

        cpuBar.setFillColor(0, 0, 0.8f);
        ramBar.setFillColor(0.5f, 1, 0);
        diskBar.setFillColor(1, 0.6f, 0);

        this.baseSize = size;

        upd();

        NvTimer updateTimer = new NvTimer(500);
        updateTimer.setIsLoop(true);
        NvContext.getInstance().addUpdatable(updateTimer);

        updateTimer.setOnFinished(() -> {
            if(!isActive())
                return;
            upd();
            NvContext.markSceneDirty();
        });

        updateTimer.start();
    }

    public void upd() {
        cpuUsage = Monitoring.getCpuLoad();
        cpuTemp = Monitoring.getCpuTemperature();
        ramUsage = Monitoring.getRamUsagePercent();

        diskUsage = 100.0 - (Monitoring.getUsableDiskSpaceBytes() * 100.0 / Monitoring.getTotalDiskSpaceBytes());

        cpuBarLabel.changeText(String.format("%.1f %%", cpuUsage));
        ramBarLabel.changeText(String.format("%.1f %%", ramUsage));
        diskBarLabel.changeText(String.format("%.1f %%", diskUsage));

        cpuBar.setValue((float) cpuUsage);
        ramBar.setValue((float) ramUsage);
        diskBar.setValue((float) diskUsage);

        tempLabel.changeText(cpuTemp > 0 ? String.format("%.1f C", cpuTemp): "");

        long uptime = Monitoring.getSystemUptimeSeconds();

        long h = uptime / 3600;
        long m = (uptime % 3600) / 60;

        uptimeLabel.changeText(
                String.format("Uptime: %dh %02dm", h, m)
        );
    }

    private final String os = Monitoring.getOsFullName();

    @Override
    public void drawIntern(NvGraphic g) {
        if (!isActive())
            return;

        g.drawText(os, 50, 50);

        heart.draw(g);

        cpuBar.draw(g);
        ramBar.draw(g);
        diskBar.draw(g);

        cpuBarLabel.draw(g);
        ramBarLabel.draw(g);
        cpuBarTitle.draw(g);

        ramBarTitle.draw(g);
        diskBarLabel.draw(g);
        diskBarTitle.draw(g);

        tempLabel.draw(g);
        uptimeLabel.draw(g);


        markDirty();
    }

    @Override
    public void update(float dt) {
        if (!isActive())
            return;

        float cpu = (float) Math.clamp(cpuUsage, 0.0, 100.0) / 40.0f;

        float frequency = 1.0f + cpu * 1.5f;

        time += dt;

        float phase = time * frequency;

        if (phase >= 1.0f) {
            phase %= 1.0f;
            time = phase / frequency;
        }

        if (phase < lastPhase) {
            AudioManager.play("beat.mp3");
        }

        lastPhase = phase;

        float pulse = (float) (
                (Math.sin(phase * Math.PI * 2.0) + 1.0) * 0.5
        );

        float amplitude = 0.03f + cpu * 0.12f;

        float size = baseSize * (1.0f + pulse * amplitude);

        float centerX = getW() / 2.0f;
        float centerY = getH() * 0.03f + baseSize / 2.0f;

        float x = centerX - size / 2.0f;
        float y = centerY - size / 2.0f;

        heart.setX((int) x);
        heart.setY((int) y);
        heart.setW((int) size);
        heart.setH((int) size);
    }

    @Override
    public String getName() {
        return "Dashboard";
    }
}