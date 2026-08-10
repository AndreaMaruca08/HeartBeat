package view;

import nv.core.NvContext;
import nv.core.components.NvComp;
import nv.core.graphic.NvGraphic;

import java.util.ArrayList;
import java.util.List;

public class WindowFrame extends NvComp {
    private List<Option> options;
    private final List<BarButton> sections = new ArrayList<>();
    private int currentOption = 0;

    public WindowFrame() {
        var ctx = NvContext.getInstance();
        super(0,0, (int) ctx.getRenderWidth()/6, (int) ctx.getRenderHeight());
    }

    public void setOptions(List<Option> options) {
        this.options = options;
        int x = getW() / 6;
        int width = getW() - 2 * x;

        int offset = getH() / 20;
        int availableHeight = getH() - offset;
        int sectionHeight = availableHeight / options.size();
        int y = offset;

        for (int i = 0; i < options.size(); i++) {
            BarButton section = new BarButton(x, y, width, sectionHeight, i, this);
            sections.add(section);
            addChild(section);
            addChild(options.get(i));
            y += sectionHeight;
        }

        options.getFirst().setActive(true);
    }
    public void changeIndex(int newIndex){
        options.get(currentOption).setActive(false);
        options.get(newIndex).setActive(true);
        currentOption = newIndex;
    }

    @Override
    public void drawIntern(NvGraphic g) {
        g.setRGB(1,1,1);
        g.drawLine(getW(),0,getW(),getH(), 3, 1,1,1);
        int x = getW()/6;
        int y = getH()/20;
        for(int i = 0; i < options.size(); i++) {
            Option option = options.get(i);
            g.drawText(option.getName(), x, y);
            g.drawLine(x, y,getW()-x, y, 3, 1,1,1);
            y += getH()/options.size();
            if(i == currentOption) {
                g.setRGB(0,0,0);
                g.drawLine(x, y-2,getW()-x, y-2, 5, 1,1,1);
                g.setRGB(1,1,1);
            }
        }
    }

    @Override
    public void update(float dt) {}
}
