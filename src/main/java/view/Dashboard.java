package view;

import data.Monitoring;
import nv.core.graphic.NvGraphic;

public class Dashboard extends Option {
    private final Heart heart;

    private final float baseSize;
    private final float minSize;
    private final float maxSize;

    private float time;

    private double cpuUsage;

    public Dashboard() {
        super();

        float size = getW() / 3.0f;

        float centerX = getW() / 2.0f;
        float centerY = getH() * 0.03f + size / 2.0f;

        this.heart = new Heart(
                (int) (centerX - size / 2.0f),
                (int) (centerY - size / 2.0f),
                (int) size,
                (int) size
        );

        this.baseSize = size;
        this.minSize = baseSize * 0.90f;
        this.maxSize = baseSize * 1.15f;

        this.cpuUsage = Monitoring.getCpuLoad();
    }

    @Override
    public void drawIntern(NvGraphic g) {
        if (!isActive())
            return;

        heart.draw(g);
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
    }

    @Override
    public void update(float dt) {
        if (!isActive())
            return;
        cpuUsage = Monitoring.getCpuLoad();

        float cpu = (float) Math.clamp(cpuUsage, 0.0, 1.0);
        time += dt;

        float frequency = 1.0f + cpu * 2.0f;

        float pulse = (float) ((Math.sin(time * frequency * Math.PI * 2.0) + 1.0) * 0.5);
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