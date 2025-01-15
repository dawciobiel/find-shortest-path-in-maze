import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.System.lineSeparator;
import static java.lang.System.out;

/**
 * Klasa do generowania labiryntu o określonym rozmiarze, z punktem startowym (S),
 * punktem końcowym (K) oraz możliwością wyznaczania najkrótszej ścieżki między nimi.
 *
 * @author Lewandowski Dominik, S29860
 */
public class MazeGenerator {

    public static final String POINT_WALL = "0";
    public static final String POINT_DOT = "•";
    public static final String POINT_FREE_WAY = "1";
    public static final String CHAR_DELIMITER = " ";
    private static final String POINT_START = "S"; // Litera 'S' w kolorze zielonym
    private static final String POINT_END = "K";   // Litera 'K' w kolorze czerwonym

    private static final String COLOR_GREEN = "\u001B[32m";
    private static final String COLOR_RED = "\u001B[31m";
    private static final String COLOR_DEFAULT = "\u001B[0m";

    private static final int SIZE = 10;              // Rozmiar labiryntu
    private static final int MIN_DISTANCE = 6;       // Minimalna odległość między S i K

    private static final Random randomNumberGenerator = new Random(); // Generator liczb losowych

    private static int[][] maze;                     // Macierz labiryntu
    private static Point start;                      // Punkty startowy
    private static Point end;                        // Punkt końcowy
    private static List<Point> guaranteedPath;       // Lista punktów gwarantowanej ścieżki

    /**
     * Generuje labirynt i wyświetla go w dwóch wersjach:
     * <ol>
     * <li>Podstawowa wersja z punktami S i K.</li>
     * <li>Wersja z zaznaczoną najkrótszą ścieżką.</li>
     * </ol>
     *
     * @param args Argumenty wejściowe programu
     */
    public static void main(String[] args) {
        do {
            generateMaze();
        } while (!validatePath());

        // Zapisanie oryginalnego labiryntu z punktami S i K
        int[][] originalMaze = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            originalMaze[i] = maze[i].clone();
        }
        originalMaze[start.x][start.y] = 'S';
        originalMaze[end.x][end.y] = 'K';

        out.println("Wygenerowany labirynt:");
        printMaze(originalMaze);

        out.println(lineSeparator() + "Najkrótsza ścieżka:");
        int[][] pathMaze = findPath();
        printMaze(pathMaze);
    }

    /**
     * Generuje nowy labirynt z punktami S i K na brzegach oraz gwarantowaną ścieżką między nimi.
     * A także dodaje losowe przejścia w labiryncie.
     */
    private static void generateMaze() {
        maze = new int[SIZE][SIZE];
        guaranteedPath = new ArrayList<>();

        // Wypełnienie labiryntu ścianami
        for (int i = 0; i < SIZE; i++) {
            Arrays.fill(maze[i], 0);
        }

        // Generowanie punktów S i K z minimalną odległością
        do {
            start = generateBorderPoint();
            end = generateBorderPoint();
        } while (!isValidDistance(start, end));

        generateGuaranteedPath();

        // Dodawanie losowych przejść
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (maze[i][j] == 0 && randomNumberGenerator.nextDouble() < 0.3) {
                    maze[i][j] = 1;
                }
            }
        }
    }

    /**
     * Sprawdza, czy odległość między dwoma punktami spełnia wymaganie minimalnej odległości.
     *
     * @param p1 Punkt pierwszy
     * @param p2 Punkt drugi
     * @return Zwraca {@code true} — jeśli odległość jest wystarczająca, {@code false} — w przeciwnym razie
     */
    private static boolean isValidDistance(Point p1, Point p2) {
        if (p1.x == p2.x && p1.y == p2.y) {
            return false;
        }
        int distance = abs(p1.x - p2.x) + abs(p1.y - p2.y);

        return distance >= MIN_DISTANCE;
    }

    /**
     * Generuje gwarantowaną ścieżkę między punktami S i K używając zmodyfikowanego algorytmu DFS.
     * Ścieżka jest zapisywana w polu {@code guaranteedPath}.
     */
    private static void generateGuaranteedPath() {
        Deque<Point> stack = new ArrayDeque<>();
        Point current = new Point(start.x, start.y);
        stack.push(current);
        maze[current.x][current.y] = 1;

        while (!stack.isEmpty() && (current.x != end.x || current.y != end.y)) {
            List<Point> neighbors = getUnvisitedNeighbors(current);

            if (!neighbors.isEmpty()) {
                Point next = neighbors.get(randomNumberGenerator.nextInt(neighbors.size()));
                stack.push(next);
                maze[next.x][next.y] = 1;
                current = next;
            } else {
                stack.pop();
                if (!stack.isEmpty()) {
                    current = stack.peek();
                }
            }
        }

        guaranteedPath = new ArrayList<>(stack);
    }

    /**
     * Zwraca listę nieodwiedzonych sąsiadów dla danego punktu.
     * Sąsiedzi muszą spełniać warunki labiryntu (być ścianą lub punktem końcowym).
     *
     * @param p Punkt, dla którego szukamy sąsiadów
     * @return Lista dostępnych sąsiadów
     */
    private static List<Point> getUnvisitedNeighbors(Point p) {
        List<Point> neighbors = new ArrayList<>();
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        List<int[]> shuffledDirs = new ArrayList<>(Arrays.asList(dirs));
        Collections.shuffle(shuffledDirs);

        for (int[] dir : shuffledDirs) {
            int newX = p.x + dir[0];
            int newY = p.y + dir[1];

            if (isValid(newX, newY) && maze[newX][newY] == 0) {
                if ((newX == end.x && newY == end.y) || (countWallNeighbors(newX, newY) >= 3)) {
                    neighbors.add(new Point(newX, newY));
                }
            }
        }

        return neighbors;
    }

    /**
     * Liczy liczbę sąsiednich ścian dla danego punktu.
     *
     * @param x Współrzędna X punktu
     * @param y Współrzędna Y punktu
     * @return Liczba sąsiednich ścian
     */
    private static int countWallNeighbors(int x, int y) {
        int count = 0;
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : dirs) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (!isValid(newX, newY) || maze[newX][newY] == 0) {
                count++;
            }
        }

        return count;
    }

    /**
     * Generuje losowy punkt na brzegu labiryntu.
     *
     * @return Punkt na brzegu labiryntu
     */
    private static Point generateBorderPoint() {
        if (randomNumberGenerator.nextBoolean()) {
            int x = randomNumberGenerator.nextBoolean() ? 0 : SIZE - 1;

            return new Point(x, randomNumberGenerator.nextInt(SIZE));
        } else {
            int y = randomNumberGenerator.nextBoolean() ? 0 : SIZE - 1;

            return new Point(randomNumberGenerator.nextInt(SIZE), y);
        }
    }

    /**
     * Sprawdza, czy istnieje ścieżka między punktami S i K używając algorytmu BFS.
     *
     * @return Zwraca {@code true}, jeśli ścieżka istnieje, w przeciwnym razie zwraca {@code false}.
     */
    private static boolean validatePath() {
        if (guaranteedPath.isEmpty()) {

            return false;
        }

        boolean[][] visited = new boolean[SIZE][SIZE];
        Queue<Point> queue = new LinkedList<>();
        queue.add(start);
        visited[start.x][start.y] = true;

        while (!queue.isEmpty()) {
            Point current = queue.poll();
            if (current.x == end.x && current.y == end.y) {

                return true;
            }

            int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            for (int[] dir : dirs) {
                int newX = current.x + dir[0];
                int newY = current.y + dir[1];
                if (isValid(newX, newY) && maze[newX][newY] == 1 && !visited[newX][newY]) {
                    visited[newX][newY] = true;
                    queue.add(new Point(newX, newY));
                }
            }
        }

        return false;
    }

    /**
     * Znajduje najkrótszą ścieżkę między S i K używając algorytmu BFS.
     *
     * @return Macierz z zaznaczoną najkrótszą ścieżką
     */
    private static int[][] findPath() {
        int[][] pathMaze = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            pathMaze[i] = maze[i].clone();
        }

        boolean[][] visited = new boolean[SIZE][SIZE];
        Point[][] parent = new Point[SIZE][SIZE];
        Queue<Point> queue = new LinkedList<>();
        queue.add(start);
        visited[start.x][start.y] = true;

        while (!queue.isEmpty()) {
            Point current = queue.poll();
            if (current.x == end.x && current.y == end.y) {
                break;
            }

            int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            for (int[] dir : dirs) {
                int newX = current.x + dir[0];
                int newY = current.y + dir[1];
                if (isValid(newX, newY) && maze[newX][newY] == 1 && !visited[newX][newY]) {
                    visited[newX][newY] = true;
                    parent[newX][newY] = current;
                    queue.add(new Point(newX, newY));
                }
            }
        }

        Point current = end;
        while (current != null && !current.equals(start)) {
            pathMaze[current.x][current.y] = 2;
            current = parent[current.x][current.y];
        }
        pathMaze[start.x][start.y] = 'S';
        if (end != null) {
            pathMaze[end.x][end.y] = 'K';
        } else {
            out.println("BŁĄD! Punkt końcowy jest null-em");
        }

        return pathMaze;
    }

    /**
     * Sprawdza, czy współrzędne punktu są poprawne w kontekście rozmiaru labiryntu.
     *
     * @param x Współrzędna X
     * @param y Współrzędna Y
     * @return Zwraca {@code true}, jeśli współrzędne są poprawne, w przeciwnym razie zwraca {@code false}.
     */
    private static boolean isValid(int x, int y) {
        return x >= 0 && y >= 0 && x < SIZE && y < SIZE;
    }

    /**
     * Wyświetla labirynt na ekranie.
     * <ul>
     * <li>S — punkt startowy</li>
     * <li>K — punkt końcowy</li>
     * <li>• — najkrótsza ścieżka</li>
     * <li>0 — ściana</li>
     * <li>1 — przejście</li>
     * </ul>
     *
     * @param m Macierz labiryntu do wyświetlenia
     */
    private static void printMaze(int[][] m) {
        // Mapowanie wartości w labiryncie na odpowiednie kolory i symbole
        Map<Integer, String> colorMap = new HashMap<>();

        colorMap.put((int) 'S', COLOR_GREEN + POINT_START + COLOR_DEFAULT); // Punkt startowy
        colorMap.put((int) 'K', COLOR_RED + POINT_END + COLOR_DEFAULT);     // Punkt końcowy
        colorMap.put(2, COLOR_GREEN + POINT_DOT + COLOR_DEFAULT);        // Najkrótsza ścieżka
        colorMap.put(0, POINT_WALL);                                        // Ściana
        colorMap.put(1, POINT_FREE_WAY);                                    // Przejście

        // Budowanie strumienia wyjściowego za pomocą StringBuilder
        StringBuilder sb = new StringBuilder();

        // Iterowanie po wierszach labiryntu
        for (int i = 0; i < SIZE; i++) {
            // Iterowanie po kolumnach w danym wierszu
            for (int j = 0; j < SIZE; j++) {
                int value = m[i][j];
                String cellRepresentation = colorMap.getOrDefault(value, value + CHAR_DELIMITER);
                sb.append(cellRepresentation).append(CHAR_DELIMITER);
            }
            sb.append(System.lineSeparator());
        }

        // Wypisanie całego labiryntu
        out.print(sb);
    }

    /**
     * Reprezentuje punkt w labiryncie, który zawiera współrzędne X i Y.
     * Klasa ta jest używana do określenia pozycji w labiryncie oraz do przechowywania
     * punktów startowych, końcowych i innych istotnych punktów w procesie generowania
     * oraz rozwiązywania labiryntu.
     */
    private static class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Point point = (Point) obj;

            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

}
