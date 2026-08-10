package view;

import nv.core.NvContext;
import nv.core.components.NvComp;
import nv.core.io.Clickable;
import nv.core.io.Hoverable;

public abstract class Option extends NvComp {
    private boolean active = false;
    public Option() {
        var ctx = NvContext.getInstance();
        int rw = (int) ctx.getRenderWidth();
        int rh = (int) ctx.getRenderHeight();
        var x = rw/6;
        super(rw/6, 0, rw-x, rh);
    }
    public void setActive(boolean active){
        this.active = active;
    }
    public boolean isActive(){
        return active;
    }

    abstract String getName();
}
