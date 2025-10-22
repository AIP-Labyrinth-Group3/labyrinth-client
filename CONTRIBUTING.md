# Contributing Guide - Das verrückte Labyrinth Client

## Current Status

🚧 **Infrastructure Phase** - CI/CD is set up, application code will be added step by step.

## Development Roadmap

### Phase 1: Infrastructure ✅
- [x] CI/CD Setup
- [x] Git Workflow
- [x] Documentation

### Phase 2: Basic Setup 🔜
- [ ] Add JavaFX dependencies
- [ ] Create main application class
- [ ] Setup basic window structure
- [ ] Add application tests

### Phase 3: UI Development 🔜
- [ ] Server selection screen
- [ ] Lobby screen
- [ ] Game board view
- [ ] Statistics view

### Phase 4: Networking 🔜
- [ ] WebSocket client
- [ ] Server communication
- [ ] State synchronization

### Phase 5: Game Logic 🔜
- [ ] Player movement
- [ ] Board manipulation
- [ ] AI implementation

---

## Git Workflow

### Branch Strategy

- **`main`**: Production-ready code
- **`develop`**: Development integration branch
- **`feature/*`**: New features
- **`bugfix/*`**: Bug fixes
- **`hotfix/*`**: Critical production fixes

### Feature Development Workflow
```bash
# 1. Update develop branch
git checkout develop
git pull origin develop

# 2. Create feature branch
git checkout -b feature/my-feature-name

# 3. Develop and commit
git add .
git commit -m "feat: description of change"

# 4. Push branch
git push -u origin feature/my-feature-name

# 5. Create Pull Request on GitHub
# 6. After approval and CI success: Merge
# 7. Delete feature branch
```

### Bugfix Workflow
```bash
git checkout develop
git pull origin develop
git checkout -b bugfix/bug-description
# ... fix bug ...
git commit -m "fix: description of bugfix"
git push -u origin bugfix/bug-description
# Create PR
```

### Hotfix Workflow (for critical production issues)
```bash
git checkout main
git pull origin main
git checkout -b hotfix/critical-fix
# ... fix issue ...
git commit -m "fix: critical issue description"
git push -u origin hotfix/critical-fix
# Create PR to BOTH main and develop
```

---

## Commit Message Convention

We use **Conventional Commits**:
```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- **feat**: New feature
- **fix**: Bug fix
- **docs**: Documentation only
- **style**: Code formatting (no functional change)
- **refactor**: Code refactoring
- **test**: Adding or updating tests
- **chore**: Build process, dependencies, etc.
- **perf**: Performance improvement

### Examples
```bash
feat(ui): add server selection screen

Implement initial server selection UI with JavaFX.
Users can now see and select available game servers.

Closes #12

---

fix(network): correct WebSocket connection handling

The client was not properly reconnecting after connection loss.
Added retry logic with exponential backoff.

Fixes #25

---

docs: update README with JavaFX setup instructions
```

---

## Code Quality Standards

### Java Code Conventions

- Follow [Java Code Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-contents.html)
- Use meaningful variable and method names
- Maximum line length: 120 characters
- Maximum method length: 150 lines

### JavaFX Best Practices

- Use FXML for UI layouts
- Separate UI logic from business logic
- Use CSS for styling
- Follow MVC/MVP pattern

### Documentation

- JavaDoc for all public and protected classes/methods
- Inline comments for complex logic
- README files in each major package
```java
/**
 * Connects to the game server and establishes a WebSocket connection.
 *
 * @param serverUri the URI of the game server
 * @return true if connection successful, false otherwise
 * @throws Exception if connection cannot be established
 */
public boolean connectToServer(String serverUri) throws ConnectionException {
    // Implementation
}
```

---

## Testing (when code is added)

### Test Structure
```
src/test/java/
└── com/labyrinth/client/
    ├── ui/              # UI tests (TestFX)
    ├── network/         # Network layer tests
    ├── service/         # Business logic tests
    └── util/            # Utility tests
```

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=GameBoardTest

# Run with coverage
mvn test jacoco:report
```

### Test Guidelines (for future)

- Minimum 70% code coverage
- Use JUnit 5
- Use Mockito for mocking
- Use TestFX for JavaFX UI tests
- Test naming: `shouldDoSomething_whenCondition()`

---

## Pull Request Process

### Before Creating PR
```bash
# 1. Update your branch with latest develop
git checkout develop
git pull origin develop
git checkout feature/my-feature
git rebase develop

# 2. Build and test locally
mvn clean compile

# 3. Commit all changes
git add .
git commit -m "feat: description"

# 4. Push to remote
git push origin feature/my-feature
```

### PR Template

When creating a PR, include:
```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Checklist
- [ ] Code compiles without errors
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex code
- [ ] Documentation updated
- [ ] No new warnings introduced
- [ ] Tests added (when applicable)
- [ ] All tests pass locally

## Screenshots (if UI changes)
[Add screenshots here]

## Related Issues
Closes #(issue number)
```

### Review Process

- At least 1 approval required
- All CI checks must pass
- All conversations must be resolved
- No merge conflicts

### Merging

- Use **"Squash and merge"** for feature branches
- Use **"Merge commit"** for release branches
- Delete branch after merge

---

## Development Environment Setup

### Required Software

1. **JDK 17 or higher**
```bash
   java -version  # Should show 17 or higher
```

2. **Maven 3.8+**
```bash
   mvn -version
```

3. **Git 2.30+**
```bash
   git --version
```

4. **IDE** (Choose one)
    - IntelliJ IDEA (recommended)
    - Eclipse
    - VS Code with Java extensions

### Project Setup
```bash
# Clone repository
git clone https://github.com/AIP-Labyrinth-Group3/labyrinth-client.git
cd labyrinth-client

# Build project
mvn clean compile

# Run (when main class exists)
mvn javafx:run
```

### IDE Configuration

#### IntelliJ IDEA

1. **Import Project**
    - File → Open → Select `pom.xml`
    - Import as Maven project

2. **Configure JDK**
    - File → Project Structure → Project
    - Set Project SDK to Java 17

3. **Install Plugins** (when needed)
    - SceneBuilder (for FXML editing)
    - SonarLint (code quality)

#### Eclipse

1. **Import Project**
    - File → Import → Maven → Existing Maven Projects
    - Select project directory

2. **Configure JDK**
    - Project Properties → Java Build Path
    - Add JDK 17

#### VS Code

1. **Install Extensions**
    - Extension Pack for Java
    - Maven for Java

2. **Open Project**
    - File → Open Folder → Select project directory

---

## Common Issues

### "Maven build fails"
```bash
# Clear Maven cache
mvn clean install -U

# Or delete local repository
rm -rf ~/.m2/repository
mvn clean install
```

### "Cannot find JDK 17"
```bash
# Set JAVA_HOME environment variable
export JAVA_HOME=/path/to/jdk-17

# Or in Maven settings
mvn -version  # Check if correct Java version
```

### "Git merge conflict"
```bash
# Update develop
git checkout develop
git pull origin develop

# Go back to feature branch
git checkout feature/my-feature

# Rebase
git rebase develop

# Resolve conflicts in IDE
# After resolving:
git add .
git rebase --continue

# Force push (if already pushed)
git push --force-with-lease
```

---

## Getting Help

- **Issues**: Create an issue on GitHub
- **Questions**: Ask in the team chat
- **Documentation**: Check README.md and docs/

---

## Resources

- [JavaFX Documentation](https://openjfx.io/)
- [Maven Guide](https://maven.apache.org/guides/)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [Git Flow](https://nvie.com/posts/a-successful-git-branching-model/)

---

## Next Steps

Once infrastructure is complete:

1. Add JavaFX dependencies to `pom.xml`
2. Create main JavaFX application class
3. Implement basic window structure
4. Add first UI tests
5. Extend CI/CD with test execution