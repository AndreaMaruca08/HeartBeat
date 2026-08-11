package view;

import data.Monitoring;
import nv.core.NvContext;
import nv.core.graphic.NvGraphic;
import nv.utils.NvTimer;
import oshi.hardware.GraphicsCard;

import java.util.List;

public class Gpu extends Option {
    private final float W = NvContext.getInstance().getRenderWidth();
    private final float H = NvContext.getInstance().getRenderHeight();

    private final int detailY = (int) (H * 0.4f);
    private final int space = (int) (H * 0.05f);

    private List<GraphicsCard> gpus = Monitoring.getGraphicsCards();

    private final String GPU_NAME = gpus.isEmpty() ? "N/A" : gpus.get(0).getName();
    private final String GPU_NAME_DISPLAY = "GPU - " + GPU_NAME;

    private final VerticalBar[] vramBars;

    public Gpu() {
        super();

        int x = (int) (W * 0.05f) + getX();
        int y = (int) (H * 0.15f) + getY();
        int barW = (int) (W * 0.03f);
        int barH = (int) (H * 0.08f);

        this.vramBars = new VerticalBar[gpus.size()];
        int maxVramGB = 1;
        for (GraphicsCard gpu : gpus) {
            int gb = (int) (gpu.getVRam() / (1024L * 1024 * 1024));
            if (gb > maxVramGB) maxVramGB = gb;
        }
        for (int i = 0; i < vramBars.length; i++) {
            vramBars[i] = new VerticalBar(x, y, barW, barH, maxVramGB, 0);
            int gb = (int) (gpus.get(i).getVRam() / (1024L * 1024 * 1024));
            vramBars[i].setValue(gb);
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

            gpus = Monitoring.getGraphicsCards();
        });
        updTimer.start();
        NvContext.getInstance().addUpdatable(updTimer);
    }

    @Override
    String getName() {
        return "Gpu";
    }

    @Override
    public void drawIntern(NvGraphic g) {
        if (!isActive())
            return;

        g.drawText(GPU_NAME_DISPLAY, 50, 50);

        if (gpus.isEmpty()) {
            g.drawText("No GPU detected", 50, detailY);
            return;
        }

        int line = 0;
        for (GraphicsCard gpu : gpus) {
            g.drawText("Name: " + gpu.getName(), 50, detailY + space * line++);
            g.drawText("Vendor: " + gpu.getVendor(), 50, detailY + space * line++);
            double vramGB = gpu.getVRam() / (1024.0 * 1024 * 1024);
            if (vramGB > 0) {
                g.drawText(String.format("VRAM: %.2f GB", vramGB), 50, detailY + space * line++);
            } else {
                g.drawText("VRAM: N/A (shared/integrated)", 50, detailY + space * line++);
            }
            line++;
        }

        for (VerticalBar bar : vramBars) {
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