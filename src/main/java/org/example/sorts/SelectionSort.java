package org.example.sorts;

import javafx.scene.paint.Color;
import org.example.Animator;
import static org.example.Animator.ANIMATION_DURATION;
import java.util.ArrayList;

public class SelectionSort implements Runnable {
    ArrayList<Integer> sortedArray;
    Animator animator;

    public SelectionSort(ArrayList<Integer> array, Animator animator) {
        sortedArray = array;
        this.animator = animator;
    }

    public void threadSleep(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // ????????????? ???? ?????????? ??????
            return;
        }
    }

    public void sort() {
        for (int i = 0; i < sortedArray.size(); i++) {
            int minIndex = i;
            for (int j = i + 1; j < sortedArray.size(); j++) {

                if (animator.isSorting()) {
                    animator.rectangles[i].setFill(Color.BLUE);
                    //?????????? ??????? ???????
                    animator.rectangles[j].setFill(Color.RED);
                    threadSleep(ANIMATION_DURATION / 2);

                    if (animator.sortType(sortedArray.get(minIndex),sortedArray.get(j))) {

                        //?????????? ??????? ??????? ???????????(???????????) ????????
                        animator.rectangles[minIndex].setFill(Color.LIGHTGRAY);
                        minIndex = j;
                        animator.rectangles[minIndex].setFill(Color.GREEN);
                        continue;
                    }

                    animator.rectangles[j].setFill(Color.LIGHTGRAY);
                } else {
                    animator.paintRectangles(0, sortedArray.size(), Color.LIGHTGRAY);
                    return;
                }
            }
            //???????????(????????????) ??????? ???????? ?? ??????? ?????
            if (minIndex != i && animator.isSorting()) {
                int temp = sortedArray.get(minIndex);
                sortedArray.set(minIndex, sortedArray.get(i));
                sortedArray.set(i, temp);

                animator.rectangles[minIndex].setFill(Color.GREEN);
                animator.rectangles[i].setFill(Color.BLUE);

                if (animator.isSorting()) {
                    animator.swapRectangles(i, minIndex);
                } else {
                    animator.paintRectangles(0, sortedArray.size(), Color.LIGHTGRAY);
                    return;
                }
                if (animator.isSorting()) {
                    threadSleep(ANIMATION_DURATION * 2L);
                } else {
                    animator.paintRectangles(0, sortedArray.size(), Color.LIGHTGRAY);
                    return;
                }
                animator.rectangles[minIndex].setFill(Color.LIGHTGRAY);
                animator.rectangles[i].setFill(Color.LIGHTGRAY);

            } else {
                animator.paintRectangles(0, sortedArray.size(), Color.LIGHTGRAY);
                if (!animator.isSorting()) {
                    return;
                }
            }
            animator.rectangles[i].setFill(Color.LIGHTGRAY);
        }
    }


    public void run() {
        sort();
        animator.isSorting = false;
    }
}
