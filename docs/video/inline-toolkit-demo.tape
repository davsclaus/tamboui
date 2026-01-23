Source shared_.tape

# Setup
Hide
Type "jbang inline-toolkit-demo"
Enter
Sleep 2
Show

# Form input phase - enter project name and author
Sleep 1s
Type "my-awesome-app"
Sleep 500ms
Tab
Sleep 500ms
Type "John Doe"
Sleep 500ms
Enter
Sleep 1s

# Watch npm install - details visible by default, press 'd' to collapse then expand
Sleep 3s
Type "d"
Sleep 3s
Type "d"
Sleep 6s

# Answer the "Continue with native module build?" prompt
Type "y"

# Watch native build - collapse then expand details
Sleep 2s
Type "d"
Sleep 2s
Type "d"
Sleep 4s

# Exit when done
Sleep 2s
