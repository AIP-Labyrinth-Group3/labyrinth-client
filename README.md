# Das verrückte Labyrinth - Client

![Build Status](https://github.com/AIP-Labyrinth-Group3/labyrinth-client/workflows/Client%20CI/badge.svg)
![Java](https://img.shields.io/badge/Java-17-blue)

Desktop client for "Das verrückte Labyrinth" - MCI AIP Project WS 2025/26

## Status

🚧 **In Development** - CI/CD Infrastructure setup complete, application code will be added incrementally.

## Planned Features

- ✅ CI/CD Infrastructure
- 🔜 JavaFX Desktop GUI
- 🔜 Server Discovery
- 🔜 WebSocket Communication
- 🔜 Interactive Game Board
- 🔜 AI Mode
- 🔜 Statistics Display

## Setup

### Prerequisites
- JDK 17+
- Maven 3.8+

### Build
```bash
mvn clean compile
```

## CI/CD

- **CI Pipeline**: Validates and compiles on every push/PR to `main` and `develop`
- **CD Pipeline**: Runs on push to `main`

## Project Structure
```
labyrinth-client/
├── .github/workflows/    # CI/CD pipelines
├── src/                  # Source code (to be added)
│   ├── main/
│   │   ├── java/        # Java source files
│   │   └── resources/   # FXML, CSS, images
│   └── test/            # Test files
├── pom.xml              # Maven configuration
└── README.md
```

## Development Workflow

See [CONTRIBUTING.md](CONTRIBUTING.md) for development guidelines.

## Future Technology Stack

- **Java 17**
- **JavaFX 21** - GUI Framework
- **Maven** - Build Tool
- **WebSocket** - Real-time communication
- **Jackson** - JSON Processing
- **JUnit 5** - Testing

## Team

**Gruppe 3**
- Clemens Siebers
- Rene Stockinger
- Andreas Rofner
- Mario Gottwald
- Simon Raass
- Manuel Kirchebner
- David Strauß

## Related Repositories

- [Server Repository](https://github.com/AIP-Labyrinth-Group3/labyrinth-server)
- [API Specification](https://github.com/thomasklammer/labyrinth-interface)

## Documentation

- [Pflichtenheft](docs/Pflichtenheft.pdf) (will be added)
- [Lastenheft](docs/Lastenheft.pdf) (will be added)

## License

Educational project for Advanced Integrative Project at MCI Innsbruck.