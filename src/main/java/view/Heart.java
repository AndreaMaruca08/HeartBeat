package view;

import nv.core.NvContext;
import nv.core.animation.NvSprite;

public class Heart extends NvSprite {
    public Heart(int x, int y, int w, int h) {
        super(x, y, w, h, NvContext.getInstance().assets().loadAtlas("heart", "").image(), "heart", "heart");
    }
}
