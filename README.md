# Maze Pathfinder

A Java-based application for generating random mazes and finding the shortest path between a start (`S`) and an end point (`K`) using pathfinding algorithms.

## Features

- **Random Maze Generation**: Creates a maze with walls (`0`) and pathways (`1`).
- **Customizable Parameters**:
  - Maze size (currently set to `10x10`).
  - Minimum distance between the start and end points.
- **Shortest Path Finding**: Uses Breadth-First Search (BFS) to calculate the shortest path from `S` to `K`.
- **Interactive Visualization**: Displays the maze with:
  - `S` (start) in **green**.
  - `K` (end) in **red**.
  - The shortest path marked with dots (`•`) in **green**.

## Preview

Example output:

### Generated Maze:

```
K 1 1 1 0 0 1 1 1 1 
0 0 1 0 1 1 1 1 1 0 
0 1 1 1 1 0 0 1 1 0 
1 1 0 1 1 1 1 1 0 0 
1 1 0 0 1 1 0 1 0 0 
1 0 0 1 1 1 0 1 0 0 
1 1 0 0 1 1 1 1 1 1 
1 0 0 1 1 0 0 0 0 1 
1 0 0 1 0 0 0 0 1 1 
1 1 1 1 1 1 0 S 1 1 
```

### Shortest Path:

```
K • • 1 0 0 1 1 1 1 
0 0 • 0 1 1 1 1 1 0 
0 1 • • • 0 0 1 1 0 
1 1 0 1 • • • • 0 0 
1 1 0 0 1 1 0 • 0 0 
1 0 0 1 1 1 0 • 0 0 
1 1 0 0 1 1 1 • • • 
1 0 0 1 1 0 0 0 0 • 
1 0 0 1 0 0 0 0 • • 
1 1 1 1 1 1 0 S • 1
```

## Requirements

- **Java 8 or higher**
- Basic knowledge of Java to modify and run the program.

## Installation

1. Clone the repository:
   
   ```bash
   git clone https://github.com/dawciobiel/find-shortest-path-in-maze
   cd find-shortest-path-in-labirynth
   ```

2. Compile the code:  
   
   ```bash
   javac MazeGenerator.java
   ```

3. Run the application:
   
   ```bash
   java MazeGenerator
   ```

## How It Works

1. **Random Maze Generation**:  
   The maze is initialized with walls (`0`) and empty pathways (`1`), with a guaranteed path between the start (`S`) and end (`K`) points using a modified DFS algorithm.

2. **Shortest Path Calculation**:  
   The Breadth-First Search (BFS) algorithm is used to calculate the shortest path between the start and end points, which is then visualized.

3. **Color-Coded Output**:  
   
   - `S` (green): Start point.  
   - `K` (red): End point.  
   - `•` (green): Shortest path.

## Configuration

You can modify the following constants in the `MazeGenerator` class:

- `SIZE`: Maze dimensions (default is `10`).
- `MIN_DISTANCE`: Minimum Manhattan distance between the start and end points (default is `6`).

## Code Highlights

### Maze Generation

- Randomly generates start (`S`) and end (`K`) points on the maze border.
- Uses a stack-based DFS algorithm to create a guaranteed path between `S` and `K`.

### Shortest Path

- Implements BFS to compute the shortest path, storing the result in a separate matrix.

### Visualization

- Custom ANSI escape sequences are used to colorize output for better readability.

## Contributing

Contributions are welcome! To contribute:

1. Fork the repository.

2. Create a new branch:
   
   ```bash
   git checkout -b feature-name
   ```

3. Make your changes and commit:
   
   ```bash
   git commit -m "Description of changes"
   ```

4. Push the branch:
   
   ```bash
   git push origin feature-name
   ```

5. Submit a pull request.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
