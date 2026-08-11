import nv.core.ContextBuilder;
import nv.core.ScreenSize;
import nv.core.components.NvCont;
import nv.core.io.AudioManager;
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
    context.changeFont(new Font("monospaced", Font.PLAIN, (int) (context.getRenderWidth()*0.015)));

    var page = context.addAndSetPage("MainPage", NvCont.newPage());
    page.setBackground(0,0,0);
    WindowFrame frame = new WindowFrame();

    AudioManager.load("beat.mp3");
    AudioManager.setSpeed("beat.mp3", 1.6f);

    List<Option> options = List.of(
            new Dashboard(),
            new Cpu()
    );

    frame.setOptions(options);
    page.addChild(frame);

    context.run();
}