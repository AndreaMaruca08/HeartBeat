package view;

import nv.core.NvContext;
import nv.core.components.NvComp;
import nv.core.graphic.NvGraphic;
import nv.core.io.Clickable;

public class BarButton extends NvComp implements Clickable {
    private final WindowFrame frame;
    private final int index;
    public BarButton(int x, int y, int w, int h, int index, WindowFrame frame) {
        super(x, y, w, h);
        this.frame = frame;
        this.index = index;
    }

    @Override
    public void drawIntern(NvGraphic g) {
    }

    @Override
    public void update(float dt) {}

    @Override
    public void onClick() {
        frame.changeIndex(index);
        NvContext.markSceneDirty();
    }

    @Override
    public void onClickRelease() {}
}
