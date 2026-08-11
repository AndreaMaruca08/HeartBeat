package view;

import data.Monitoring;
import nv.core.NvContext;
import nv.core.graphic.NvGraphic;
import nv.utils.NvTimer;
import oshi.hardware.PhysicalMemory;

public class Ram extends Option {
    private final float W = NvContext.getInstance().getRenderWidth();
    private final float H = NvContext.getInstance().getRenderHeight();

    private final int detailY = (int) (H * 0.4f);
    private final int space = (int) (H * 0.05f);

    private long totalBytes = Monitoring.getTotalRamBytes();
    private long usedBytes = Monitoring.getUsedRamBytes();
    private long availableBytes = Monitoring.getAvailableRamBytes();
    private double usagePercent = Monitoring.getRamUsagePercent();
    private long swapTotal = Monitoring.getSwapTotalBytes();
    private long swapUsed = Monitoring.getSwapUsedBytes();
    private final int bankCount = Monitoring.getPhysicalMemoryBanks().size();

    private final VerticalBar usageBar;
    private final VerticalBar[] bankBars;

    public Ram() {
        super();

        int x = (int) (W * 0.05f) + getX();
        int y = (int) (H * 0.15f) + getY();
        int barW = (int) (W * 0.03f);
        int barH = (int) (H * 0.08f);

        this.usageBar = new VerticalBar(x, y, barW, barH, 100, 0);
        usageBar.setFillColor(0.5f, 0.8f, 0.5f);

        x += (int) (barW + W * 0.1f);
        this.bankBars = new VerticalBar[bankCount];
        int maxBankGB = 1;
        for (PhysicalMemory pm : Monitoring.getPhysicalMemoryBanks()) {
            int gb = (int) (pm.getCapacity() / (1024L * 1024 * 1024));
            if (gb > maxBankGB) maxBankGB = gb;
        }
        for (int i = 0; i < bankBars.length; i++) {
            bankBars[i] = new VerticalBar(x, y, barW, barH, maxBankGB, 0);
            PhysicalMemory pm = Monitoring.getPhysicalMemoryBanks().get(i);
            bankBars[i].setValue((float) (pm.getCapacity() / (1024L * 1024 * 1024)));
            x += (int) (barW + W * 0.015f);
            if (x > W * 0.9f) {
                x = (int) (W * 0.05f) + getX() + (int) (barW + W * 0.015f);
                y += (int) (H * 0.05f) + barH + getY();
            }
        }

        NvTimer updTimer = new NvTimer(1000);
        updTimer.setIsLoop(true);
        updTimer.setOnFinished(() -> {
            if (!isActive())
                return;

            totalBytes = Monitoring.getTotalRamBytes();
            usedBytes = Monitoring.getUsedRamBytes();
            availableBytes = Monitoring.getAvailableRamBytes();
            swapTotal = Monitoring.getSwapTotalBytes();
            swapUsed = Monitoring.getSwapUsedBytes();

            double newUsage = Monitoring.getRamUsagePercent();
            if (usagePercent != newUsage) {
                usagePercent = newUsage;
                usageBar.setValue((float) usagePercent);
                markDirty();
            }
        });
        updTimer.start();
        NvContext.getInstance().addUpdatable(updTimer);
    }

    @Override
    String getName() {
        return "Ram";
    }

    @Override
    public void drawIntern(NvGraphic g) {
        if (!isActive())
            return;

        g.drawText("RAM", 50, 50);
        g.drawText("Usage %", usageBar.getX() - getX(),  usageBar.getY()*0.8f);
        g.drawText("Physical banks (GB)", bankBars[0].getX() - getX(), bankBars[0].getY()*0.8f);

        g.drawText(String.format("Total: %.2f GB", totalBytes / (1024.0 * 1024 * 1024)), 50, detailY);
        g.drawText(String.format("Used: %.2f GB", usedBytes / (1024.0 * 1024 * 1024)), 50, detailY + space);
        g.drawText(String.format("Available: %.2f GB", availableBytes / (1024.0 * 1024 * 1024)), 50, detailY + space * 2);
        g.drawText(String.format("Usage: %.2f%%", usagePercent), 50, detailY + space * 3);
        g.drawText(String.format("Banks: %d", bankCount), 50, detailY + space * 4);
        if (swapTotal > 0) {
            g.drawText(String.format("Swap: %.2f / %.2f GB",
                    swapUsed / (1024.0 * 1024 * 1024),
                    swapTotal / (1024.0 * 1024 * 1024)), 50, detailY + space * 5);
        }

        usageBar.draw(g);

        if (bankBars.length > 0) {
            for (VerticalBar bar : bankBars) {
                bar.draw(g);
            }
        }
    }
    @Override
    public void update(float dt) {
        if (!isActive()) {
            return;
        }
    }
}