# Java 8 Compatibility Notes

- Target: core library modules now avoid Java 9+ APIs and Java 16+ syntax (records, sealed, pattern matching).
- Collections: Added `io.github.jratatui.util.CollectionUtil` with `listCopyOf` / `mapCopyOf` / `toList` helpers and static imports; removed `List.of`, `List.copyOf`, `Map.of`, `Stream.toList`.
- Types converted from records/sealed to classes/interfaces with getters and explicit equals/hashCode/toString:
  - Core: `layout/{Rect,Position,Size,Margin,Constraint}`, `text/{Span,Line,Text}`, `style/{Color,Style}`, `buffer/{Cell,CellUpdate}`, `terminal/CompletedFrame`.
  - Events/config: `tui/{TuiConfig, event/Event, KeyEvent, KeyModifiers, MouseEvent, ResizeEvent, TickEvent}`; updated `Keys` to avoid pattern matching.
  - Widgets: `block/{Padding,Title,BorderSet}`, `list/ListItem`, `barchart/BarChart.BarSet`, `sparkline/Sparkline.BarSet`, `scrollbar/SymbolSet`, `gauge/LineGauge.LineSet`, canvas shapes (`Circle`, `Line`, `Rectangle`, `Points`), painter helpers (`Painter.GridPoint`), context labels.
  - Backend: `JLineBackend` color handling rewritten without pattern matching on records.
- Demos: `tui-demo` switch expression replaced with if/instanceof chain (sealed exhaustiveness no longer available).
- Input parsing: `EventParser` rewritten to classic switch statements; String `formatted()` replaced with `String.format` across tui events/config.
- DSL: replaced pattern matching and switch expressions in `EventRouter`; removed `List.getFirst/getLast` and Java 11 `toArray` overloads in focus manager and layout DSL widgets.
- Tests: `./gradlew check` executed successfully after each change (required elevated permission to write Gradle wrapper cache).
- Remaining considerations:
  - Demos can still use newer Java, but core artifacts now compile on Java 8.
  - Equality/hashCode implementations follow record semantics; immutability preserved via final fields and defensive copies.




TODO:
  [ ] - add vistor or polymorphic handler to make Event handling nice again