package org.example.sorts;

import javafx.animation.SequentialTransition;
import javafx.scene.paint.Color;
import org.example.Animator;
import static org.example.Animator.ANIMATION_DURATION;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;



public class BubbleSort implements Runnable {

    private ArrayList<Integer> sortedArray;
    private Animator animator;

    public BubbleSort(ArrayList<Integer> array, Animator animator) {
        sortedArray = array;
        this.animator = animator;
    }

    public void run() {
        sort(sortedArray, animator);
        animator.isSorting = false;
    }

    private void sort(ArrayList<Integer> array, Animator animator) {
        for (int i = 0; i < array.size(); i++) {
            for (int j = 0; j + 1 < array.size() - i; j++) {
                if (animator.isSorting()) {
                    animator.rectangles[j].setFill(Color.GREEN);
                    animator.rectangles[j + 1].setFill(Color.RED);


                    if (animator.sortType(array.get(j), array.get(j+1))) {
                        int temp = array.get(j);
                        array.set(j, array.get(j + 1));
                        array.set(j + 1, temp);

                        animator.swapRectangles(j, j+1);
                    }
                    threadSleep((long) (ANIMATION_DURATION * 1.25));
                    animator.rectangles[j].setFill(Color.LIGHTGRAY);
                    animator.rectangles[j + 1].setFill(Color.LIGHTGRAY);
                } else {
                    return;
                }
            }
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

