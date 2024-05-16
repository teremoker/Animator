package org.example;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.scene.control.Label;
import javafx.util.Duration;
import org.example.sorts.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class Animator extends Application {

    //Поток в котором выполняется сортировка
    public static Thread sort;
    //Строка для запоминания текущего метода сортировки
    private String currentSort;
    //Флаг, который устанавливается в true если в данный момент происходит сортировка
    public boolean isSorting = false;
    //Флаг для определения сортировки - по возрастанию или убыванию
    public boolean isIncreasingSort = true;


    //Константы для задания размеров графических элементов, размера массива, длительности анимации в мс
    private final  int SCREEN_HEIGHT = 400;
    private final int SCREEN_WIDTH = 1200;
    private final double HBOX_HEIGHT = 200;
    private final double HBOX_WIDTH = SCREEN_WIDTH;
    private final int BUTTON_WIDTH = 150;
    private final int BUTTON_HEIGHT = 40;
    private final int ARRAY_SIZE = 15;
    private static final int RECTANGLE_WIDTH = 40;
    private static final int RECTANGLE_HEIGHT = 40;
    private static final int X_OFFSET = 50;
    private static final int Y_OFFSET = 50;

    public static int ANIMATION_DURATION = 1000;
    //Диапазон генерации случайных целых чисел
    private final int RMIN = 1;
    private final int RMAX = 1000;


    //Поля для хранения сортируемого массива, а также его копии для восстановления исходного состояния
    private ArrayList<Integer> sortedArray = new ArrayList<Integer>();
    private ArrayList<Integer> copyArray = new ArrayList<Integer>();

    //Массив прямоугольников и текста, для получения доступа к элементам при анимации
    public Rectangle[] rectangles = new Rectangle[ARRAY_SIZE];
    public Text[] texts = new Text[ARRAY_SIZE];
    public Group[] groups = new Group[ARRAY_SIZE];


    private Stage primaryStage;
    //Корневой контейнер для хранения всех графических элементов - VBox
    private VBox rootLayout;

    //Здесь будут хранится визуализированные числа в виде прямоугольников
    private HBox drawedArray;



    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Аниматор сортировки");

        initRootLayout();
    }

    //Инициализация сцены
    private void initRootLayout() {
        rootLayout = new VBox();
        rootLayout.setSpacing(20);

        Scene scene = new Scene(rootLayout, SCREEN_WIDTH, SCREEN_HEIGHT);


        //Добавляем элементы управления
        addButtons();

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void addButtons() {
        //Элементы управления будут храниться в контейнере gridPane
        GridPane gridPane = new GridPane();

        //Инициализация drawedArray
        drawedArray = new HBox();
        drawedArray.setAlignment(Pos.CENTER);
        drawedArray.setPrefSize(HBOX_WIDTH, HBOX_HEIGHT);
        VBox.setMargin(drawedArray, new Insets(50, 0, 50, 0));


        //Кнопки для сортировок
        Button bubbleSort = new Button("Bubble Sort");
        bubbleSort.setMinWidth(BUTTON_WIDTH);
        bubbleSort.setMinHeight(BUTTON_HEIGHT);

        Button selectionSort = new Button("Selection Sort");
        selectionSort.setMinWidth(BUTTON_WIDTH);
        selectionSort.setMinHeight(2 * BUTTON_HEIGHT);

        Button insertionSort = new Button("Insertion Sort");
        insertionSort.setMinWidth(BUTTON_WIDTH);
        insertionSort.setMinHeight(BUTTON_HEIGHT);

        Button mergeSort = new Button("Merge Sort");
        mergeSort.setMinWidth(BUTTON_WIDTH);
        mergeSort.setMinHeight(BUTTON_HEIGHT);

        Button quickSort = new Button("Quick Sort");
        quickSort.setMinWidth(BUTTON_WIDTH);
        quickSort.setMinHeight(BUTTON_HEIGHT);

        //Другие кнопки и элементы управления
        Button loadArray = new Button("Загрузить массив чисел");
        loadArray.setMinWidth(BUTTON_WIDTH);
        loadArray.setMinHeight(BUTTON_HEIGHT);

        Button randomArray = new Button("Случайный массив");
        randomArray.setMinWidth(BUTTON_WIDTH);
        randomArray.setMinHeight(BUTTON_HEIGHT);

        TextField field = new TextField();
        field.setMinHeight(BUTTON_HEIGHT);
        field.setMinWidth(BUTTON_WIDTH);

        Button resetArray = new Button("Сбросить массив");
        resetArray.setMinHeight(BUTTON_HEIGHT);
        resetArray.setMinWidth(BUTTON_WIDTH);

        Button stopAnimation = new Button("Остановить анимацию");
        stopAnimation.setMinHeight(BUTTON_HEIGHT);
        stopAnimation.setMinWidth(BUTTON_WIDTH);

        Button startAnimation = new Button("Продолжить анимацию");
        startAnimation.setMinHeight(BUTTON_HEIGHT);
        startAnimation.setMinWidth(BUTTON_WIDTH);

        Button speedButton = new Button("Изменить скорость");
        speedButton.setMinWidth(BUTTON_WIDTH);
        speedButton.setMinHeight(BUTTON_HEIGHT);

        TextField speedField = new TextField();
        speedField.setText("Введите скорость в МС");
        speedField.setMinHeight(BUTTON_HEIGHT);
        speedField.setMinWidth(BUTTON_WIDTH);

        ToggleButton toggle = new ToggleButton();
        toggle.setMinHeight(BUTTON_HEIGHT * 2);
        toggle.setMinWidth(BUTTON_WIDTH);
        toggle.setText("По возрастанию");

        //Действия кнопок при нажатии
        //------------------------------------------------------------------------------------------------------
        //randomArray
        randomArray.setOnAction(event -> {
            generateRandomArray();
            drawArray();
        });

        //loadArray
        loadArray.setOnAction(event -> {
            String fileName = field.getText();
            loadArray(fileName);
            drawArray();
        });

        //resetArray
        resetArray.setOnAction(event -> {
            stopAnimation.fire();
            resetArray();
            drawArray();
        });

        //stopAnimation
        stopAnimation.setOnAction(event ->  {
            if (sort != null && sort.isAlive()) {
                this.isSorting = false;
            }
        });

        //startAnimation
        startAnimation.setOnAction(event -> {
            // Последняя сортировка начнет заново свое выполнение для текущего состояния массива
            if (sort != null) {
                isSorting = false;
                drawArray();
                startAnimation(currentSort);
            }

        });

        //speedButton
        speedButton.setOnAction(event -> {
            ANIMATION_DURATION = Integer.parseInt(speedField.getText());

        });

        //toggleButton
        toggle.setOnAction(event -> {
            if (toggle.isSelected()) {
                isIncreasingSort = false;
                toggle.setText("По убыванию");
            } else {
                isIncreasingSort = true;
                toggle.setText("По возрастанию");
            }
        });

        //bubbleSort
        bubbleSort.setOnAction(event ->  {
            currentSort = "Bubble Sort";
            startAnimation("Bubble Sort");

        });

        //selectionSort
        selectionSort.setOnAction(event -> {
                currentSort = "Selection Sort";
                startAnimation("Selection Sort");
        });

        //insertionSort
        insertionSort.setOnAction(event -> {
            currentSort = "Insertion Sort";
            startAnimation("Insertion Sort");
        });

        //mergeSort
        mergeSort.setOnAction(event -> {
            currentSort = "Merge Sort";
            startAnimation("Merge Sort");
        });

        //quickSort
        quickSort.setOnAction(event -> {
            currentSort = "Quick Sort";
            startAnimation("Quick Sort");
        });


        //------------------------------------------------------------------------------------------------------

        //Добавляем элементы в gridPane и vbox
        gridPane.add(bubbleSort, 0, 0);
        gridPane.add(insertionSort, 1, 0);
        gridPane.add(selectionSort, 2, 0);
        gridPane.add(mergeSort, 0, 1);
        gridPane.add(quickSort, 1, 1);
        gridPane.setRowSpan(selectionSort,2);

        gridPane.add(field,5,0);
        gridPane.add(loadArray,5,1);
        gridPane.add(randomArray,6,1);
        gridPane.add(resetArray,6,0);
        gridPane.add(stopAnimation, 3, 0);
        gridPane.add(startAnimation,3 ,1);
        gridPane.add(speedField, 4, 0);
        gridPane.add(speedButton, 4, 1);
        gridPane.add(toggle,7,0);
        gridPane.setRowSpan(toggle, 2);

        rootLayout.getChildren().addAll(drawedArray, gridPane);
    }

    //Отрисовка массива
    public void drawArray() {
        drawedArray.getChildren().clear(); // Очищаем контейнер перед отрисовкой
        for(int i = 0; i < ARRAY_SIZE; i++) {
            Rectangle rectangle = new Rectangle(X_OFFSET, Y_OFFSET, RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
            rectangle.setFill(Color.LIGHTGRAY);
            rectangle.setStroke(Color.BLACK);

            Text text = new Text(String.valueOf(sortedArray.get(i)));
            text.setFont(Font.font(14));
            text.setX(X_OFFSET + RECTANGLE_WIDTH / 2 - text.getBoundsInLocal().getWidth() / 2);
            text.setY(Y_OFFSET + RECTANGLE_HEIGHT / 2 + text.getBoundsInLocal().getHeight() / 2);

            rectangles[i] = rectangle;
            texts[i] = text;

            Group textField = new Group(rectangle, text);
            groups[i] = textField;
            drawedArray.getChildren().addAll(textField);
        }
    }

    //Генерация случайного массива
    private void generateRandomArray() {
        Random rand = new Random();
        int random;
        sortedArray.clear();
        copyArray.clear();
        for(int i = 0; i < ARRAY_SIZE; i++) {
            random = rand.nextInt(RMAX-RMIN + 1) + RMIN;
            sortedArray.add(random);
            copyArray.add(random);
        }
    }

    //Загрузка массива
    private void loadArray(String fileName) {
        try {
            if (!fileName.isEmpty()) {
                FileReader fileReader = new FileReader(fileName);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] numbers = line.split(" ");
                    for(String number: numbers) {
                        int n = Integer.parseInt(number);
                        copyArray.add(n);
                        sortedArray.add(n);
                    }
                }
                bufferedReader.close();
                fileReader.close();
            } else {
                showMessage("Задайте путь к файлу!");
            }
        } catch (Exception e) {
            showMessage("Ошибка загрузки из файла");
        }
    }

    //Сброс массива до изначального состояния
    public void resetArray() {
        isSorting = false;
        sortedArray.clear();
        sortedArray.addAll(copyArray);
    }

    private void showMessage(String message) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setTitle("Ошибка");

        Label label = new Label(message);
        Button okButton = new Button("OK");
        okButton.setOnAction(event -> dialogStage.close());

        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);

        vbox.getChildren().addAll(label, okButton);

        Scene dialogScene = new Scene(vbox, 200, 100);
        dialogStage.setScene(dialogScene);

        dialogStage.show();
    }

    //Запуск анимации
    public void startAnimation(String sortType) {
        if (!isSorting) {
            switch (sortType) {
                case "Bubble Sort":
                    sort = new Thread(new BubbleSort(sortedArray, this));
                    break;
                case "Insertion Sort":
                    sort = new Thread(new InsertionSort(sortedArray, this));
                    break;
                case "Selection Sort":
                    sort = new Thread(new SelectionSort(sortedArray, this));
                    break;
                case "Quick Sort":
                    sort = new Thread(new QuickSort(sortedArray, this));
                    break;
                case "Merge Sort":
                    sort = new Thread(new MergeSort(sortedArray, this));
                    break;

            }
            isSorting = true;
            sort.start();
        }
    }

    //Анимация перемещения прямоугольников
    public void swapRectangles(int i1, int i2) {
        Duration duration = Duration.millis((double) ANIMATION_DURATION / 2);

        //Анимация первого элемента
        TranslateTransition moveFirst = new TranslateTransition(duration, groups[i1]);
        moveFirst.setToY(-50);

        TranslateTransition moveFirstToSecond = new TranslateTransition(duration, groups[i1]);
        moveFirstToSecond.setToY(0);
        moveFirstToSecond.setToX(groups[i1].getTranslateX() + (i2 - i1) * rectangles[i1].getWidth() + (i2 - i1));

        //Анимация второго элемента
        TranslateTransition moveSecond = new TranslateTransition(duration, groups[i2]);
        moveSecond.setToY(50);
        TranslateTransition moveSecondToFirst = new TranslateTransition(duration, groups[i2]);
        moveSecondToFirst.setToY(0);
        moveSecondToFirst.setToX(groups[i2].getTranslateX() + (i1 - i2) * rectangles[i2].getWidth() + (i1 - i2));

        moveSecondToFirst.setOnFinished(event -> {
            //По завершению анимации меняем сравниваемые элементы в массиве
            Rectangle swapRectangles = rectangles[i1];
            rectangles[i1] = rectangles[i2];
            rectangles[i2] = swapRectangles;


            Group swapGroups = groups[i1];
            groups[i1] = groups[i2];
            groups[i2] = swapGroups;
        });

        ParallelTransition parallelTransition1 = new ParallelTransition(moveFirst, moveSecond);
        ParallelTransition parallelTransition2 = new ParallelTransition(moveFirstToSecond, moveSecondToFirst);
        SequentialTransition swapping = new SequentialTransition(parallelTransition1, parallelTransition2);
        if (isSorting()) {
            swapping.play();
        } else {
            paintRectangles(0, sortedArray.size(), Color.LIGHTGRAY);
        }
    }

    //Метод для определения порядка сортировки в массиве
    public boolean sortType(int n1, int n2) {
        if (this.isIncreasingSort) {
            return (n1 - n2 > 0);
        } else {
            return (n1 - n2 < 0);
        }
    }

    //Метод для проверки процесса сортировки
    public boolean isSorting() {
        return this.isSorting;
    }

    //Метод для изменения цвета заливки диапазона элементов
    public void paintRectangles(int left, int right, Color color) {
        for (int i = left; i < right; i++) {
            rectangles[i].setFill(color);
        }
    }


    public static void main(String[] args) {

        Locale.setDefault(new Locale("ru", "RU")); // Устанавливаем русскую локаль
        launch(args);
    }
}







