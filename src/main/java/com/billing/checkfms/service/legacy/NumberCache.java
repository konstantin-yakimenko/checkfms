package com.billing.checkfms.service.legacy;

import java.util.Arrays;

public class NumberCache {

    /**
     * Начальный размер буфера.
     */
    private int initSize = 1024;

    /**
     * Коэффициент роста буфера по мере его заполнения.
     */
    private float koeffSize = 1.5f;

    /**
     * Буфер.
     */
    private long buffer[];

    /**
     * Текущее количество элементов в буфере.
     */
    private int size = 0;

    public NumberCache(int initSize, float koeffSize) {
        this.initSize = initSize > 0 ? initSize : 1;
        this.koeffSize = koeffSize >= 1 ? koeffSize : 1;
    }

    public boolean contains(long x) {
        return buffer != null && Arrays.binarySearch(buffer, 0, size, x) >= 0;
    }

    protected boolean isEmpty() {
        return size == 0;
    }

    protected long getSize() {
        return size;
    }

    protected void clear() {
        buffer = null;
        size = 0;
    }

    protected void sort() {
        if (buffer != null)
            Arrays.sort(buffer, 0, size);
    }

    protected void addItem(long x) {
        if (buffer == null) {
            buffer = new long[initSize];
        }

        //если достигли границы, то увеличиваем ёмкость.
        checkSize(size + 1);

        buffer[size++] = x;
    }

    /**
     * Проверяет буфер на минимальный размер. Если размера не хватает, то увеличивает буфер.
     * @param minCapacity минимальный размер.
     */
    private void checkSize(int minCapacity) {
        int oldCapacity = buffer.length;
        if (minCapacity > oldCapacity) {

            int newCapacity = (int)((float)oldCapacity * koeffSize) + 1;
            if (newCapacity < minCapacity)
                newCapacity = minCapacity;

            buffer = Arrays.copyOf(buffer, newCapacity);
        }
    }
}
