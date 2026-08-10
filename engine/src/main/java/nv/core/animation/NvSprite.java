package nv.core.animation;

import nv.core.NvContext;
import nv.core.annotations.EngineCore;
import nv.core.assets.AtlasConverter;
import nv.core.components.NvComp;
import nv.core.data.NvImage;
import nv.core.graphic.NvGraphic;

/**
 * Special component with sprite embedded can be used for animation with {@link nv.core.animation.NvAnimation}
 *
 * @author Andrea Maruca
 * @since 1.3
 */
@EngineCore
@SuppressWarnings("unused")
public class NvSprite extends NvComp {
    protected final String atlasName;
    protected final String defaultSpriteFileName;
    protected String spriteFileName;
    protected AtlasConverter.Region region;
    protected final NvImage atlasImage;

    public NvSprite(int x, int y, int w, int h, NvImage atlasImage, String atlasName, String spriteFileName) {
        super(x, y, w, h);
        this.atlasName = atlasName;
        this.atlasImage = atlasImage;
        this.defaultSpriteFileName = spriteFileName;
        this.spriteFileName = spriteFileName;
        loadSprites();
    }

    public void changeImage(String spriteFileName){
        this.spriteFileName = spriteFileName;
        loadSprites();
    }

    public void changeToDefault(){
        this.spriteFileName = defaultSpriteFileName;
        loadSprites();
    }

    private void loadSprites(){
        this.region = NvContext.getInstance().assets().getRegion(atlasName, spriteFileName);
    }


    @Override
    public void drawIntern(NvGraphic g) {
        g.drawImageRegion(atlasImage, 0, 0, getW(), getH(), region.u1(), region.v1(), region.u2(), region.v2());
    }

    @Override
    public void update(float dt) {

    }
}
