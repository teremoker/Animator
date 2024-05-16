package org.example.sorts;

import javafx.scene.paint.Color;
import org.example.Animator;

import java.util.ArrayList;

import static org.example.Animator.ANIMATION_DURATION;

public class QuickSort implements Runnable {
    private ArrayList<Integer> sortedArray;
    private Animator animator;
    private boolean wasSwapped;

    public QuickSort(ArrayList<Integer> array, Animator animator) {
        sortedArray = array;
        this.animator = animator;
    }

    public void run() {
            sort(0, sortedArray.size() - 1);
    }

    private void sort(int low, int high) {
        if (low < high) {
            int pi = partition(low, high);
            if (animator.isSorting()) {
                sort(low, pi - 1);
            } else {
                //Снимаем выделение
                highlightRectangles(0, sortedArray.size() - 1, Color.LIGHTGRAY);
                return;
            }
            if (animator.isSorting()) {
                sort(pi + 1, high);
            } else {
                //Снимаем выделение
                highlightRectangles(0, sortedArray.size() - 1, Color.LIGHTGRAY);
                return;
            }
        }
    }

    private int partition(int low, int high) {
            int pivot = sortedArray.get(high);
            int i = low - 1;
            animator.rectangles[high].setFill(Color.BLUE);
            for (int j = low; j < high; j++) {
                //Зеленым помечается текущий сравниваемый с pivot элемент
                animator.rectangles[j].setFill(Color.GREEN);
                //Красным отмечается место, куда встанет текущий элемент с индексом j
                animator.rectangles[i + 1].setFill(Color.RED);

                //Условие при котором происходит смена двух элементов
                if (animator.sortType(pivot,sortedArray.get(j))) {
                    i += 1;
                    swap(i, j);
                    wasSwapped = true;
                }
                if (animator.isSorting()) {
                    threadSleep((long) (ANIMATION_DURATION * 1.25));
                } else {
                    highlightRectangles(0, sortedArray.size() - 1, Color.LIGHTGRAY);
                    return 0;
                }

                if (wasSwapped) {
                    animator.rectangles[i].setFill(Color.LIGHTGRAY);
                } else {
                    animator.rectangles[i + 1].setFill(Color.LIGHTGRAY);
                }
                wasSwapped = false;
                animator.rectangles[j].setFill(Color.LIGHTGRAY);
            }
            animator.rectangles[i + 1].setFill(Color.RED);
            //После проверки всего массива, в место с индексом i, становится pivot элемент, чтобы
            //Левая часть от pivot была меньше его, а правая больше
            if (animator.isSorting()) {
                swap(i + 1, high);
            } else {
                highlightRectangles(0, sortedArray.size() - 1, Color.LIGHTGRAY);
                return 0;
            }
            if (animator.isSorting()) {
                threadSleep((long) (ANIMATION_DURATION * 1.25));
            } else {
                highlightRectangles(0, sortedArray.size() - 1, Color.LIGHTGRAY);
                return 0;
            }
            animator.rectangles[high].setFill(Color.LIGHTGRAY);
            animator.rectangles[i + 1].setFill(Color.LIGHTGRAY);
            return i + 1;
    }


    private void swap(int i, int j) {
        int temp = sortedArray.get(i);
        sortedArray.set(i, sortedArray.get(j));
        sortedArray.set(j, temp);
        if (i != j) {
            animator.swapRectangles(i, j);
        }
    }

    public void highlightRectangles(int left, int right, Color color) {
        for (int i = left; i <= right; i++) {
            animator.rectangles[i].setFill(color);
        }
    }

    public void threadSleep(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
