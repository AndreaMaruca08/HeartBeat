import nv.core.ContextBuilder;
import nv.core.components.NvCont;
import view.Cpu;
import view.Dashboard;
import view.Option;
import view.WindowFrame;

import java.awt.*;
import java.util.List;

void main() {
    var context = new ContextBuilder("HeartBeat")
            .setVsync(true)
            .build();

    var page = context.addAndSetPage("MainPage", NvCont.newPage());
    page.setBackground(0,0,0);
    WindowFrame frame = new WindowFrame();

    List<Option> options = List.of(
            new Dashboard(),
            new Cpu()
    );
    context.changeFont(new Font("monospaced", Font.PLAIN, (int) (context.getRenderWidth()*0.015)));

    frame.setOptions(options);
    page.addChild(frame);

    context.run();
}