# JRatatui

A pure Java port of [ratatui](https://github.com/ratatui/ratatui), the Rust library for building terminal user interfaces.

## Overview

JRatatui brings the immediate-mode TUI paradigm from Rust to the Java ecosystem. It provides a comprehensive set of widgets and a layout system for building rich terminal applications with modern Java idioms.

### Key Features

- **Immediate-mode rendering** - Redraw the entire UI each frame for simple state management
- **Intermediate buffer system** - Widgets render to a buffer, enabling diff-based terminal updates
- **Constraint-based layout** - Flexible layout system with percentage, fixed, ratio, and proportional sizing
- **JLine 3 backend** - Cross-platform terminal support including Windows via Jansi
- **High-level TUI framework** - TuiRunner eliminates boilerplate with built-in event handling
- **PicoCLI integration** - Optional module for CLI argument parsing
- **GraalVM native image support** - Compile to native executables for instant startup
- **Works everywhere** - Core is Java 8+ compatible, but following patterns that works really well with modern Java idioms

## Modules

| Module | Description |
|--------|-------------|
| `jratatui-core` | Core types: Buffer, Cell, Rect, Style, Layout, Text primitives |
| `jratatui-widgets` | All widget implementations (see below) |
| `jratatui-jline` | JLine 3 terminal backend |
| `jratatui-tui` | High-level TUI framework with TuiRunner, event handling, and key helpers |
| `jratatui-dsl` | Fluent DSL for declarative UI construction with components and focus management |
| `jratatui-picocli` | Optional PicoCLI integration for CLI argument parsing |
| `demos/*` | Demo applications showcasing widgets and features |

## Requirements

- Java 8 or later, Java 17+ highly recommended
- Gradle 9.x

## Quick Start

### Using TuiRunner (Recommended)

The `jratatui-tui` module provides a high-level framework that eliminates boilerplate:

[examples/HelloWorld.java](examples/HelloWorld.java):
```java
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
```

### With Mouse and Animation


```java
import io.github.jratatui.tui.TuiConfig;
import io.github.jratatui.tui.TuiRunner;
import java.time.Duration;

var config = TuiConfig.builder()
    .mouseCapture(true)
    .tickRate(Duration.ofMillis(16))  // ~60fps animation
    .build();

try (var tui = TuiRunner.create(config)) {
    tui.run(
        (event, runner) -> switch (event) {
            case KeyEvent k when Keys.isQuit(k) -> { runner.quit(); yield false; }
            case MouseEvent m -> { handleMouse(m); yield true; }
            case TickEvent t -> { updateAnimation(t); yield true; }
            default -> false;
        },
        frame -> render(frame)
    );
}
```

### With PicoCLI Integration

```java
import io.github.jratatui.picocli.TuiCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "myapp", mixinStandardHelpOptions = true)
public class MyApp extends TuiCommand {

    @Option(names = {"-t", "--title"}, description = "Window title")
    private String title = "My App";

    @Override
    protected void runTui(TuiRunner runner) throws Exception {
        runner.run(this::handleEvent, this::render);
    }

    public static void main(String[] args) {
        System.exit(new CommandLine(new MyApp()).execute(args));
    }
}
```

### Using the DSL (Declarative)

The `jratatui-dsl` module provides a fluent DSL for declarative UI construction:

```java
import static io.github.jratatui.dsl.Dsl.*;
import io.github.jratatui.dsl.app.DslApp;
import io.github.jratatui.dsl.element.Element;

public class HelloDsl extends DslApp {

    @Override
    protected Element render() {
        return panel("Hello",
            text("Welcome to JRatatui DSL!").bold().cyan(),
            spacer(),
            text("Press 'q' to quit").dim()
        ).rounded();
    }

    public static void main(String[] args) throws Exception {
        new HelloDsl().run();
    }
}
```

#### DSL Elements

| Element | Factory Method | Description |
|---------|----------------|-------------|
| Text | `text("Hello")` | Styled text content |
| Panel | `panel("Title", ...)` | Bordered container with title |
| Row | `row(a, b, c)` | Horizontal layout |
| Column | `column(a, b, c)` | Vertical layout |
| Spacer | `spacer()` | Flexible empty space |
| Gauge | `gauge(0.75)` | Progress bar |
| LineGauge | `lineGauge(50)` | Single-line progress indicator |
| Sparkline | `sparkline(1,2,3,4,5)` | Mini data chart |
| List | `list("A", "B", "C")` | Selectable list |
| Table | `table()` | Data table with rows/columns |
| Tabs | `tabs("Home", "Settings")` | Tab bar |
| TextInput | `textInput(state)` | Text input field |
| BarChart | `barChart(10, 20, 30)` | Bar chart |
| Chart | `chart()` | Line/scatter plots |
| Canvas | `canvas()` | Drawing surface |
| Calendar | `calendar()` | Monthly calendar |
| Scrollbar | `scrollbar()` | Scroll position indicator |

#### DSL Examples

```java
// Progress with color coding
gauge(0.75)
    .label("Loading...")
    .gaugeColor(Color.GREEN)
    .title("Progress")
    .rounded()

// Sparkline chart
sparkline(cpuHistory)
    .color(Color.CYAN)
    .title("CPU Usage")
    .rounded()

// Selectable list
list("Option 1", "Option 2", "Option 3")
    .state(listState)
    .highlightColor(Color.YELLOW)
    .title("Menu")
    .rounded()

// Tab bar
tabs("Home", "Settings", "About")
    .selected(0)
    .highlightColor(Color.CYAN)

// Table with data
table()
    .header("Name", "Age", "City")
    .row("Alice", "30", "NYC")
    .row("Bob", "25", "LA")
    .widths(Constraint.percentage(40), Constraint.length(10), Constraint.fill())
    .title("Users")
    .rounded()

// Canvas drawing
canvas(-10, 10, -10, 10)
    .paint(ctx -> {
        ctx.draw(new Circle(0, 0, 5, Color.RED));
        ctx.draw(new Line(-5, -5, 5, 5, Color.GREEN));
    })
    .title("Drawing")
    .rounded()
```

#### Features

- **Static imports** - `text()`, `panel()`, `row()`, `column()`, `spacer()`, and all widget factories
- **Fluent styling** - `.bold()`, `.cyan()`, `.onBlue()`, `.rounded()`, `.borderColor()`
- **Layout constraints** - `.length(n)`, `.percent(n)`, `.fill()`, `.min(n)`, `.max(n)`
- **Automatic focus** - Tab navigation, click-to-focus
- **Component events** - Components handle their own key/mouse events via `.onKeyEvent()` and `.onMouseEvent()`
- **Drag support** - `.draggable()` for movable elements

### Low-Level API

For more control, use the terminal directly:

```java
import io.github.jratatui.backend.jline.JLineBackend;
import io.github.jratatui.terminal.Terminal;

try (var backend = new JLineBackend()) {
    backend.enableRawMode();
    backend.enterAlternateScreen();

    var terminal = new Terminal<>(backend);
    terminal.draw(frame -> {
        // render widgets...
    });
}
```

## Building

```bash
# Build the project
./gradlew build

# Run demos
./gradlew :demos:basic-demo:run
./gradlew :demos:tui-demo:run
./gradlew :demos:dsl-demo:run
./gradlew :demos:picocli-demo:run
./gradlew :demos:chart-demo:run
./gradlew :demos:canvas-demo:run

# Build native image (requires GraalVM)
./gradlew :demos:basic-demo:nativeCompile
```

## Widgets

All standard ratatui widgets are implemented:

| Widget | Type | Description |
|--------|------|-------------|
| **Block** | Stateless | Container with borders, titles, and padding |
| **Paragraph** | Stateless | Multi-line text with wrapping and alignment |
| **List** | Stateful | Scrollable list with selection |
| **Table** | Stateful | Grid with rows, columns, and selection |
| **Tabs** | Stateful | Tab bar with selection |
| **Gauge** | Stateless | Progress bar with percentage |
| **LineGauge** | Stateless | Horizontal line progress indicator |
| **Sparkline** | Stateless | Mini line chart for data series |
| **BarChart** | Stateless | Vertical bar chart |
| **Chart** | Stateless | Line and scatter plots with axes |
| **Canvas** | Stateless | Drawing surface with shapes (braille/block characters) |
| **Calendar** | Stateless | Monthly calendar with date styling |
| **Scrollbar** | Stateful | Visual scroll position indicator |
| **Clear** | Stateless | Clears an area (for widget layering) |
| **TextInput** | Stateful | Single-line text input (JRatatui addition) |

## Demo Applications

| Demo | Description |
|------|-------------|
| `basic-demo` | Interactive list with text input |
| `tui-demo` | Showcases TuiRunner with keyboard, mouse, and animation |
| `dsl-demo` | Widget Playground showcasing DSL with draggable panels |
| `jtop-demo` | System monitor (like "top") using the DSL |
| `picocli-demo` | PicoCLI integration with CLI options |
| `gauge-demo` | Progress bars and line gauges |
| `table-demo` | Table widget with selection |
| `tabs-demo` | Tab navigation |
| `scrollbar-demo` | Scrollbar with content |
| `sparkline-demo` | Sparkline data visualization |
| `barchart-demo` | Bar chart widget |
| `chart-demo` | Line and scatter charts |
| `canvas-demo` | Canvas with shapes and braille drawing |
| `calendar-demo` | Monthly calendar with events |

## Key Bindings (via Keys utility)

The `Keys` class provides helpers for common key patterns:

```java
import static io.github.jratatui.tui.Keys.*;

// Quit patterns
isQuit(event)      // q, Q, or Ctrl+C

// Vim-style navigation (also accepts arrow keys)
isUp(event)        // Up arrow or k/K
isDown(event)      // Down arrow or j/J
isLeft(event)      // Left arrow or h/H
isRight(event)     // Right arrow or l/L

// Page navigation
isPageUp(event)    // PageUp or Ctrl+U
isPageDown(event)  // PageDown or Ctrl+D
isHome(event)      // Home or g
isEnd(event)       // End or G

// Actions
isSelect(event)    // Enter or Space
isEscape(event)    // Escape
isTab(event)       // Tab
isBackTab(event)   // Shift+Tab
```

## Acknowledgments

This project is a port of [ratatui](https://github.com/ratatui/ratatui), an excellent Rust TUI library. We thank the ratatui maintainers and contributors for their work.

## License

MIT License - see [LICENSE](LICENSE) for details.
