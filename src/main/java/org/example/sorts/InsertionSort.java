package org.example.sorts;

import javafx.scene.paint.Color;
import org.example.Animator;

import java.util.ArrayList;

import static org.example.Animator.ANIMATION_DURATION;

public class InsertionSort implements Runnable{

    private ArrayList<Integer> sortedArray;
    private Animator animator;

    private boolean wasSwapped = false;

    public InsertionSort(ArrayList<Integer> array, Animator animator) {
        sortedArray = array;
        this.animator = animator;
    }

    public void run() {
        sort();
        animator.isSorting = false;
    }

    public void sort() {
        for(int i = 1; i < sortedArray.size(); i++) {
            if (animator.isSorting()) {
                int currentRectangle = i;
                animator.rectangles[currentRectangle].setFill(Color.GREEN);
                for (int j = i; j > 0; j--) {
                    animator.rectangles[j - 1].setFill(Color.RED);

                    if (animator.sortType(sortedArray.get(j-1),sortedArray.get(j))) {
                        int temp = sortedArray.get(j);
                        sortedArray.set(j, sortedArray.get(j - 1));
                        sortedArray.set(j - 1, temp);
                        wasSwapped = true;
                        animator.swapRectangles(j, j - 1);
                        currentRectangle = j - 1;
                    } else {
                        for(int k = 0; k < sortedArray.size(); k++) {
                            animator.rectangles[k].setFill(Color.LIGHTGRAY);
                        }
                        continue;
                    }
                    if (animator.isSorting()) {
                        threadSleep((long) (ANIMATION_DURATION * 1.25));
                    } else {
                        animator.paintRectangles(0, sortedArray.size(), Color.LIGHTGRAY);
                        return;
                    }
                    if (animator.isSorting()) {
                        if (wasSwapped) {
                            animator.rectangles[j].setFill(Color.LIGHTGRAY);
                        } else {
                            animator.rectangles[j - 1].setFill(Color.LIGHTGRAY);
                        }
                        wasSwapped = false;
                    } else {
                        animator.paintRectangles(0, sortedArray.size(), Color.LIGHTGRAY);
                        return;
                    }
                }
                animator.rectangles[currentRectangle].setFill(Color.LIGHTGRAY);
            } else {
                animator.paintRectangles(0, sortedArray.size(), Color.LIGHTGRAY);
                return;
            }
        }
    }

    public void threadSleep(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Устанавливаем флаг прерывания потока
            return;
        }
    }

}
