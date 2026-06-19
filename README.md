# 🖥️ Terminal MAC

A custom POSIX-style shell built in **Java** — runs on macOS, supports built-in commands, PATH execution, I/O redirection, quoting, and tab completion.

---

## 🚀 Features

- Built-in shell commands (`echo`, `pwd`, `cd`, `type`, `exit`)
- Execute any external command from `PATH`
- I/O redirection (`>`, `>>`, `2>`, `2>>`)
- Single/double quote and backslash escaping
- Tab completion with `complete`

---

## 📁 Project Structure

```
Terminal/
├── Main.java               # Entry point + shell loop
├── Main$CommandLine.class  # Compiled inner class
├── Main$Redirect.class     # Compiled redirection handler
├── Main.class              # Compiled main class
├── aadi.txt                # Sample test file
└── README.md
```

---

## ⚙️ Setup & Run

### Compile

```bash
javac Main.java
```

### Run

```bash
java Main
```

---

## 📖 Commands Reference

### Built-in Commands

| Command | Kya karta hai | Example |
|---|---|---|
| `echo` | Text print karta hai | `echo hello world` |
| `pwd` | Current directory path | `pwd` |
| `cd` | Directory change karta hai | `cd ..` / `cd ~/Desktop` |
| `type` | Builtin hai ya PATH file | `type echo` / `type ls` |
| `exit` | Shell band karta hai | `exit` |
| `complete` | Tab completion setup | `complete -W "foo bar" mycmd` |

#### `complete` Sub-options

```bash
complete -W "word1 word2" command   # us command ke liye words set karo
complete -p                          # saari registered completions dikhao
complete -r command                  # kisi command ki completion hatao
```

---

### External Commands

PATH mein jo bhi executable ho, wo seedha chalega:

```bash
ls -la
cat file.txt
python script.py
java Main
```

Relative/absolute path se bhi:

```bash
./script.sh
/usr/bin/whoami
```

Agar command na mile:

```
command: command not found
```

---

### I/O Redirection

| Syntax | Matlab |
|---|---|
| `> file` | stdout → file (overwrite) |
| `>> file` | stdout → file (append) |
| `1> file` | stdout redirect |
| `1>> file` | stdout append |
| `2> file` | stderr redirect |
| `2>> file` | stderr append |

**Examples:**

```bash
echo hi > out.txt
ls -la 2> errors.txt
echo more >> out.txt
```

---

### Quoting & Escaping

| Style | Matlab |
|---|---|
| `'hello world'` | Single quotes — sab literal |
| `"hello world"` | Double quotes — limited escaping |
| `hello\ world` | Backslash — space escape |

**Examples:**

```bash
echo 'hello world'
echo "hello world"
echo hello\ world
```

---

### Tab Completion

Tab dabane par:
- **Pehla word** → builtins + PATH executables + current folder files
- **Baad ke words** → `complete -W` se set kiye words, ya files

```bash
complete -W "apple banana cherry" fruit
# phir: fruit ap<Tab> → apple
```

---

## ❌ Supported Nahi Hai (Abhi)

| Feature | Example |
|---|---|
| Pipes | `ls \| grep foo` |
| Command chaining | `&&`, `\|\|`, `;` |
| Background jobs | `command &` |
| Variables | `$HOME`, `$PATH` |
| Globbing | `*.txt` |
| Input redirect | `< file` |
| Builtins | `export`, `unset`, `history` |

---

## 🧪 Example Session

```bash
$ echo "Hello my name is Aditya"
Hello my name is Aditya

$ pwd
/Users/adityapandit/Desktop/Terminal

$ type echo
echo is a shell builtin

$ type ls
ls is /bin/ls

$ cd ..
$ ls
Terminal   Gen AI   Backend ...

$ exit
```

---

## 🔧 Tech

- **Language:** Java
- **Platform:** macOS (Unix-compatible)
- **Dependencies:** None — pure Java standard library

---

## 📄 License

MIT License — free to use and modify.
