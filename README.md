# Directory Tree Visualizer 🌳
![DirectoryTreePrinter](https://socialify.git.ci/toaha63/DirectoryTreePrinter/image?description=1&font=JetBrains+Mono&forks=1&issues=1&language=1&name=1&owner=1&pattern=Signal&pulls=1&stargazers=1&theme=Dark)
A modern, high-performance Java command-line application that generates clean, hierarchical directory structure visualizations. Built with Java 21+, this tool leverages **virtual threads** for efficient non-blocking I/O operations, demonstrating modern Java concurrency patterns in a practical utility.

## ✨ Features

*   **Visual Tree Output**: Creates professional directory trees using Unicode box-drawing characters
*   **Multiple Output Modes**: Console output, file export, or simultaneous both
*   **Hidden File Support**: Optional inclusion of hidden files and directories (`.git`, `.config`, etc.)
*   **Modern Concurrency**: Utilizes Java 21's virtual threads for non-blocking file I/O operations
*   **Sorted Listing**: Alphabetical sorting with directories grouped before files
*   **Cross-Platform**: Works seamlessly on Windows, Linux, and macOS
*   **Zero Dependencies**: Single Java file implementation - just compile and run

## 🚀 Why This Project?

This tool serves as an excellent example of **applying cutting-edge Java concurrency features to solve real-world problems**. It demonstrates how virtual threads can dramatically simplify asynchronous programming while maintaining performance and readability, making it an ideal learning resource for modern Java development.

## 📋 Prerequisites

**OpenJDK 21 or later is required.** Virtual threads are a feature of Project Loom introduced in Java 21.

### Recommended Installation

**Eclipse Temurin (OpenJDK) 21+** is recommended for best performance and licensing:

```bash
# Ubuntu/Debian
sudo apt install openjdk-21-jdk

# macOS (Homebrew)
brew install openjdk@21

# Windows (Winget)
winget install EclipseAdoptium.Temurin.21.JDK
```

### Verification

Verify your installation:
```bash
java -version
```

Expected output should indicate version 21 or higher.

## 🛠️ Installation & Compilation

1.  **Clone or Download:**
    ```bash
    git clone https://github.com/toaha63/DirectoryTreePrinter.git
    cd DirectoryTreePrinter
    ```

2.  **Compile the Source:**
    ```bash
    javac DirectoryTreePrinter.java
    ```
    This generates the '.class' files ready for execution.

## 💻 Usage

Run the program from the command line with the target directory and optional flags:

**Basic Syntax:**
```bash
java DirectoryTreePrinter <path-to-directory> [flags]
```

### Command Line Flags

| Flag | Description | Example |
| :--- | :--- | :--- |
| (none) | Default: Print to console, exclude hidden files | `java DirectoryTreePrinter .` |
| `-c` | Console output only | `java DirectoryTreePrinter . -c` |
| `-f` | File output only (generates `<dirname>_tree.txt`) | `java DirectoryTreePrinter . -f` |
| `-b` | Both console and file output | `java DirectoryTreePrinter . -b` |
| `-h` | Include hidden files and directories | `java DirectoryTreePrinter . -h` |

### Examples

**Basic usage (current directory):**
```bash
java DirectoryTreePrinter .
```

**Include hidden files in console output:**
```bash
java DirectoryTreePrinter /path/to/project -c -h
```

**Export complete structure including hidden files:**
```bash
java DirectoryTreePrinter /path/to/project -f -h
```

**Real-time view with simultaneous backup:**
```bash
java DirectoryTreePrinter /path/to/project -b -h
```

## 📖 Sample Output

Example output for a development project:

```
my-project/
├── .git/
│   ├── HEAD
│   ├── config
│   └── refs/
├── .gitignore
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── App.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── AppTest.java
├── target/
│   ├── classes/
│   │   └── com/
│   │       └── example/
│   │           ├── App.class
│   │           └── AppTest.class
│   └── my-app.jar
├── pom.xml
└── README.md
```

## ⚙️ Technical Implementation

### Virtual Threads in Action

This application demonstrates practical use of Project Loom's virtual threads for I/O-bound operations:

```java
Thread virtualThread = Thread.startVirtualThread(() ->
{
    try (PrintWriter writer = new PrintWriter(new FileWriter(outputFileName)))
    {
        // Non-blocking file write operation
        printDirectoryTreeToFile(rootDir, new Vector<>(), writer, showHidden);
        System.out.println("Directory tree successfully written to: " + outputFileName);
    }
    catch (IOException e)
    {
        exceptionRef.set(e);
    }
});
```

**Performance Benefits:**
- **Non-blocking I/O**: File operations don't block carrier threads
- **Resource efficiency**: Millions of virtual threads can coexist with minimal overhead
- **Simplified code**: Maintains synchronous style without callback complexity

### Hidden File Detection

Uses Java's built-in `isHidden()` method for cross-platform compatibility:
```java
if (showHidden || !file.isHidden())
{
    filteredFiles.add(file);
}
```

## 🧪 Testing

Test the application with various directory structures:

```bash
# Test with home directory (excluding hidden files)
java DirectoryTreePrinter ~

# Test with system directories (including hidden files)
java DirectoryTreePrinter /etc -h

# Test output to file
java DirectoryTreePrinter /var/log -f
```

## 🤝 Contributing

We welcome contributions! Please feel free to submit issues, feature requests, or pull requests.

**Potential Enhancements:**
- [ ] Depth limiting (`--depth` flag)
- [ ] File size and metadata display
- [ ] Output format options (JSON, XML)
- [ ] Exclusion patterns (`.gitignore` support)
- [ ] Colorized output support
- [ ] Graphical user interface (GUI) version

## 📊 Performance Notes

- **Memory Efficient**: Uses vectors for thread-safe operations with minimal overhead
- **Scalable**: Handles directories with thousands of files efficiently
- **Fast Execution**: Virtual threads ensure responsive performance during I/O operations

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

## 🙋‍♂️ Support

For questions or issues:
1. Check existing [GitHub Issues](../../issues)
2. Create a new issue with detailed description
3. Include your Java version and OS environment

---

**Built with ☕ Java 21+ and Virtual Threads for modern development.**
