package view;

import nv.core.graphic.NvGraphic;

public class Cpu extends Option{
    @Override
    public String getName() {
        return "Cpu";
    }

    @Override
    public void drawIntern(NvGraphic g) {
        if(!isActive())
            return;
        g.drawRect(1000,1000,500,500);
    }

    @Override
    public void update(float dt) {
        if(!isActive()){
            return;
        }
    }
}
