package org.example.sorts;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import org.example.Animator;

import java.util.ArrayList;

import static org.example.Animator.ANIMATION_DURATION;



public class MergeSort implements Runnable {
    private ArrayList<Integer> sortedArray;
    private Animator animator;

    public MergeSort(ArrayList<Integer> array, Animator animator) {
        sortedArray = array;
        this.animator = animator;
    }

    public void sort(int low, int high) {
        if (low < high) {
            int mid = (low + high) / 2;
            if (animator.isSorting()) {
                sort(low, mid); // ���������� ����� �����
            }
            if (animator.isSorting()) {
                sort(mid + 1, high); // ���������� ������ �����
            }
            if (animator.isSorting()) {
                merge(low, mid, high); // �������
            } else {
                highlighRectangles(0, sortedArray.size() - 1, Color.LIGHTGRAY);
            }
        }
    }

    public void merge(int low, int mid, int high) {
        int n1 = mid - low + 1;
        int n2 = high - mid;

        ArrayList<Integer> tempLeft = new ArrayList<>(n1);
        ArrayList<Integer> tempRight = new ArrayList<>(n2);

        for (int i = 0; i < n1; ++i) {
            if (!animator.isSorting()) {
                highlighRectangles(0, sortedArray.size() - 1, Color.LIGHTGRAY);
                return;
            }
            tempLeft.add(sortedArray.get(low + i));
        }
        for (int j = 0; j < n2; ++j) {
            if (!animator.isSorting()) {
                highlighRectangles(0, sortedArray.size() - 1, Color.LIGHTGRAY);
                return;
            }
            tempRight.add(sortedArray.get(mid + 1 + j));
        }

        int i = 0, j = 0;
        int k = low; // ��������� ��������� ������� �������

        // ������������ ����� � ������ ����������
        highlighRectangles(low, mid, Color.BLUE); // ����� ���������
        highlighRectangles(mid + 1, high, Color.RED); // ������ ���������

        // ������� ��������� �������� ������� � �������� ������
        while (i < n1 && j < n2) {

            if (animator.sortType(tempRight.get(j), tempLeft.get(i))) {
                sortedArray.set(k, tempLeft.get(i)); // ������� �� ������ �������
                i++;
            } else {
                sortedArray.set(k, tempRight.get(j)); // ������� �� ������� �������
                j++;
            }

            highlightElement(k, Color.GREEN); // ��������� ������������ ��������
            Platform.runLater(() -> {
                if (!animator.isSorting()) {
                    highlighRectangles(0, sortedArray.size() - 1, Color.LIGHTGRAY);
                    return;
                }
                //������������ ������
                animator.drawArray();
                // ��������������� ��������� ��������
                highlighRectangles(low, mid, Color.BLUE);
                highlighRectangles(mid + 1, high, Color.RED);
            });

            k++; // ����������� ������� �������
            if (animator.isSorting()) {
                threadSleep(ANIMATION_DURATION / 2); // ����� ��� ������������
            } else {
                highlighRectangles(0, sortedArray.size() - 1, Color.LIGHTGRAY);
                return;
            }
        }

        // ����������� ���������� ��������� �� ������ �������
        while (i < n1) {
            if (!animator.isSorting()) {
                highlighRectangles(0, sortedArray.size() - 1, Color.LIGHTGRAY);
                return;
            }
            sortedArray.set(k, tempLeft.get(i));
            highlightElement(k, Color.GREEN);
            Platform.runLater(() -> {
                if (!animator.isSorting()) {
                    highlighRectangles(0, sortedArray.size() - 1, Color.LIGHTGRAY);
                    return;
                }
                animator.drawArray();
                highlighRectangles(low, mid, Color.BLUE);
                highlighRectangles(mid + 1, high, Color.RED);
            });
            i++;
            k++;
            threadSleep(ANIMATION_DURATION / 2);
        }

        // ����������� ���������� ��������� �� ������� �������
        while (j < n2) {
            if (!animator.isSorting()) {
                highlighRectangles(0, sortedArray.size() - 1, Color.LIGHTGRAY);
                return;
            }
            sortedArray.set(k, tempRight.get(j));
            highlightElement(k, Color.GREEN);
            if (animator.isSorting()) {
                Platform.runLater(() -> {
                    animator.drawArray();
                    highlighRectangles(low, mid, Color.BLUE);
                    highlighRectangles(mid + 1, high, Color.RED);
                });
            } else {
                highlighRectangles(0, sortedArray.size() - 1, Color.LIGHTGRAY);
                return;
            }
            j++;
            k++;
            threadSleep(ANIMATION_DURATION / 2);
        }

        // ������� ��������� �������� ����� ����������
        highlighRectangles(low, high, Color.LIGHTGRAY);
        Platform.runLater(() -> {
            animator.drawArray();
        });
    }

    public void highlightElement(int index, Color color) {
        if (animator.isSorting()) {
            Platform.runLater(() -> {
                animator.rectangles[index].setFill(color); // ��������� ��������
            });
            threadSleep(ANIMATION_DURATION / 2);
            Platform.runLater(() -> {
                animator.rectangles[index].setFill(Color.LIGHTGRAY);
            });
        } else {
            highlighRectangles(0, sortedArray.size() - 1, Color.LIGHTGRAY);
        }
    }

    public void highlighRectangles(int left, int right, Color color) {
        if (animator.isSorting()) {
            for (int i = left; i <= right; i++) {
                animator.rectangles[i].setFill(color);
            }
        } else {
            for (int i = 0; i < sortedArray.size(); i++) {
                animator.rectangles[i].setFill(Color.LIGHTGRAY);
            }
        }
    }

    public void threadSleep(long duration) {
        if (!animator.isSorting()) {
            highlighRectangles(0, sortedArray.size() - 1, Color.LIGHTGRAY);
            return;
        }
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // ������������� ���� ���������� ������
        }
    }

    public void run() {
            sort(0, sortedArray.size() - 1);
            animator.isSorting = false;
    }
}

