//DEPS io.github.jratatui:jratatui-dsl:LATEST
//JAVA 11+

import io.github.jratatui.tui.TuiRunner;
import io.github.jratatui.tui.Keys;
import io.github.jratatui.widgets.paragraph.Paragraph;
import io.github.jratatui.text.Text;

public class HelloWorld {
    public static void main(String[] args) throws Exception {
        try (var tui = TuiRunner.create()) {
            tui.run(
                (event, runner) -> {
                    if (Keys.isQuit(event)) {
                        runner.quit();
                        return false;
                    }
                    return false;
                },
                frame -> {
                    var paragraph = Paragraph.builder()
                        .text(Text.from("Hello, JRatatui! Press 'q' to quit."))
                        .build();
                    frame.renderWidget(paragraph, frame.area());
                }
            );
        }
    }
}